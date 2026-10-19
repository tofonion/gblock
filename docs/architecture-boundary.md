# Architecture

GBLOCKS separates platform-independent game rules from Android presentation and services.

- `core`: board, pieces, scoring, collision, rotation, game commands, snapshots, and gesture interpretation.
- `app`: Compose rendering, touch capture, session timing, settings, recovery, and audio.

```text
Touch input -> Gesture interpreter -> Game commands -> Game core
Game snapshot -> Compose renderer -> Selected skin
```

The game core has no Android UI dependency. Skins control presentation without changing rules. Android owns the game clock, persistence, and audio output.

## Controls

Tap rotates clockwise. Horizontal dragging moves a piece. Downward dragging soft-drops; a sufficiently long, fast downward flick hard-drops. A two-finger tap toggles pause. Gesture sensitivity is configurable in settings.

## Skins

Bundled skins use `GameSkin`, `SkinPalette`, and `SkinLayout` in `MainActivity.kt`. They configure colors, spacing, panel sizes, and typography. Selection persists locally. User-loaded skin packages are not currently supported.
