# FireGhostFix

A client side Fabric mod that fixes two desync ghosts: flint and steel ghost fire when you place too fast, and crossbow loading ghosts where the client thinks a crossbow is loaded but the server does not.

- Minecraft: 1.21.11
- Loader: Fabric (fabricloader >= 0.19.0)
- Java: 21
- Side: client only, works on any server including vanilla

## Flint and steel ghost fire

The ghost happens because the client places the fire as a local prediction the instant you click, before the server has confirmed it. If the server does not end up placing that exact fire, the prediction is left behind as a ghost block. It looks placed but it is not really there, and it only disappears later when a block update reaches it, for example when an arrow flies through it or you interact with that spot.

This fix removes the prediction for flint and steel. The interact packet is still sent to the server exactly as normal, but the client no longer pre places the fire itself. The only fire you see is the real one the server places and sends back, so there is no prediction to revert and nothing to ghost. Valid placements become real fire that stays.

A short debounce is also kept (default 4 ticks, about 0.2 seconds) so spamming the same spot does not send a burst of duplicate ignites. Aiming at a new block resets it instantly, so normal placement is unaffected.

## Crossbow loading ghost

In 1.21.11 the loaded state of a crossbow (the charged projectiles component) is set on the server and synced to the client, so a false loaded model cannot appear on its own. The ghost players see is a crossbow that looks like it finished loading but never became charged on the server, so it fires nothing.

This does not change loading speed, does not auto load, and is not a cheat. It only adds a visual warning by recolouring the crossbow item itself:

- When you finish a load but the server does not confirm a charge within a short grace window, the crossbow item is tinted red.
- The red tint stays until that crossbow actually loads (becomes charged) or you swap it out.
- A normally unloaded crossbow that you have not tried to load is left untinted.
- Optional: tint a confirmed loaded crossbow green.
- Optional: by default only the hotbar item is tinted, with the held in world item left normal. You can turn on tinting the held item too.

## Config

A file is created at `config/fireghost.json` on first launch:

```json
{
  "flintEnabled": true,
  "debounceTicks": 4,
  "flintPreventGhost": true,
  "crossbowEnabled": true,
  "crossbowGreenWhenLoaded": false,
  "crossbowTintHeldItem": false
}
```

- `flintEnabled`: toggle the flint and steel fix.
- `debounceTicks`: minimum client ticks between ignites on the same spot (0 to 100). Raise it if you still see flicker on a high latency server, lower it toward 1 for a snappier feel.
- `flintPreventGhost`: remove the client side fire prediction so only the server confirmed fire is shown and nothing ghosts. On a high latency server the fire appears a little later because it waits for the server.
- `crossbowEnabled`: toggle the crossbow ghost detection and red tint.
- `crossbowGreenWhenLoaded`: tint a confirmed loaded crossbow green.
- `crossbowTintHeldItem`: also tint the held in world crossbow, not just the hotbar item.

## Mod Menu

Mod Menu is optional. When it is installed you can open the config screen from the mods list. The screen has two tabs:

- Fire Ghost: the flint and steel toggle, the debounce slider, and the prevent ghost fire toggle.
- Crossbow Ghost: the crossbow toggle, the green tint toggle, and the held item tint toggle.

Changes are saved to `config/fireghost.json` when you close the screen. Without Mod Menu the mod still works, you just edit the json file directly.

## Building

Requires JDK 21. Point `JAVA_HOME` at a 21 install, then:

```bash
./gradlew build
```

```bat
gradlew.bat build
```

The finished jar lands in `build/libs/fireghost-1.9.1.jar`. Drop it into your `mods` folder.
