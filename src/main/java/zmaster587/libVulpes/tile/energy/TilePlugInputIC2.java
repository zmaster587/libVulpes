package zmaster587.libVulpes.tile.energy;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.MinecraftForge;
import zmaster587.libVulpes.Configuration;

import javax.annotation.Nullable;

public class TilePlugInputIC2 extends TileForgePowerOutput implements IEnergySink, 
ITickable {

	public TilePlugInputIC2() {
		super();
	}
	boolean tickedOnce = false;
	@Override
	public String getModularInventoryName() {
		return "tile.IC2Plug.name";
	}

	//[Redacted]
	//Apologies for the previous comment located here

	@Override
	public void update() {
		super.update();
		if(!tickedOnce) {
			if(!world.isRemote)
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			tickedOnce = true;
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if(tickedOnce && !world.isRemote)
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if(tickedOnce && !world.isRemote)
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
	}

	@Override
	@Nullable
	public String getName() {
		return null;
	}

	@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
		return true;
	}

	@Override
	public double getDemandedEnergy() {
		return Math.min(getMaxEnergyStored() - getUniversalEnergyStored(), 128.0);
	}

	@Override
	public int getSinkTier() {
		return 2;
	}

	@Override
	public double injectEnergy(EnumFacing directionFrom, double amount,
			double voltage) {
		storage.acceptEnergy((int)(amount*Configuration.EUMult), false);
		return 0;
	}

	@Override
	public int acceptEnergy(int amt, boolean simulate) {
		return 0;
	}

	@Override
	public boolean canReceive() {
		return false;
	}

	@Override
	public boolean canExtract() {
		return true;
	}

}
