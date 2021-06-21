package zmaster587.libVulpes.cap;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import zmaster587.libVulpes.api.IUniversalEnergy;

public class TeslaHandler {

	public TeslaHandler() {
	}
	
	@CapabilityInject(ITeslaConsumer.class)
	public static Capability<ITeslaConsumer> TESLA_CONSUMER = null;

	@CapabilityInject(ITeslaProducer.class)
	public static Capability<ITeslaProducer> TESLA_PRODUCER = null;

	@CapabilityInject(ITeslaHolder.class)
	public static Capability<ITeslaHolder> TESLA_HOLDER = null;

	
	public static boolean hasTeslaCapability(ICapabilityProvider e, Capability<?> cap) {
		if(TESLA_CONSUMER != null && TESLA_HOLDER != null && TESLA_PRODUCER != null) {
			return cap == TESLA_CONSUMER || cap == TESLA_HOLDER || cap == TESLA_PRODUCER;
		}
		return false;
	}
	
	public static TeslaPowerCapability getHandler(IUniversalEnergy e) {
		if(TESLA_CONSUMER == null && TESLA_HOLDER == null && TESLA_PRODUCER == null)
			return null;
		return new TeslaPowerCapability(e);
	}
}
