package zmaster587.libVulpes.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import zmaster587.libVulpes.util.IconResource;

import javax.annotation.Nonnull;
import java.util.List;

public interface IModularArmor {
	
	void addArmorComponent(World world, @Nonnull ItemStack armor, @Nonnull ItemStack componentStack, int slot);

	@Nonnull
	ItemStack removeComponent(World world, @Nonnull ItemStack armor, int index);
	
	List<ItemStack> getComponents(@Nonnull ItemStack armor);

	@Nonnull
	ItemStack getComponentInSlot(@Nonnull ItemStack stack, int slot);
	
	//Returns a list of externally modifiable fluidtanks
	boolean canBeExternallyModified(@Nonnull ItemStack armor, int slot);
	
	int getNumSlots(@Nonnull ItemStack stack);
	
	IInventory loadModuleInventory(@Nonnull ItemStack stack);
	
	void saveModuleInventory(@Nonnull ItemStack stack, IInventory inv);
	
	//returns true if the stack is valid for the given slot
	boolean isItemValidForSlot(@Nonnull ItemStack stack, int slot);
	
	//Returns an IconResource to be displayed in the slot, if null default slot texture is used
	IconResource getResourceForSlot(int slot);
}
