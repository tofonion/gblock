package foundry.gestureblocks.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GestureInterpreterTest {
    private val interpreter = GestureInterpreter()

    @Test
    fun `tap emits clockwise rotation command`() {
        assertEquals(GameCommand.RotateClockwise, interpreter.interpret(GestureEvent.Tap))
    }

    @Test
    fun `two finger tap emits pause command`() {
        assertEquals(GameCommand.PauseToggle, interpreter.interpret(GestureEvent.TwoFingerTap))
    }

    @Test
    fun `horizontal drag emits left and right movement commands`() {
        assertEquals(
            GameCommand.MoveLeft,
            interpreter.interpret(GestureEvent.Drag(deltaX = -64f, deltaY = 10f)),
        )
        assertEquals(
            GameCommand.MoveRight,
            interpreter.interpret(GestureEvent.Drag(deltaX = 64f, deltaY = 10f)),
        )
    }

    @Test
    fun `downward drag emits soft drop command`() {
        assertEquals(
            GameCommand.SoftDrop,
            interpreter.interpret(GestureEvent.Drag(deltaX = 8f, deltaY = 90f, velocityY = 400f)),
        )
    }

    @Test
    fun `fast downward drag emits hard drop command`() {
        assertEquals(
            GameCommand.HardDrop,
            interpreter.interpret(GestureEvent.Drag(deltaX = 8f, deltaY = 220f, velocityY = 3000f)),
        )
    }

    @Test
    fun `short fast downward drag stays soft drop to avoid accidental hard drop`() {
        assertEquals(
            GameCommand.SoftDrop,
            interpreter.interpret(GestureEvent.Drag(deltaX = 8f, deltaY = 90f, velocityY = 3000f)),
        )
    }

    @Test
    fun `long moderate downward drag stays soft drop to avoid accidental hard drop`() {
        assertEquals(
            GameCommand.SoftDrop,
            interpreter.interpret(GestureEvent.Drag(deltaX = 8f, deltaY = 220f, velocityY = 1800f)),
        )
    }

    @Test
    fun `small ambiguous drag emits no command`() {
        assertNull(interpreter.interpret(GestureEvent.Drag(deltaX = 12f, deltaY = 16f)))
    }
}
