package zmaster587.libVulpes.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Set;

public class SlotListAllowed extends Slot {

	Set<ItemStack> allowedItems;
	
	public SlotListAllowed(IInventory inventory, int slotIndex, int x, int y, Set<ItemStack> allowedItems) {
		super(inventory, slotIndex, x, y);
		this.allowedItems = allowedItems;
	}
	
	@Override
	public boolean isItemValid(@Nonnull ItemStack stack)
	{
		for(ItemStack itemStack : allowedItems){
			if(itemStack.isItemEqual(stack))
				return true;
		}
		return false;
	}
}
