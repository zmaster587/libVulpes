package zmaster587.libVulpes.interfaces;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IRecipe {
	List<ItemStack> getOutput();
	
	List<FluidStack> getFluidOutputs();
	
	List<List<ItemStack>> getIngredients();
	
	List<FluidStack> getFluidIngredients();
	
	int getTime();
	
	int getPower();
	
	String getOreDictString(int slot);
}
