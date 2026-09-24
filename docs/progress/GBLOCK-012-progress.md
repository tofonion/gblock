# GBLOCK-012 Progress

Packet: `GBLOCK-012`

Scope: V1 test and regression harness. This entry records the rules-compliance audit and added core regression coverage.

## Step 0 - Standard Rules Audit

Status: COMPLETE

User requested confirmation against standard falling-block tetromino rules:

- 10 x 20 playfield.
- Seven tetrominoes.
- Left/right movement.
- Clockwise and counterclockwise rotation.
- Soft drop.
- Hard drop.
- Completed horizontal line clearing.
- Scoring with four-line clear as highest line-clear award.
- Speed increase with level.
- Game over when new pieces cannot enter.

Findings:

- Already present:
  - 10 x 20 board.
  - Seven tetromino types.
  - Left/right movement.
  - Clockwise rotation.
  - Soft drop.
  - Hard drop.
  - Line clear.
  - Score and level progression.
  - Gravity speed increase in Android session loop.
  - Game-over/top-out spawn block check.
- Gap found:
  - Counterclockwise rotation was not present in core.

## Step 1 - Counterclockwise Rotation Added

Status: COMPLETE

Changed:

- `core/src/main/kotlin/foundry/gestureblocks/core/GameCommand.kt`
- `core/src/main/kotlin/foundry/gestureblocks/core/Tetromino.kt`
- `core/src/main/kotlin/foundry/gestureblocks/core/GameCore.kt`
- `core/src/test/kotlin/foundry/gestureblocks/core/GameCoreTest.kt`
- `app/src/main/kotlin/foundry/gestureblocks/MainActivity.kt`

Added:

- `GameCommand.RotateCounterclockwise`
- `Tetromino.rotateCounterclockwise()`
- shared core rotation helper for clockwise and counterclockwise rotation.
- core regression test for counterclockwise rotation.
- audio cue mapping for counterclockwise rotation.

Note:

- Counterclockwise rotation is now supported by the rules core.
- No gesture binding has been assigned yet; the original prototype still maps tap to clockwise rotation only.

## Step 2 - Rules Documentation

Status: COMPLETE

Added:

- `docs/rules-compliance.md`

Documents:

- implemented rules.
- current score table.
- prototype input mapping.
- deferred/non-guideline-complete features.
- source notes.

## Step 3 - Validation

Status: COMPLETE

Validation command:

```powershell
$env:GRADLE_USER_HOME='C:\Foundry\Projects\gesture-blocks\.gradle-user'
.\gradlew.bat test assembleDebug --no-daemon
```

Result:

- `BUILD SUCCESSFUL in 29s`
- Core tests passed.
- Android debug assembly passed.

Warnings:

- SDK XML version mismatch warning.
- AGP 9.0.0 compile SDK 37.0 support warning.

GBLOCK-012 Status:

- IN PROGRESS.
- Rules audit coverage improved.
- Full V1 regression harness is not complete yet.
