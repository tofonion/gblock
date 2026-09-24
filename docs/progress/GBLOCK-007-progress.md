# GBLOCK-007 Progress

Packet: `GBLOCK-007`

Scope: prototype UX layout and minimal menu only. No movement buttons, skin system, persistence, audio, deployment, permanent product name, App Store release work, or unrelated mechanics.

## Step 0 - Phase Start

Status: COMPLETE

Evidence:

- Began after `GBLOCK-006` packet validation passed.
- Target was the provisional pause/game-over presentation and restart flow.

## Step 1 - Minimal Overlay Added

Status: COMPLETE

Changed:

- `app/src/main/kotlin/foundry/gestureblocks/MainActivity.kt`

Behavior added:

- Board now renders a dim overlay when paused or game over.
- Paused overlay shows `PAUSED`, `RESUME`, and `RESTART`.
- Game-over overlay shows `GAME OVER` and `RESTART`.
- Resume dispatches `GameCommand.PauseToggle`.
- Restart dispatches `GameCommand.Restart`.
- The earlier provisional game-over tap-to-restart status text was replaced by the explicit restart affordance.

Boundary:

- Overlay dispatches commands only.
- Renderer still consumes `GameSnapshot`.
- No scoring, collision, rotation, line-clear, randomization, or gravity rules were added to the UI.

## Step 2 - Movement Button Check

Status: COMPLETE

Command:

```powershell
rg -n "MoveLeft|MoveRight|ROTATE|LEFT|RIGHT|DROP|SOFT|HARD|◄|►|▼|Button\(" app\src\main\kotlin\foundry\gestureblocks
```

Result:

- No matches.

Finding:

- No permanent movement buttons were introduced.

## Step 3 - Validation

Status: COMPLETE

Validation command:

```powershell
.\gradlew.bat test assembleDebug --no-daemon
```

Result:

- `BUILD SUCCESSFUL in 24s`
- `:core:test` passed.
- `:app:assembleDebug` passed.

Warnings:

- SDK XML version mismatch warning from local Android tooling.
- AGP 9.0.0 warning about compile SDK 37.0 support.

Manual QA Gap:

- No emulator or physical-device screenshot was captured in this step.
- Text overlap and real-device ergonomics remain candidates for `GBLOCK-008` device calibration/playtest.

GBLOCK-007 Status:

- PASS: playfield, stats, next piece, pause, restart, and game-over presentation are present.
- PASS: no permanent movement buttons were added.
- PASS: pause/restart menu flow dispatches commands.
- PASS: renderer remains separate from game rules.
- WARNING: visual confirmation on phone/emulator is still pending.
