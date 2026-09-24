package foundry.gestureblocks.core

// Tetromino shape plus rotation; coordinates are local to the piece origin.
data class Tetromino(
    val type: TetrominoType,
    val rotation: Int = 0,
) {
    // Rotation wraps through the four precomputed orientations below.
    val blocks: List<Position> = SHAPES.getValue(type)[rotation.floorMod(4)]

    fun rotateClockwise(): Tetromino = copy(rotation = (rotation + 1).floorMod(4))

    fun rotateCounterclockwise(): Tetromino = copy(rotation = (rotation - 1).floorMod(4))

    companion object {
        // Shape tables are intentionally rule-core data, not renderer assets.
        private val SHAPES: Map<TetrominoType, List<List<Position>>> =
            mapOf(
                TetrominoType.I to listOf(
                    listOf(Position(0, 1), Position(1, 1), Position(2, 1), Position(3, 1)),
                    listOf(Position(2, 0), Position(2, 1), Position(2, 2), Position(2, 3)),
                    listOf(Position(0, 2), Position(1, 2), Position(2, 2), Position(3, 2)),
                    listOf(Position(1, 0), Position(1, 1), Position(1, 2), Position(1, 3)),
                ),
                TetrominoType.O to List(4) {
                    listOf(Position(0, 0), Position(1, 0), Position(0, 1), Position(1, 1))
                },
                TetrominoType.T to listOf(
                    listOf(Position(1, 0), Position(0, 1), Position(1, 1), Position(2, 1)),
                    listOf(Position(1, 0), Position(1, 1), Position(2, 1), Position(1, 2)),
                    listOf(Position(0, 1), Position(1, 1), Position(2, 1), Position(1, 2)),
                    listOf(Position(1, 0), Position(0, 1), Position(1, 1), Position(1, 2)),
                ),
                TetrominoType.S to listOf(
                    listOf(Position(1, 0), Position(2, 0), Position(0, 1), Position(1, 1)),
                    listOf(Position(1, 0), Position(1, 1), Position(2, 1), Position(2, 2)),
                    listOf(Position(1, 1), Position(2, 1), Position(0, 2), Position(1, 2)),
                    listOf(Position(0, 0), Position(0, 1), Position(1, 1), Position(1, 2)),
                ),
                TetrominoType.Z to listOf(
                    listOf(Position(0, 0), Position(1, 0), Position(1, 1), Position(2, 1)),
                    listOf(Position(2, 0), Position(1, 1), Position(2, 1), Position(1, 2)),
                    listOf(Position(0, 1), Position(1, 1), Position(1, 2), Position(2, 2)),
                    listOf(Position(1, 0), Position(0, 1), Position(1, 1), Position(0, 2)),
                ),
                TetrominoType.J to listOf(
                    listOf(Position(0, 0), Position(0, 1), Position(1, 1), Position(2, 1)),
                    listOf(Position(1, 0), Position(2, 0), Position(1, 1), Position(1, 2)),
                    listOf(Position(0, 1), Position(1, 1), Position(2, 1), Position(2, 2)),
                    listOf(Position(1, 0), Position(1, 1), Position(0, 2), Position(1, 2)),
                ),
                TetrominoType.L to listOf(
                    listOf(Position(2, 0), Position(0, 1), Position(1, 1), Position(2, 1)),
                    listOf(Position(1, 0), Position(1, 1), Position(1, 2), Position(2, 2)),
                    listOf(Position(0, 1), Position(1, 1), Position(2, 1), Position(0, 2)),
                    listOf(Position(0, 0), Position(1, 0), Position(1, 1), Position(1, 2)),
                ),
            )
    }
}

private fun Int.floorMod(modulus: Int): Int = ((this % modulus) + modulus) % modulus
