# GBLOCK-013 Progress

Packet: `GBLOCK-013`

Scope: build tooling and source hygiene. This entry records the project-wide comment pass requested during V1 prototype work.

## Step 0 - Project-Wide Source Comments

Status: PARTIAL

User request:

- Add comments throughout the project, not only the newest code.
- Assume a human will want to make changes to the source.
- Comments should be short, concise, and explain what code blocks do.

Changed:

- `app/src/main/kotlin/foundry/gestureblocks/MainActivity.kt`
- `app/src/main/kotlin/foundry/gestureblocks/AndroidToneAudio.kt`
- `core/src/main/kotlin/foundry/gestureblocks/core/Board.kt`
- `core/src/main/kotlin/foundry/gestureblocks/core/GameCommand.kt`
- `core/src/main/kotlin/foundry/gestureblocks/core/GameCore.kt`
- `core/src/main/kotlin/foundry/gestureblocks/core/GameState.kt`
- `core/src/main/kotlin/foundry/gestureblocks/core/GestureInterpreter.kt`
- `core/src/main/kotlin/foundry/gestureblocks/core/Position.kt`
- `core/src/main/kotlin/foundry/gestureblocks/core/Tetromino.kt`
- `core/src/main/kotlin/foundry/gestureblocks/core/TetrominoType.kt`

Implementation:

- Added concise comments for game-core ownership, reducer flow, line clearing, scoring, wall kicks, deterministic queue, gesture thresholds, rendering boundaries, settings persistence, skins, and generated audio.
- Avoided line-by-line comments for obvious assignments.
- Preserved the architecture boundary: core rules remain platform-independent; Android shell owns rendering, settings, persistence, input, and audio.

Validation:

- `BUILD SUCCESSFUL in 30s`
- Updated APK installed successfully on Zebra TC57 after comment/persistence pass.

Open:

- Full GBLOCK-013 source inventory, warning register, and build-tooling review remain to be completed before V1 release-candidate review.

## Step 1 - Source Inventory And Warning Register

Status: COMPLETE

Changed:

- `docs/source-hygiene.md`
- `docs/progress/GBLOCK-013-progress.md`

Recorded:

- Canonical source areas.
- Generated and ignored paths.
- Private local audio override policy.
- Upstream research repository isolation.
- Upstream remotes, branches, and inspected commits.
- Android build configuration.
- Reproducible local build command.
- Current accepted SDK/AGP warnings and rationale.

Validation:

- `BUILD SUCCESSFUL in 30s`
- Packet validation pending in this turn.

GBLOCK-013 Status:

- IN PROGRESS.
- Source comments, source inventory, warning register, ignored file posture, upstream isolation, and reproducible build command are now documented.
- Remaining work is final V1 release-candidate hygiene review after test/regression packet completion.
