package zmaster587.libVulpes.api;


public interface IUniversalEnergy  {
	void setEnergyStored(int amt);

	int extractEnergy(int amt, boolean simulate);

	int getUniversalEnergyStored();

	int getMaxEnergyStored();

	int acceptEnergy(int amt, boolean simulate);
	
	void setMaxEnergyStored(int max);

	boolean canReceive();

	boolean canExtract();
}
