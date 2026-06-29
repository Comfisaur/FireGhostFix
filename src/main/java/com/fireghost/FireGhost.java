package com.fireghost;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FireGhost implements ClientModInitializer {
	public static final String MOD_ID = "fireghost";
	public static final Logger LOGGER = LoggerFactory.getLogger("FireGhostFix");

	@Override
	public void onInitializeClient() {
		FireGhostConfig cfg = FireGhostConfig.get();
		LOGGER.info("[FireGhostFix] active, enabled={}, debounce={} ticks", cfg.enabled, cfg.debounceTicks);
	}
}
