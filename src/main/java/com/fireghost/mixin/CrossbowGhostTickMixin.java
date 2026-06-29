package com.fireghost.mixin;

import com.fireghost.CrossbowGhostState;
import com.fireghost.FireGhostConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class CrossbowGhostTickMixin {
	@Inject(method = "tick()V", at = @At("TAIL"))
	private void fireghost$trackCrossbowGhost(CallbackInfo ci) {
		if (!FireGhostConfig.get().crossbowEnabled) {
			return;
		}
		ClientWorld world = MinecraftClient.getInstance().world;
		if (world == null) {
			return;
		}
		CrossbowGhostState.tick((ClientPlayerEntity) (Object) this, world.getTime());
	}
}
