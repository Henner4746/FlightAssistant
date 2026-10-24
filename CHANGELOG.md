Please report bugs on [GitHub](https://github.com/Henner4746/FlightAssistant/issues). This is an unofficial fork, so please don't report its problems to the original FlightAssistant project.

## Changes in 3.2.0

- Added support for Minecraft 26.3
- Fixed a crash when opening the FlightAssistant setup screen or its menus while in a world ("Can only blur once per frame")
- The 26.2 version is now built against the real 26.2 versions of Fabric API, YetAnotherConfigLib and Architectury API
- Declared all required dependencies (YetAnotherConfigLib, Architectury API) so launchers install them automatically

### Minecraft 26.3 specifics

- Default key bindings were adapted to the new input system in 26.3 (Numpad Enter, Numpad 0, Numpad ., arrow keys, Caps Lock, Left Alt)
- Saving and loading flight plans now uses the system file dialog provided by 26.3
