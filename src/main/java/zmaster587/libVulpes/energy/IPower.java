package zmaster587.libVulpes.energy;

import net.minecraft.util.EnumFacing;
import zmaster587.libVulpes.api.IUniversalEnergy;

public interface IPower extends IUniversalEnergy {
	
	boolean canConnectEnergy(EnumFacing facing);
	
	int extractEnergy(EnumFacing dir, int maxExtract, boolean simulate);
	
	int getEnergyStored(EnumFacing dir);
	
	int getMaxEnergyStored(EnumFacing dir);
	
	int receiveEnergy(EnumFacing dir, int amt, boolean simulate);

	/**
	 * @param amt
	 * @param simulate
	 * @return amount of energy used out of amt
	 */
	int receiveEnergy(int amt, boolean simulate);
}
