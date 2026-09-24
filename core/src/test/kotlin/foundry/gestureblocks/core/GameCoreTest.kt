package foundry.gestureblocks.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameCoreTest {
    @Test
    fun `move commands update current position`() {
        val state = GameState.initial(listOf(TetrominoType.T, TetrominoType.I))

        val movedLeft = GameCore.dispatch(state, GameCommand.MoveLeft)
        val movedRight = GameCore.dispatch(movedLeft, GameCommand.MoveRight)

        assertEquals(Position(2, 0), movedLeft.currentPosition)
        assertEquals(state.currentPosition, movedRight.currentPosition)
    }

    @Test
    fun `movement is blocked by wall`() {
        val state =
            GameState(
                currentPiece = Tetromino(TetrominoType.O),
                currentPosition = Position(0, 0),
            )

        val result = GameCore.dispatch(state, GameCommand.MoveLeft)

        assertEquals(Position(0, 0), result.currentPosition)
    }

    @Test
    fun `movement is blocked by locked cells`() {
        val state =
            GameState(
                board = Board(cells = mapOf(Position(0, 0) to TetrominoType.I)),
                currentPiece = Tetromino(TetrominoType.O),
                currentPosition = Position(1, 0),
            )

        val result = GameCore.dispatch(state, GameCommand.MoveLeft)

        assertEquals(Position(1, 0), result.currentPosition)
    }

    @Test
    fun `rotate clockwise changes piece rotation`() {
        val state =
            GameState(
                currentPiece = Tetromino(TetrominoType.T),
                currentPosition = Position(3, 0),
            )

        val result = GameCore.dispatch(state, GameCommand.RotateClockwise)

        assertEquals(1, result.currentPiece.rotation)
    }

    @Test
    fun `rotate counterclockwise changes piece rotation`() {
        val state =
            GameState(
                currentPiece = Tetromino(TetrominoType.T),
                currentPosition = Position(3, 0),
            )

        val result = GameCore.dispatch(state, GameCommand.RotateCounterclockwise)

        assertEquals(3, result.currentPiece.rotation)
    }

    @Test
    fun `soft drop moves down and awards one point`() {
        val state = GameState.initial(listOf(TetrominoType.T, TetrominoType.I))

        val result = GameCore.dispatch(state, GameCommand.SoftDrop)

        assertEquals(Position(3, 1), result.currentPosition)
        assertEquals(1, result.score)
    }

    @Test
    fun `hard drop locks piece and spawns next piece`() {
        val state =
            GameState(
                currentPiece = Tetromino(TetrominoType.O),
                currentPosition = Position(4, 0),
                nextQueue = listOf(TetrominoType.I),
            )

        val result = GameCore.dispatch(state, GameCommand.HardDrop)

        assertEquals(TetrominoType.I, result.currentPiece.type)
        assertTrue(result.board.isOccupied(Position(4, 18)))
        assertTrue(result.board.isOccupied(Position(5, 19)))
        assertTrue(result.score > 0)
    }

    @Test
    fun `locking a piece clears completed line and updates score lines and level`() {
        val almostFullBottom =
            (0 until 10)
                .filterNot { it == 4 || it == 5 }
                .associate { x -> Position(x, 19) to TetrominoType.I }
        val state =
            GameState(
                board = Board(cells = almostFullBottom),
                currentPiece = Tetromino(TetrominoType.O),
                currentPosition = Position(4, 18),
                nextQueue = listOf(TetrominoType.I),
                lines = 9,
                level = 1,
            )

        val result = GameCore.dispatch(state, GameCommand.HardDrop)

        assertEquals(10, result.lines)
        assertEquals(2, result.level)
        assertEquals(100, result.score)
        assertTrue(result.board.isOccupied(Position(4, 19)))
        assertTrue(result.board.isOccupied(Position(5, 19)))
        assertFalse(result.board.isOccupied(Position(0, 19)))
    }

    @Test
    fun `pause command toggles pause and play commands are ignored while paused`() {
        val state = GameState.initial(listOf(TetrominoType.T, TetrominoType.I))
        val paused = GameCore.dispatch(state, GameCommand.PauseToggle)
        val afterMove = GameCore.dispatch(paused, GameCommand.MoveLeft)
        val resumed = GameCore.dispatch(afterMove, GameCommand.PauseToggle)

        assertTrue(paused.isPaused)
        assertEquals(paused.currentPosition, afterMove.currentPosition)
        assertFalse(resumed.isPaused)
    }

    @Test
    fun `tick applies gravity without awarding soft drop score`() {
        val state = GameState.initial(listOf(TetrominoType.T, TetrominoType.I))

        val result = GameCore.dispatch(state, GameCommand.Tick)

        assertEquals(Position(3, 1), result.currentPosition)
        assertEquals(0, result.score)
    }

    @Test
    fun `tick locks piece and spawns next piece when grounded`() {
        val state =
            GameState(
                currentPiece = Tetromino(TetrominoType.O),
                currentPosition = Position(4, 18),
                nextQueue = listOf(TetrominoType.I),
            )

        val result = GameCore.dispatch(state, GameCommand.Tick)

        assertEquals(TetrominoType.I, result.currentPiece.type)
        assertTrue(result.board.isOccupied(Position(4, 18)))
        assertTrue(result.board.isOccupied(Position(5, 19)))
    }

    @Test
    fun `game over ignores play and tick commands but accepts restart`() {
        val state =
            GameState.initial(listOf(TetrominoType.T, TetrominoType.I))
                .copy(isGameOver = true)

        val afterMove = GameCore.dispatch(state, GameCommand.MoveLeft)
        val afterTick = GameCore.dispatch(state, GameCommand.Tick)
        val restarted = GameCore.dispatch(state, GameCommand.Restart)

        assertEquals(state, afterMove)
        assertEquals(state, afterTick)
        assertFalse(restarted.isGameOver)
        assertEquals(Position(3, 0), restarted.currentPosition)
    }
}
