package com.fireghost.mixin;

import com.fireghost.CrossbowGhostState;
import com.fireghost.FireGhostConfig;
import com.fireghost.LayerTintApplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.HeldItemContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemModelManager.class)
public class ItemModelManagerTintMixin {
	@Inject(
			method = "clearAndUpdate(Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/world/World;Lnet/minecraft/util/HeldItemContext;I)V",
			at = @At("TAIL")
	)
	private void fireghost$tagCrossbowTint(ItemRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, World world, HeldItemContext heldItemContext, int seed, CallbackInfo ci) {
		int tint = 0;
		FireGhostConfig cfg = FireGhostConfig.get();
		if (cfg.crossbowEnabled && stack.isOf(Items.CROSSBOW)) {
			boolean isGui = displayContext == ItemDisplayContext.GUI;
			boolean isHeld = displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
					|| displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
					|| displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
					|| displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;

			if (isGui || (isHeld && cfg.crossbowTintHeldItem)) {
				PlayerEntity player = MinecraftClient.getInstance().player;
				if (player != null && CrossbowGhostState.isGhostStack(player, stack)) {
					tint = CrossbowGhostState.RED_TINT;
				} else if (cfg.crossbowGreenWhenLoaded && CrossbowItem.isCharged(stack)) {
					tint = CrossbowGhostState.GREEN_TINT;
				}
			}
		}

		((LayerTintApplier) (Object) renderState).fireghost$applyTint(tint);
	}
}
