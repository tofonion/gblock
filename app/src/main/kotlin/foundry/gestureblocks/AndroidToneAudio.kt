package foundry.gestureblocks

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Handler
import android.os.Looper
import kotlin.math.PI
import kotlin.math.sin

// App-level sound events; core rules never reference Android audio.
internal enum class AudioCue {
    Move,
    Rotate,
    SoftDrop,
    HardDrop,
    LineClear,
    Pause,
    Restart,
    GameOver,
}

// Generated placeholder audio for the prototype; no external assets are bundled.
internal class AndroidToneAudio {
    private val handler = Handler(Looper.getMainLooper())
    private var musicEnabled = false
    private var musicStep = 0
    private var volumeScale = 1.0

    // Short generated loop used until original music assets exist.
    private val musicLoop =
        listOf(
            MusicNote(330.0, 160, 210),
            MusicNote(392.0, 160, 210),
            MusicNote(494.0, 160, 210),
            MusicNote(659.0, 190, 260),
            MusicNote(494.0, 160, 210),
            MusicNote(392.0, 160, 210),
            MusicNote(370.0, 190, 280),
            MusicNote(440.0, 160, 240),
        )
    private val musicTick =
        object : Runnable {
            override fun run() {
                if (!musicEnabled) return
                val note = musicLoop[musicStep % musicLoop.size]
                musicStep++
                playTone(note.frequencyHz, note.durationMs, volume = 0.72)
                handler.postDelayed(this, note.delayMs.toLong())
            }
        }

    // Maps game/session events to simple diagnostic tones.
    fun play(cue: AudioCue) {
        val tone =
            when (cue) {
                AudioCue.Move -> ToneSpec(440.0, 75, 0.82)
                AudioCue.Rotate -> ToneSpec(660.0, 85, 0.82)
                AudioCue.SoftDrop -> ToneSpec(330.0, 75, 0.78)
                AudioCue.HardDrop -> ToneSpec(180.0, 145, 0.88)
                AudioCue.LineClear -> ToneSpec(880.0, 220, 0.88)
                AudioCue.Pause -> ToneSpec(260.0, 180, 0.82)
                AudioCue.Restart -> ToneSpec(520.0, 180, 0.84)
                AudioCue.GameOver -> ToneSpec(140.0, 360, 0.9)
            }
        playTone(tone.frequencyHz, tone.durationMs, tone.volume)
    }

    // Long tone used from settings to test device/app audio routing.
    fun playDiagnosticTone() {
        playTone(880.0, 1_200, volume = 0.95)
    }

    // Scales generated PCM without changing Android system media volume.
    fun setVolumeScale(scale: Float) {
        volumeScale = scale.toDouble().coerceIn(0.0, 1.0)
    }

    // Starts or stops the generated music loop on the main thread.
    fun setMusicEnabled(enabled: Boolean) {
        if (musicEnabled == enabled) return
        musicEnabled = enabled
        if (enabled) {
            musicStep = 0
            handler.post(musicTick)
        } else {
            handler.removeCallbacks(musicTick)
        }
    }

    fun release() {
        setMusicEnabled(false)
    }

    private fun playTone(frequencyHz: Double, durationMs: Int, volume: Double) {
        val effectiveVolume = (volume * volumeScale).coerceIn(0.0, 1.0)
        val samples = (SampleRate * durationMs) / MillisPerSecond
        val buffer = ShortArray(samples)

        // Build a mono sine wave with a tiny envelope to avoid clicks.
        for (i in buffer.indices) {
            val envelope = envelope(i, samples)
            val sample = sin(TwoPi * i * frequencyHz / SampleRate) * effectiveVolume * envelope
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }

        // Static AudioTrack is enough for short generated one-shot tones.
        val track =
            AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build(),
                )
                .setTransferMode(AudioTrack.MODE_STATIC)
                .setBufferSizeInBytes(buffer.size * ShortSizeBytes)
                .build()

        track.write(buffer, 0, buffer.size)
        track.play()
        handler.postDelayed(
            {
                track.stop()
                track.release()
            },
            (durationMs + ReleasePaddingMs).toLong(),
        )
    }

    // Simple attack/release envelope for less abrupt generated tones.
    private fun envelope(index: Int, total: Int): Double {
        val attack = (total * 0.08).toInt().coerceAtLeast(1)
        val release = (total * 0.12).toInt().coerceAtLeast(1)
        return when {
            index < attack -> index.toDouble() / attack
            index > total - release -> (total - index).coerceAtLeast(0).toDouble() / release
            else -> 1.0
        }
    }

    private data class ToneSpec(
        val frequencyHz: Double,
        val durationMs: Int,
        val volume: Double,
    )

    private data class MusicNote(
        val frequencyHz: Double,
        val durationMs: Int,
        val delayMs: Int,
    )
}

private const val SampleRate = 44_100
private const val MillisPerSecond = 1_000
private const val ShortSizeBytes = 2
private const val ReleasePaddingMs = 40
private const val TwoPi = PI * 2.0
