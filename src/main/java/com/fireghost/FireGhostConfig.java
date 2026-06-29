package com.fireghost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FireGhostConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static FireGhostConfig instance;

	public boolean flintEnabled = true;
	public int debounceTicks = 4;
	public int flintSwapDelayThreshold = 3;
	public long flintSwapWindowMillis = 5;
	public boolean crossbowEnabled = true;
	public boolean crossbowGreenWhenLoaded = false;
	public boolean crossbowTintHeldItem = false;

	public static FireGhostConfig get() {
		if (instance == null) {
			instance = load();
		}
		return instance;
	}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("fireghost.json");
	}

	private static FireGhostConfig load() {
		Path p = path();
		FireGhostConfig cfg;
		try {
			if (Files.exists(p)) {
				cfg = GSON.fromJson(Files.readString(p), FireGhostConfig.class);
				if (cfg == null) {
					cfg = new FireGhostConfig();
				}
			} else {
				cfg = new FireGhostConfig();
			}
		} catch (IOException | RuntimeException e) {
			FireGhost.LOGGER.warn("[FireGhostFix] Could not read config, using defaults", e);
			cfg = new FireGhostConfig();
		}
		cfg.clamp();
		cfg.save();
		return cfg;
	}

	public void save() {
		try {
			Files.writeString(path(), GSON.toJson(this));
		} catch (IOException e) {
			FireGhost.LOGGER.warn("[FireGhostFix] Could not write config", e);
		}
	}

	private void clamp() {
		if (debounceTicks < 0) {
			debounceTicks = 0;
		}
		if (debounceTicks > 100) {
			debounceTicks = 100;
		}
		if (flintSwapDelayThreshold < 0) {
			flintSwapDelayThreshold = 0;
		}
		if (flintSwapDelayThreshold > 9) {
			flintSwapDelayThreshold = 9;
		}
		if (flintSwapWindowMillis < 0) {
			flintSwapWindowMillis = 0;
		}
		if (flintSwapWindowMillis > 1000) {
			flintSwapWindowMillis = 1000;
		}
	}
}
