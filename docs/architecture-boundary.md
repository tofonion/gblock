# Architecture Boundary

Status markers used here:

- LOCKED / APPROVED: current planning decision from the handoff.
- PROPOSED: architecture direction requiring later approval before implementation.
- RESEARCH FINDING: source-level evidence from upstream inspection.
- OPEN QUESTION: unresolved item for the next phase.

## LOCKED / APPROVED

- The game is Android-first and should be developed with Kotlin, Android Studio, Jetpack Compose, and Compose Canvas where appropriate.
- The first prototype is a simple 10 x 20 falling-block game screen with score, lines, level, next piece, and minimal pause/menu support.
- Permanent on-screen movement buttons are out of scope for the gesture prototype.
- Hold, counterclockwise rotation, and extra modern mechanics are not required for the initial prototype.
- The first visual skin may evoke NES-era simplicity, but proprietary Nintendo/Tetris assets, logos, music, ROM resources, or branding must not be imported, copied, redistributed, or committed.
- No deployed copy belongs under `C:\Foundry\Apps` before the project crosses the V1 barrier.

## PROPOSED

Define the first implementation around explicit contracts:

```text
GameCore
  accepts: GameCommand
  emits: GameSnapshot, GameEvent

GestureInterpreter
  accepts: TouchSample / GestureEvent
  emits: GameCommand

PresentationAdapter
  accepts: GameSnapshot, GameEvent
  emits: RendererModel

Renderer/Skin
  accepts: RendererModel, SkinManifest
  cannot alter: GameCommand, scoring, collision, randomization, gravity, line clearing
```

The Game Core should be a Kotlin module with no Android UI dependency. Android may own clocks, persistence adapters, audio backends, and touch event capture, but rules should remain testable outside Compose.

## Gesture Concept

LOCKED / APPROVED prototype mapping:

- Tap: rotate clockwise.
- Swipe/flick left: move left.
- Swipe/flick right: move right.
- Horizontal hold/drag: repeated or continuous horizontal movement.
- Swipe down: soft drop.
- Fast downward flick: hard drop.
- Two-finger tap: pause.

PROPOSED interpreter outputs:

- `RotateClockwise`
- `MoveLeft`
- `MoveRight`
- `SoftDrop`
- `HardDrop`
- `PauseToggle`

OPEN QUESTION: exact thresholds, repeat cadence, velocity cutoffs, and multi-touch handling require device testing.

## Skin Boundary

RESEARCH FINDING / CURRENT IMPLEMENTATION:

- V1 currently supports bundled in-APK skins through `GameSkin`.
- `GBLOCK` is the default/reference skin and appears in settings as `GBLOCK DEFAULT`.
- A skin currently owns a `SkinPalette` and `SkinLayout`.
- `SkinPalette` controls simple visual color values.
- `SkinLayout` controls simple screen arrangement values such as padding, side-panel width, spacing, panel padding, and font sizes.
- `HANDHELD` is a bundled visual variation.
- Skin selection persists locally.
- Game rules remain in `core` and do not depend on skin.

PROPOSED future skin packages may provide:

- block graphics
- palette
- background
- frame
- typography
- UI positioning
- animations
- music
- sound effects

LOCKED / APPROVED constraint: a skin must not alter game rules.

OPEN QUESTION: user-loadable skin packaging should be designed later. Candidate model is a manifest plus original art/audio assets loaded from app-bundled or user-provided storage, with private experimental skins excluded from distributable builds.
