package foundry.gestureblocks

import foundry.gestureblocks.core.Board
import foundry.gestureblocks.core.GameState
import foundry.gestureblocks.core.Position
import foundry.gestureblocks.core.Tetromino
import foundry.gestureblocks.core.TetrominoType

private const val RecoveryVersion = "v1"
private const val FieldSeparator = "|"
private const val ListSeparator = ","
private const val CellSeparator = ";"
private const val CellFieldSeparator = ":"

internal fun encodeRecoverableGameState(state: GameState): String {
    val pausedState = state.copy(isPaused = true)
    val queue = pausedState.nextQueue.joinToString(ListSeparator) { it.name }
    val cells =
        pausedState.board.cells.entries
            .sortedWith(compareBy({ it.key.y }, { it.key.x }))
            .joinToString(CellSeparator) { (position, type) ->
                listOf(position.x, position.y, type.name).joinToString(CellFieldSeparator)
            }

    return listOf(
        RecoveryVersion,
        pausedState.board.width,
        pausedState.board.height,
        pausedState.currentPiece.type.name,
        pausedState.currentPiece.rotation,
        pausedState.currentPosition.x,
        pausedState.currentPosition.y,
        queue,
        pausedState.score,
        pausedState.lines,
        pausedState.level,
        pausedState.isPaused,
        pausedState.isGameOver,
        cells,
    ).joinToString(FieldSeparator)
}

internal fun decodeRecoverableGameState(encoded: String): GameState? {
    val parts = encoded.split(FieldSeparator)
    if (parts.size != 14 || parts[0] != RecoveryVersion) return null

    return runCatching {
        val width = parts[1].toInt()
        val height = parts[2].toInt()
        val currentPiece = Tetromino(
            type = enumValueOf(parts[3]),
            rotation = parts[4].toInt(),
        )
        val currentPosition = Position(parts[5].toInt(), parts[6].toInt())
        val nextQueue =
            parts[7]
                .takeIf { it.isNotBlank() }
                ?.split(ListSeparator)
                ?.map { enumValueOf<TetrominoType>(it) }
                ?: emptyList()
        val cells =
            parts[13]
                .takeIf { it.isNotBlank() }
                ?.split(CellSeparator)
                ?.associate { encodedCell ->
                    val cellParts = encodedCell.split(CellFieldSeparator)
                    require(cellParts.size == 3)
                    Position(cellParts[0].toInt(), cellParts[1].toInt()) to enumValueOf<TetrominoType>(cellParts[2])
                }
                ?: emptyMap()

        GameState(
            board = Board(width = width, height = height, cells = cells),
            currentPiece = currentPiece,
            currentPosition = currentPosition,
            nextQueue = nextQueue,
            score = parts[8].toInt(),
            lines = parts[9].toInt(),
            level = parts[10].toInt(),
            isPaused = true,
            isGameOver = parts[12].toBooleanStrict(),
        )
    }.getOrNull()
}
