# GBLOCK Source Hygiene Register

Date: 2026-09-24

## Canonical Source

Canonical repository:

- `C:\Foundry\Projects\gesture-blocks`

Primary source areas:

- `app/src/main/kotlin/foundry/gestureblocks`: Android app shell, Compose renderer, gesture wiring, settings, persistence, and audio adapter.
- `core/src/main/kotlin/foundry/gestureblocks/core`: platform-independent game rules, board model, tetromino model, commands, snapshots, and gesture interpreter.
- `core/src/test/kotlin/foundry/gestureblocks/core`: core reducer and gesture interpreter tests.
- `docs`: architecture, progress logs, roadmap, evidence, source hygiene, and research reports.

## Generated And Local Files

Ignored/generated:

- `.gradle/`
- `.gradle-user/`
- `build/`
- `**/build/`
- `.idea/`
- `local.properties`

Private local files:

- `private-audio-overrides/*.mp3`
- `private-audio-overrides/*.wav`
- `private-audio-overrides/*.ogg`

These private audio files are explicitly not canonical project assets.

## Upstream Research Isolation

Upstream repositories remain research inputs only and are ignored from canonical application history:

- `research/upstream/compose-tetris`
  - Remote: `https://github.com/vitaviva/compose-tetris.git`
  - Branch: `main`
  - Commit inspected: `234416c`
- `research/upstream/Fluidtris`
  - Remote: `https://github.com/nascimentolwtn/Fluidtris.git`
  - Branch: `master`
  - Commit inspected: `e5de3d9`
- `research/upstream/Simplis`
  - Remote: `https://github.com/gabrielrovesti/Simplis.git`
  - Branch: `master`
  - Commit inspected: `6f9e12f`
- `research/upstream/TetrisLite`
  - Remote: `https://github.com/yet300/TetrisLite.git`
  - Branch: `main`
  - Commit inspected: `fcac89e`

## Build Configuration

Android app:

- Application id: `foundry.gestureblocks.prototype`
- Namespace: `foundry.gestureblocks`
- Min SDK: `24`
- Target SDK: `37`
- Compile SDK: `37`
- Version: `0.1.0`

Build command:

```powershell
$env:GRADLE_USER_HOME='C:\Foundry\Projects\gesture-blocks\.gradle-user'
.\gradlew.bat test assembleDebug --no-daemon
```

## Accepted Warnings

Current build warning:

- Android SDK XML version warning: command-line tooling understands SDK XML up to version 3, but an SDK XML version 4 file is present.
- Android Gradle Plugin warning: AGP `9.0.0` has been tested up to compile SDK `36.1`, while project compile SDK is `37`.

Rationale:

- Build and tests complete successfully.
- Broad Android Gradle Plugin or SDK tooling churn is deferred until a focused tooling packet or explicit approval.
- Warning is tracked here for V1 release-candidate review.

## Source Comment Pass

Project source now includes concise comments in:

- Android app state/persistence/rendering/input/audio code.
- Core game reducer, board rules, line clearing, scoring, wall kicks, snapshots, tetromino definitions, and gesture interpreter.

Comment style:

- Explain ownership boundaries and non-obvious behavior.
- Avoid comments for obvious assignments.
- Keep game rules, rendering, input, persistence, and audio boundaries explicit for future maintainers.
