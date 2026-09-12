# Custom HUD+

Make your Minecraft interface fully yours. **Custom HUD+** lets you place, resize, recolor, and hide any HUD element right from an in-game editor — including vanilla ones like the hotbar, health, hunger, and XP bar.

A client-side [Fabric](https://fabricmc.net/) mod.

## Features

- **17 custom overlays** — FPS, coordinates, facing direction, speed, biome, real-time clock, in-game time, day counter, light level, held item, target block, memory usage, potion timers (with icons), keystrokes + CPS, armor preview, and totem count.
- **Move vanilla elements** — reposition, resize, or hide the hotbar, health, hunger, XP bar, crosshair, and potion icons.
- **Full control per element** — drag to move, scroll to resize, pick a text color, toggle the background, or reset to default.
- **Everything saves automatically** to `config/hudditor.json`.

## Usage

Press **Right Shift** in-game to open the menu.

- **Menu** — toggle each element ON/OFF, or open **OPTIONS** (color, background, size).
- **Edit HUD Layout** — drag to move, scroll to resize (hold **Shift** for fine steps), right-click to hide, **R** to reset, **Esc** to save.

You can rebind the open key in **Options → Controls**.

## Requirements

- Minecraft **1.21.9**, **1.21.10**, or **1.21.11** (use the matching jar)
- [Fabric Loader](https://fabricmc.net/) 0.16+
- [Fabric API](https://modrinth.com/mod/fabric-api)
- Java 21

## Install

1. Install Fabric Loader for your Minecraft version.
2. Put **Fabric API** and the **Custom HUD+** jar for your version into `.minecraft/mods/`.
3. Launch and press **Right Shift**.

## Build from source

```bash
./gradlew build
```
The jar is output to `build/libs/`. To build for a specific version:
```bash
./gradlew build -Pminecraft_version=1.21.10 -Pminecraft_dep=1.21.10 \
  -Pyarn_mappings=1.21.10+build.3 -Pfabric_version=0.138.4+1.21.10
```

## License

BSD 3-Clause — see [LICENSE](LICENSE).

Made by **olivvi_**.
