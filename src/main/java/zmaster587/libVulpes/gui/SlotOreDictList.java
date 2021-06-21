package zmaster587.libVulpes.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.Set;

public class SlotOreDictList extends Slot {

	private Set<String> allowed;

	public SlotOreDictList(IInventory inv, int slot, int x, int y, Set<String> set) {
		super(inv, slot, x, y);
		allowed = set;
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack)
	{
		for(String acceptedNames : allowed) {
			int oreId = OreDictionary.getOreID(acceptedNames);
			if(oreId == -1)
				continue;

			for(int i : OreDictionary.getOreIDs(stack)) {
				if(i == oreId)
					return true;
			}
		}
		return false;
	}
}
