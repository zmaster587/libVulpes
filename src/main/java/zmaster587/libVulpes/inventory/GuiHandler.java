package zmaster587.libVulpes.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import zmaster587.libVulpes.inventory.modules.IModularInventory;

public class GuiHandler implements IGuiHandler {

	public enum guiId {
		MODULAR,
		MODULARNOINV,
		MODULARCENTEREDFULLSCREEN,
		MODULARFULLSCREEN
	}

	//X coord is entity ID num if entity
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {

		Object tile;
		BlockPos pos = new BlockPos(x,y,z);

		if(y > -1)
			tile = world.getTileEntity(pos);
		else if(x == -1) {
			ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
			
			//If there is latency or some desync odd things can happen so check for that
			if(stack.isEmpty() || !(stack.getItem() instanceof IModularInventory)) {
				return null;
			}
			
			tile = player.getHeldItem(EnumHand.MAIN_HAND).getItem();
		}
		else
			tile = world.getEntityByID(x);

		if(ID == guiId.MODULAR.ordinal() || ID == guiId.MODULARNOINV.ordinal() || ID == guiId.MODULARFULLSCREEN.ordinal() || ID == guiId.MODULARCENTEREDFULLSCREEN.ordinal()) {
			return new ContainerModular(player, ((IModularInventory)tile).getModules(ID, player), ((IModularInventory)tile), ID == guiId.MODULAR.ordinal(), ID != guiId.MODULARFULLSCREEN.ordinal() && ID != guiId.MODULARCENTEREDFULLSCREEN.ordinal());
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {

		Object tile;
		BlockPos pos = new BlockPos(x,y,z);
		
		if(y > -1)
			tile = world.getTileEntity(pos);
		else if(x == -1) {
			ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
			
			//If there is latency or some desync odd things can happen so check for that
			if(stack.isEmpty() || !(stack.getItem() instanceof IModularInventory)) {
				return null;
			}
			
			tile = player.getHeldItem(EnumHand.MAIN_HAND).getItem();
		}
		else
			tile = world.getEntityByID(x);

		if(ID == guiId.MODULAR.ordinal() || ID == guiId.MODULARNOINV.ordinal() || ID == guiId.MODULARCENTEREDFULLSCREEN.ordinal()) {
			IModularInventory modularTile = ((IModularInventory)tile);
			return new GuiModular(player, modularTile.getModules(ID, player), modularTile, ID == guiId.MODULAR.ordinal(), ID != guiId.MODULARCENTEREDFULLSCREEN.ordinal(), modularTile.getModularInventoryName());
		}
		else if(ID == guiId.MODULARFULLSCREEN.ordinal()) {
			IModularInventory modularTile = ((IModularInventory)tile);
			return new GuiModularFullScreen(player,modularTile.getModules(ID, player), modularTile, ID == guiId.MODULAR.ordinal(), false, modularTile.getModularInventoryName());
		}
		return null;
	}

}
