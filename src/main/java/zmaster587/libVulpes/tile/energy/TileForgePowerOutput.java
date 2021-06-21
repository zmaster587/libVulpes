package zmaster587.libVulpes.tile.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class TileForgePowerOutput extends TilePlugBase implements IEnergyStorage, ITickable {

	public TileForgePowerOutput() {
		super(1);
	}

	@Override
	public String getModularInventoryName() {
		return "tile.forgePowerOutput.name";
	}

	@Override
	@Nullable
	public String getName() {
		return null;
	}

	@Override
	public int receiveEnergy(int amt, boolean simulate) {
		return acceptEnergy(amt, simulate);
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
	public int getEnergyStored() {
		return getUniversalEnergyStored();
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
