# Source guide

## Layout

- `app/src/main/kotlin/io/github/tofonion/gblock`: Android UI, input, settings, recovery, and audio.
- `core/src/main/kotlin/io/github/tofonion/gblock/core`: platform-independent game rules and gesture interpreter.
- Tests live under each module's `src/test/kotlin` directory.
- `docs`: technical guides and previews.
- `research/upstream-inventory.md`: historical research references and license findings.

## Build

Application ID and namespace: `io.github.tofonion.gblock`.

Use JDK 21 and Android SDK 37. Run `gradlew.bat test assembleDebug --no-daemon` on Windows, or `sh gradlew test assembleDebug --no-daemon` on macOS/Linux.

Generated build files, IDE settings, local SDK configuration, and local audio experiments are ignored by Git. Research checkouts under `research/upstream/` are independent, ignored repositories.

The Android Gradle Plugin may warn that SDK 37 is newer than its tested SDK range.
