# GBLOCK-008 Progress

Packet: `GBLOCK-008`

Scope: device/emulator gesture calibration and playtest only.

## Step 0 - Phase Start

Status: BLOCKED

Evidence:

- Began after `GBLOCK-007` packet validation passed.
- Purpose was to run the prototype on a real or emulated Android target and evaluate gesture feel.

## Step 1 - Device / Emulator Availability Check

Status: BLOCKED

Commands:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices
```

Result:

```text
List of devices attached
```

Finding:

- No connected Android devices were available.

Command:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -list-avds
```

Result:

- No AVD names were returned.

Finding:

- No configured Android emulator device was available.

Command:

```powershell
Get-ChildItem -Recurse -Filter avdmanager.bat "$env:LOCALAPPDATA\Android\Sdk\cmdline-tools"
```

Result:

- `cmdline-tools not found`

Finding:

- This environment does not currently expose `avdmanager.bat`, so creating an emulator target from this shell was not available.

## Step 2 - Calibration Status

Status: BLOCKED

Blocked criteria:

- Device/emulator target could not be recorded beyond "none available".
- Gesture thresholds and velocity cutoffs cannot be validated from device evidence yet.
- Full play session using gestures only could not be attempted.
- Input failures and accidental gestures could not be observed.
- V1 default thresholds cannot be locked from evidence yet.

Already satisfied:

- Latest build validation from `GBLOCK-007` passed with:
  - `.\gradlew.bat test assembleDebug --no-daemon`
  - `BUILD SUCCESSFUL in 24s`

Required next action:

- Connect a physical Android device with USB debugging enabled, or create/start an Android emulator/AVD from Android Studio.
- Then rerun `adb devices`, install/run the debug build, and perform the gesture-only playtest checklist.

GBLOCK-008 Status:

- BLOCKED on Android device/emulator availability.
- No threshold changes were made.
- No playtest findings were invented.

## Step 3 - Zebra Scanner Attempt

Status: BLOCKED

Context:

- User identified the Zebra scanner as the intended Android test device.
- Existing debug APK was present:
  - `C:\Foundry\Projects\gesture-blocks\app\build\outputs\apk\debug\app-debug.apk`

Command:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices -l
```

Result:

```text
List of devices attached
```

Command:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" kill-server
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" start-server
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices -l
```

Result:

```text
* daemon not running; starting now at tcp:5037
* daemon started successfully
List of devices attached
```

Finding:

- The Zebra scanner was not visible to ADB from this workstation during this attempt.
- Install, launch, screenshot, and playtest could not proceed.

Additional host evidence:

```powershell
Get-PnpDevice -PresentOnly | Where-Object { $_.FriendlyName -match 'Zebra|Symbol|Motorola|Android|ADB|Portable|MTP|USB Composite|Scanner' -or $_.InstanceId -match 'VID_05E0|VID_18D1' }
```

Result:

```text
Status : OK
Class  : WPD
Name   : TC57
ID     : USB\VID_05E0&PID_2104\21076522507700
```

Finding:

- Windows sees the Zebra TC57 as a portable/MTP device from Zebra Technologies.
- The cradle/USB path is physically present enough for Windows device detection.
- ADB still does not see the device, so the likely gap is the ADB interface: USB debugging disabled/not authorized, Zebra ADB USB driver missing, or the Windows driver binding is MTP-only rather than ADB.

Official Zebra guidance reviewed:

- Zebra TC57 ADB USB setup documentation says to install Android SDK/platform tools and install Zebra ADB/USB drivers from Zebra Support Central.
- Zebra ADB install documentation says to enable USB debugging on the device and accept the RSA debugging prompt; if the device number does not appear in `adb devices`, ensure ADB drivers are installed properly.

Required next action:

- On the Zebra scanner, enable Developer Options and USB debugging.
- Connect the scanner by USB with a data-capable cable.
- Accept the RSA debugging prompt on the scanner if it appears.
- Confirm the scanner is in a USB mode that permits debugging/data transfer.
- If `adb devices -l` still shows no device, install the Zebra ADB and USB Driver Setup package for Windows from Zebra Support Central and reconnect the scanner.
- Then rerun `adb devices -l`; expected result is a listed device in `device` state.

## Step 4 - Retry After ADB Network Allowance

Status: BLOCKED

Context:

- User allowed ADB to access the network and asked to retry.

Command:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices -l
```

Result:

```text
List of devices attached
```

Command:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" kill-server
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" start-server
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices -l
```

Result:

```text
* daemon not running; starting now at tcp:5037
* daemon started successfully
List of devices attached
```

Host confirmation:

```powershell
Get-PnpDevice -PresentOnly | Where-Object { $_.FriendlyName -match 'Zebra|Symbol|Motorola|Android|ADB|Portable|MTP|USB Composite|Scanner|TC57' -or $_.InstanceId -match 'VID_05E0|VID_18D1' }
```

Result:

```text
Status : OK
Class  : WPD
Name   : TC57
ID     : USB\VID_05E0&PID_2104\21076522507700
```

Finding:

- Network allowance did not change ADB device visibility.
- Windows still detects the Zebra TC57 over USB/MTP.
- ADB still does not detect an authorized debug interface.

Current likely causes:

- USB debugging is not enabled on the TC57.
- The TC57 has not accepted the RSA debugging authorization prompt.
- The Windows driver binding is MTP-only.
- Zebra ADB/USB driver package is not installed or not bound to this device.

## Step 5 - TC57 Authorized And App Installed

Status: PARTIAL PASS

Context:

- User unlocked the Zebra TC57, enabled/allowed debugging, and asked to retry.

Command:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices -l
```

Result:

```text
21076522507700 device product:TC57 model:TC57 device:TC57 transport_id:5
```

Device target:

```text
Model: TC57
Android: 8.1.0
API: 27
Screen: 720x1280
Density: 320
```

Install command:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" -s 21076522507700 install -r app\build\outputs\apk\debug\app-debug.apk
```

Result:

```text
Performing Streamed Install
Success
```

Launch command:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" -s 21076522507700 shell monkey -p foundry.gestureblocks.prototype 1
```

Result:

```text
Events injected: 1
```

Screenshot evidence:

- `docs/evidence/GBLOCK-008-zebra-tc57-launch.png`
- `docs/evidence/GBLOCK-008-zebra-tc57-relaunch.png`

Visual finding:

- The game launches on the Zebra TC57.
- The 10 x 20 playfield is visible.
- Score, lines, level, next piece, and status fit without obvious overlap on the 720 x 1280 display.
- Initial presentation is readable enough for calibration.

ADB input smoke-test caveat:

- A later simulated ADB input/screenshot sequence produced black screenshots and was not counted as a valid gesture result.
- Logcat did not show a clear app fatal exception in the inspected tail.
- Relaunch restored the visible game screen.

Current calibration status:

- Device target is now recorded.
- Install and launch are proven.
- Visual layout on TC57 is proven by screenshot.
- Human touch-feel playtest is still required before locking thresholds.
- No threshold changes were made from simulated input.

## Step 6 - First Human Gesture Feedback And Threshold Adjustment

Status: IN PROGRESS

Human playtest feedback:

- Pause was a little hard to trigger.
- Hard drop was accidental.

Changes made from evidence:

- Two-finger pause tap window increased from 220 ms to 420 ms.
- One-finger tap slop increased from 18 px to 24 px.
- Two-finger tap slop split out and set to 64 px.
- Hard drop now requires at least 180 px downward travel.
- Hard drop velocity increased from 1600 px/s to 2600 px/s.
- Added tests proving:
  - deliberate long fast downward drag emits hard drop.
  - short fast downward drag remains soft drop.
  - long moderate downward drag remains soft drop.

Validation command:

```powershell
$env:GRADLE_USER_HOME='C:\Foundry\Projects\gesture-blocks\.gradle-user'
.\gradlew.bat test assembleDebug --no-daemon
```

Result:

- `BUILD SUCCESSFUL in 2m 37s` after the calibration patch.
- `BUILD SUCCESSFUL in 27s` after the subsequent settings/skin patch.

Device install:

```powershell
adb -s 21076522507700 install -r app\build\outputs\apk\debug\app-debug.apk
```

Result:

- `Success`

Current calibration status:

- Tuned build is installed on TC57.
- Retest is required to determine whether pause is now easier and hard drop is no longer accidental.
- V1 defaults are not locked yet.

## Step 6 - First Human Gesture Feedback And Threshold Adjustment

Status: IN PROGRESS

Human playtest feedback:

- Pause was a little hard to trigger.
- Hard drop was accidental.

Changes made from evidence:

- Two-finger pause tap window increased from 220 ms to 420 ms.
- One-finger tap slop increased from 18 px to 24 px.
- Two-finger tap slop split out and set to 64 px.
- Hard drop now requires at least 180 px downward travel.
- Hard drop velocity increased from 1600 px/s to 2600 px/s.
- Added tests proving:
  - deliberate long fast downward drag emits hard drop.
  - short fast downward drag remains soft drop.
  - long moderate downward drag remains soft drop.

Validation command:

```powershell
$env:GRADLE_USER_HOME='C:\Foundry\Projects\gesture-blocks\.gradle-user'
.\gradlew.bat test assembleDebug --no-daemon
```

Result:

- `BUILD SUCCESSFUL in 2m 37s` after the calibration patch.
- `BUILD SUCCESSFUL in 27s` after the subsequent settings/skin patch.

Device install:

```powershell
adb -s 21076522507700 install -r app\build\outputs\apk\debug\app-debug.apk
```

Result:

- `Success`

Current calibration status:

- Tuned build is installed on TC57.
- Retest is required to determine whether pause is now easier and hard drop is no longer accidental.
- V1 defaults are not locked yet.
