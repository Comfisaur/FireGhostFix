package com.fireghost.mixin;

import com.fireghost.CrossbowTintHolder;
import com.fireghost.LayerTintApplier;
import net.minecraft.client.render.item.ItemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemRenderState.class)
public class ItemRenderStateTintMixin implements LayerTintApplier {
	@Shadow
	private ItemRenderState.LayerRenderState[] layers;

	@Shadow
	private int layerCount;

	@Override
	public void fireghost$applyTint(int color) {
		for (int i = 0; i < this.layerCount; i++) {
			((CrossbowTintHolder) (Object) this.layers[i]).fireghost$setTint(color);
		}
	}
}
