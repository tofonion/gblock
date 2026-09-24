package foundry.gestureblocks.core

// Immutable 10x20 playfield; occupied cells are sparse so empty space is cheap.
data class Board(
    val width: Int = 10,
    val height: Int = 20,
    val cells: Map<Position, TetrominoType> = emptyMap(),
) {
    fun isInside(position: Position): Boolean =
        position.x in 0 until width && position.y in 0 until height

    fun isOccupied(position: Position): Boolean = cells.containsKey(position)

    // A piece can be placed only when every block is in-bounds and empty.
    fun canPlace(piece: Tetromino, origin: Position): Boolean =
        piece.absolutePositions(origin).all { isInside(it) && !isOccupied(it) }

    // Locking merges the falling piece into the settled board.
    fun lock(piece: Tetromino, origin: Position): Board =
        copy(cells = cells + piece.absolutePositions(origin).associateWith { piece.type })

    fun clearCompletedLines(): LineClearResult {
        // Completed rows are full across the entire board width.
        val completedRows =
            (0 until height).filter { y ->
                (0 until width).all { x -> cells.containsKey(Position(x, y)) }
            }

        if (completedRows.isEmpty()) {
            return LineClearResult(this, 0, emptyList())
        }

        val clearedSet = completedRows.toSet()
        val shifted =
            cells.entries.mapNotNull { (position, type) ->
                if (position.y in clearedSet) {
                    null
                } else {
                    // Rows above cleared lines fall by the number of cleared rows below them.
                    val rowsBelowCleared = completedRows.count { it > position.y }
                    Position(position.x, position.y + rowsBelowCleared) to type
                }
            }.toMap()

        return LineClearResult(copy(cells = shifted), completedRows.size, completedRows)
    }
}

// Includes both the new board and clear metadata for scoring/leveling.
data class LineClearResult(
    val board: Board,
    val linesCleared: Int,
    val clearedRows: List<Int>,
)

// Converts piece-local block coordinates into board coordinates.
fun Tetromino.absolutePositions(origin: Position): List<Position> =
    blocks.map { origin + it }
