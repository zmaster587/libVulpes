package zmaster587.libVulpes.inventory;

import java.util.List;

import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

//import javax.annotation.Nonnull;

public class ContainerModular extends Container {

	List<ModuleBase> modules;
	int numSlots;
	IModularInventory modularInventory;


	public ContainerModular(EntityPlayer playerInv, List<ModuleBase> modules, IModularInventory modulularInv, boolean includePlayerInv, boolean includeHotBar) {
		this.modularInventory = modulularInv;
		this.modules = modules;
		numSlots = 0;

		for(ModuleBase module : modules)
			for(Slot slot : module.getSlots(this)) {
				addSlotToContainer(slot);
				numSlots++;
			}

		if(includePlayerInv) {
			// Player inventory
			for (int i1 = 0; i1 < 3; i1++) {
				for (int l1 = 0; l1 < 9; l1++) {
					addSlotToContainer(new Slot(playerInv.inventory, l1 + i1 * 9 + 9, 8 + l1 * 18, 89 + i1 * 18));
				}
			}
		}

		if(includeHotBar) {
			// Player hotbar
			for (int j1 = 0; j1 < 9; j1++) {
				addSlotToContainer(new Slot(playerInv.inventory, j1, 8 + j1 * 18, 147));
			}
		}
	}

	public Slot addSlotToContainer(Slot slot) {
		return super.addSlotToContainer(slot);
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);

		int moduleIndex = 0;

		for(ModuleBase module : modules) {
			//for(int i = 0; i < module.numChangesToSend(); i++) {
			module.sendInitialChanges(this, listener, moduleIndex);

			moduleIndex+= module.numberOfChangesToSend();
			//}
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		int moduleIndex = 0;

		for(ModuleBase module : modules) {
			for(int i = 0; i < module.numberOfChangesToSend(); i++) {
				if(module.isUpdateRequired(i)) {

                    for (IContainerListener listener : this.listeners) {
                        module.sendChanges(this, listener, moduleIndex, i);
                    }
				}
				moduleIndex++;
			}
		}
	}

	@Override
	public void updateProgressBar(int slot, int value) {
		super.updateProgressBar(slot, value);

		int moduleIndex = 0;

		for(ModuleBase module : modules) {
			if(slot - moduleIndex < module.numberOfChangesToSend() && slot - moduleIndex >= 0) {
				module.onChangeRecieved(slot - moduleIndex, value);
			}
			moduleIndex += module.numberOfChangesToSend();
		}

	}

	@Override
	@Nonnull
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {

		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(slotId);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			//Try to merge to player inventory
			if (slotId < numSlots)
			{
				if (!this.mergeItemStack(itemstack1, numSlots, this.inventorySlots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 0, slotId, false))
			{
				return ItemStack.EMPTY;
			}

			if (itemstack1.getCount() == 0)
			{
				slot.putStack(ItemStack.EMPTY);
			}
			else
			{
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return modularInventory.canInteractWithContainer(player);
	}
}
