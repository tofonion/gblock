# GBLOCK Skin Authoring

Status: V1 in-APK skin boundary

GBLOCK supports simple bundled skins inside the APK. A skin can currently change presentation values such as colors, panel styling, font sizes, spacing, and side-panel layout. A skin must not change game rules.

## Reference Skin

`GBLOCK` is the default and reference skin.

Use `GameSkin.Gblock` as the example when adding a new bundled skin. It defines:

- `label`: short internal/user-facing name.
- `menuLabel`: settings-menu label. GBLOCK currently appears as `GBLOCK DEFAULT`.
- `palette`: colors for background, board, grid, borders, panels, text, menu, and cells.
- `layout`: screen arrangement values such as padding, side-panel width, spacing, panel padding, and font sizes.

## Current Bundled Skins

- `GBLOCK DEFAULT`: dark prototype reference skin.
- `HANDHELD`: monochrome low-color reference skin.

These are selected through the settings menu and persisted locally.

## Skin Boundary

Allowed for current in-APK skins:

- colors
- board/panel colors
- block colors
- side panel width
- screen padding
- stat/next/status font sizes
- next panel height
- spacing between screen elements
- settings button size

Not allowed:

- scoring rules
- collision logic
- rotation rules
- gravity/timing rules
- randomization
- line clearing
- input commands
- proprietary graphics, logos, music, fonts, ROM assets, or branding

## Adding A Bundled Skin

1. Add a new `GameSkin` enum entry in `MainActivity.kt`.
2. Start by copying `GameSkin.Gblock`.
3. Change `menuLabel` so it is recognizable in settings.
4. Change `SkinPalette` for color and simple tile appearance.
5. Use `SkinLayout.Reference.copy(...)` to override only layout values that need to differ.
6. Build and test on a physical device.

Keep skins original unless they are private experiments that will never be distributed.

## Future Direction

Later versions may move skins out of source code into package manifests or user-loadable files. For V1, the supported customization path is bundled in-APK skins only.
