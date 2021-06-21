package zmaster587.libVulpes.api;

import net.minecraft.util.EnumFacing;

public interface IUniversalEnergyTransmitter {
	
	/**
	 * @param side side requesting energy, UNKNOWN for internal tranmission or for teleportation
	 * @return max energy units that can be transmitted
	 */
	int getEnergyMTU(EnumFacing side);
	
	/**
	 * @param side side requesting energy, UNKNOWN for internal tranmission or for teleportation
	 * @param simulate false to commit the change, true to only simulate
	 * @return amount of energy actually transmitted
	 */
	int transmitEnergy(EnumFacing side, boolean simulate);
}
