package foundry.gestureblocks.core

// Board-space coordinate with origin at the top-left cell.
data class Position(val x: Int, val y: Int) {
    operator fun plus(other: Position): Position = Position(x + other.x, y + other.y)
}
