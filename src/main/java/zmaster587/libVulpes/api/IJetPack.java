package zmaster587.libVulpes.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IJetPack {
	boolean isActive(@Nonnull ItemStack stack, EntityPlayer player);
	
	boolean isEnabled(@Nonnull ItemStack stack);
	
	void setEnabledState(@Nonnull ItemStack stack, boolean state);
	
	void onAccelerate(@Nonnull ItemStack stack, IInventory inv, EntityPlayer player);
	
	void changeMode(@Nonnull ItemStack stack, IInventory modules, EntityPlayer player);
}
