package foundry.gestureblocks

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import foundry.gestureblocks.core.GameCommand
import foundry.gestureblocks.core.GameCore
import foundry.gestureblocks.core.GameState
import foundry.gestureblocks.core.GameSnapshot
import foundry.gestureblocks.core.GestureEvent
import foundry.gestureblocks.core.GestureInterpreter
import foundry.gestureblocks.core.Position
import foundry.gestureblocks.core.Tetromino
import foundry.gestureblocks.core.TetrominoType
import foundry.gestureblocks.core.absolutePositions
import kotlinx.coroutines.delay
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    private var foregroundLossPauseRequest: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GestureBlocksApp(
                onForegroundLossPauseRequest = { foregroundLossPauseRequest = it },
            )
        }
    }

    override fun onPause() {
        foregroundLossPauseRequest?.invoke()
        super.onPause()
    }
}

@Composable
fun GestureBlocksApp(
    onForegroundLossPauseRequest: ((() -> Unit)?) -> Unit = {},
) {
    val context = LocalContext.current
    val preferences = remember(context) { GblockPreferences(context) }

    // App-owned session state; the pure game rules stay in the core module.
    var state by remember {
        mutableStateOf(
            preferences.loadRecoverableGameState()
                ?: GameState.initial(listOf(TetrominoType.T, TetrominoType.I, TetrominoType.O)),
        )
    }
    var sensitivity by remember { mutableStateOf(preferences.loadSensitivity()) }
    var skin by remember { mutableStateOf(preferences.loadSkin()) }
    var settingsOpen by remember { mutableStateOf(false) }
    var audioEnabled by remember { mutableStateOf(preferences.loadAudioEnabled()) }
    var musicEnabled by remember { mutableStateOf(preferences.loadMusicEnabled()) }
    var audioVolume by remember { mutableStateOf(preferences.loadAudioVolume()) }
    var highScore by remember { mutableStateOf(preferences.loadHighScore()) }
    val audio = remember { AndroidToneAudio() }
    DisposableEffect(audio) {
        onDispose { audio.release() }
    }

    // Rebuild gesture thresholds whenever the user changes sensitivity.
    val gestureInterpreter =
        remember(sensitivity) {
            GestureInterpreter(
                horizontalThresholdPx = sensitivity.horizontalThresholdPx,
                softDropThresholdPx = sensitivity.softDropThresholdPx,
                hardDropThresholdPx = sensitivity.hardDropThresholdPx,
                hardDropVelocityPxPerSecond = sensitivity.hardDropVelocityPxPerSecond,
            )
        }

    // Keep audio adapter state in sync with the settings menu.
    LaunchedEffect(audioEnabled, musicEnabled) {
        audio.setMusicEnabled(audioEnabled && musicEnabled)
    }
    LaunchedEffect(audioVolume) {
        audio.setVolumeScale(audioVolume)
    }

    // Promote the current score into the saved high score when it beats it.
    LaunchedEffect(state.score) {
        if (state.score > highScore) {
            highScore = state.score
            preferences.saveHighScore(state.score)
        }
    }

    // Persist user-facing settings as they change; no game rule state is stored.
    LaunchedEffect(skin) {
        preferences.saveSkin(skin)
    }
    LaunchedEffect(sensitivity) {
        preferences.saveSensitivity(sensitivity)
    }
    LaunchedEffect(audioEnabled) {
        preferences.saveAudioEnabled(audioEnabled)
    }
    LaunchedEffect(musicEnabled) {
        preferences.saveMusicEnabled(musicEnabled)
    }
    LaunchedEffect(audioVolume) {
        preferences.saveAudioVolume(audioVolume)
    }
    val latestState by rememberUpdatedState(state)

    // All UI and gesture commands pass through the core reducer here.
    val dispatch: (GameCommand) -> Unit = { command ->
        val before = state
        val effectiveCommand =
            if (state.isGameOver && command == GameCommand.RotateClockwise) {
                GameCommand.Restart
            } else {
                command
            }
        val after = GameCore.dispatch(state, effectiveCommand)
        state = after
        preferences.saveRecoverableGameState(after)
        if (audioEnabled) {
            audioCueFor(effectiveCommand, before, after)?.let(audio::play)
        }
    }

    DisposableEffect(dispatch) {
        onForegroundLossPauseRequest {
            dispatch(GameCommand.Pause)
        }
        onDispose { onForegroundLossPauseRequest(null) }
    }

    // Drive gravity from the app layer so timing stays separate from core rules.
    LaunchedEffect(state.isPaused, state.isGameOver, state.level) {
        while (!latestState.isPaused && !latestState.isGameOver) {
            delay(gravityDelayMillis(latestState.level))
            val afterTick = GameCore.dispatch(latestState, GameCommand.Tick)
            state = afterTick
            preferences.saveRecoverableGameState(afterTick)
        }
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = skin.palette.background,
        ) {
            GameSnapshotScreen(
                snapshot = state.snapshot(),
                gestureInterpreter = gestureInterpreter,
                onCommand = dispatch,
                highScore = highScore,
                skin = skin,
                sensitivity = sensitivity,
                settingsOpen = settingsOpen,
                onSettingsOpenChange = { open ->
                    // Settings is modal: opening it pauses active play, closing it does not resume.
                    if (open && !settingsOpen && !state.isPaused && !state.isGameOver) {
                        dispatch(GameCommand.PauseToggle)
                    }
                    settingsOpen = open
                },
                onSkinChange = { skin = it },
                onSensitivityChange = { sensitivity = it },
                audioEnabled = audioEnabled,
                onAudioEnabledChange = { audioEnabled = it },
                musicEnabled = musicEnabled,
                onMusicEnabledChange = { musicEnabled = it },
                audioVolume = audioVolume,
                onAudioVolumeChange = {
                    audioVolume = it
                    audioEnabled = true
                },
                onHighScoreReset = {
                    highScore = 0
                    preferences.saveHighScore(0)
                },
                onTestSound = {
                    audioEnabled = true
                    audio.playDiagnosticTone()
                },
            )
        }
    }
}

@Composable
private fun GameSnapshotScreen(
    snapshot: GameSnapshot,
    gestureInterpreter: GestureInterpreter,
    onCommand: (GameCommand) -> Unit,
    highScore: Int,
    skin: GameSkin,
    sensitivity: GestureSensitivityPreset,
    settingsOpen: Boolean,
    onSettingsOpenChange: (Boolean) -> Unit,
    onSkinChange: (GameSkin) -> Unit,
    onSensitivityChange: (GestureSensitivityPreset) -> Unit,
    audioEnabled: Boolean,
    onAudioEnabledChange: (Boolean) -> Unit,
    musicEnabled: Boolean,
    onMusicEnabledChange: (Boolean) -> Unit,
    audioVolume: Float,
    onAudioVolumeChange: (Float) -> Unit,
    onHighScoreReset: () -> Unit,
    onTestSound: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(skin.palette.background)
                .padding(
                    start = skin.layout.screenPaddingStart.dp,
                    top = skin.layout.screenPaddingTop.dp,
                    end = skin.layout.screenPaddingEnd.dp,
                    bottom = skin.layout.screenPaddingBottom.dp,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Keep high score lightweight so it does not shrink the playfield.
        Text(
            text = "HIGH SCORE  $highScore",
            color = skin.palette.textMuted,
            fontSize = skin.layout.highScoreFontSp.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(skin.layout.highScoreBottomGap.dp))
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GameBoardCanvas(
                snapshot = snapshot,
                onCommand = onCommand,
                skin = skin,
                modifier =
                    Modifier
                        .weight(1f)
                        .aspectRatio(snapshot.board.width.toFloat() / snapshot.board.height.toFloat())
                        .gestureControls(gestureInterpreter, onCommand),
            )

            Spacer(modifier = Modifier.width(skin.layout.boardSideGap.dp))

            SidePanel(
                snapshot = snapshot,
                skin = skin,
                sensitivity = sensitivity,
                settingsOpen = settingsOpen,
                onSettingsOpenChange = onSettingsOpenChange,
                onSkinChange = onSkinChange,
                onSensitivityChange = onSensitivityChange,
                audioEnabled = audioEnabled,
                onAudioEnabledChange = onAudioEnabledChange,
                musicEnabled = musicEnabled,
                onMusicEnabledChange = onMusicEnabledChange,
                audioVolume = audioVolume,
                onAudioVolumeChange = onAudioVolumeChange,
                onHighScoreReset = onHighScoreReset,
                onTestSound = onTestSound,
            )
        }
    }
}

@Composable
private fun SidePanel(
    snapshot: GameSnapshot,
    skin: GameSkin,
    sensitivity: GestureSensitivityPreset,
    settingsOpen: Boolean,
    onSettingsOpenChange: (Boolean) -> Unit,
    onSkinChange: (GameSkin) -> Unit,
    onSensitivityChange: (GestureSensitivityPreset) -> Unit,
    audioEnabled: Boolean,
    onAudioEnabledChange: (Boolean) -> Unit,
    musicEnabled: Boolean,
    onMusicEnabledChange: (Boolean) -> Unit,
    audioVolume: Float,
    onAudioVolumeChange: (Float) -> Unit,
    onHighScoreReset: () -> Unit,
    onTestSound: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .width(skin.layout.sidePanelWidth.dp)
                .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Right rail mirrors the classic handheld layout: stats, next, status, settings.
        StatPanel(label = "SCORE", value = snapshot.score.toString(), skin = skin)
        Spacer(modifier = Modifier.height(skin.layout.scoreLevelGap.dp))
        StatPanel(label = "LEVEL", value = snapshot.level.toString(), skin = skin)
        Spacer(modifier = Modifier.height(skin.layout.levelLinesGap.dp))
        StatPanel(label = "LINES", value = snapshot.lines.toString(), skin = skin)
        Spacer(modifier = Modifier.height(skin.layout.linesNextGap.dp))
        NextPanel(snapshot = snapshot, skin = skin)
        Spacer(modifier = Modifier.height(skin.layout.nextStatusGap.dp))
        Text(
            text = statusText(snapshot),
            color = skin.palette.status,
            fontSize = skin.layout.statusFontSp.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd,
        ) {
            SettingsMenu(
                skin = skin,
                sensitivity = sensitivity,
                isOpen = settingsOpen,
                onOpenChange = onSettingsOpenChange,
                onSkinChange = onSkinChange,
                onSensitivityChange = onSensitivityChange,
                audioEnabled = audioEnabled,
                onAudioEnabledChange = onAudioEnabledChange,
                musicEnabled = musicEnabled,
                onMusicEnabledChange = onMusicEnabledChange,
                audioVolume = audioVolume,
                onAudioVolumeChange = onAudioVolumeChange,
                onHighScoreReset = onHighScoreReset,
                onTestSound = onTestSound,
            )
        }
    }
}

@Composable
private fun StatPanel(label: String, value: String, skin: GameSkin) {
    FramedPanel(
        skin = skin,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            color = skin.palette.textMuted,
            fontSize = skin.layout.statLabelFontSp.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = value,
            color = skin.palette.accent,
            fontSize = skin.layout.statValueFontSp.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun NextPanel(snapshot: GameSnapshot, skin: GameSkin) {
    FramedPanel(
        skin = skin,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(skin.layout.nextPanelHeight.dp),
    ) {
        Text(
            text = "NEXT",
            color = skin.palette.textMuted,
            fontSize = skin.layout.nextLabelFontSp.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
        )
        NextTetrominoPreview(
            type = snapshot.nextPiece,
            skin = skin,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(skin.layout.nextPreviewHeight.dp),
        )
    }
}

@Composable
private fun NextTetrominoPreview(
    type: TetrominoType?,
    skin: GameSkin,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        if (type == null) return@Canvas

        // Center the real tetromino shape inside the compact preview canvas.
        val blocks = Tetromino(type).blocks
        val minX = blocks.minOf { it.x }
        val maxX = blocks.maxOf { it.x }
        val minY = blocks.minOf { it.y }
        val maxY = blocks.maxOf { it.y }
        val pieceWidth = maxX - minX + 1
        val pieceHeight = maxY - minY + 1
        val cellSize = minOf(size.width / 4.5f, size.height / 3.2f)
        val piecePixelWidth = pieceWidth * cellSize
        val piecePixelHeight = pieceHeight * cellSize
        val origin =
            Offset(
                (size.width - piecePixelWidth) / 2f - minX * cellSize,
                (size.height - piecePixelHeight) / 2f - minY * cellSize,
            )

        blocks.forEach { position ->
            drawCell(origin, cellSize, position, type, skin)
        }
    }
}

@Composable
private fun FramedPanel(
    skin: GameSkin,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier =
            modifier
                .background(skin.palette.panelBackground)
                .padding(skin.layout.panelPadding.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        content()
    }
}

@Composable
private fun GameBoardCanvas(
    snapshot: GameSnapshot,
    onCommand: (GameCommand) -> Unit,
    skin: GameSkin,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        // Renderer projects the immutable snapshot; it does not mutate game state.
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellWidth = size.width / snapshot.board.width
            val cellHeight = size.height / snapshot.board.height
            val cellSize = minOf(cellWidth, cellHeight)
            val boardWidth = cellSize * snapshot.board.width
            val boardHeight = cellSize * snapshot.board.height
            val origin = Offset((size.width - boardWidth) / 2f, (size.height - boardHeight) / 2f)

            drawRect(
                color = skin.palette.boardBackground,
                topLeft = origin,
                size = Size(boardWidth, boardHeight),
            )

            for (y in 0 until snapshot.board.height) {
                for (x in 0 until snapshot.board.width) {
                    val topLeft = origin + Offset(x * cellSize, y * cellSize)
                    drawRect(
                        color = skin.palette.grid,
                        topLeft = topLeft,
                        size = Size(cellSize, cellSize),
                        style = Stroke(width = 1.dp.toPx()),
                    )
                }
            }

            snapshot.board.cells.forEach { (position, type) ->
                drawCell(origin, cellSize, position, type, skin)
            }

            Tetromino(snapshot.currentPiece.type, snapshot.currentPiece.rotation)
                .absolutePositions(snapshot.currentPosition)
                .forEach { position ->
                    drawCell(origin, cellSize, position, snapshot.currentPiece.type, skin)
                }

            drawRect(
                color = skin.palette.border,
                topLeft = origin,
                size = Size(boardWidth, boardHeight),
                style = Stroke(width = 2.dp.toPx()),
            )
        }

        if (snapshot.isPaused || snapshot.isGameOver) {
            SessionOverlay(
                snapshot = snapshot,
                onCommand = onCommand,
                skin = skin,
                modifier = Modifier.matchParentSize(),
            )
        }
    }
}

@Composable
private fun SessionOverlay(
    snapshot: GameSnapshot,
    onCommand: (GameCommand) -> Unit,
    skin: GameSkin,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .background(Color(0xCC05070D))
                .padding(18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = if (snapshot.isGameOver) "GAME OVER" else "PAUSED",
                color = if (snapshot.isGameOver) skin.palette.danger else skin.palette.status,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
            )

            if (snapshot.isPaused) {
                MenuCommand(
                    label = "RESUME",
                    skin = skin,
                    onClick = { onCommand(GameCommand.PauseToggle) },
                )
            }

            MenuCommand(
                label = "RESTART",
                skin = skin,
                onClick = { onCommand(GameCommand.Restart) },
            )
        }
    }
}

@Composable
private fun MenuCommand(label: String, skin: GameSkin, onClick: () -> Unit) {
    Text(
        text = label,
        color = skin.palette.menuText,
        fontSize = 16.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        modifier =
            Modifier
                .background(skin.palette.menuBackground)
                .clickable(onClick = onClick)
                .padding(horizontal = 18.dp, vertical = 10.dp),
    )
}

@Composable
private fun SettingsMenu(
    skin: GameSkin,
    sensitivity: GestureSensitivityPreset,
    isOpen: Boolean,
    onOpenChange: (Boolean) -> Unit,
    onSkinChange: (GameSkin) -> Unit,
    onSensitivityChange: (GestureSensitivityPreset) -> Unit,
    audioEnabled: Boolean,
    onAudioEnabledChange: (Boolean) -> Unit,
    musicEnabled: Boolean,
    onMusicEnabledChange: (Boolean) -> Unit,
    audioVolume: Float,
    onAudioVolumeChange: (Float) -> Unit,
    onHighScoreReset: () -> Unit,
    onTestSound: () -> Unit,
) {
    Box {
        // A compact text control fits the narrow side rail better than an icon button here.
        Text(
            text = "SET",
            color = skin.palette.menuText,
            fontSize = skin.layout.settingsButtonFontSp.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier =
                Modifier
                    .background(skin.palette.menuBackground)
                    .clickable { onOpenChange(true) }
                    .padding(
                        horizontal = skin.layout.settingsButtonHorizontalPadding.dp,
                        vertical = skin.layout.settingsButtonVerticalPadding.dp,
                    ),
        )
        DropdownMenu(
            expanded = isOpen,
            onDismissRequest = { onOpenChange(false) },
            modifier = Modifier.width(190.dp),
        ) {
            DropdownMenuItem(
                text = { MenuText("SKIN: ${skin.menuLabel}") },
                onClick = {},
                enabled = false,
            )
            GameSkin.entries.forEach { option ->
                DropdownMenuItem(
                    text = { MenuText(option.menuLabel) },
                    onClick = {
                        onSkinChange(option)
                        onOpenChange(false)
                    },
                )
            }
            DropdownMenuItem(
                text = { MenuText("SENS: ${sensitivity.label}") },
                onClick = {},
                enabled = false,
            )
            GestureSensitivityPreset.entries.forEach { option ->
                DropdownMenuItem(
                    text = { MenuText(option.label) },
                    onClick = {
                        onSensitivityChange(option)
                        onOpenChange(false)
                    },
                )
            }
            DropdownMenuItem(
                text = { MenuText("AUDIO: ${if (audioEnabled) "ON" else "OFF"}") },
                onClick = {
                    onAudioEnabledChange(!audioEnabled)
                    onOpenChange(false)
                },
            )
            DropdownMenuItem(
                text = { MenuText("MUSIC: ${if (musicEnabled) "ON" else "OFF"}") },
                onClick = {
                    onMusicEnabledChange(!musicEnabled)
                    onOpenChange(false)
                },
            )
            DropdownMenuItem(
                text = { MenuText("VOL: ${(audioVolume * 100).toInt()}%") },
                onClick = {},
                enabled = false,
            )
            Slider(
                value = audioVolume,
                onValueChange = onAudioVolumeChange,
                valueRange = 0f..1f,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
            )
            DropdownMenuItem(
                text = { MenuText("TEST SND") },
                onClick = {
                    onTestSound()
                    onOpenChange(false)
                },
            )
            DropdownMenuItem(
                text = { MenuText("RESET HIGH") },
                onClick = {
                    onHighScoreReset()
                    onOpenChange(false)
                },
            )
        }
    }
}

@Composable
private fun MenuText(text: String) {
    Text(
        text = text,
        fontFamily = FontFamily.Monospace,
        fontSize = 15.sp,
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCell(
    boardOrigin: Offset,
    cellSize: Float,
    position: Position,
    type: TetrominoType,
    skin: GameSkin,
) {
    val inset = 2.dp.toPx()
    val topLeft = boardOrigin + Offset(position.x * cellSize + inset, position.y * cellSize + inset)
    drawRect(
        color = type.color(skin),
        topLeft = topLeft,
        size = Size(cellSize - inset * 2f, cellSize - inset * 2f),
    )
    if (skin == GameSkin.Handheld) {
        // Handheld skin uses original inset linework to evoke low-color tile art.
        val detailInset = inset + 4.dp.toPx()
        drawRect(
            color = skin.palette.boardBackground,
            topLeft = boardOrigin + Offset(position.x * cellSize + detailInset, position.y * cellSize + detailInset),
            size = Size(cellSize - detailInset * 2f, cellSize - detailInset * 2f),
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}

private fun TetrominoType.color(skin: GameSkin): Color =
    if (skin == GameSkin.Handheld) {
        skin.palette.cell
    } else {
        when (this) {
            TetrominoType.I -> Color(0xFF7DE2D1)
            TetrominoType.O -> Color(0xFFFFD166)
            TetrominoType.T -> Color(0xFFC792EA)
            TetrominoType.S -> Color(0xFF95E06C)
            TetrominoType.Z -> Color(0xFFFF6B6B)
            TetrominoType.J -> Color(0xFF82AAFF)
            TetrominoType.L -> Color(0xFFFFB86C)
        }
    }

private fun statusText(snapshot: GameSnapshot): String =
    when {
        snapshot.isGameOver -> "GAME OVER"
        snapshot.isPaused -> "PAUSED"
        else -> "PLAY"
    }

private fun gravityDelayMillis(level: Int): Long =
    (900L - ((level - 1).coerceAtLeast(0) * 65L)).coerceAtLeast(120L)

// Translate state transitions into audio cues without coupling audio to core rules.
private fun audioCueFor(command: GameCommand, before: GameState, after: GameState): AudioCue? {
    if (before == after && command != GameCommand.PauseToggle) {
        return null
    }
    return when {
        !before.isGameOver && after.isGameOver -> AudioCue.GameOver
        after.lines > before.lines -> AudioCue.LineClear
        command == GameCommand.Restart -> AudioCue.Restart
        command == GameCommand.Pause || command == GameCommand.PauseToggle -> AudioCue.Pause
        command == GameCommand.RotateClockwise -> AudioCue.Rotate
        command == GameCommand.RotateCounterclockwise -> AudioCue.Rotate
        command == GameCommand.MoveLeft || command == GameCommand.MoveRight -> AudioCue.Move
        command == GameCommand.SoftDrop -> AudioCue.SoftDrop
        command == GameCommand.HardDrop -> AudioCue.HardDrop
        else -> null
    }
}

// Local-only persistence for V1 prototype settings and high score.
private class GblockPreferences(context: Context) {
    private val preferences =
        context.getSharedPreferences(PreferenceFileName, Context.MODE_PRIVATE)

    fun loadHighScore(): Int = preferences.getInt(KeyHighScore, 0)

    fun saveHighScore(value: Int) {
        preferences.edit().putInt(KeyHighScore, value.coerceAtLeast(0)).apply()
    }

    fun loadSkin(): GameSkin =
        preferences.getEnum(KeySkin, GameSkin.Gblock)

    fun saveSkin(value: GameSkin) {
        preferences.edit().putString(KeySkin, value.name).apply()
    }

    fun loadSensitivity(): GestureSensitivityPreset =
        preferences.getEnum(KeySensitivity, GestureSensitivityPreset.Normal)

    fun saveSensitivity(value: GestureSensitivityPreset) {
        preferences.edit().putString(KeySensitivity, value.name).apply()
    }

    fun loadAudioEnabled(): Boolean = preferences.getBoolean(KeyAudioEnabled, true)

    fun saveAudioEnabled(value: Boolean) {
        preferences.edit().putBoolean(KeyAudioEnabled, value).apply()
    }

    fun loadMusicEnabled(): Boolean = preferences.getBoolean(KeyMusicEnabled, false)

    fun saveMusicEnabled(value: Boolean) {
        preferences.edit().putBoolean(KeyMusicEnabled, value).apply()
    }

    fun loadAudioVolume(): Float =
        preferences.getFloat(KeyAudioVolume, 1f).coerceIn(0f, 1f)

    fun saveAudioVolume(value: Float) {
        preferences.edit().putFloat(KeyAudioVolume, value.coerceIn(0f, 1f)).apply()
    }

    fun loadRecoverableGameState(): GameState? =
        preferences.getString(KeyRecoverableGameState, null)
            ?.let(::decodeRecoverableGameState)
            ?.takeUnless { it.isGameOver }

    fun saveRecoverableGameState(value: GameState) {
        if (value.isGameOver) {
            preferences.edit().remove(KeyRecoverableGameState).apply()
        } else {
            preferences.edit().putString(KeyRecoverableGameState, encodeRecoverableGameState(value)).apply()
        }
    }

    private inline fun <reified T : Enum<T>> android.content.SharedPreferences.getEnum(
        key: String,
        default: T,
    ): T =
        // Unknown stored enum names fall back safely after refactors.
        getString(key, null)
            ?.let { stored -> enumValues<T>().firstOrNull { it.name == stored } }
            ?: default
}

// Presets keep tuning visible while device testing continues.
private enum class GestureSensitivityPreset(
    val label: String,
    val horizontalThresholdPx: Float,
    val softDropThresholdPx: Float,
    val hardDropThresholdPx: Float,
    val hardDropVelocityPxPerSecond: Float,
) {
    Responsive(
        label = "RESPONSIVE",
        horizontalThresholdPx = 40f,
        softDropThresholdPx = 64f,
        hardDropThresholdPx = 180f,
        hardDropVelocityPxPerSecond = 2600f,
    ),
    Normal(
        label = "NORMAL",
        horizontalThresholdPx = 48f,
        softDropThresholdPx = 72f,
        hardDropThresholdPx = 180f,
        hardDropVelocityPxPerSecond = 2600f,
    ),
    Deliberate(
        label = "DELIBERATE",
        horizontalThresholdPx = 62f,
        softDropThresholdPx = 90f,
        hardDropThresholdPx = 240f,
        hardDropVelocityPxPerSecond = 3300f,
    ),
}

// Runtime skins own presentation values only; game rules must not depend on them.
private enum class GameSkin(
    val label: String,
    val menuLabel: String,
    val palette: SkinPalette,
    val layout: SkinLayout,
) {
    Gblock(
        label = "GBLOCK",
        menuLabel = "GBLOCK DEFAULT",
        palette =
            SkinPalette(
                background = Color(0xFF10141F),
                boardBackground = Color(0xFF1B2433),
                grid = Color(0xFF263247),
                border = Color(0xFFE8EDF7),
                panelBackground = Color(0xFF1B2433),
                textMuted = Color(0xFFBFC7D8),
                accent = Color(0xFFFFD166),
                status = Color(0xFF7DE2D1),
                danger = Color(0xFFFF6B6B),
                menuBackground = Color(0xFF1B2433),
                menuText = Color(0xFFFFD166),
                cell = Color(0xFFE8EDF7),
            ),
        layout = SkinLayout.Reference,
    ),
    Handheld(
        label = "HANDHELD",
        menuLabel = "HANDHELD",
        palette =
            SkinPalette(
                background = Color(0xFFC8D3A3),
                boardBackground = Color(0xFFC8D3A3),
                grid = Color(0xFF8A936A),
                border = Color(0xFF38422F),
                panelBackground = Color(0xFFB7C48F),
                textMuted = Color(0xFF38422F),
                accent = Color(0xFF38422F),
                status = Color(0xFF38422F),
                danger = Color(0xFF20271C),
                menuBackground = Color(0xFF38422F),
                menuText = Color(0xFFC8D3A3),
                cell = Color(0xFF596348),
            ),
        layout =
            SkinLayout.Reference.copy(
                highScoreFontSp = 12,
                sidePanelWidth = 92,
                statLabelFontSp = 12,
                statValueFontSp = 17,
                nextPanelHeight = 76,
                settingsButtonFontSp = 12,
            ),
    ),
}

// Palette contract consumed by renderer components.
private data class SkinPalette(
    val background: Color,
    val boardBackground: Color,
    val grid: Color,
    val border: Color,
    val panelBackground: Color,
    val textMuted: Color,
    val accent: Color,
    val status: Color,
    val danger: Color,
    val menuBackground: Color,
    val menuText: Color,
    val cell: Color,
)

// Layout values are part of a skin so APK-bundled skins can change simple UX arrangement.
private data class SkinLayout(
    val screenPaddingStart: Int,
    val screenPaddingTop: Int,
    val screenPaddingEnd: Int,
    val screenPaddingBottom: Int,
    val highScoreFontSp: Int,
    val highScoreBottomGap: Int,
    val boardSideGap: Int,
    val sidePanelWidth: Int,
    val scoreLevelGap: Int,
    val levelLinesGap: Int,
    val linesNextGap: Int,
    val nextStatusGap: Int,
    val panelPadding: Int,
    val statLabelFontSp: Int,
    val statValueFontSp: Int,
    val nextPanelHeight: Int,
    val nextLabelFontSp: Int,
    val nextPreviewHeight: Int,
    val statusFontSp: Int,
    val settingsButtonFontSp: Int,
    val settingsButtonHorizontalPadding: Int,
    val settingsButtonVerticalPadding: Int,
) {
    companion object {
        // GBLOCK is the reference layout for future in-APK skins.
        val Reference =
            SkinLayout(
                screenPaddingStart = 10,
                screenPaddingTop = 14,
                screenPaddingEnd = 8,
                screenPaddingBottom = 14,
                highScoreFontSp = 13,
                highScoreBottomGap = 4,
                boardSideGap = 6,
                sidePanelWidth = 96,
                scoreLevelGap = 10,
                levelLinesGap = 8,
                linesNextGap = 12,
                nextStatusGap = 10,
                panelPadding = 5,
                statLabelFontSp = 13,
                statValueFontSp = 18,
                nextPanelHeight = 78,
                nextLabelFontSp = 12,
                nextPreviewHeight = 42,
                statusFontSp = 10,
                settingsButtonFontSp = 13,
                settingsButtonHorizontalPadding = 12,
                settingsButtonVerticalPadding = 8,
            )
    }
}

private const val PreferenceFileName = "gblock_preferences"
private const val KeyHighScore = "high_score"
private const val KeySkin = "skin"
private const val KeySensitivity = "sensitivity"
private const val KeyAudioEnabled = "audio_enabled"
private const val KeyMusicEnabled = "music_enabled"
private const val KeyAudioVolume = "audio_volume"
private const val KeyRecoverableGameState = "recoverable_game_state"

// Attaches gesture interpretation to the board without embedding input in core rules.
private fun Modifier.gestureControls(
    interpreter: GestureInterpreter,
    onCommand: (GameCommand) -> Unit,
): Modifier =
    pointerInput(interpreter) {
        detectGameGestures(interpreter, onCommand)
    }

private suspend fun PointerInputScope.detectGameGestures(
    interpreter: GestureInterpreter,
    onCommand: (GameCommand) -> Unit,
) {
    awaitPointerEventScope {
        while (true) {
            // Track one gesture from first touch until all fingers lift.
            val firstDown = awaitPointerEvent().changes.firstOrNull { it.pressed } ?: continue
            val startedAt = firstDown.uptimeMillis
            val startedPosition = firstDown.position
            var lastPosition = startedPosition
            var accumulated = Offset.Zero
            var totalDrag = Offset.Zero
            var maxPointerCount = 1
            var movementExceededTap = false
            var movementExceededTwoFingerTap = false
            var emittedDragCommand = false

            while (true) {
                val event = awaitPointerEvent()
                maxPointerCount = maxOf(maxPointerCount, event.changes.size)

                val primary = event.changes.firstOrNull { it.id == firstDown.id } ?: event.changes.first()
                if (primary.pressed) {
                    val change = primary.positionChange()
                    if (change != Offset.Zero) {
                        accumulated += change
                        totalDrag += change
                        lastPosition = primary.position
                        if (abs(totalDrag.x) > TapSlopPx || abs(totalDrag.y) > TapSlopPx) {
                            movementExceededTap = true
                        }
                        if (abs(totalDrag.x) > TwoFingerTapSlopPx || abs(totalDrag.y) > TwoFingerTapSlopPx) {
                            movementExceededTwoFingerTap = true
                        }

                        // Emit repeated drag commands as thresholds are crossed.
                        interpreter.interpret(
                            GestureEvent.Drag(
                                deltaX = accumulated.x,
                                deltaY = accumulated.y,
                            ),
                        )?.let { command ->
                            onCommand(command)
                            emittedDragCommand = true
                            accumulated = Offset.Zero
                        }
                    }
                    event.changes.forEach(PointerInputChange::consume)
                }

                if (event.changes.all { it.changedToUp() || !it.pressed }) {
                    val elapsedMs = (primary.uptimeMillis - startedAt).coerceAtLeast(1L)
                    val velocityY = ((lastPosition.y - startedPosition.y) / elapsedMs) * MillisPerSecond

                    when {
                        // Two-finger tap pauses without needing a visible button.
                        maxPointerCount >= 2 &&
                            !movementExceededTwoFingerTap &&
                            primary.uptimeMillis - startedAt <= TwoFingerTapDurationMs ->
                            interpreter.interpret(GestureEvent.TwoFingerTap)?.let(onCommand)

                        !movementExceededTap &&
                            primary.uptimeMillis - startedAt <= TapDurationMs ->
                            interpreter.interpret(GestureEvent.Tap)?.let(onCommand)

                        totalDrag.y > 0f -> {
                            // Release velocity helps distinguish hard drop from soft drop.
                            val releaseCommand =
                                interpreter.interpret(
                                    GestureEvent.Drag(
                                        deltaX = totalDrag.x,
                                        deltaY = totalDrag.y,
                                        velocityY = velocityY,
                                    ),
                                )
                            if (releaseCommand == GameCommand.HardDrop || !emittedDragCommand) {
                                releaseCommand?.let(onCommand)
                            }
                        }
                    }
                    event.changes.forEach(PointerInputChange::consume)
                    break
                }
            }
        }
    }
}

// Gesture constants are prototype defaults tuned on the Zebra TC57.
private const val TapDurationMs = 220L
private const val TwoFingerTapDurationMs = 420L
private const val TapSlopPx = 24f
private const val TwoFingerTapSlopPx = 64f
private const val MillisPerSecond = 1000f
