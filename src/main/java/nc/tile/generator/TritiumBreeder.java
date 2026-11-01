package nc.tile.generator;

import nc.NuclearCraft;
import nc.item.NCItems;
import net.minecraft.item.ItemStack;

/**
 * Tritium breeding in fission reactors (He-3 + n -> T + p): while a reactor runs, He-3 cells in its breeding slot turn
 * into tritium cells, one per fissionTritiumBreeding of reactor output (RF, or steam for steam reactors).
 */
public final class TritiumBreeder {

	/** Breeding input (He-3 cells) and output (tritium cells) slots of the fission controllers. */
	public static final int IN = 2, OUT = 3;
	private static final int TRITIUM = 38, HE3 = 39;

	private TritiumBreeder() {}

	/** Adds this tick's reactor output to the progress and breeds a cell when it's full; returns the new progress. */
	public static int breed(ItemStack[] slots, int progress, double output) {
		int cost = NuclearCraft.fissionTritiumBreeding;
		if (cost <= 0 || output <= 0 || !isHe3(slots[IN]) || !hasRoom(slots[OUT])) return progress;
		long next = progress + (long) output;
		if (next < cost) return (int) next;
		if (--slots[IN].stackSize <= 0) slots[IN] = null;
		if (slots[OUT] == null) slots[OUT] = new ItemStack(NCItems.fuel, 1, TRITIUM);
		else slots[OUT].stackSize++;
		// At most one cell per tick, and no more than one cell's worth carried over
		return (int) Math.min(next - cost, cost);
	}

	/** Progress towards the next cell, 0-100. */
	public static int percent(int progress) {
		int cost = NuclearCraft.fissionTritiumBreeding;
		return cost <= 0 ? 0 : (int) Math.min(100, 100L * progress / cost);
	}

	public static boolean isHe3(ItemStack stack) {
		return stack != null && stack.getItem() == NCItems.fuel && stack.getItemDamage() == HE3;
	}

	private static boolean hasRoom(ItemStack stack) {
		return stack == null || stack.getItem() == NCItems.fuel && stack.getItemDamage() == TRITIUM
			&& stack.stackSize < stack.getMaxStackSize();
	}
}
