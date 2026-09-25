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

## Step 5 - Foreground Loss Pause And Recovery

Status: COMPLETE

Changed:

- `core/src/main/kotlin/foundry/gestureblocks/core/GameCommand.kt`
- `core/src/main/kotlin/foundry/gestureblocks/core/GameCore.kt`
- `core/src/test/kotlin/foundry/gestureblocks/core/GameCoreTest.kt`
- `app/src/main/kotlin/foundry/gestureblocks/MainActivity.kt`
- `app/src/main/kotlin/foundry/gestureblocks/RecoverableGameStateCodec.kt`
- `app/src/test/kotlin/foundry/gestureblocks/RecoverableGameStateCodecTest.kt`

Behavior added:

- Android `onPause` requests `GameCommand.Pause` when the app loses foreground.
- `GameCommand.Pause` is one-way and idempotent; explicit player resume still uses `PauseToggle`.
- Foreground return does not auto-resume because restored and paused sessions remain paused until player input.
- Recoverable session state is stored app-private and decoded back into a paused `GameState`.
- Recovery preserves board, active piece, current position, next queue, score, lines, level, and pause/game-over flags.

Boundary:

- Android lifecycle code requests pause only.
- Core owns paused semantics.
- Persistence format stays in the Android app layer.

Validation command:

```powershell
.\gradlew.bat test assembleDebug --no-daemon
```

Result:

- `BUILD SUCCESSFUL in 24s`
- `:core:test` passed: 22 tests, 0 failures, 0 errors.
- `:app:testDebugUnitTest` passed: 2 tests, 0 failures, 0 errors.
- `:app:assembleDebug` passed.

Warnings:

- Initial validation without `GRADLE_USER_HOME` failed because the wrapper tried to create `C:\.gradle`.
- Reran with `$env:GRADLE_USER_HOME='C:\Foundry\Projects\gesture-blocks\.gradle-user'`.
- SDK XML version mismatch warning from local Android tooling remains.
- AGP 9.0.0 warning about compile SDK 37.0 support remains.
