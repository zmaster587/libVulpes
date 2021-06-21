package zmaster587.libVulpes.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileInventoriedPointer extends TilePointer implements IInventoryMultiblock, ISidedInventory {

	@Override
	public int getSizeInventory() {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			return ((IInventory)e).getSizeInventory();
		return 0;
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int i) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			return ((IInventory)e).getStackInSlot(i);
		return ItemStack.EMPTY;
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int i, int j) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			return ((IInventory)e).decrStackSize(i,j);
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int i, @Nonnull ItemStack itemstack) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			 ((IInventory)e).setInventorySlotContents(i,itemstack);
	}

	@Override
	@Nullable
	public String getName() {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			return ((IInventory)e).getName();
		return null;
	}

	@Override
	public boolean hasCustomName() {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			return ((IInventory)e).hasCustomName();
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			return ((IInventory)e).getInventoryStackLimit();
		return 0;
	}
	
	@Override
	public boolean isUsableByPlayer(@Nonnull EntityPlayer entityplayer) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			return ((IInventory)e).isUsableByPlayer(entityplayer);
		return false;
	}

	@Override
	public boolean isEmpty() {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			return ((IInventory)e).isEmpty();
		return true;
	}
	
	@Override
	public void openInventory(EntityPlayer player) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			((IInventory)e).openInventory(player);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			((IInventory)e).closeInventory(player);
	}

	@Override
	public boolean isItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof IInventory)
			return ((IInventory)e).isItemValidForSlot(i,itemstack);
		return false;
	}
	
	@Override
	@Nonnull
	public int[] getSlotsForFace(@Nonnull EnumFacing side) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e != null && e instanceof ISidedInventory)
			return ((ISidedInventory)e).getSlotsForFace(side);
		else
			return new int[] {};
	}
	
	@Override
	public boolean canInsertItem(int i, @Nonnull ItemStack itemstack, EnumFacing direction) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof ISidedInventory)
			return ((ISidedInventory)e).canInsertItem(i,itemstack, direction);
		return true;
	}

	@Override
	public boolean canExtractItem(int i, @Nonnull ItemStack itemstack, EnumFacing direction) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof ISidedInventory)
			return ((ISidedInventory)e).canExtractItem(i,itemstack, direction);
		
		return true;
	}

	@Override
	@Nonnull
	public ItemStack removeStackFromSlot(int index) {
		TileEntity e = world.getTileEntity(masterBlockPos);
		if(e instanceof ISidedInventory)
			return ((ISidedInventory)e).removeStackFromSlot(index);
		
		return ItemStack.EMPTY;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		
	}
}
