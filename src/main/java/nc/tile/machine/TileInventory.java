package nc.tile.machine;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class TileInventory extends TileEntity implements ISidedInventory {
	public String localizedName;
	public ItemStack[] slots;
	private int lastComparatorOutput = -1;

	public void setGuiDisplayName(String displayName) {
		localizedName = displayName;
	}

	public boolean isInventoryNameLocalized() {
		return localizedName != null && localizedName.length() > 0;
	}
	
	public String getInventoryName() {
		return isInventoryNameLocalized() ? localizedName : "NCTileEntity";
	}

	public int getSizeInventory() {
		return slots.length;
	}

	public ItemStack getStackInSlot(int var1) {
		return slots[var1];
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a new stack.
	 */
	public ItemStack decrStackSize(int var1, int var2) {
		if (slots[var1] != null) {
			ItemStack itemstack;
			if (slots[var1].stackSize <= var2) {
				itemstack = slots[var1];
				slots[var1] = null;
				return itemstack;
			} else {
				itemstack = slots[var1].splitStack(var2);
				if (slots[var1].stackSize == 0) {
					slots[var1] = null;
				}
				return itemstack;
			}
		} else {
			return null;
		}
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem - like when you close a workbench GUI.
	 */
	public ItemStack getStackInSlotOnClosing(int i) {
		if (slots[i] != null) {
			ItemStack itemstack = slots[i];
			slots[i] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		slots[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public boolean isUseableByPlayer(EntityPlayer entityplayer)  {
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this;
	}

	public void openInventory() {}

	public void closeInventory() {}

	public boolean hasCustomInventoryName() {
		return false;
	}

	/**
	 * Subclasses call this every tick. The full markDirty also notifies all 6 neighbours (and reads blocks 2 away,
	 * loading chunks across borders), which only matters for comparators, so only do that when the output changed.
	 */
	public void markDirty() {
		// The comparator code dereferences the tile at this position, which is gone e.g. after a meltdown
		if (worldObj != null && worldObj.getTileEntity(xCoord, yCoord, zCoord) == this) {
			Block block = getBlockType();
			int output = block.hasComparatorInputOverride() ? block.getComparatorInputOverride(worldObj, xCoord, yCoord, zCoord, 0) : 0;
			if (output == lastComparatorOutput) {
				worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, this);
				return;
			}
			lastComparatorOutput = output;
		}
		super.markDirty();
	}
}
