package zmaster587.libVulpes.tile;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import zmaster587.libVulpes.api.IToggleableMachine;
import zmaster587.libVulpes.util.UniversalBattery;

import javax.annotation.Nonnull;

public abstract class TileEntityMachine extends TileEntity implements ISidedInventory, ITickable,  IToggleableMachine {
	
	
	protected UniversalBattery energy;
	
	protected ItemStack[] inv;
	
	protected int progress, totalTime;
	
	public int getProgressTime() { return progress; }
	
	public void setProgressTime(int time) { progress = time; }
	
	public int getTotalProgressTime() { return totalTime; }
	
	public void setTotalProgressTime(int time) { totalTime = time; }
	
	protected void setRunning(boolean on, World world) {
		//TODO set block state
		/*if(on)
			world.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, world.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) | 8, 2);
		else
			world.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, world.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) & (~8), 2);*/
	}
	
	protected boolean getIsRunning() {
		return (this.getBlockMetadata() & 8) == 8;
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);

		return new SPacketUpdateTileEntity(this.pos, 0, nbt);
	}
	
	@Override 
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		NBTTagList list = new NBTTagList();

		NBTTagList itemList = new NBTTagList();
		for(int i = 0; i < inv.length; i++)
		{
			ItemStack stack = inv[i];

			if(!stack.isEmpty()) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte)(i));
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		nbt.setTag("Inventory", itemList);
		
		nbt.setInteger("progress", progress);
		nbt.setInteger("totalTime", totalTime);
		
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		NBTTagList tagList = nbt.getTagList("Inventory", (byte)10);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = tagList.getCompoundTagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < inv.length) {
				inv[slot] = new ItemStack(tag);
			}
		}
		
		progress = nbt.getInteger("progress");
		totalTime = nbt.getInteger("totalTime");
	}
	
	@Override
	public int getSizeInventory() {
		return inv.length;
	}
	
	@Override
	@Nonnull
	public ItemStack getStackInSlot(int i) {
		return inv[i];
	}
	
	public void incStackSize(int i, int amt) {

		if(!inv[i].isEmpty()) {
			inv[i].setCount(Math.min(inv[i].getCount() + amt, inv[i].getMaxStackSize()));
		}
	}
	
	@Override
	@Nonnull
	public ItemStack decrStackSize(int i, int j) {
		ItemStack ret;
		if(inv[i].isEmpty())
			ret = ItemStack.EMPTY;
		else if(inv[i].getCount() > j) {
			ret = inv[i].splitStack(j);
		}
		else {
			ret = inv[i].copy();
			inv[i] = ItemStack.EMPTY;
		}
		return ret;
	}
	
	public abstract void onInventoryUpdate();
	
	@Override
	public void setInventorySlotContents(int i, @Nonnull ItemStack itemstack) {
		inv[i] = itemstack;

		//if(!this.worldObj.isRemote)
			onInventoryUpdate();
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	
	@Override
	public boolean isUsableByPlayer(EntityPlayer entityplayer) {
		return entityplayer.getDistanceSq(this.pos) < 64;
	}
}