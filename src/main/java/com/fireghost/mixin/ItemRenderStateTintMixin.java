package com.fireghost.mixin;

import com.fireghost.CrossbowTintHolder;
import com.fireghost.TintContext;
import net.minecraft.client.render.item.ItemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderState.class)
public class ItemRenderStateTintMixin implements CrossbowTintHolder {
	@Unique
	private int fireghost$tint = 0;

	@Override
	public int fireghost$getTint() {
		return this.fireghost$tint;
	}

	@Override
	public void fireghost$setTint(int color) {
		this.fireghost$tint = color;
	}

	@Inject(method = "clear()V", at = @At("TAIL"))
	private void fireghost$resetTint(CallbackInfo ci) {
		this.fireghost$tint = 0;
	}

	@Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;III)V", at = @At("HEAD"))
	private void fireghost$beginTint(CallbackInfo ci) {
		TintContext.active = this.fireghost$tint;
	}

	@Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;III)V", at = @At("TAIL"))
	private void fireghost$endTint(CallbackInfo ci) {
		TintContext.active = 0;
	}
}
