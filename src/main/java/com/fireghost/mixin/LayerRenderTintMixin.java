package com.fireghost.mixin;

import com.fireghost.TintContext;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(targets = "net.minecraft.client.render.item.ItemRenderState$LayerRenderState")
public abstract class LayerRenderTintMixin {
	@WrapOperation(
			method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;III)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitItem(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemDisplayContext;III[ILjava/util/List;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/item/ItemRenderState$Glint;)V"
			)
	)
	private void fireghost$tintItem(OrderedRenderCommandQueue queue, MatrixStack matrices, ItemDisplayContext context, int light, int overlay, int outlineColor, int[] tints, List<BakedQuad> quads, RenderLayer renderLayer, ItemRenderState.Glint glint, Operation<Void> original) {
		int tint = TintContext.active;
		if (tint != 0 && !quads.isEmpty()) {
			List<BakedQuad> recoloured = new ArrayList<>(quads.size());
			for (BakedQuad quad : quads) {
				recoloured.add(new BakedQuad(
						quad.position0(), quad.position1(), quad.position2(), quad.position3(),
						quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
						0, quad.face(), quad.sprite(), quad.shade(), quad.lightEmission()));
			}
			original.call(queue, matrices, context, light, overlay, outlineColor, new int[]{tint}, recoloured, renderLayer, glint);
		} else {
			original.call(queue, matrices, context, light, overlay, outlineColor, tints, quads, renderLayer, glint);
		}
	}
}
