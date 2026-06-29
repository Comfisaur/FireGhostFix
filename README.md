# FireGhostFix

A client side Fabric mod that fixes flint and steel ghost fire, the fire that flickers in and out when you right click flint and steel too fast.

- Minecraft: 1.21.11
- Loader: Fabric (fabricloader >= 0.19.0)
- Java: 21
- Side: client only, works on any server including vanilla

## What causes the ghosting

Every right click runs the client's `interactBlock`, which predicts a fire block on your screen and sends an interact packet to the server. When you spam click faster than the server can acknowledge, multiple predictions pile up on the same spot. The client guess and the server state diverge and the predicted fire flickers.

## The fix

A single client Mixin debounces repeated ignites on the same block face within a short window (default 4 ticks, about 0.2 seconds):

- The first click of a burst goes through normally.
- Rapid repeats on the same spot are dropped before any prediction or packet is generated.
- Aiming at a new block resets the debounce instantly, so normal placement is unaffected.

## Config

A file is created at `config/fireghost.json` on first launch:

```json
{
  "enabled": true,
  "debounceTicks": 4
}
```

- `enabled`: master toggle.
- `debounceTicks`: minimum client ticks between ignites on the same spot (0 to 100). Raise it if you still see flicker on a high latency server, lower it toward 1 for a snappier feel.

## Building

Requires JDK 21. Point `JAVA_HOME` at a 21 install, then:

```bash
./gradlew build
```

```bat
gradlew.bat build
```

The finished jar lands in `build/libs/fireghost-1.1.0.jar`. Drop it into your `mods` folder.
