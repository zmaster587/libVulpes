package zmaster587.libVulpes.tile.multiblock.hatch;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import zmaster587.libVulpes.interfaces.IInventoryUpdateCallback;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.util.EmbeddedInventory;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class TileInventoryHatch extends TilePointer implements ISidedInventory, IModularInventory, IInventoryUpdateCallback {

	protected EmbeddedInventory inventory;

	public TileInventoryHatch() {
		inventory = new EmbeddedInventory(0, this);
	}

	public TileInventoryHatch(int invSize) {
		inventory = new EmbeddedInventory(invSize, this);
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

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}


	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) inventory;
		}
		return super.getCapability(capability, facing);
	}

	@Override
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
		setInventorySlotContentsNoUpdate(slot, stack);
		if(this.hasMaster() && this.getMasterBlock() instanceof TileMultiBlock)
			((TileMultiBlock)this.getMasterBlock()).onInventoryUpdated();
	}


	public void setInventorySlotContentsNoUpdate(int slot, @Nonnull ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}

	@Override
	public boolean hasCustomName() {
		return inventory.hasCustomName();
	}
	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return player.getDistanceSq(pos) < 64;
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	public void openInventory(EntityPlayer entity) {

	}

	@Override
	public void closeInventory(EntityPlayer entity) {

	}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
		return inventory.isItemValidForSlot(slot, stack);
	}

	@Override
	@Nonnull
	public int[] getSlotsForFace(EnumFacing side) {
		return inventory.getSlotsForFace(side);
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
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		LinkedList<ModuleBase> modules = new LinkedList<>();

		modules.add(new ModuleSlotArray(8, 18, this, 0, this.getSizeInventory()));

		return modules;
	}

	@Override
	@Nonnull
	public String getName() {
		return getModularInventoryName();
	}

	@Override
	public String getModularInventoryName() {
		return null;
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if(!world.isRemote) {
			TileEntity tile = getFinalPointedTile();
			if(tile instanceof TileMultiBlock) {
				((TileMultiBlock) tile).invalidateComponent(this);
			}
		}
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
	public void onInventoryUpdated(int slot) {
		if(this.hasMaster() && this.getMasterBlock() instanceof TileMultiBlock)
			((TileMultiBlock)this.getMasterBlock()).onInventoryUpdated();
	}

}
