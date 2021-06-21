package zmaster587.libVulpes.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;

public class SlotOreDict extends Slot {
	
	String acceptedNames;
	
	public SlotOreDict(IInventory par1iInventory, int par2, int par3, int par4, String allowedNames) {
		super(par1iInventory, par2, par3, par4);
		acceptedNames = allowedNames;
	}
	
	@Override
	public boolean isItemValid(@Nonnull ItemStack stack)
	{
		int oreId = OreDictionary.getOreID(acceptedNames);
		if(oreId == -1)
			return false;
		
		for(int i : OreDictionary.getOreIDs(stack)) {
			if(i == oreId)
				return true;
		}
		
		return false;
	}
}
