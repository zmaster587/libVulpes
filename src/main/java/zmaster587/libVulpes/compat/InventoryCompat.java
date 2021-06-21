package zmaster587.libVulpes.compat;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;

public class InventoryCompat {

	static boolean buildCraft_injectable;
	
	public static void initCompat() {
		try {
			Class.forName("buildcraft.api.transport.IInjectable");
			buildCraft_injectable =  true;
		} catch (ClassNotFoundException e) {
			buildCraft_injectable = false;
		}
		
		buildCraft_injectable = false;
	}
	
	public static boolean canInjectItems(TileEntity tile) {
		
		if(buildCraft_injectable) {
			return true;
		}
		return tile instanceof IInventory && ZUtils.numEmptySlots((IInventory) tile) > 0;
	}
	
	public static boolean canInjectItems(TileEntity tile, @Nonnull ItemStack item) {
		
		if(buildCraft_injectable) {
			return true;
		}
		return tile instanceof IInventory && (ZUtils.numEmptySlots((IInventory) tile) > 0 || ZUtils.doesInvHaveRoom(item, (IInventory) tile));
	}
	
	public static boolean canInjectItems(IInventory tile, @Nonnull ItemStack item) {
		
		if(buildCraft_injectable) {
			return true;
		}
		return tile != null && (ZUtils.numEmptySlots(tile) > 0 || ZUtils.doesInvHaveRoom(item, tile));
	}
	
	public static void injectItem(Object tile, @Nonnull ItemStack item) {
		if(buildCraft_injectable) {}
			
		ZUtils.mergeInventory(item, (IInventory)tile);
		
	}
}
