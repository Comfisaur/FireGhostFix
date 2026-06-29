package com.fireghost;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public final class CrossbowGhostState {
	private static final int GRACE_TICKS = 10;

	private static int ghostSlot = -1;
	private static long chargeStartTick = -1;
	private static int chargeSlot = -1;
	private static long pendingSince = -1;
	private static int pendingSlot = -1;

	private CrossbowGhostState() {
	}

	public static void reset() {
		ghostSlot = -1;
		chargeStartTick = -1;
		chargeSlot = -1;
		pendingSince = -1;
		pendingSlot = -1;
	}

	public static void tick(ClientPlayerEntity self, long now) {
		if (!FireGhostConfig.get().crossbowEnabled) {
			reset();
			return;
		}

		PlayerInventory inv = self.getInventory();
		int selected = inv.getSelectedSlot();
		ItemStack main = self.getMainHandStack();
		boolean usingCrossbow = self.isUsingItem() && self.getActiveItem().isOf(Items.CROSSBOW);

		if (ghostSlot >= 0) {
			ItemStack flagged = inv.getStack(ghostSlot);
			if (!flagged.isOf(Items.CROSSBOW) || CrossbowItem.isCharged(flagged)) {
				ghostSlot = -1;
			}
		}

		if (usingCrossbow) {
			if (chargeStartTick < 0) {
				chargeStartTick = now;
				chargeSlot = selected;
			}
			int pull = CrossbowItem.getPullTime(self.getActiveItem(), self);
			if (now - chargeStartTick > (long) pull + GRACE_TICKS && !CrossbowItem.isCharged(main)) {
				ghostSlot = chargeSlot;
			}
		} else {
			if (chargeStartTick >= 0) {
				ItemStack slotStack = inv.getStack(chargeSlot);
				int pull = CrossbowItem.getPullTime(slotStack, self);
				long held = now - chargeStartTick;
				if (held >= pull && slotStack.isOf(Items.CROSSBOW) && !CrossbowItem.isCharged(slotStack)) {
					pendingSlot = chargeSlot;
					pendingSince = now;
				}
				chargeStartTick = -1;
				chargeSlot = -1;
			}

			if (pendingSlot >= 0) {
				ItemStack slotStack = inv.getStack(pendingSlot);
				if (!slotStack.isOf(Items.CROSSBOW) || CrossbowItem.isCharged(slotStack)) {
					pendingSlot = -1;
				} else if (now - pendingSince >= GRACE_TICKS) {
					ghostSlot = pendingSlot;
					pendingSlot = -1;
				}
			}
		}
	}

	public static boolean isGhostStack(PlayerEntity player, ItemStack stack) {
		if (ghostSlot < 0) {
			return false;
		}
		return stack == player.getInventory().getStack(ghostSlot);
	}
}
