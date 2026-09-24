# GBLOCK

`GBLOCK` is the current working title for a Foundry Android falling-block puzzle game research project. The canonical project folder remains `gesture-blocks`.

Do not use "Tetris" as a product name, package identity, or distributable brand.

## Phase Boundary

Current phase: packetized prototype implementation under Foundry governance.

Completed baseline packets established upstream research, a pure Kotlin game core, and a minimal Android Compose renderer shell.

Active implementation remains packet-scoped. Do not add deployment under `C:\Foundry\Apps`, permanent product naming, App Store release work, proprietary assets, unrelated mechanics, or broad upstream rewrites without a packet that explicitly authorizes them.

Private-use licensing posture: this is a personal, non-commercial project with no App Store distribution or exchange for money currently planned. Upstream projects may be used as personal prototype references under that assumption, but provenance should remain documented and any future distribution, publication, sale, or store release must reopen the license/IP review gate.

## Canonical Locations

- Canonical project source: `C:\Foundry\Projects\gesture-blocks`
- Canonical packet authority: `C:\Foundry\Projects\foundry-packets\packets\inbox\GBLOCK-*.json`
- Upstream research clones: `research/upstream/`
- Research reports: `research/reports/`

## Approved Direction

- Android-first development.
- Kotlin, Android Studio, Jetpack Compose, and Compose Canvas are the preferred prototype stack.
- Prototype UX should be extremely simple and NES-era inspired, but architecture must not be an NES clone.
- The main research question is whether invisible gesture controls can feel natural and precise for a classic falling-block puzzle game.
- The default skin is named `GBLOCK`.
- The alternate monochrome handheld skin is a private-use visual reference implementation inspired by the attached Game Boy-era screenshot; it uses original Compose drawing constants and does not import proprietary bitmap assets.

## Tooling Note

Android Studio's integrated AI assistant is Google's Gemini in Android Studio, not OpenAI Codex. Treat Codex as an external Foundry/Codex app or CLI workflow from the canonical repository root unless a supported Android Studio integration path is separately proven.

References:

- Google: https://developers.google.com/solutions/pages/android-with-ai
- OpenAI Codex: https://developers.openai.com/learn/codex

## Architecture Boundary

The target architecture must keep these responsibilities separate:

- Game Core
- Input/Gesture Interpretation
- Rendering/Presentation
- Audio
- Skin System
- Android platform services
- Persistence/settings

Conceptually:

```text
Touch Input -> Gesture Interpreter -> Game Commands -> Game Core

Game Core -> Presentation API -> Renderer/Skin
```

See [Architecture Boundary](docs/architecture-boundary.md) and [Bootstrap Evaluation Report](research/reports/bootstrap-evaluation-report.md).

## Validation

For the current pure Kotlin core:

```powershell
.\gradlew.bat test --no-daemon
```

For the current Android renderer shell:

```powershell
.\gradlew.bat test assembleDebug --no-daemon
```
