package zmaster587.libVulpes.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.cap.ForgePowerCapability;
import zmaster587.libVulpes.cap.TeslaHandler;
import zmaster587.libVulpes.energy.IPower;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.util.UniversalBattery;

import java.util.LinkedList;
import java.util.List;

public abstract class TileEntityForgeProducer extends TileEntity implements IModularInventory, IEnergyStorage,  IPower, IUniversalEnergy, ITickable {
	protected UniversalBattery energy;

	protected TileEntityForgeProducer(int energy) {
		this.energy = new UniversalBattery(energy);
	}


	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		LinkedList<ModuleBase> modules = new LinkedList<>();
		modules.add(new ModulePower(18, 20, energy));

		return modules;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos,
			IBlockState oldState, IBlockState newSate) {
		return (oldState.getBlock() != newSate.getBlock());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
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

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return false;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing arg0) {
		return true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		energy.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		energy.readFromNBT(nbt);
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive,
			boolean simulate) {
		return acceptEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract,
			boolean simulate) {
		return extractEnergy(maxExtract, simulate);
	}

	public boolean hasEnoughEnergyBuffer(int amt) {
		return getMaxEnergyStored() - getUniversalEnergyStored() >= amt;
	}

	public int getPowerPerOperation() {
		return 0;
	}

	public abstract boolean canGeneratePower();


	@Override
	public void update() {

		if(canGeneratePower()) {

			if(hasEnoughEnergyBuffer(getPowerPerOperation())) {
				if(!world.isRemote) this.energy.acceptEnergy(getPowerPerOperation(), false);
				onGeneratePower();
			}
			else
				notEnoughBufferForFunction();
		}
		if(!world.isRemote)
			transmitPower();

	}

	protected void transmitPower() {
		for(EnumFacing facing : EnumFacing.VALUES) {
			TileEntity tile = world.getTileEntity(this.getPos().offset(facing));

			if(tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
				IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY,  facing.getOpposite());
				if(storage != null && storage.canReceive())
					this.extractEnergy(storage.receiveEnergy(getUniversalEnergyStored(), false),false);
			}
		}
	}

	public abstract void onGeneratePower();

	public void notEnoughBufferForFunction() {

	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return energy.getUniversalEnergyStored();
	}
	
	@Override
	public int getEnergyStored() {
		return energy.getUniversalEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return energy.getMaxEnergyStored();
	}

	public boolean hasEnergy() { return energy.getUniversalEnergyStored() > 0; }

	public void setEnergyStored(int value) {
		energy.setEnergyStored(value);
	}

	@Override
	public int extractEnergy(int amt, boolean simulate) {
		return energy.extractEnergy(amt, simulate);
	}

	@Override
	public int getUniversalEnergyStored() {
		return energy.getUniversalEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}

	public void setMaxEnergyStored(int max) {
		energy.setEnergyStored(max);
	}

	@Override
	public int acceptEnergy(int amt, boolean simulate) {
		return energy.acceptEnergy(amt, simulate);
	}

	@Override
	public int receiveEnergy(int amt, boolean simulate) {
		return energy.acceptEnergy(amt, simulate);
	}
}
