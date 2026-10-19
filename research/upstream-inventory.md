# Upstream Inventory

Snapshot date: 2026-09-24

All repositories were cloned under `research/upstream/` and left as separate Git repositories. They are ignored by the parent `gesture-blocks` repository.

| Project | URL | Branch | Commit | GitHub archived | License finding | Build task discovery |
| --- | --- | --- | --- | --- | --- | --- |
| Simplis | https://github.com/gabrielrovesti/Simplis | `master` | `6f9e12fdc9c2dd29784cb6025ace5887c7bb2ad5` | Yes | No license file / GitHub API license null | PASS: `.\gradlew.bat tasks --no-daemon` |
| TetrisLite | https://github.com/yet300/TetrisLite | `main` | `fcac89e8c1421c274ae9e3407cafdb28e9a72c7e` | Yes | Apache-2.0 | WARNING: task discovery passed, but emits unused Kotlin source-set warnings and Gradle 10 deprecation warning |
| compose-tetris | https://github.com/vitaviva/compose-tetris | `main` | `234416c455cd0b5524b7f2a7e91aaa9f6206457a` | No | MIT | WARNING: task discovery passed, but old Gradle 7.3-rc-1 / AGP 7.1.2 / Kotlin 1.6.10 stack and deprecated Gradle features |
| Fluidtris | https://github.com/nascimentolwtn/Fluidtris | `master` | `e5de3d963e5efde35f09beeb45d4971b34872aee` | No | No license file / GitHub API license null | PASS: `.\gradlew.bat tasks --no-daemon` |

## Toolchain Versions Observed

- Simplis: Gradle 8.13, AGP 8.13.0, Kotlin 2.0.21, Compose BOM 2024.09.00.
- TetrisLite: Gradle 9.1.0, AGP 9.0.0, Kotlin 2.3.10, Compose Multiplatform 1.10.2, Android compile/target SDK 36.
- compose-tetris: Gradle 7.3-rc-1, AGP 7.1.2, Kotlin 1.6.10, Compose 1.1.1, compile/target SDK 32.
- Fluidtris: Gradle 9.4.1, AGP 9.2.1, Kotlin 2.2.10.

