package com.fireghost;

import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public final class FlintSwapState {
	private static long clientTick = 0;
	private static long swapTick = -1;
	private static int swapCount = 0;
	private static long lastSwapMillis = Long.MIN_VALUE;

	private static Hand pendingHand = null;
	private static BlockHitResult pendingHit = null;
	private static int pendingSlot = -1;
	private static boolean replaying = false;
	private static int pendingSwapSlot = -1;
	private static long swapDeferredTick = 0;
	private static boolean placementSinceDefer = false;

	private FlintSwapState() {
	}

	public static void nextTick() {
		clientTick++;
	}

	public static void onSlotChanged() {
		if (swapTick != clientTick) {
			swapTick = clientTick;
			swapCount = 0;
		}
		swapCount++;
		lastSwapMillis = System.currentTimeMillis();
	}

	public static int swapsThisTick() {
		return swapTick == clientTick ? swapCount : 0;
	}

	public static boolean swappedWithinMillis(long windowMillis) {
		return System.currentTimeMillis() - lastSwapMillis <= windowMillis;
	}

	public static void setPending(Hand hand, BlockHitResult hit, int slot) {
		pendingHand = hand;
		pendingHit = hit;
		pendingSlot = slot;
	}

	public static boolean hasPending() {
		return pendingHand != null && pendingHit != null;
	}

	public static Hand pendingHand() {
		return pendingHand;
	}

	public static BlockHitResult pendingHit() {
		return pendingHit;
	}

	public static int pendingSlot() {
		return pendingSlot;
	}

	public static void clearPending() {
		pendingHand = null;
		pendingHit = null;
		pendingSlot = -1;
	}

	public static boolean isReplaying() {
		return replaying;
	}

	public static void setReplaying(boolean value) {
		replaying = value;
	}

	public static void setPendingSwap(int slot) {
		pendingSwapSlot = slot;
		swapDeferredTick = clientTick;
		placementSinceDefer = false;
	}

	public static boolean hasPendingSwap() {
		return pendingSwapSlot >= 0;
	}

	public static int pendingSwapSlot() {
		return pendingSwapSlot;
	}

	public static void clearPendingSwap() {
		pendingSwapSlot = -1;
		placementSinceDefer = false;
	}

	public static void markPlacement() {
		if (pendingSwapSlot >= 0) {
			placementSinceDefer = true;
		}
	}

	public static boolean shouldApplyPendingSwap(long maxHoldTicks) {
		if (pendingSwapSlot < 0) {
			return false;
		}
		return placementSinceDefer || clientTick - swapDeferredTick >= maxHoldTicks;
	}
}
