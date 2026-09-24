# GBLOCK-010 Progress

Packet: `GBLOCK-010`

Scope: local settings and persistence. This entry records early runtime settings work only; persistence is not complete.

## Step 0 - Runtime Settings Added

Status: PARTIAL

Changed:

- `app/src/main/kotlin/foundry/gestureblocks/MainActivity.kt`

Added:

- A compact `SET` control in the side panel.
- Dropdown menu entries for skin selection.
- Runtime sensitivity presets:
  - `RESPONSIVE`
  - `NORMAL`
  - `DELIBERATE`

Sensitivity behavior:

- Presets create `GestureInterpreter` instances with different horizontal, soft-drop, hard-drop distance, and hard-drop velocity thresholds.
- Default remains `NORMAL`.

Limitations:

- Settings are runtime-only.
- Skin and sensitivity do not yet survive app restart.
- No high score persistence has been added.
- No reset/clear behavior exists yet.

GBLOCK-010 Status:

- IN PROGRESS / PARTIAL.
- Persistence requirements remain open.

## Step 1 - Settings Modal Behavior

Status: PARTIAL

User feedback:

- Opening settings should automatically pause the game.
- The settings box can be larger and take up more space.

Changed:

- `app/src/main/kotlin/foundry/gestureblocks/MainActivity.kt`

Implementation:

- Opening `SET` while the game is actively running now dispatches `PauseToggle`.
- Opening `SET` while already paused or game-over does not toggle pause state.
- Closing the settings menu does not automatically resume gameplay; the player must resume intentionally.
- Settings dropdown width increased to `190.dp`.
- Settings menu text increased to `15.sp`.

Validation:

- `BUILD SUCCESSFUL in 25s`
- Updated APK installed successfully on Zebra TC57.

Still open:

- Runtime settings are still not persisted across app restarts.
- High score persistence remains unimplemented.

## Step 2 - Runtime High Score Display

Status: PARTIAL

User feedback:

- Add a high-score text line showing the player's most recent highest score.

Changed:

- `app/src/main/kotlin/foundry/gestureblocks/MainActivity.kt`

Implementation:

- Added runtime high-score tracking in the app layer.
- High score updates when the current score exceeds the previous in-session best.
- Display is rendered as a simple top-screen text line, not a boxed stat panel.

Boundary:

- This does not complete high-score persistence.
- The value does not yet survive app restart.

Validation:

- `BUILD SUCCESSFUL in 27s`
- Updated APK installed successfully on Zebra TC57.

## Step 3 - Local Persistence

Status: COMPLETE

Changed:

- `app/src/main/kotlin/foundry/gestureblocks/MainActivity.kt`

Stored data:

- High score
- Selected skin
- Gesture sensitivity preset
- Audio enabled
- Music enabled
- App audio volume

Implementation:

- Added `GblockPreferences`, a local Android `SharedPreferences` adapter.
- Loads persisted settings when `GestureBlocksApp` starts.
- Saves settings as they change.
- Saves high score when the current score exceeds the previous best.
- Added `RESET HIGH` in the `SET` menu to clear the stored high score.

Boundary:

- Persistence is app-private and local-only.
- No network, account, analytics, credential, or external service was added.
- Game core remains persistence-free.

Validation:

- `BUILD SUCCESSFUL in 30s`
- Updated APK installed successfully on Zebra TC57.
- App launched successfully on Zebra TC57.
- ADB `run-as` confirmed app-private `shared_prefs/gblock_preferences.xml` exists.

GBLOCK-010 Status:

- COMPLETE for V1 local persistence scope.
- Future polish may replace `SharedPreferences` with DataStore if settings become more complex.
