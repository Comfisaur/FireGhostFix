package com.fireghost.mixin;

import com.fireghost.FireGhostConfig;
import com.fireghost.FlintSwapState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventorySwapMixin {
	@Inject(method = "setSelectedSlot(I)V", at = @At("HEAD"), cancellable = true)
	private void fireghost$countSwap(int slot, CallbackInfo ci) {
		if (!FireGhostConfig.get().flintEnabled || FlintSwapState.isReplaying()) {
			return;
		}
		PlayerInventory self = (PlayerInventory) (Object) this;
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || self.player != client.player) {
			return;
		}
		int current = self.getSelectedSlot();
		if (slot == current) {
			return;
		}

		if (self.getSelectedStack().isOf(Items.FLINT_AND_STEEL) && client.options.useKey.isPressed()) {
			FlintSwapState.setPendingSwap(slot);
			ci.cancel();
			return;
		}

		FlintSwapState.onSlotChanged();
	}
}
