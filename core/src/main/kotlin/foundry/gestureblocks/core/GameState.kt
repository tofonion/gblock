package foundry.gestureblocks.core

// Complete game state owned by the core reducer.
data class GameState(
    val board: Board = Board(),
    val currentPiece: Tetromino = Tetromino(TetrominoType.T),
    val currentPosition: Position = Position(3, 0),
    val nextQueue: List<TetrominoType> = defaultQueue(),
    val score: Int = 0,
    val lines: Int = 0,
    val level: Int = 1,
    val isPaused: Boolean = false,
    val isGameOver: Boolean = false,
) {
    // Snapshot exposes only what the renderer/input shell needs to display.
    fun snapshot(): GameSnapshot =
        GameSnapshot(
            board = board,
            currentPiece = currentPiece,
            currentPosition = currentPosition,
            score = score,
            lines = lines,
            level = level,
            nextPiece = nextQueue.firstOrNull(),
            isPaused = isPaused,
            isGameOver = isGameOver,
        )

    companion object {
        // Starts a new game using a deterministic queue for prototype repeatability.
        fun initial(queue: List<TetrominoType> = defaultQueue()): GameState {
            val first = queue.firstOrNull() ?: TetrominoType.T
            val rest = if (queue.isEmpty()) defaultQueue() else queue.drop(1)
            return GameState(currentPiece = Tetromino(first), nextQueue = rest)
        }
    }
}

// Read model for presentation; rendering code should not need full reducer state.
data class GameSnapshot(
    val board: Board,
    val currentPiece: Tetromino,
    val currentPosition: Position,
    val score: Int,
    val lines: Int,
    val level: Int,
    val nextPiece: TetrominoType?,
    val isPaused: Boolean,
    val isGameOver: Boolean,
)

// Temporary deterministic queue; randomization belongs in a later core pass.
fun defaultQueue(): List<TetrominoType> =
    listOf(
        TetrominoType.I,
        TetrominoType.O,
        TetrominoType.T,
        TetrominoType.S,
        TetrominoType.Z,
        TetrominoType.J,
        TetrominoType.L,
    )
