package zmaster587.libVulpes.inventory.modules;

import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public interface IModularInventory {
	
	/**
	 * @return a list of modules to add to the inventory
	 */
	List<ModuleBase> getModules(int id, EntityPlayer player);
	
	String getModularInventoryName();
	
	boolean canInteractWithContainer(EntityPlayer entity);
	
}
