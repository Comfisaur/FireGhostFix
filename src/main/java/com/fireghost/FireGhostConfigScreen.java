package com.fireghost;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class FireGhostConfigScreen extends Screen {
	private static final int MAX_TICKS = 100;

	private final Screen parent;
	private final FireGhostConfig config;

	public FireGhostConfigScreen(Screen parent) {
		super(Text.literal("FireGhostFix"));
		this.parent = parent;
		this.config = FireGhostConfig.get();
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int centerY = this.height / 2;

		this.addDrawableChild(ButtonWidget.builder(toggleText(), button -> {
			config.enabled = !config.enabled;
			button.setMessage(toggleText());
		}).dimensions(centerX - 100, centerY - 24, 200, 20).build());

		this.addDrawableChild(new DebounceSlider(centerX - 100, centerY, 200, 20));

		this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> this.close())
				.dimensions(centerX - 100, centerY + 24, 200, 20).build());
	}

	private Text toggleText() {
		return Text.literal("Fix: " + (config.enabled ? "Enabled" : "Disabled"));
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 52, 0xFFFFFF);
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
}
