package com.fireghost;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class FireGhostConfigScreen extends Screen {
	private static final int MAX_TICKS = 100;
	private static final int MAX_SWAPS = 9;

	private final Screen parent;
	private final FireGhostConfig config;
	private int tab = 0;

	public FireGhostConfigScreen(Screen parent) {
		super(Text.literal("FireGhostFix"));
		this.parent = parent;
		this.config = FireGhostConfig.get();
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;

		ButtonWidget fireTab = ButtonWidget.builder(Text.literal("Fire Ghost"), b -> selectTab(0))
				.dimensions(centerX - 102, 28, 100, 20).build();
		ButtonWidget crossbowTab = ButtonWidget.builder(Text.literal("Crossbow Ghost"), b -> selectTab(1))
				.dimensions(centerX + 2, 28, 100, 20).build();
		fireTab.active = tab != 0;
		crossbowTab.active = tab != 1;
		this.addDrawableChild(fireTab);
		this.addDrawableChild(crossbowTab);

		int y = 70;
		if (tab == 0) {
			this.addDrawableChild(ButtonWidget.builder(flintText(), b -> {
				config.flintEnabled = !config.flintEnabled;
				b.setMessage(flintText());
			}).dimensions(centerX - 100, y, 200, 20).build());

			this.addDrawableChild(new DebounceSlider(centerX - 100, y + 24, 200, 20));
			this.addDrawableChild(new SwapThresholdSlider(centerX - 100, y + 48, 200, 20));
		} else {
			this.addDrawableChild(ButtonWidget.builder(crossbowText(), b -> {
				config.crossbowEnabled = !config.crossbowEnabled;
				b.setMessage(crossbowText());
			}).dimensions(centerX - 100, y, 200, 20).build());

			this.addDrawableChild(ButtonWidget.builder(greenText(), b -> {
				config.crossbowGreenWhenLoaded = !config.crossbowGreenWhenLoaded;
				b.setMessage(greenText());
			}).dimensions(centerX - 100, y + 24, 200, 20).build());

			this.addDrawableChild(ButtonWidget.builder(heldText(), b -> {
				config.crossbowTintHeldItem = !config.crossbowTintHeldItem;
				b.setMessage(heldText());
			}).dimensions(centerX - 100, y + 48, 200, 20).build());
		}

		this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> this.close())
				.dimensions(centerX - 100, this.height - 32, 200, 20).build());
	}

	private void selectTab(int t) {
		this.tab = t;
		this.clearAndInit();
	}

	private Text flintText() {
		return Text.literal("Flint and Steel fix: " + onOff(config.flintEnabled));
	}

	private Text crossbowText() {
		return Text.literal("Crossbow ghost fix: " + onOff(config.crossbowEnabled));
	}

	private Text greenText() {
		return Text.literal("Green tint when loaded: " + onOff(config.crossbowGreenWhenLoaded));
	}

	private Text heldText() {
		return Text.literal("Tint held crossbow: " + onOff(config.crossbowTintHeldItem));
	}

	private static String onOff(boolean value) {
		return value ? "ON" : "OFF";
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, 0xFFFFFF);
	}

	@Override
	public void close() {
		config.save();
		if (this.client != null) {
			this.client.setScreen(parent);
		}
	}

	private class DebounceSlider extends SliderWidget {
		DebounceSlider(int x, int y, int width, int height) {
			super(x, y, width, height, Text.empty(), (double) config.debounceTicks / MAX_TICKS);
			this.updateMessage();
		}

		@Override
		protected void updateMessage() {
			this.setMessage(Text.literal("Debounce: " + config.debounceTicks + " ticks"));
		}

		@Override
		protected void applyValue() {
			config.debounceTicks = (int) Math.round(this.value * MAX_TICKS);
		}
	}

	private class SwapThresholdSlider extends SliderWidget {
		SwapThresholdSlider(int x, int y, int width, int height) {
			super(x, y, width, height, Text.empty(), (double) config.flintSwapDelayThreshold / MAX_SWAPS);
			this.updateMessage();
		}

		@Override
		protected void updateMessage() {
			this.setMessage(Text.literal("Swap delay above: " + config.flintSwapDelayThreshold + " swaps/tick"));
		}

		@Override
		protected void applyValue() {
			config.flintSwapDelayThreshold = (int) Math.round(this.value * MAX_SWAPS);
		}
	}
}
