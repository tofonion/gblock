# Private Audio Override Policy

Status: LOCAL-ONLY POLICY

GBLOCK may support private local audio experiments for personal testing, but commercial soundtrack files must not be committed, redistributed, bundled into release builds, or treated as project-owned assets.

Current policy:

- Original placeholder audio may be generated or implemented in source.
- Commercial soundtrack files may not be downloaded into canonical source by Codex.
- Private local experiments belong under `private-audio-overrides/`.
- Audio files in that folder are ignored by Git.
- Any future distribution, publication, sale, App Store release, or `C:\Foundry\Apps` promotion reopens the license/IP gate.

Current implementation:

- The app uses original Android tone-based placeholder cues.
- The private override folder is documented but not loaded by the app yet.
