# GBLOCK-009 Progress

Packet: `GBLOCK-009`

Scope: original retro presentation baseline and runtime skin selection. No proprietary bitmap assets, no store release, no skin marketplace, and no user-loadable skin packages.

## Step 0 - Phase Start

Status: IN PROGRESS

Context:

- User approved `GBLOCK` as the working title.
- User requested a settings button, sensitivity tuning, skin dropdown, and a Game Boy-era visual skin based on the attached reference image.
- The project remains personal and non-commercial, but provenance is still tracked for future distribution cleanup.

## Step 1 - Runtime Skin Model

Status: COMPLETE

Changed:

- `app/src/main/kotlin/foundry/gestureblocks/MainActivity.kt`

Added:

- `GameSkin.Gblock`
- `GameSkin.Handheld`
- `SkinPalette`
- Renderer palette usage for app background, board background, grid, border, text, status, menu colors, and cells.

Finding:

- Renderer now uses skin/presentation constants rather than hard-coded colors throughout the main play screen.
- Game rules remain in core and do not depend on skin.

## Step 2 - Default GBLOCK Skin

Status: COMPLETE

Decision:

- The current dark prototype presentation is now named `GBLOCK`.
- App display name changed to `GBLOCK`.

Changed:

- `app/src/main/res/values/strings.xml`
- `README.md`

## Step 3 - Handheld Skin

Status: IN PROGRESS

Reference:

- User-provided screenshot of a monochrome Game Boy-era falling-block presentation.

Implementation:

- Added an original monochrome green/olive palette.
- Added monochrome block fill behavior.
- Added inset block-outline detail for the handheld skin.

Boundary:

- No proprietary bitmap asset, ROM resource, logo, original music, or font was imported.
- The skin is private-use reference work and should be reviewed again before any distribution posture changes.

## Step 4 - Game Boy-Style Side Layout

Status: COMPLETE

User request:

- Arrange the game screen like the provided Game Boy Tetris reference image.
- Reserve space in the bottom-right corner for the settings button.

Changed:

- `app/src/main/kotlin/foundry/gestureblocks/MainActivity.kt`

Implementation:

- Moved the playfield into a dominant left column.
- Moved score, level, lines, and next-piece preview into a stacked right-side panel.
- Kept the `SET` menu at the bottom-right of the right-side panel.
- Added framed side-panel components using skin palette constants.
- Added a panel background color to each runtime skin.

Boundary:

- The attached image was used as a visual layout reference only.
- No proprietary bitmap assets, logos, extracted graphics, music, or fonts were imported.

## Step 5 - Validation

Status: COMPLETE

Validation command:

```powershell
$env:GRADLE_USER_HOME='C:\Foundry\Projects\gesture-blocks\.gradle-user'
.\gradlew.bat test assembleDebug --no-daemon
```

Result:

- `BUILD SUCCESSFUL in 27s`
- `BUILD SUCCESSFUL in 26s` after adding side-panel layout.
- Known SDK XML and AGP compile SDK warnings remain.

Device status:

- Updated APK installed successfully on the Zebra TC57.
- Updated side-panel layout APK installed successfully on the Zebra TC57.
- ADB screenshot attempt captured the lock screen, not the app, so it was not counted as valid visual evidence.

Open:

- Unlock TC57 and capture/inspect the app screen for visual confirmation of the side-panel layout.
- Unlock TC57 and visually confirm the settings menu and handheld skin on-device.
- Decide whether the handheld skin should be renamed from `HANDHELD` to a more specific internal label.

## Step 6 - Larger Playfield Iteration

Status: COMPLETE

User feedback:

- Physical TC57 photo showed the side panel working, but the playfield was still too small.
- Score, level, lines, and next boxes may be smaller to give the board more room.

Changed:

- `app/src/main/kotlin/foundry/gestureblocks/MainActivity.kt`

Implementation:

- Reduced outer screen padding.
- Reduced board-to-side-panel spacing.
- Reduced side panel width from `132.dp` to `96.dp`.
- Reduced stat panel font sizes.
- Reduced next panel height and font sizes.
- Kept `SET` anchored in the bottom-right side-panel area.

Validation:

- `BUILD SUCCESSFUL in 46s` after the first larger-playfield/audio diagnostic change.
- `BUILD SUCCESSFUL in 25s` after adding settings auto-pause and larger settings dropdown.
- Updated APK installed successfully on Zebra TC57.

Visual evidence:

- User-provided physical photo confirmed the previous side-panel arrangement.
- ADB screenshot after this revision again captured the black lock/screen state, so it was not counted as visual confirmation.

## Step 7 - High Score Line And Next Preview

Status: COMPLETE

User feedback:

- Add a high-score text line near the top of the screen.
- Under `NEXT`, show a picture of the tetromino instead of a letter.

Changed:

- `app/src/main/kotlin/foundry/gestureblocks/MainActivity.kt`

Implementation:

- Added top-screen `HIGH SCORE` text using the best score seen during the current app session.
- Replaced the next-piece letter with a mini Canvas-rendered tetromino preview.
- The next-piece preview reuses the core `Tetromino` block definitions rather than a separate hand-coded shape table.

Boundary:

- High score display is runtime-only in this iteration.
- Durable high-score persistence remains in `GBLOCK-010`.

Validation:

- `BUILD SUCCESSFUL in 27s`
- Updated APK installed successfully on Zebra TC57.
- ADB screenshot again produced a black 6KB capture, so visual confirmation remains physical-device/user observation.

## Step 8 - Bundled Skinability Boundary

Status: COMPLETE

User feedback:

- Skinability is important.
- Users should eventually be able to customize simple UX details such as colors and screen arrangement.
- This does not need to live in settings yet beyond choosing included skins.
- `GBLOCK` should be the default/reference skin that future skin authors can copy.

Changed:

- `app/src/main/kotlin/foundry/gestureblocks/MainActivity.kt`
- `docs/architecture-boundary.md`
- `docs/skin-authoring.md`
- `docs/progress/GBLOCK-009-progress.md`

Implementation:

- Kept `GBLOCK` as the default skin.
- Settings now labels the default skin as `GBLOCK DEFAULT`.
- Added `menuLabel` to bundled skins so the settings menu can display clearer skin names.
- Added `SkinLayout` alongside `SkinPalette`.
- Moved simple screen-arrangement values into `SkinLayout`, including screen padding, high-score text sizing, side-panel width, panel spacing, next-panel sizing, status font size, and settings-button padding.
- `HANDHELD` now overrides the reference layout with small bundled differences.

Boundary:

- Current skinability is bundled in-APK only.
- A skin can change colors and simple layout values.
- A skin still cannot change scoring, collision, rotation, gravity, line clearing, randomization, or input commands.
- User-loadable skin packages remain future work.

Validation:

- `BUILD SUCCESSFUL in 27s`
- Updated APK installed successfully on Zebra TC57.
