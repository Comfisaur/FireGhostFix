package com.fireghost;

import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public final class FlintSwapState {
	private static long clientTick = 0;
	private static long swapTick = -1;
	private static int swapCount = 0;

	private static Hand pendingHand = null;
	private static BlockHitResult pendingHit = null;

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
	}

	public static int swapsThisTick() {
		return swapTick == clientTick ? swapCount : 0;
	}

	public static void setPending(Hand hand, BlockHitResult hit) {
		pendingHand = hand;
		pendingHit = hit;
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

	public static void clearPending() {
		pendingHand = null;
		pendingHit = null;
	}
}
