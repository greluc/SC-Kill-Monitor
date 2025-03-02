# Changelog

## [v1.x.x](https://github.com/greluc/SC-Kill-Monitor/releases/tag/v1.x.x)

- BUG: Fix spacing in ``SettingsView``.
- FEATURE: Add killer mode.

## [v1.2.1](https://github.com/greluc/SC-Kill-Monitor/releases/tag/v1.2.1)

- BUG: Make ``KillEvent`` data selectable again.
- BUG: Show Alert and return to ``StartView`` when the selected log file can’t be found or read.
- FEATURE: Add an option to settings to select if the ``KillEvent`` should be written to a file.
- FEATURE: Add class type to ``KillEvent``.
- FEATURE: Update the default scan interval to be 60 seconds.
- CHORE: Update dependencies.
- CHORE: Update persistent storage of settings in the registry. **This means that your settings will be reset to the default values when you start this version for the first time.**

## [v1.2.0](https://github.com/greluc/SC-Kill-Monitor/releases/tag/v1.2.0)

- FEATURE: Add a toggle to show all kills or only kills committed by other players. ([#27](https://github.com/greluc/SC-Kill-Monitor/issues/27))
- CHORE: Update dependencies.

## [v1.1.0](https://github.com/greluc/SC-Kill-Monitor/releases/tag/v1.1.0)

- BUG: Fix a Null Pointer Exception (NPE) when the file chooser is closed without having selected a file.
- FEATURE: Write the kill events to an own human-readable file. ([#22](https://github.com/greluc/SC-Kill-Monitor/issues/22), [#23](https://github.com/greluc/SC-Kill-Monitor/issues/23))
- FEATURE: Add application icon. ([#19](https://github.com/greluc/SC-Kill-Monitor/issues/19))
- FEATURE: Add a clear button to path settings. ([#25](https://github.com/greluc/SC-Kill-Monitor/issues/25))
- CHORE: Change start and stop scan button to icons.
- DOCUMENTATION: Add installation and operating documentation. ([#20](https://github.com/greluc/SC-Kill-Monitor/issues/20), [#21](https://github.com/greluc/SC-Kill-Monitor/issues/21))

## [v1.0.1](https://github.com/greluc/SC-Kill-Monitor/releases/tag/v1.0.1)

- FEATURE: Add about page (``AboutView``).
- BUG: Fix layout bugs in ``ScanView`` and ``StartView``.
- DOCUMENTATION: Change link in copyright text to HTTPS.
- DOCUMENTATION: Update disclaimer.
- DOCUMENTATION: Update ``README.md``.
- DOCUMENTATION: Update ``CONTRIBUTING.md``.

## [v1.0.0](https://github.com/greluc/SC-Kill-Monitor/releases/tag/v1.0.0)

- Initial release.