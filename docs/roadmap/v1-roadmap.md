# Gesture Blocks V1 Roadmap

Status: PACKET ROADMAP / PROPOSED V1 DEFINITION

This roadmap defines what must be complete before the project can be treated as V1.0. It is intentionally firm about gates and acceptance criteria, while allowing implementation details to evolve through packet execution and device evidence.

## Current Baseline

Completed:

- `GBLOCK-001`: project bootstrap, upstream research, license/build/source comparison.
- `GBLOCK-002`: pure Kotlin game core, command boundary, gesture interpreter tests.
- `GBLOCK-003`: minimal Android app shell and Compose Canvas renderer fed by `GameSnapshot`.

Current validated command:

```powershell
.\gradlew.bat test assembleDebug --no-daemon
```

Known warnings:

- SDK XML version mismatch warning from local Android tooling.
- AGP 9.0.0 warning about compile SDK 37.0 support.
- Debug native strip warning for `libandroidx.graphics.path.so`.

## V1 Definition

V1.0 means a locally buildable Android falling-block game prototype that is playable from launch to game over using gesture controls, with original presentation/audio placeholders, deterministic core tests, device-tested control thresholds, basic persistence, and a documented release-candidate acceptance report.

V1.0 does not mean:

- Play Store publication.
- Permanent product branding.
- A complete user-loadable skin marketplace.
- Online services.
- Multiplayer.
- Hold/counterclockwise rotation/modern competitive mechanics.
- Tetris/Nintendo branding, assets, music, logos, or ROM-derived resources.
- Deployment under `C:\Foundry\Apps` without a separate release-promotion packet.

## Private-Use Licensing Posture

LOCKED / APPROVED:

- The project is currently personal, non-commercial, and not intended for App Store distribution or exchange for money.
- Upstream projects may be treated as available for local personal prototype reference and adaptation under that assumption.
- Provenance and attribution notes still need to be preserved so the project can be cleaned up if its distribution posture changes.
- Any future distribution, publication, sale, store release, or promotion under `C:\Foundry\Apps` must reopen the license/IP gate before release.

RESEARCH FINDING:

- This posture lowers practical risk for private prototyping, but it does not convert third-party code, brand, art, music, or no-license repositories into distributable assets.

## V1 Packet Graph

| Packet | Title | Purpose | Depends On | Completion Gate |
| --- | --- | --- | --- | --- |
| `GBLOCK-004` | V1 Roadmap And Packet Graph | Establish this roadmap and downstream packet set. | `GBLOCK-001`, `GBLOCK-002`, `GBLOCK-003` | Roadmap docs and packet JSON validate. |
| `GBLOCK-005` | Android Gesture Input Integration | Wire Compose touch input to `GestureInterpreter`, `GameCommand`, `GameCore`, and refreshed snapshots. | `GBLOCK-003` | Playfield responds to tap, swipes, drag, flick, and two-finger pause in app shell. |
| `GBLOCK-006` | Game Loop And Session State | Add gravity timing, soft-drop cadence, pause/resume, restart, game-over lifecycle, and snapshot state holder. | `GBLOCK-005` | A complete session can run from start to game over without manual state resets. |
| `GBLOCK-007` | Prototype UX Layout And Minimal Menu | Polish the simple play screen, pause overlay, restart flow, and information density. | `GBLOCK-006` | Screen remains simple, readable, and button-free for movement. |
| `GBLOCK-008` | Device Gesture Calibration And Playtest | Test thresholds on physical/emulated Android devices and lock provisional control defaults. | `GBLOCK-007` | Device evidence supports natural, precise gesture play. |
| `GBLOCK-009` | Original Retro Presentation Baseline | Replace placeholder visual style with original NES-era-inspired presentation assets and renderer constants. | `GBLOCK-008` | Original distributable presentation is present without protected assets. |
| `GBLOCK-010` | Persistence Settings And High Scores | Add local settings and score persistence with clear privacy boundaries. | `GBLOCK-009` | Sensitivity/settings/high score survive app restart. |
| `GBLOCK-011` | Audio Boundary And Original Placeholder Audio | Add audio interface and original placeholder sounds/music toggle without coupling audio to rules. | `GBLOCK-010` | Audio cues work and can be disabled. |
| `GBLOCK-012` | V1 Test And Regression Harness | Expand core, gesture, Android, screenshot/smoke, and deterministic regression coverage. | `GBLOCK-011` | V1 validation suite is documented and green or explicitly waived. |
| `GBLOCK-013` | Build Tooling And Source Hygiene | Resolve build warnings where practical, document remaining local SDK issues, and prepare reviewable source state. | `GBLOCK-012` | Clean source inventory and reproducible build receipt exist. |
| `GBLOCK-014` | Licensing Privacy Accessibility Review | Audit code/assets/audio/name/privacy/accessibility before release candidate. | `GBLOCK-013` | No known IP/privacy/accessibility blocker remains untracked. |
| `GBLOCK-015` | V1 Release Candidate Acceptance Gate | Produce final V1 acceptance report and human approval gate. | `GBLOCK-014` | Human accepts or returns RC with explicit revisions. |

## Locked V1 Requirements

- Android-first.
- Kotlin core stays Android-independent.
- Android shell owns platform input, lifecycle, and rendering only.
- Touch input emits commands; it does not mutate rules directly.
- Renderer consumes snapshots/presentation state; it does not implement scoring, collision, line clear, or randomization.
- Skin/presentation must not alter rules.
- Active gameplay must immediately pause when the Android app loses foreground execution, including app switch, Home, screen lock, or backgrounding.
- Returning to foreground must leave gameplay paused until the player explicitly resumes.
- Pause must preserve board, active piece, next/randomizer state, score, lines, level, and relevant timing state.
- If Android terminates the app after backgrounding, the interrupted session should recover in a paused state where feasible.
- V1 distributable assets must be original or clearly licensed.
- Movement buttons are not permanent V1 controls.
- All packet progress must be documented in `docs/progress/`.

## Speculative But Required Decisions

These are allowed to evolve, but must be resolved before V1 acceptance:

- Final V1 gesture thresholds.
- Initial gravity/level timing curve.
- Whether V1 includes ghost piece.
- Whether V1 includes music or only sound effects.
- Exact persistence mechanism.
- Minimum Android API after real device testing.
- V1 package/application ID if the temporary prototype ID is retired.
- V1 name or explicit decision to ship under a temporary internal name only.

## V1 Acceptance Gate

V1.0 requires:

- `.\gradlew.bat test assembleDebug --no-daemon` passes or has a documented, approved waiver.
- Core and gesture tests cover all locked rules and commands.
- App launches to playable game screen.
- Game can be played from fresh start to game over using invisible gestures.
- Pause/restart/game-over flow works.
- Score, lines, level, and next piece display correctly.
- At least one original visual presentation baseline is bundled.
- Any audio is original or removed from V1.
- Local persistence works if included.
- License/IP review is clean enough for internal V1.
- Privacy review confirms no unexpected network, account, or personal data behavior.
- Accessibility review records minimum readable contrast/text/interaction findings.
- Human V1 acceptance is recorded in `GBLOCK-015`.
