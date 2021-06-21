package zmaster587.libVulpes.tile.multiblock.hatch;

import net.minecraft.entity.player.EntityPlayer;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleOutputSlotArray;

import java.util.LinkedList;
import java.util.List;

public class TileOutputHatch extends TileInventoryHatch {

	

	public TileOutputHatch() {
		super();
	}

	public TileOutputHatch(int size) {
		super(size);
		inventory.setCanInsertSlot(0, false);
		inventory.setCanInsertSlot(1, false);
		inventory.setCanInsertSlot(2, false);
		inventory.setCanInsertSlot(3, false);
		inventory.setCanExtractSlot(0, true);
		inventory.setCanExtractSlot(1, true);
		inventory.setCanExtractSlot(2, true);
		inventory.setCanExtractSlot(3, true);

	}

	@Override
	public String getModularInventoryName() {
		return "tile.hatch.1.name";
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		LinkedList<ModuleBase> modules = new LinkedList<>();
		modules.add(new ModuleOutputSlotArray(8, 18, this, 0, this.getSizeInventory()));
		return modules;
	}
	
	
}
