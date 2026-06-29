# FireGhostFix

A client side Fabric mod that fixes two desync ghosts: flint and steel ghost fire when you place too fast, and crossbow loading ghosts where the client thinks a crossbow is loaded but the server does not.

- Minecraft: 1.21.11
- Loader: Fabric (fabricloader >= 0.19.0)
- Java: 21
- Side: client only, works on any server including vanilla

## Flint and steel ghost fire

Every right click runs the client's `interactBlock`, which predicts a fire block on your screen and sends an interact packet to the server. When you spam click faster than the server can acknowledge, multiple predictions pile up on the same spot. The client guess and the server state diverge and the predicted fire flickers.

A client Mixin debounces repeated ignites on the same block face within a short window (default 4 ticks, about 0.2 seconds):

- The first click of a burst goes through normally.
- Rapid repeats on the same spot are dropped before any prediction or packet is generated.
- Aiming at a new block resets the debounce instantly, so normal placement is unaffected.

There is a second cause tied to hotbar swap lockouts. Swapping to more than a couple of items in the same tick can cancel the input, so an ignite sent the moment you swap to the flint and steel lands on the wrong item and ghosts. When the number of hotbar swaps in a single tick goes over a threshold (default 3), the ignite is held back and sent on the next tick instead, after the slot has synced. The same hold also kicks in when an ignite lands within a few milliseconds of a swap (default 5 ms), which catches clicking at the exact moment you swap to the flint and steel. Because cross tick gaps are about 50 ms, this only triggers on a true simultaneous swap and click, so normal play where you swap and then click a moment later is not affected.

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
  "flintSwapDelayThreshold": 3,
  "flintSwapWindowMillis": 5,
  "crossbowEnabled": true,
  "crossbowGreenWhenLoaded": false,
  "crossbowTintHeldItem": false
}
```

- `flintEnabled`: toggle the flint and steel fix.
- `debounceTicks`: minimum client ticks between ignites on the same spot (0 to 100). Raise it if you still see flicker on a high latency server, lower it toward 1 for a snappier feel.
- `flintSwapDelayThreshold`: hold the ignite to the next tick when hotbar swaps in one tick go above this number (0 to 9). Default 3. Set to 0 to delay after any swap in the same tick, set high to effectively turn this off.
- `flintSwapWindowMillis`: also hold the ignite to the next tick when it lands within this many milliseconds of a swap (0 to 1000). Default 5. Set to 0 to turn this off.
- `crossbowEnabled`: toggle the crossbow ghost detection and red tint.
- `crossbowGreenWhenLoaded`: tint a confirmed loaded crossbow green.
- `crossbowTintHeldItem`: also tint the held in world crossbow, not just the hotbar item.

## Mod Menu

Mod Menu is optional. When it is installed you can open the config screen from the mods list. The screen has two tabs:

- Fire Ghost: the flint and steel toggle and the debounce slider.
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

The finished jar lands in `build/libs/fireghost-1.6.0.jar`. Drop it into your `mods` folder.
