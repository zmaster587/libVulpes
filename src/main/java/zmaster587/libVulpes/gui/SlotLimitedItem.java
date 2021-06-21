package zmaster587.libVulpes.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotLimitedItem extends Slot {
	
	IInventory tile;
	
	public SlotLimitedItem(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
		this.tile = inventory;
	}
	
	@Override
	public boolean isItemValid(@Nonnull ItemStack stack)
	{
		return tile.isItemValidForSlot(this.slotNumber, stack);
	}
}
