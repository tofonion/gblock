# Bootstrap Evaluation Report

Status: RESEARCH FINDING / APPROVAL GATE

No implementation was started.

## Executive Recommendation

Recommendation: C. Build a small clean game core ourselves while using upstream projects only as references.

Rationale: none of the upstreams cleanly matches the desired shape of Android-first Compose + invisible gestures + replaceable skin boundary. TetrisLite has the strongest domain separation and test culture, but it carries broad multiplatform/product complexity that would dominate a small prototype. Simplis is closest in compact Compose shape, but its code license is unclear and its board model couples core state to Compose `Color`. Fluidtris is valuable input research, not a rules base. compose-tetris is useful rendering reference, but it is older, button-oriented, and tightly coupled.

Approval gate: next phase should approve a fresh minimal core plus selectively adapted concepts, not upstream source import.

## Android Studio AI Tooling

RESEARCH FINDING:

- Android Studio's integrated AI assistant is Google's Gemini in Android Studio, not OpenAI Codex.
- OpenAI Codex is available through Codex surfaces such as the Codex app/CLI and documented IDE workflows, including JetBrains-related material, but Android Studio should not be assumed to have native integrated Codex support.
- For this project, use Android Studio/Gemini for Android-specific IDE assistance if desired, and use Codex from the canonical repository root for governed Foundry work, packetized changes, validation, and reports.

OPEN QUESTION:

- Whether a reliable Android Studio path exists through JetBrains MCP/plugin support remains unproven for this project and should be treated as a separate tooling research task before relying on it.

References:

- Google Android with AI / Gemini in Android Studio: https://developers.google.com/solutions/pages/android-with-ai
- OpenAI Codex docs: https://developers.openai.com/learn/codex

## Comparison Matrix

| Area | Simplis | TetrisLite | compose-tetris | Fluidtris |
| --- | --- | --- | --- | --- |
| Best use | Study compact Compose app and simple immutable engine | Study domain/usecase/test architecture and gesture use cases | Study mature Compose Canvas rendering tricks | Study touch/drag feel and test ideas |
| Bootstrap suitability | Medium technically, blocked by license uncertainty | Medium-low due to scope and complexity | Low due to old stack and coupling | Low due to custom View and non-classic physics |
| License risk | FAIL: no license found | PASS: Apache-2.0 code license | PASS: MIT code license | FAIL: no license found |
| Build health | PASS task discovery | WARNING task discovery | WARNING task discovery | PASS task discovery |
| Skin readiness | Low-medium | Medium conceptually | Low | Low |
| Gesture relevance | Basic drag thresholds only | Strongest command-oriented gesture model | Button/repeat reference only | Strongest physical-touch feel reference |

## Simplis

RESEARCH FINDING:

- Board is `List<List<Color?>>`, which means the game board directly stores Compose presentation color.
- Pieces are immutable data objects with matrix shapes, row/col, rotation state, and simple clockwise matrix rotation.
- GameEngine is an object of mostly pure functions over `GameState`, which is a good direction.
- ViewModel owns loop timing via coroutine delays and exposes command-like methods: move left/right, soft drop, hard drop, rotate, hold, pause, restart.
- Rendering uses Compose `Canvas` in `GameScreen.kt`.
- Input currently uses `detectDragGestures`; horizontal movement fires after about 50 px, soft drop after downward drag greater than about 100 px, and an upward drag triggers hard drop. Tap rotation and two-finger pause are not present in the inspected input path.
- UI still includes visible movement/rotate/hold/drop buttons, so it does not satisfy the invisible-control prototype without redesign.
- Testing is only generated example unit/instrumentation tests.
- GitHub reports archived true. No license file was present and GitHub API returned license null.

ASSESSMENT:

Simplis verifies the earlier impression of separated `game` and `ui` folders, but the separation is incomplete because core state uses Compose `Color` and the app includes modern mechanics like hold/ghost/line-clear animation. Useful as a compact reference, not suitable for source bootstrap unless licensing is clarified and core is rewritten.

## TetrisLite

RESEARCH FINDING:

- Architecture is much larger: `core`, `feature`, `shared`, `app`, `build-logic`, Android, web, wear, desktop/native targets, Decompose/MVI, SQLDelight, settings, audio, and baseline profile support.
- Core domain uses Android-independent models such as `GameBoard`, `Position`, `Tetromino`, `TetrominoType`, `GameState`, use cases for movement, collision, rotation, hard drop, scoring, game loop, gesture handling, persistence, and audio.
- Board representation is a `Map<Position, TetrominoType>`, which is presentation-neutral.
- Rotation is explicit per tetromino and supports clockwise, counterclockwise, and 180 degree rotation.
- Gesture handling is use-case based. `GestureHandlingUseCase` translates drag start/drag/drag end into `MoveLeft`, `MoveRight`, `MoveDown`, and `HardDrop`, using a 50 px base threshold, sensitivity multipliers, 25 percent board-height hard-drop distance, and duration under 500 ms. `HandleSwipeInputUseCase` also considers velocity.
- UI exposes `GameInputActions` and Compose screens call component methods rather than directly mutating core state.
- Rendering is Compose/Compose Multiplatform and has theme concepts, visual effects, panels, board renderers, and settings-driven visual theme values.
- Tests are broad compared with the others: domain model/usecase tests, feature store/component tests, data/database tests.
- GitHub reports archived true. License is Apache-2.0.
- Task discovery passed but emitted unused Kotlin source-set warnings and Gradle 10 deprecation warnings.

ASSESSMENT:

TetrisLite is the best architectural study source. It proves a clean game-domain/usecase split is practical, and its gesture-usecase tests are highly relevant. It is too broad to use as the bootstrap base for this Android-first prototype unless the project explicitly accepts multiplatform and framework complexity.

## compose-tetris

RESEARCH FINDING:

- Single Android app using Kotlin, ViewModel, Compose, and Canvas.
- Game logic and UI are tightly coupled around `GameViewModel.ViewState`, `Brick`, `Spirit`, Compose `Offset`, sound, and UI actions.
- Board is effectively a list of `Brick` objects with Compose geometry types.
- Rendering is the strongest part: Canvas-based board, piece, next-piece, score panel, and retro handheld-style frame.
- Input is permanent visible buttons, including auto-repeat for movement buttons. It does not provide invisible gesture research.
- Testing is only generated example tests.
- License is MIT.
- Build task discovery passed, but the project uses Gradle 7.3-rc-1, AGP 7.1.2, Kotlin 1.6.10, Compose 1.1.1, SDK 32, and deprecated Gradle features.
- UI draws the text `TETRIS`, which is acceptable as upstream observation but should not be copied into the Foundry product.

ASSESSMENT:

Use as a rendering/style reference only. Avoid adopting its architecture or assets/text. If ideas are adapted, recreate them with original names, art, typography, and code in a clean renderer.

## Fluidtris

RESEARCH FINDING:

- Custom Android View (`FluidTetrisView`) and imperative canvas drawing, not Compose.
- GameEngine is physics/fluid oriented: `ActivePiece` has float x/y, rotation, spring force, snap animation, bounce state, lock timers, maze mode, and mutable grid state.
- Touch handling is direct Android `MotionEvent` processing. Touching a block can drag/move the piece; touching non-center cells can rotate via drag delta. It includes snap pull, wall clamp, spring carry, side buttons, pause overlays, next-piece button, maze swipe handling, and sound toggles.
- Gesture ideas are valuable: drag threshold tied to cell size for maze movement, resetting the reference point after a movement to allow chained drag steps, separating center-drag movement from edge/cell rotation, and using tests around touch/rotation/snap behaviors.
- Gameplay is intentionally not classic falling-block behavior. Board is 8 x 20 and includes physics and maze mode.
- Tests are substantial for this style: touch control, snap, rotation, collision, line, level, lock, maze, game-over, buttons.
- No license file was present and GitHub API returned license null.
- Task discovery passed.

ASSESSMENT:

Fluidtris should not be a rules source or bootstrap base. It is the most useful source for interaction-feel hypotheses and for test names/cases to recreate in a clean gesture interpreter.

## Gesture-System Findings

RESEARCH FINDING:

- TetrisLite supports the desired command boundary better than the others: raw gesture events become command-like results before state mutation.
- Simplis shows continuous horizontal drag can work by resetting the drag anchor after threshold crossing, but the implementation lives in UI and directly calls ViewModel methods.
- Fluidtris offers strong tactile ideas: cell-relative drag thresholds, chained motion after threshold crossing, touch-zone distinction, and tests around touch behavior. These should inform prototype experiments without adopting its fluid physics.
- compose-tetris has button auto-repeat behavior that can inform horizontal repeat timing, but not invisible touch input.

PROPOSED:

- Build a dedicated `GestureInterpreter` with deterministic unit tests before attaching to Compose pointer input.
- Treat tap rotation, horizontal drag repeat, soft drop, hard-drop flick, and two-finger pause as commands. Do not mutate core state from Compose pointer callbacks.

## Skin-System Implications

RESEARCH FINDING:

- Simplis and compose-tetris both mix board rendering with color/style assumptions.
- TetrisLite has theme and visual-effect concepts, but its renderer is still product-specific and not a Foundry skin-package boundary.
- Fluidtris mixes rendering, touch zones, sound controls, and game state in a custom View.

PROPOSED:

- Keep the first skin simple and original, but define a presentation model that contains piece type, occupancy, effects, score/line/level, and next piece without Android graphics types.
- Skin assets and audio should be original. Private experimental skins must stay outside distributable application source.

## Licensing Audit

RESEARCH FINDING:

- Simplis: no license file found; GitHub API license null. Treat as all-rights-reserved until clarified. Do not copy source.
- TetrisLite: Apache-2.0. Compatible for study and possible code reuse with license/notice obligations, but archived status and complexity argue against source import.
- compose-tetris: MIT. Compatible for study and possible code reuse with copyright/license notice retention, but do not copy brand text or assets.
- Fluidtris: no license file found; GitHub API license null. Treat as all-rights-reserved until clarified. Do not copy source.
- No repository license grants rights to Tetris branding, Nintendo assets, ROM resources, original music recordings, or protected marks. Code license and brand/art/music/IP status are separate.

## Build / Maintenance Risks

- Simplis: archived, no license, minimal tests, core/presentation color coupling.
- TetrisLite: archived, large multiplatform dependency surface, Gradle task discovery warnings, likely more maintenance than the prototype needs.
- compose-tetris: old Android/Gradle stack and deprecated Gradle features; architecture is not a good match.
- Fluidtris: no license, custom View, non-classic physics/gameplay, no Compose architecture.

## Acceptance Criteria Status

- PASS: project folder created under `C:\Foundry\Projects`.
- PASS: Git repository initialized.
- PASS: upstream repositories acquired under separated research area.
- PASS: remotes, branches, commits, licenses, archive status, and toolchains recorded.
- PASS: source-level inspection performed.
- PASS: Gradle task discovery executed without source modifications.
- PASS: packet created in central packet authority.
- PASS: implementation gate not crossed.
- WARNING: task discovery generated local ignored Gradle/build outputs inside upstream repos.
- WARNING: Simplis and Fluidtris license status blocks code reuse.

## Open Questions

- What permanent product name should replace `gesture-blocks`?
- Should the next phase allow creating an Android app skeleton, or first create only a pure Kotlin game-core module and tests?
- What exact gesture thresholds should be tested on physical Android hardware?
- Should hard drop be downward flick only, or also long downward swipe?
- Should skin packages be app-bundled only for v1, with user-loaded skins deferred?

## Proposed Next Phase

Create packet `GBLOCK-002` for a non-deployed implementation prototype:

- pure Kotlin game-core module with deterministic tests
- command model
- gesture-interpreter unit tests using synthetic gesture events
- Compose Canvas renderer fed by a presentation snapshot
- one original placeholder retro skin
- no proprietary assets
- no permanent product name
- no deployment under `C:\Foundry\Apps`
