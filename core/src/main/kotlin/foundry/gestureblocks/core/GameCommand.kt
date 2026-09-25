package foundry.gestureblocks.core

// Platform-neutral commands accepted by the game reducer.
sealed interface GameCommand {
    data object MoveLeft : GameCommand
    data object MoveRight : GameCommand
    data object RotateClockwise : GameCommand
    data object RotateCounterclockwise : GameCommand
    data object SoftDrop : GameCommand
    data object HardDrop : GameCommand
    data object Pause : GameCommand
    data object PauseToggle : GameCommand
    data object Tick : GameCommand
    data object Restart : GameCommand
}
