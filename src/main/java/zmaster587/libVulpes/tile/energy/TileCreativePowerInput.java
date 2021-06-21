package zmaster587.libVulpes.tile.energy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import zmaster587.libVulpes.energy.IPower;
import zmaster587.libVulpes.util.CreativeBattery;

import javax.annotation.Nullable;

public class TileCreativePowerInput extends TilePlugBase implements IPower, ITickable {

	public TileCreativePowerInput() {
		//super(1);
		storage = new CreativeBattery();
	}

	public TileCreativePowerInput(int teir) {
		//super(1);
		storage = new CreativeBattery();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		teir = nbt.getInteger("teir");
		
		storage = new CreativeBattery();//new UniversalBattery(getMaxEnergy(teir));
		storage.readFromNBT(nbt);
	}
	
	@Override
	public int extractEnergy(EnumFacing dir, int amt, boolean sim) {
		return extractEnergy(amt, sim);
	}

	@Override
	public int getEnergyStored(EnumFacing arg0) {
		return getUniversalEnergyStored();//getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing arg0) {
		return getMaxEnergyStored();
	}

	@Override
	public int receiveEnergy(EnumFacing arg0, int maxReceive, boolean simulate) {
		return acceptEnergy(maxReceive, simulate);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing arg0) {
		return true;
	}


	@Override
	public String getModularInventoryName() {
		return "tile.creativePowerBattery.name";
	}

	@Override
	@Nullable
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int receiveEnergy(int amt, boolean simulate) {
		return 0;
	}

	@Override
	public boolean canReceive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public void update() {
		if(!world.isRemote) {
			for(EnumFacing facing : EnumFacing.VALUES) {
				TileEntity tile = world.getTileEntity(this.getPos().offset(facing));

				if(tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
					IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY,  facing.getOpposite());
					if(storage != null)
						this.extractEnergy(storage.receiveEnergy(getUniversalEnergyStored(), false),false);
				}
			}
		}
	}
	
}
