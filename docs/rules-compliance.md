# GBLOCK Rules Compliance

Status: RESEARCH FINDING / IMPLEMENTATION AUDIT

This document tracks how the current GBLOCK prototype maps to standard falling-block tetromino rules.

## Implemented

- Playfield is 10 columns by 20 rows.
- All seven tetromino types exist:
  - `I`
  - `O`
  - `T`
  - `S`
  - `Z`
  - `J`
  - `L`
- Pieces spawn one at a time.
- Pieces can move left and right.
- Core supports clockwise rotation.
- Core supports counterclockwise rotation.
- Core supports soft drop.
- Core supports hard drop.
- Completed horizontal rows clear.
- Clearing lines awards score.
- Four-line clears award the largest line-clear score in the current table.
- Level increases every 10 cleared lines.
- Gravity speed increases with level in the Android session loop.
- Game over occurs when a newly spawned piece cannot enter the playfield.

## Current Score Table

The current placeholder score table is:

- 1 line: `100 * level`
- 2 lines: `300 * level`
- 3 lines: `500 * level`
- 4 lines: `800 * level`
- soft drop: `1` point per manual soft-drop cell
- hard drop: `2` points per hard-drop cell

This is a conventional simple table and gives the four-line clear the highest line-clear award. Final V1 scoring can still be tuned before release candidate.

## Prototype Input Mapping

Current visible/touch controls do not expose every core command yet.

- Tap maps to clockwise rotation.
- Horizontal gesture maps to left/right movement.
- Downward gesture maps to soft drop or hard drop.
- Two-finger tap maps to pause.
- Counterclockwise rotation exists in core but has no gesture binding yet.

This preserves the original prototype decision to avoid unnecessary gestures while keeping the rules core capable of standard rotation.

## Deferred / Not Guideline-Complete

The project is not trying to become a licensed guideline implementation. The following are not currently complete:

- Super Rotation System exact wall kicks.
- 7-bag randomizer.
- Hold piece.
- Ghost piece.
- Lock delay rules.
- DAS/ARR keyboard-style repeat semantics.
- T-spin recognition.
- Back-to-back bonuses.
- Combo bonuses.
- Official branding, logos, music, or assets.

## Source Notes

The audit is based on the user's listed rule summary and a source check of:

- Wikipedia's gameplay overview, which describes tetrominoes, filled horizontal lines disappearing, points, and preventing the stack from reaching the top.
- Hard Drop's Tetris Guideline page as a reference for modern guideline concepts.
- The user's linked rule summary article as a non-authoritative overview.

GBLOCK should continue to describe these as standard falling-block or tetromino rules in project materials, not as a licensed Tetris product.
