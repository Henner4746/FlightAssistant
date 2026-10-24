# FlightAssistant Continued

> [!IMPORTANT]
> **Unofficial community continuation**  
> This is an unofficial fork of [FlightAssistant by Octol1ttle](https://github.com/Octol1ttle/FlightAssistant), kept up to date for Minecraft 26.2 and newer.  
> It is **independent** and neither affiliated with nor endorsed by Octol1ttle. Please report problems with this fork [here](https://github.com/Henner4746/FlightAssistant/issues), not to the original project.

<p align=center>
    <a href="https://modrinth.com/mod/flightassistant-continued">
        <img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.2.0/assets/cozy/available/modrinth_vector.svg"
            alt="Available on Modrinth"></a>
    <a href="https://modrinth.com/mod/fabric-api/">
        <img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.2.0/assets/cozy/requires/fabric-api_vector.svg"
            alt="Requires Fabric API"></a>
</p>

---

**FlightAssistant Continued** is a client-side mod that adds fully configurable avionics, situational awareness tools and advanced automation to Minecraft elytras, while still being friendly to casual users.

## Supported versions

| Minecraft | Loader |
|-----------|--------|
| 26.3      | Fabric |
| 26.2      | Fabric |

Older Minecraft versions, Forge and NeoForge are covered by the [original FlightAssistant](https://modrinth.com/mod/flightassistant).

## Main features

- Fully customizable flight HUD ([wiki](https://github.com/Octol1ttle/FlightAssistant/wiki/HUD))
  - An informative display showing flight data in real time
    <img src="https://github.com/Octol1ttle/FlightAssistant/wiki/img/hud/full_screenshot_alt.png" alt="HUD screenshot 1">
- Smart flight safety alerts ([wiki](https://github.com/Octol1ttle/FlightAssistant/wiki/Alerts))
  - Be aware of unsafe flight conditions before it's too late
    <img src="https://github.com/Octol1ttle/FlightAssistant/wiki/img/hud/alert_display_alt.png" alt="Alert screenshot">
- Non-intrusive automatic flight protections ([wiki](https://github.com/Octol1ttle/FlightAssistant/wiki/Flight-protections))
  - When you're not paying attention, FlightAssistant will protect you
  - Examples: ground collision damage avoidance, void damage avoidance, automatic elytra deploying
- Advanced autopilot with flight planning: fly completely hands-free ([wiki](https://github.com/Octol1ttle/FlightAssistant/wiki/Autopilot%3A-overview-and-basics))
  - Flight plans can be saved to disk and loaded later
  - With flight plans, a flight can be performed completely automatically from takeoff to landing
    <img src="https://github.com/Octol1ttle/FlightAssistant/wiki/img/hud/full_screenshot.png" alt="HUD screenshot 2">

The wiki and screenshots belong to the original project; the features work the same way in this fork.

## Installation

Install [Fabric Loader](https://fabricmc.net/use/) and put these mods into your `mods` folder:

- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin)
- [YetAnotherConfigLib](https://modrinth.com/mod/yacl)
- [Architectury API](https://modrinth.com/mod/architectury-api)
- [Mod Menu](https://modrinth.com/mod/modmenu) (optional, for the config button)

[Do a Barrel Roll](https://modrinth.com/mod/do-a-barrel-roll) is supported but not required.

## Default controls

| Action | Key |
|--------|-----|
| Open the FlightAssistant setup screen | Numpad Enter |
| Disconnect autopilot | Caps Lock |
| Global automation override (hold) | Left Alt |
| Hide current alert / show last hidden alert | Numpad 0 / Numpad . |
| Idle / decrease / increase / TOGA thrust | ← / ↓ / ↑ / → |

All keys can be changed in Options → Controls → Key Binds.

## Building

You need JDK 25. The project uses [Stonecutter](https://stonecutter.kikugie.dev/) for multiple Minecraft versions:

```
./gradlew :26.3-fabric:build
```

The jar ends up in `versions/26.3-fabric/build/libs/`. Replace `26.3` with `26.2` for the other version.

## Credits and license

FlightAssistant was created by [Octol1ttle](https://github.com/Octol1ttle) with contributions from mctaylors and others. This fork is maintained by [Henner4746](https://github.com/Henner4746).

Licensed under the [GNU Lesser General Public License v3.0](LICENSE), like the original.
