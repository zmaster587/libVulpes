package zmaster587.libVulpes.util;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IFluidHandlerInternal extends IFluidHandler {
	int fillInternal(FluidStack resource, boolean doDrain);
	
	FluidStack drainInternal(int maxDrain, boolean doDrain);
	
	FluidStack drainInternal(FluidStack maxDrain, boolean doDrain);
}
