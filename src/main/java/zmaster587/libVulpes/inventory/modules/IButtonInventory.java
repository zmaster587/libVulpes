package zmaster587.libVulpes.inventory.modules;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IButtonInventory {

	/**
	 * Called on the client when a user presses a button
	 * @param buttonId id of the button pressed
	 */
	@SideOnly(Side.CLIENT)
	void onInventoryButtonPressed(int buttonId);
	
}
