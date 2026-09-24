package foundry.gestureblocks.core

import kotlin.math.abs

// Converts raw touch gestures into game commands; it never mutates game state.
class GestureInterpreter(
    private val horizontalThresholdPx: Float = 48f,
    private val softDropThresholdPx: Float = 72f,
    private val hardDropThresholdPx: Float = 180f,
    private val hardDropVelocityPxPerSecond: Float = 2600f,
) {
    fun interpret(event: GestureEvent): GameCommand? =
        when (event) {
            GestureEvent.Tap -> GameCommand.RotateClockwise
            GestureEvent.TwoFingerTap -> GameCommand.PauseToggle
            is GestureEvent.Drag -> interpretDrag(event)
        }

    private fun interpretDrag(event: GestureEvent.Drag): GameCommand? {
        val absX = abs(event.deltaX)
        val absY = abs(event.deltaY)

        return when {
            // Horizontal gestures win only when they are clearly more horizontal than vertical.
            absX >= horizontalThresholdPx && absX > absY ->
                if (event.deltaX > 0f) GameCommand.MoveRight else GameCommand.MoveLeft

            // Hard drop requires both distance and speed to avoid accidental drops.
            event.deltaY >= hardDropThresholdPx && absY >= absX && event.velocityY >= hardDropVelocityPxPerSecond ->
                GameCommand.HardDrop

            // Slower downward movement becomes soft drop.
            event.deltaY >= softDropThresholdPx && absY >= absX ->
                GameCommand.SoftDrop

            else -> null
        }
    }
}

// UI-layer gestures normalized for the platform-independent interpreter.
sealed interface GestureEvent {
    data object Tap : GestureEvent
    data object TwoFingerTap : GestureEvent
    data class Drag(
        val deltaX: Float,
        val deltaY: Float,
        val velocityY: Float = 0f,
    ) : GestureEvent
}
