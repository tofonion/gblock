# Reusable Game Functions Library

Status: PROPOSED / SEED INVENTORY

This project is beginning to reveal reusable functions and boundaries that should become a small Foundry game-development library. The goal is to extract only stable, proven pieces after they have survived GBLOCK gameplay and device testing.

## Candidate Library Areas

### Pure Grid Rules

- `Position`
- board bounds checks
- occupancy checks
- line/row clear helpers
- piece absolute-position mapping

Reuse target:

- Grid-based puzzle games.

### Command Boundary

- command objects for player intent
- dispatch pipeline from input to rules
- paused/game-over command guards

Reuse target:

- Any game where input should not directly mutate rules.

### Gesture Interpretation

- tap
- two-finger tap
- thresholded drag
- velocity-gated flick
- sensitivity presets

Reuse target:

- Touch-first games that need deterministic input tests.

### Presentation Snapshot Boundary

- immutable snapshot consumed by renderer
- renderer-independent rule state
- skin palette and presentation constants

Reuse target:

- Games with replaceable visual themes.

### Device Validation Receipts

- adb install/run/screenshot command receipts
- physical device model/API/screen inventory
- human playtest finding format

Reuse target:

- Any Android game packet requiring prove-or-block validation.

## Extraction Rule

Do not extract a library prematurely. A candidate function becomes library-worthy only after:

- it has at least one deterministic test,
- it is free of Android dependencies if intended for core reuse,
- it has a second plausible game use case,
- it has a clear package boundary,
- and the GBLOCK implementation no longer needs rapid local iteration.

## Proposed Future Packet

Create a later packet for `Foundry Game Kit` extraction after GBLOCK reaches stable V1 behavior. Until then, this document is the inventory and design pressure valve.
