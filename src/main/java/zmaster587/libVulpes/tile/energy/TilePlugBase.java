package zmaster587.libVulpes.tile.energy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.cap.ForgePowerCapability;
import zmaster587.libVulpes.cap.TeslaHandler;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.tile.IComparatorOverride;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.util.UniversalBattery;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public abstract class TilePlugBase extends TilePointer implements IModularInventory, IUniversalEnergy, IMultiblock, IInventory, IComparatorOverride {

	protected UniversalBattery storage;
	protected int teir;
	
	public TilePlugBase() {
		storage = new UniversalBattery(getMaxEnergy(0));
	}
	
	public TilePlugBase(int teir) {
		storage = new UniversalBattery(getMaxEnergy(0));
		setTeir(teir);
	}
	
	public void setTeir(int teir) {
		this.teir = teir;
		storage.setMaxEnergyStored(getMaxEnergy(teir));
	}
	
	protected int getMaxEnergy(int teir) {
		return (int)Math.pow(10,teir)*10000;
	}
	
	@Override
	public void setMaxEnergyStored(int max) {
		storage.setMaxEnergyStored(max);
		
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

		return capability == CapabilityEnergy.ENERGY || TeslaHandler.hasTeslaCapability(this, capability);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		if(capability == CapabilityEnergy.ENERGY )
			return (T)(new ForgePowerCapability(this));
		else if(TeslaHandler.hasTeslaCapability(this, capability))
			return (T)(TeslaHandler.getHandler(this));
		
		return super.getCapability(capability, facing);
	}
	
	@Override
	public NBTTagCompound serializeNBT() {
		return new NBTTagCompound();
	}
	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		
	}
	
	protected int getMaxDrainRate(int teir) {
		return 250*(int)Math.pow(2, teir);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("teir", teir);
		storage.writeToNBT(nbt);
		
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		teir = nbt.getInteger("teir");
		
		storage = new UniversalBattery(getMaxEnergy(teir));
		storage.readFromNBT(nbt);
	}

	@Override
	public void setEnergyStored(int amt) {
		markDirty();
		storage.setEnergyStored(amt);
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int p_70301_1_) {
		return ItemStack.EMPTY;
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, @Nonnull ItemStack p_70299_2_) {
		
	}

	@Override
	public boolean hasCustomName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}
	
	
	@Override
	public void openInventory(EntityPlayer player) {
		
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		
	}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
		return true;
	}

	@Override
	@Nonnull
	public ItemStack removeStackFromSlot(int index) {
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
	
	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<>();
		modules.add(new ModulePower(18, 20,this));
		return modules;
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public int extractEnergy(int amt, boolean simulate) {
		if (!simulate && getUniversalEnergyStored()/15 != (-amt + getUniversalEnergyStored())/15)
			markDirty();
		return storage.extractEnergy(amt, simulate);
	}

	@Override
	public int getUniversalEnergyStored() {
		return storage.getUniversalEnergyStored();
	}
	
	@Override
	public int getMaxEnergyStored() {
		return storage.getMaxEnergyStored();
	}

	@Override
	public int acceptEnergy(int amt, boolean simulate) {
		if (!simulate && getUniversalEnergyStored()/15 != (amt + getUniversalEnergyStored())/15)
			markDirty();
		return  storage.acceptEnergy(amt, simulate);
	}

	@Override
	public boolean isUsableByPlayer(@Nullable EntityPlayer player) {
		return true;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public int getComparatorOverride() {
		return getUniversalEnergyStored() * 15/getMaxEnergyStored();
	}
}
