package foundry.gestureblocks.core

// Pure game reducer: commands in, immutable state out.
object GameCore {
    fun dispatch(state: GameState, command: GameCommand): GameState =
        when (command) {
            GameCommand.MoveLeft -> move(state, dx = -1, dy = 0)
            GameCommand.MoveRight -> move(state, dx = 1, dy = 0)
            GameCommand.RotateClockwise -> rotateClockwise(state)
            GameCommand.RotateCounterclockwise -> rotateCounterclockwise(state)
            GameCommand.SoftDrop -> softDrop(state)
            GameCommand.HardDrop -> hardDrop(state)
            GameCommand.Pause -> pause(state)
            GameCommand.PauseToggle -> togglePause(state)
            GameCommand.Tick -> tick(state)
            GameCommand.Restart -> GameState.initial()
        }

    // Gravity uses the same drop path without awarding soft-drop points.
    fun tick(state: GameState): GameState = softDrop(state, awardPoint = false)

    private fun move(state: GameState, dx: Int, dy: Int): GameState {
        if (!state.acceptsPlayCommand()) return state
        val nextPosition = Position(state.currentPosition.x + dx, state.currentPosition.y + dy)
        return if (state.board.canPlace(state.currentPiece, nextPosition)) {
            state.copy(currentPosition = nextPosition)
        } else {
            state
        }
    }

    private fun rotateClockwise(state: GameState): GameState {
        if (!state.acceptsPlayCommand()) return state
        return rotate(state, state.currentPiece.rotateClockwise())
    }

    private fun rotateCounterclockwise(state: GameState): GameState {
        if (!state.acceptsPlayCommand()) return state
        return rotate(state, state.currentPiece.rotateCounterclockwise())
    }

    private fun rotate(state: GameState, rotated: Tetromino): GameState {
        // Minimal wall-kick set for touch prototype forgiveness near walls/stacks.
        val kickOffsets = listOf(
            Position(0, 0),
            Position(-1, 0),
            Position(1, 0),
            Position(0, -1),
        )

        for (offset in kickOffsets) {
            val candidatePosition = state.currentPosition + offset
            if (state.board.canPlace(rotated, candidatePosition)) {
                return state.copy(currentPiece = rotated, currentPosition = candidatePosition)
            }
        }

        return state
    }

    private fun softDrop(state: GameState, awardPoint: Boolean = true): GameState {
        if (!state.acceptsPlayCommand()) return state
        val nextPosition = Position(state.currentPosition.x, state.currentPosition.y + 1)
        return if (state.board.canPlace(state.currentPiece, nextPosition)) {
            state.copy(
                currentPosition = nextPosition,
                score = if (awardPoint) state.score + 1 else state.score,
            )
        } else {
            lockAndSpawn(state)
        }
    }

    private fun hardDrop(state: GameState): GameState {
        if (!state.acceptsPlayCommand()) return state

        // Walk downward until the next row would collide, then lock once.
        var position = state.currentPosition
        var distance = 0
        while (state.board.canPlace(state.currentPiece, Position(position.x, position.y + 1))) {
            position = Position(position.x, position.y + 1)
            distance++
        }

        return lockAndSpawn(
            state.copy(
                currentPosition = position,
                score = state.score + distance * 2,
            ),
        )
    }

    private fun togglePause(state: GameState): GameState =
        if (state.isGameOver) state else state.copy(isPaused = !state.isPaused)

    private fun pause(state: GameState): GameState =
        if (state.isGameOver) state else state.copy(isPaused = true)

    private fun lockAndSpawn(state: GameState): GameState {
        // Lock, clear, score, level, then spawn the queued next piece.
        val locked = state.board.lock(state.currentPiece, state.currentPosition)
        val lineClear = locked.clearCompletedLines()
        val nextType = state.nextQueue.firstOrNull() ?: defaultQueue().first()
        val remainingQueue =
            if (state.nextQueue.size > 1) {
                state.nextQueue.drop(1)
            } else {
                defaultQueue()
            }
        val nextPiece = Tetromino(nextType)
        val spawnPosition = Position(3, 0)
        val newLines = state.lines + lineClear.linesCleared
        val newLevel = newLines / 10 + 1
        // Classic-style line rewards, multiplied by the level before the clear.
        val lineScore =
            when (lineClear.linesCleared) {
                1 -> 100
                2 -> 300
                3 -> 500
                4 -> 800
                else -> 0
            } * state.level
        val gameOver = !lineClear.board.canPlace(nextPiece, spawnPosition)

        return state.copy(
            board = lineClear.board,
            currentPiece = nextPiece,
            currentPosition = spawnPosition,
            nextQueue = remainingQueue,
            score = state.score + lineScore,
            lines = newLines,
            level = newLevel,
            isGameOver = gameOver,
        )
    }

    // Play commands are ignored while paused or after top-out.
    private fun GameState.acceptsPlayCommand(): Boolean = !isPaused && !isGameOver
}
