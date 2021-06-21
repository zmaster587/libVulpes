package zmaster587.libVulpes.util;

import net.minecraft.nbt.NBTTagCompound;

public class BlockDirectionFunction {

	byte[] states = new byte[6];
	byte maxNumStates;
	
	public BlockDirectionFunction(int maxStates) {
		maxNumStates = (byte) maxStates;
	}
	
	public void advanceState(int direction) {
		states[direction]++;
		if(states[direction] >= maxNumStates)
			states[direction] = 0;
	}
	
	public void retractState(int direction) {
		states[direction]--;
		if(states[direction] < 0)
			states[direction] = (byte) (maxNumStates - 1);
	}
	
	public int getState(int direction) {
		return states[direction];
	}
	
	public void setState(int direction, int state) {
		states[direction] = (byte)state;
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setByteArray("BDFstates", states);
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		if(nbt.hasKey("BDFstates"))
			states = nbt.getByteArray("BDFstates");
	}
}
