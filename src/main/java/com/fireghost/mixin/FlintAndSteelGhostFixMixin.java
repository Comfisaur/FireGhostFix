package com.fireghost.mixin;

import com.fireghost.FireGhostConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class FlintAndSteelGhostFixMixin {
	private BlockPos fireghost$lastFirePos = null;
	private Direction fireghost$lastSide = null;
	private long fireghost$lastUseTick = Long.MIN_VALUE;

	@Inject(
			method = "interactBlock(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;",
			at = @At("HEAD"),
			cancellable = true
	)
	private void fireghost$debounceFlintAndSteel(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
		FireGhostConfig cfg = FireGhostConfig.get();
		if (!cfg.flintEnabled || player == null || hitResult == null) {
			return;
		}
		if (!player.getStackInHand(hand).isOf(Items.FLINT_AND_STEEL)) {
			return;
		}

		ClientWorld world = MinecraftClient.getInstance().world;
		if (world == null) {
			return;
		}
		long now = world.getTime();

		Direction side = hitResult.getSide();
		BlockPos firePos = hitResult.getBlockPos().offset(side);

		boolean sameTarget = firePos.equals(fireghost$lastFirePos) && side == fireghost$lastSide;
		boolean withinWindow = now >= fireghost$lastUseTick && (now - fireghost$lastUseTick) < cfg.debounceTicks;

		if (sameTarget && withinWindow) {
			cir.setReturnValue(ActionResult.FAIL);
			return;
		}

		fireghost$lastFirePos = firePos;
		fireghost$lastSide = side;
		fireghost$lastUseTick = now;
	}
}
