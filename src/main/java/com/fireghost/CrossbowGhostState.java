package com.fireghost;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public final class CrossbowGhostState {
	public static final int RED_TINT = 0xFFFF4040;
	public static final int GREEN_TINT = 0xFF40FF40;

	private static final int GRACE_TICKS = 10;

	private static ItemStack ghostStack = null;
	private static ItemStack chargeStack = null;
	private static long chargeStartTick = -1;
	private static ItemStack pendingStack = null;
	private static long pendingSince = -1;

	private CrossbowGhostState() {
	}

	public static void reset() {
		ghostStack = null;
		chargeStack = null;
		chargeStartTick = -1;
		pendingStack = null;
		pendingSince = -1;
	}

	public static void tick(ClientPlayerEntity self, long now) {
		if (!FireGhostConfig.get().crossbowEnabled) {
			reset();
			return;
		}

		PlayerInventory inv = self.getInventory();
		boolean usingCrossbow = self.isUsingItem() && self.getActiveItem().isOf(Items.CROSSBOW);

		if (ghostStack != null && !fireghost$isLiveGhost(inv)) {
			ghostStack = null;
		}

		if (usingCrossbow) {
			ItemStack active = self.getActiveItem();
			if (chargeStartTick < 0) {
				chargeStartTick = now;
				chargeStack = active;
			}
			int pull = CrossbowItem.getPullTime(active, self);
			if (now - chargeStartTick > (long) pull + GRACE_TICKS && !CrossbowItem.isCharged(active)) {
				ghostStack = active;
			}
		} else {
			if (chargeStartTick >= 0) {
				if (chargeStack != null && chargeStack.isOf(Items.CROSSBOW) && !CrossbowItem.isCharged(chargeStack)) {
					int pull = CrossbowItem.getPullTime(chargeStack, self);
					if (now - chargeStartTick >= pull) {
						pendingStack = chargeStack;
						pendingSince = now;
					}
				}
				chargeStartTick = -1;
				chargeStack = null;
			}

			if (pendingStack != null) {
				if (!pendingStack.isOf(Items.CROSSBOW) || CrossbowItem.isCharged(pendingStack)) {
					pendingStack = null;
				} else if (now - pendingSince >= GRACE_TICKS) {
					ghostStack = pendingStack;
					pendingStack = null;
				}
			}
		}
	}

	private static boolean fireghost$isLiveGhost(PlayerInventory inv) {
		if (ghostStack == null || ghostStack.isEmpty() || !ghostStack.isOf(Items.CROSSBOW) || CrossbowItem.isCharged(ghostStack)) {
			return false;
		}
		for (int i = 0; i < inv.size(); i++) {
			if (inv.getStack(i) == ghostStack) {
				return true;
			}
		}
		return false;
	}

	public static boolean isGhostStack(ItemStack stack) {
		return ghostStack != null && stack == ghostStack;
	}
}
