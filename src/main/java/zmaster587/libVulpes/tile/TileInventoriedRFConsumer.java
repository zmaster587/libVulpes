package zmaster587.libVulpes.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import zmaster587.libVulpes.util.EmbeddedInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileInventoriedRFConsumer extends TileEntityRFConsumer implements ISidedInventory {

	protected EmbeddedInventory inventory;
	
	protected TileInventoriedRFConsumer(int energy,int invSize) {
		super(energy);
		inventory = new EmbeddedInventory(invSize);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		inventory.writeToNBT(nbt);
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		inventory.readFromNBT(nbt);
	}
	
	
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int slot, int amt) {
		return inventory.decrStackSize(slot, amt);
	}


	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}

	
	@Override
	public boolean hasCustomName() {
		return false;
	}
	
	@Override
	@Nullable
	public String getName() {
		return null;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return player.getDistanceSq(this.pos) < 64;
	}
	@Override
	public void openInventory(EntityPlayer player) {
		inventory.openInventory(player);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		inventory.closeInventory(player);
	}
	
	@Override
	public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn,
			EnumFacing direction) {
		return inventory.canInsertItem(index, itemStackIn, direction);
	}

	@Override
	public boolean canExtractItem(int index, @Nonnull ItemStack stack,
			EnumFacing direction) {
		return inventory.canExtractItem(index, stack, direction);
	}

	@Override
	@Nonnull
	public ItemStack removeStackFromSlot(int index) {
		return inventory.removeStackFromSlot(index);
	}

	@Override
	public int getField(int id) {
		return inventory.getField(id);
	}

	@Override
	public void setField(int id, int value) {
		inventory.setField(id, value);
	}

	@Override
	public int getFieldCount() {
		return inventory.getFieldCount();
	}

	@Override
	public void clear() {
		inventory.clear();
	}
	
	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}
}
