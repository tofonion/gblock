# GBLOCK-011 Progress

Packet: `GBLOCK-011`

Scope: audio boundary and original placeholder audio only. No commercial soundtrack import, no proprietary samples, no network audio services, no deployment.

## Step 0 - Phase Start

Status: COMPLETE

Context:

- User asked about using Tetris GB MP3s from KHInsider.
- Codex declined to download/import commercial soundtrack files into canonical source.
- User approved starting with a safe alternative: original placeholder audio and private local override policy.

## Step 1 - Private Audio Override Policy

Status: COMPLETE

Added:

- `private-audio-overrides/README.md`
- `docs/audio-private-overrides.md`

Git ignore:

- `private-audio-overrides/*.mp3`
- `private-audio-overrides/*.wav`
- `private-audio-overrides/*.ogg`

Policy:

- Commercial soundtrack files may be used only as private local experiments if the user places them there.
- They are not project assets.
- They are not loaded by the app yet.
- They must not be committed, bundled, redistributed, or treated as licensed GBLOCK content.

## Step 2 - Audio Boundary Added

Status: COMPLETE

Added:

- `app/src/main/kotlin/foundry/gestureblocks/AndroidToneAudio.kt`

Audio boundary:

- `AudioCue`
- `AndroidToneAudio`

Placeholder implementation:

- Android `ToneGenerator` cues only.
- No audio files were imported.
- No copyrighted music or samples were added.

Audio cues considered/covered:

- Move
- Rotate
- Soft drop
- Hard drop
- Line clear
- Pause
- Restart
- Game over

## Step 3 - Audio Toggle Added

Status: COMPLETE

Changed:

- `app/src/main/kotlin/foundry/gestureblocks/MainActivity.kt`

Behavior:

- Runtime `AUDIO: ON/OFF` toggle added to the `SET` menu.
- Audio cue selection is driven by app/session events outside the pure game rules.
- Core remains Android-independent.

Limitations:

- Audio toggle is runtime-only and not persisted yet.
- No volume slider.
- No private override loading yet.

## Step 4 - Original Placeholder Music Loop

Status: COMPLETE

Changed:

- `app/src/main/kotlin/foundry/gestureblocks/AndroidToneAudio.kt`
- `app/src/main/kotlin/foundry/gestureblocks/MainActivity.kt`

Added:

- Original generated tone sequence for placeholder background music.
- `MUSIC: ON/OFF` setting in the `SET` menu.
- Music loop start/stop lifecycle handling through `AndroidToneAudio`.

Boundary:

- No MP3/WAV/OGG files were added.
- No commercial soundtrack material was downloaded or imported.
- The loop is generated from Android tone constants and is intended only as a temporary placeholder.

Limitations:

- The generated loop is intentionally crude.
- Music setting is runtime-only and not persisted yet.
- Music and sound share the same global audio toggle behavior.
- Manual listening check on TC57 is still needed.

## Step 5 - Validation

Status: COMPLETE

Validation command:

```powershell
$env:GRADLE_USER_HOME='C:\Foundry\Projects\gesture-blocks\.gradle-user'
.\gradlew.bat test assembleDebug --no-daemon
```

Result:

- `BUILD SUCCESSFUL in 29s`
- `BUILD SUCCESSFUL in 26s` after adding placeholder music loop.
- Known SDK XML and AGP compile SDK warnings remain.

Device install:

```powershell
adb -s 21076522507700 install -r app\build\outputs\apk\debug\app-debug.apk
```

Result:

- `Success`
- `Success` after adding placeholder music loop.

GBLOCK-011 Status:

- COMPLETE within current safe scope.
- Manual audio/music toggle listening check on TC57 remains recommended.

## Step 6 - TC57 Inaudible ToneGenerator Fix

Status: COMPLETE

Issue:

- User reported that both music and sound were enabled but nothing was audible on the Zebra TC57.
- ADB temporarily lost the device during audio-state inspection, so volume/DND state could not be fully captured at that moment.

Change:

- Replaced `ToneGenerator` placeholder backend with generated PCM playback via `AudioTrack`.

Changed:

- `app/src/main/kotlin/foundry/gestureblocks/AndroidToneAudio.kt`

Why:

- `ToneGenerator` can be device/OEM/stream-policy dependent and was not audible on the TC57.
- `AudioTrack` writes explicit PCM samples to the music stream, which should be more predictable for game placeholder audio.

Validation command:

```powershell
$env:GRADLE_USER_HOME='C:\Foundry\Projects\gesture-blocks\.gradle-user'
.\gradlew.bat test assembleDebug --no-daemon
```

Result:

- `BUILD SUCCESSFUL in 21s`

Device install:

```powershell
adb -s 21076522507700 install -r app\build\outputs\apk\debug\app-debug.apk
```

Result:

- `Success`

Retest needed:

- On TC57, confirm media volume is up.
- Open `SET`.
- Confirm `AUDIO: ON`.
- Confirm `MUSIC: ON`.
- Listen for generated music loop and move/rotate/drop cues.

## Step 7 - TC57 Audio Diagnostic Iteration

Status: IN PROGRESS

Issue:

- User still cannot hear audio on the Zebra TC57 after the `AudioTrack` backend change.

ADB findings:

- `dumpsys audio` shows `STREAM_MUSIC` is not muted.
- Active media route is `speaker`.
- `STREAM_MUSIC` speaker volume was reported at `12/15`.
- Playback monitor showed GBLOCK-created `AudioTrack` players starting with app UID.

Interpretation:

- Evidence suggests the app is requesting and starting audio playback.
- The remaining problem may be tone audibility on this hardware, Zebra speaker policy/output behavior, or a device speaker/routing issue.

Changed:

- `app/src/main/kotlin/foundry/gestureblocks/AndroidToneAudio.kt`
- `app/src/main/kotlin/foundry/gestureblocks/MainActivity.kt`

Implementation:

- Increased placeholder cue volumes and durations.
- Increased sample rate to `44_100`.
- Changed generated tone attributes to `USAGE_MEDIA` and `CONTENT_TYPE_MUSIC`.
- Added a `TEST SND` settings-menu item that plays a louder 1.2 second diagnostic tone and forces `AUDIO` on.

Validation:

- `BUILD SUCCESSFUL in 46s`
- `BUILD SUCCESSFUL in 25s` after settings auto-pause follow-up.
- Updated APK installed successfully on Zebra TC57.

Retest needed:

- Open `SET`.
- Tap `TEST SND`.
- If still silent, test any known-good media/audio app on the TC57 speaker to separate app audio from device output.

## Step 8 - Runtime Volume Slider

Status: COMPLETE

User feedback:

- Audio is still inaudible.
- Add a volume slider in settings.

Changed:

- `app/src/main/kotlin/foundry/gestureblocks/AndroidToneAudio.kt`
- `app/src/main/kotlin/foundry/gestureblocks/MainActivity.kt`

Implementation:

- Added a runtime `VOL` slider to the `SET` menu.
- Slider scales generated audio amplitude from 0% to 100%.
- Moving the slider forces `AUDIO` on.
- The slider controls app-generated placeholder audio only; it does not change Android system media volume.

Validation:

- `BUILD SUCCESSFUL in 27s`
- Updated APK installed successfully on Zebra TC57.

Retest needed:

- Open `SET`.
- Confirm `VOL` is at 100%.
- Tap `TEST SND`.
- If still silent, verify TC57 speaker output with another media source.
