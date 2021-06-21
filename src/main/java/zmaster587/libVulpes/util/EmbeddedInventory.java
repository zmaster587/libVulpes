package zmaster587.libVulpes.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.items.ItemStackHandler;
import zmaster587.libVulpes.interfaces.IInventoryUpdateCallback;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class EmbeddedInventory extends ItemStackHandler implements ISidedInventory {

	private ItemStackHandler handler;

	private IInventoryUpdateCallback tile;

	private NonNullList<Boolean> slotInsert;
	private NonNullList<Boolean> slotExtract;

	public EmbeddedInventory(int size) {
		this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
		this.slotInsert = NonNullList.withSize(size, true);
		this.slotExtract = NonNullList.withSize(size, true);
	}

	public EmbeddedInventory(int size, IInventoryUpdateCallback tile) {
		this(size);
		this.tile = tile;
	}

	public void writeToNBT(NBTTagCompound nbt) {

		nbt.setInteger("size", this.stacks.size());

		NBTTagList list = new NBTTagList();
		for(int i = 0; i < this.stacks.size(); i++)
		{
			ItemStack stack = this.stacks.get(i);

			if(!stack.isEmpty()) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte)(i));
				stack.writeToNBT(tag);
				list.appendTag(tag);
			}
		}

		nbt.setTag("outputItems", list);

		ArrayList<Byte> list2 = new ArrayList<>();
		for(int i = 0; i < this.slotInsert.size(); i++) {
			list2.add(i, slotInsert.get(i) ? (byte)1 : (byte)0);
		}
		nbt.setTag("slotInsert", new NBTTagByteArray(list2));

		ArrayList<Byte> list3 = new ArrayList<>();
		for(int i = 0; i < this.slotExtract.size(); i++) {
			list3.add(i, slotExtract.get(i) ? (byte)1 : (byte)0);
		}
		nbt.setTag("slotExtract", new NBTTagByteArray(list3));

	}

	@Override
	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);

		if(tile != null)
			tile.onInventoryUpdated(slot);

	}

	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList list = nbt.getTagList("outputItems", 10);
		this.stacks = NonNullList.withSize(Math.max(nbt.getInteger("size") == 0 ? 4 : nbt.getInteger("size"), this.stacks.size()), ItemStack.EMPTY);
		handler = new ItemStackHandler(this.stacks);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < this.stacks.size()) {
				this.stacks.set(slot, new ItemStack(tag));
			}
		}


		byte[] list2 = nbt.getByteArray("slotInsert");
		this.slotInsert = NonNullList.withSize(list2.length, false);
		for (int i = 0; i < list2.length; i++) {
			this.slotInsert.set(i, list2[i] == 1);
		}
		byte[] list3 = nbt.getByteArray("slotExtract");
		this.slotExtract = NonNullList.withSize(list3.length, false);
		for (int i = 0; i < list3.length; i++) {
			this.slotExtract.set(i, list3[i] == 1);
		}

		//Backcompat, to allow older worlds to load
        if (this.slotInsert.size() == 0) {
			this.slotInsert = NonNullList.withSize(4, false);
		}
		if (this.slotExtract.size() == 0) {
			this.slotExtract = NonNullList.withSize(4, false);
		}
	}


	public int getSizeInventory() {
		return this.stacks.size();
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		if(slot >= this.stacks.size())
			return ItemStack.EMPTY;

		return this.stacks.get(slot);
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = this.stacks.get(slot);
		if(!stack.isEmpty()) {
			ItemStack stack2 = stack.splitStack(Math.min(amt, stack.getCount()));
			if(stack.getCount() == 0)
				this.stacks.set(slot, ItemStack.EMPTY);

			return stack2;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		this.stacks.set(slot, stack);
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	public void setCanInsertSlot(int index, boolean bool) {
		this.slotInsert.set(index, bool);
	}

	public void setCanExtractSlot(int index, boolean bool) {
		this.slotExtract.set(index, bool);
	}

	@Override
	public boolean isUsableByPlayer(@Nullable EntityPlayer player) {
		return true;
	}

	@Override
	public boolean isEmpty() {
		for(ItemStack stack : this.stacks) {
			if(!stack.isEmpty())
				return false;
		}
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack item) {
		return this.stacks.get(slot).isEmpty() || (this.stacks.get(slot).isItemEqual(item) && this.stacks.get(slot).getMaxStackSize() != this.stacks.get(slot).getCount());
	}

	@Override
	@Nonnull
	public int[] getSlotsForFace(@Nullable EnumFacing side) {
		int[] array = new int[this.stacks.size()];

		for(int i = 0; i < this.stacks.size(); i++) {
			array[i] = i;
		}
		return array;
	}



	public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, @Nullable EnumFacing direction) {
		return this.slotInsert.get(index);
	}


	public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nullable EnumFacing direction) {
		return this.slotExtract.get(index);
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (!slotInsert.isEmpty() && slotInsert.get(slot)){
			return super.insertItem(slot, stack, simulate);
		}
		return stack;
	}

	@Override
	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (!slotExtract.isEmpty() && slotExtract.get(slot)){
			return super.extractItem(slot, amount, simulate);
		}
		return ItemStack.EMPTY;
	}

	@Nonnull
	public String getName() {
		return "";
	}

	@Override
	public void markDirty() {
	}

	@Override
	@Nonnull
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = this.stacks.get(index);
		this.stacks.set(index, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	@Nonnull
	public ITextComponent getDisplayName() {
		return new TextComponentString("Inventory");
	}
}
