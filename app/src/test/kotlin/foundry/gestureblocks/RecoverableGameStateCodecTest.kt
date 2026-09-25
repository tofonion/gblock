package foundry.gestureblocks

import foundry.gestureblocks.core.Board
import foundry.gestureblocks.core.GameState
import foundry.gestureblocks.core.Position
import foundry.gestureblocks.core.Tetromino
import foundry.gestureblocks.core.TetrominoType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RecoverableGameStateCodecTest {
    @Test
    fun `encoded active game restores paused with gameplay state preserved`() {
        val state =
            GameState(
                board = Board(cells = mapOf(Position(1, 19) to TetrominoType.Z)),
                currentPiece = Tetromino(TetrominoType.J, rotation = 3),
                currentPosition = Position(4, 8),
                nextQueue = listOf(TetrominoType.I, TetrominoType.O, TetrominoType.S),
                score = 4321,
                lines = 18,
                level = 3,
                isPaused = false,
            )

        val restored = decodeRecoverableGameState(encodeRecoverableGameState(state))

        assertNotNull(restored)
        assertTrue(restored.isPaused)
        assertEquals(state.board, restored.board)
        assertEquals(state.currentPiece, restored.currentPiece)
        assertEquals(state.currentPosition, restored.currentPosition)
        assertEquals(state.nextQueue, restored.nextQueue)
        assertEquals(state.score, restored.score)
        assertEquals(state.lines, restored.lines)
        assertEquals(state.level, restored.level)
    }

    @Test
    fun `invalid recovery payload is ignored`() {
        val restored = decodeRecoverableGameState("not-a-valid-session")

        assertEquals(null, restored)
    }
}
