package zmaster587.libVulpes.tile.energy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.tile.IComparatorOverride;
import zmaster587.libVulpes.tile.TileInventoriedForgePowerMachine;

import javax.annotation.Nonnull;
import java.util.List;

public class TileCoalGenerator extends TileInventoriedForgePowerMachine implements IComparatorOverride {

	int powerPerTick;
	private static final int INPUT_SLOT = 0;
	ModuleText textModule;

	public TileCoalGenerator() {
		super(10000, 1);
		powerPerTick = 40;
		textModule = new ModuleText(40, 20, "Generating 0 RF/t", 0x2b2b2b);
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleSlotArray(40, 40, this, 0, 1));
		modules.add(new ModuleProgress(80, 40, 1, TextureResources.progressGenerator, this));
		modules.add(textModule);

		return modules;
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		super.setInventorySlotContents(slot, stack);

		if(!canGeneratePower())
			setTimeAndAmounts();
	}

	@Override
	public int getPowerPerOperation() {
		return powerPerTick;
	}

	@Override
	protected void onOperationFinish() {
		super.onOperationFinish();
		setTimeAndAmounts();
	}

	private void setTimeAndAmounts() {
		timeRemaining = TileEntityFurnace.getItemBurnTime(inventory.getStackInSlot(INPUT_SLOT));
		if(timeRemaining > 0)
			inventory.decrStackSize(INPUT_SLOT, 1);
	}

	@Override
	public void update() {
		super.update();
		markDirty();
		if(world.isRemote)
			textModule.setText("Generating " + getLastAmtGenerated() + " RF/t");
	}
	@Override
	public String getModularInventoryName() {
		return "tile.coalGenerator.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public int getComparatorOverride() {
		return energy.getUniversalEnergyStored() * 15/energy.getMaxEnergyStored();
	}
}