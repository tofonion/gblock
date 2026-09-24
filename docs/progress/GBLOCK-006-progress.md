# GBLOCK-006 Progress

Packet: `GBLOCK-006`

Scope: game/session loop only. No skin system, persistence, audio, deployment, permanent product name, App Store release work, or unrelated mechanics.

## Step 0 - Phase Start

Status: COMPLETE

Evidence:

- Began immediately after `GBLOCK-005` completed and packet validation passed.
- Weekly quota remained healthy enough to continue under the user's usage rule.
- Existing implementation state:
  - Android shell rendered snapshots and accepted gesture commands.
  - Core already owned move, rotate, soft drop, hard drop, pause, locking, scoring, line clear, and game-over checks.

## Step 1 - Session Commands Added

Status: COMPLETE

Changed:

- `core/src/main/kotlin/foundry/gestureblocks/core/GameCommand.kt`
- `core/src/main/kotlin/foundry/gestureblocks/core/GameCore.kt`

Behavior added:

- `GameCommand.Tick` applies gravity through `GameCore.tick`.
- `GameCommand.Restart` creates a fresh `GameState.initial()` session.
- Game-over play commands and tick remain ignored by existing core guards.
- Restart is allowed even after game over.

Boundary:

- Gravity mutation remains in pure core rules.
- Timer orchestration remains outside pure core.

## Step 2 - Android Gravity Loop Added

Status: COMPLETE

Changed:

- `app/src/main/kotlin/foundry/gestureblocks/MainActivity.kt`

Behavior added:

- Android Compose shell runs a lifecycle-scoped `LaunchedEffect` gravity loop.
- Loop dispatches `GameCommand.Tick` at a provisional level-based interval.
- Pause and game over cancel/suspend ongoing tick mutation through state keys and core guards.
- Tap after game over maps to `GameCommand.Restart` as a temporary session path until `GBLOCK-007` adds a cleaner minimal menu/restart UI.

Provisional timing:

- Level 1 delay: 900 ms.
- Delay reduces by 65 ms per level.
- Minimum delay: 120 ms.

Open for later:

- Final gravity/level timing belongs to calibration and UX packets.

## Step 3 - Core Tests Added

Status: COMPLETE

Changed:

- `core/src/test/kotlin/foundry/gestureblocks/core/GameCoreTest.kt`

Coverage added:

- Tick applies gravity without awarding soft-drop score.
- Tick locks a grounded piece and spawns the next piece.
- Game over ignores play/tick commands but accepts restart.

## Step 4 - Validation

Status: COMPLETE

Validation command:

```powershell
.\gradlew.bat test assembleDebug --no-daemon
```

Result:

- `BUILD SUCCESSFUL in 26s`
- `:core:test` passed.
- `:app:assembleDebug` passed.

Warnings:

- SDK XML version mismatch warning from local Android tooling.
- AGP 9.0.0 warning about compile SDK 37.0 support.

GBLOCK-006 Status:

- PASS: game starts from launch.
- PASS: pieces fall on a timed gravity loop.
- PASS: pause/game over stop tick mutation.
- PASS: restart creates a fresh session.
- PASS: game-over command/tick behavior has core tests.
- WARNING: restart path is provisional tap-after-game-over behavior; `GBLOCK-007` should replace it with a clearer minimal menu/restart presentation.
