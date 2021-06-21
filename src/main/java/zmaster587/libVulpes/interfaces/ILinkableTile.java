package zmaster587.libVulpes.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public interface ILinkableTile {
	
	/**
	 * Called when the linker has no coords set and user rightclicks an ILinkableTile
	 * @param item Itemstack (the ItemLinker Object more likely than not)
	 * @param entity TileEntity at the coords being rightclicked
	 * @param player the player
	 * @param world the world
	 * @return true If link is allowed
	 */
	boolean onLinkStart(@Nonnull ItemStack item, TileEntity entity, EntityPlayer player, World world);
	
	boolean onLinkComplete(@Nonnull ItemStack item, TileEntity entity, EntityPlayer player, World world);
}
