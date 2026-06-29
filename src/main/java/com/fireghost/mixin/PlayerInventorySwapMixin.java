package com.fireghost.mixin;

import com.fireghost.FireGhostConfig;
import com.fireghost.FlintSwapState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventorySwapMixin {
	@Inject(method = "setSelectedSlot(I)V", at = @At("HEAD"))
	private void fireghost$countSwap(int slot, CallbackInfo ci) {
		if (!FireGhostConfig.get().flintEnabled || FlintSwapState.isReplaying()) {
			return;
		}
		PlayerInventory self = (PlayerInventory) (Object) this;
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || self.player != client.player) {
			return;
		}
		if (slot != self.getSelectedSlot()) {
			FlintSwapState.onSlotChanged();
		}
	}
}
