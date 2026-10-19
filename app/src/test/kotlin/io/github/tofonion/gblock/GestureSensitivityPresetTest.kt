package io.github.tofonion.gblock

import io.github.tofonion.gblock.core.GameCommand
import io.github.tofonion.gblock.core.GestureEvent
import io.github.tofonion.gblock.core.GestureInterpreter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GestureSensitivityPresetTest {
    @Test
    fun `responsive accepts shorter movement than normal and deliberate`() {
        val event = GestureEvent.Drag(deltaX = 40f, deltaY = 8f)

        assertEquals(GameCommand.MoveRight, GestureSensitivityPreset.Responsive.interpreter().interpret(event))
        assertNull(GestureSensitivityPreset.Normal.interpreter().interpret(event))
        assertNull(GestureSensitivityPreset.Deliberate.interpreter().interpret(event))
    }

    @Test
    fun `normal accepts medium soft drops while deliberate waits`() {
        val event = GestureEvent.Drag(deltaX = 8f, deltaY = 90f, velocityY = 900f)

        assertEquals(GameCommand.SoftDrop, GestureSensitivityPreset.Responsive.interpreter().interpret(event))
        assertEquals(GameCommand.SoftDrop, GestureSensitivityPreset.Normal.interpreter().interpret(event))
        assertNull(GestureSensitivityPreset.Deliberate.interpreter().interpret(event))
    }

    @Test
    fun `hard drop effort increases across sensitivity presets`() {
        val responsiveOnly = GestureEvent.Drag(deltaX = 8f, deltaY = 170f, velocityY = 2500f)
        val normalOnly = GestureEvent.Drag(deltaX = 8f, deltaY = 230f, velocityY = 3200f)
        val deliberate = GestureEvent.Drag(deltaX = 8f, deltaY = 320f, velocityY = 4200f)

        assertEquals(
            GameCommand.HardDrop,
            GestureSensitivityPreset.Responsive.interpreter().interpret(responsiveOnly),
        )
        assertEquals(GameCommand.SoftDrop, GestureSensitivityPreset.Normal.interpreter().interpret(responsiveOnly))

        assertEquals(GameCommand.HardDrop, GestureSensitivityPreset.Normal.interpreter().interpret(normalOnly))
        assertEquals(GameCommand.SoftDrop, GestureSensitivityPreset.Deliberate.interpreter().interpret(normalOnly))

        assertEquals(GameCommand.HardDrop, GestureSensitivityPreset.Deliberate.interpreter().interpret(deliberate))
    }

    private fun GestureSensitivityPreset.interpreter(): GestureInterpreter =
        GestureInterpreter(
            horizontalThresholdPx = horizontalThresholdPx,
            softDropThresholdPx = softDropThresholdPx,
            hardDropThresholdPx = hardDropThresholdPx,
            hardDropVelocityPxPerSecond = hardDropVelocityPxPerSecond,
        )
}
