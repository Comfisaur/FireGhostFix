package com.fireghost.mixin;

import com.fireghost.CrossbowGhostState;
import com.fireghost.FireGhostConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class CrossbowTintMixin {
	private static final int RED = 0x66FF0000;
	private static final int GREEN = 0x6600FF00;

	@Inject(
			method = "renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V",
			at = @At("TAIL")
	)
	private void fireghost$tintCrossbow(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
		FireGhostConfig cfg = FireGhostConfig.get();
		if (!cfg.crossbowEnabled || !stack.isOf(Items.CROSSBOW)) {
			return;
		}

		int color = 0;
		if (CrossbowGhostState.isGhostStack(player, stack)) {
			color = RED;
		} else if (cfg.crossbowGreenWhenLoaded && CrossbowItem.isCharged(stack)) {
			color = GREEN;
		}

		if (color != 0) {
			context.fill(x, y, x + 16, y + 16, color);
		}
	}
}
