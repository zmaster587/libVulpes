package zmaster587.libVulpes.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ZUtils {

	public enum RedstoneState {
		ON,
		OFF,
		INVERTED;

		public RedstoneState getNext() {
			int  i = ordinal()+1;
			if(i >= RedstoneState.values().length)
				return RedstoneState.values()[0];
			else
				return RedstoneState.values()[i];
		}

		public RedstoneState getPrev() {
			int  i = ordinal()-1;
			if(i < 0)
				return RedstoneState.values()[RedstoneState.values().length-1];
			else
				return RedstoneState.values()[i];
		}
		
		public void writeToNBT(NBTTagCompound tag) {
			tag.setByte("redstoneState", (byte)this.ordinal());
		}
		
		public static RedstoneState createFromNBT(NBTTagCompound tag) {
			return RedstoneState.values()[tag.getByte("redstoneState")];
		}
	}

	public static int getAverageColor(long r, long g, long b, int total) {
		return (int)(( (r/total) ) | ( (g/total) << 8 ) | ( ( b/total ) << 16 ) );
	}

	public static int getDirectionFacing(float rotationYaw) {
		int l = MathHelper.floor((double)(MathHelper.wrapDegrees(rotationYaw) * 4.0F / 360.0F) + 0.5D) & 3;

		if(l == 0)
			l = 2;
		else if(l == 1)
			l = 5;
		else if(l == 2)
			l = 3;
		else
			l = 4;

		return l;
	}

	public static List copyRandomElements(List fromList, int maxElements) {

		List returnList = new LinkedList();
		List moveList = new LinkedList(fromList);
		Random rand = new Random(System.nanoTime());

		for(int i = 0; i < maxElements && !moveList.isEmpty(); i++) {
			returnList.add(moveList.remove(rand.nextInt(moveList.size())));
		}
		return returnList;
	}


	public static TileEntity createTile(NBTTagCompound nbt)
	{
		TileEntity tileentity = null;
		ResourceLocation s = new ResourceLocation(nbt.getString("id"));
		Class <? extends TileEntity > oclass = null;


		try
		{
			oclass = ((RegistryNamespaced < ResourceLocation, Class <? extends TileEntity >>)ObfuscationReflectionHelper.getPrivateValue(TileEntity.class, null, "field_190562_f", "REGISTRY")).getObject(s);

			if (oclass != null)
			{
				tileentity = oclass.newInstance();
			}
		}
		catch (Throwable throwable1)
		{
			net.minecraftforge.fml.common.FMLLog.log(org.apache.logging.log4j.Level.ERROR, throwable1,
					"A TileEntity %s(%s) has thrown an exception during loading, its state cannot be restored. Report this to the mod author",
					s, oclass.getName());
		}

		if (tileentity != null)
		{
			try
			{
				tileentity.readFromNBT(nbt);
			}
			catch (Throwable throwable)
			{
				net.minecraftforge.fml.common.FMLLog.log(org.apache.logging.log4j.Level.ERROR, throwable,
						"A TileEntity %s(%s) has thrown an exception during loading, its state cannot be restored. Report this to the mod author",
						s, oclass.getName());
				tileentity = null;
			}
		}
		else
		{
			net.minecraftforge.fml.common.FMLLog.warning("Skipping BlockEntity with id {}", s);
		}

		return tileentity;
	}
	/**
	 * @param axis Axis Aligned Bounding box to rotate
	 * @param angleDeg amount to rotate the bounding box in radians
	 * @return copy of axis with the rotation applied
	 */
	public static AxisAlignedBB rotateAABB(AxisAlignedBB axis, double angleDeg) {

		double minX, maxX, minZ, maxZ;
		double angle = Math.toRadians(90 - angleDeg);

		minZ = -((axis.minX*Math.sin(angle)) + (axis.minZ*Math.cos(angle)));
		maxZ = -((axis.maxX*Math.sin(angle)) + (axis.maxZ*Math.cos(angle)));

		minX = -((axis.minZ*Math.sin(angle)) + (axis.minX*Math.cos(angle)));
		maxX = -((axis.maxZ*Math.sin(angle)) + (axis.maxX*Math.cos(angle)));

		if(minZ > maxZ) {
			double buffer = minZ;
			minZ = maxZ;
			maxZ = buffer;
		}
		if(minX > maxZ) {
			double buffer = minX;
			minX = maxX;
			maxX = buffer;
		}

		return new AxisAlignedBB(minX,
				axis.minY,
				minZ,
				maxX,
				axis.maxY,
				maxZ);
	}

	/**
	 * 
	 * @param local bounding box in local coords
	 * @param global global bounding box
	 * @param e entity that owns the bounding box
	 * @param angle amount to rotate by
	 * @return rotated bounding box in global coords
	 */
	public static AxisAlignedBB convertLocalBBToGlobal(AxisAlignedBB local, AxisAlignedBB global, Entity e, double angle) {
		AxisAlignedBB rotatedLocal = rotateAABB(local, angle);


		return new AxisAlignedBB(e.posX + rotatedLocal.minX, e.posY + rotatedLocal.minY, e.posZ + rotatedLocal.minZ, rotatedLocal.maxX + e.posX, rotatedLocal.maxY + e.posY, rotatedLocal.maxZ + e.posZ);
	}

	public static String formatNumber(int number) {
		if(number > 999999999) 
			return ((number/1000000)/10f) + "T";
		if(number > 999999)
			return ((number/1000000)/10f) + "M";
		if(number > 999) 
			return ((number/100)/10f) + "K";

		return String.valueOf(number);

	}

	public static boolean isInvEmpty(ItemStack[] stack) {
		boolean empty = true;
		if(stack == null)
			return true;

		for(ItemStack i : stack) {
			if(!i.isEmpty())
				return false;
		}

		return true;
	}

	/***
	 * Returns true if the array of object contains object2
	 */
	public static boolean doesArrayContains(Object[] object, Object object2) {
		for(Object obj : object) {
			if(obj.equals(object2))
				return true;
		}

		return false;
	}

	public static boolean doesArrayContains(int[] object, Object object2) {
		for(Object obj : object) {
			if(obj.equals(object2))
				return true;
		}
		return false;
	}

	public static boolean isInvEmpty(IInventory inv) {
		boolean empty = true;
		if(inv == null)
			return true;

		for(int i = 0; i < inv.getSizeInventory(); i++) {
			if(!inv.getStackInSlot(i).isEmpty())
				return false;
		}

		return true;
	}

	public static boolean doesInvHaveRoom(@Nonnull ItemStack item, IInventory inv) {
		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			if(inv.getStackInSlot(i).isEmpty() || (item.isItemEqual(inv.getStackInSlot(i)) && inv.getStackInSlot(i).getCount() < inv.getInventoryStackLimit()))
				return true;
		}

		return false;
	}

	public static boolean hasFullStack(IInventory inv) {

		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			if(!inv.getStackInSlot(i).isEmpty() && inv.getStackInSlot(i).getMaxStackSize() == inv.getStackInSlot(i).getCount())
				return true;
		}

		return false;
	}

	public static int numEmptySlots(IInventory inv) {
		int num = 0;

		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			if(inv.getStackInSlot(i).isEmpty())
				num++;
		}

		return num;
	}

	public static int numFilledSlots(IInventory inv) {
		int num = 0;

		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			if(!inv.getStackInSlot(i).isEmpty() && inv.getStackInSlot(i).getCount() == inv.getStackInSlot(i).getMaxStackSize())
				num++;
		}

		return num;
	}

	public static void mergeInventory(ItemStack[] a, IInventory b) {
		for(int i = 0; i < a.length; i++) {
			int firstEmtpySlot = -1;
			int slot;

			if(!a[i].isEmpty()) {
				for(slot = 0; slot < b.getSizeInventory(); slot++) {

					if(b.getStackInSlot(slot).isEmpty()) {
						if(firstEmtpySlot == -1)
							firstEmtpySlot = slot;
					}
					else if(b.getStackInSlot(slot).isItemEqual(a[i])) //b.isItemValidForSlot(slot, a[i]))//
					{
						int maxTransfer = b.getInventoryStackLimit() - b.getStackInSlot(slot).getCount();

						if(a[i].getCount() < maxTransfer) {
							//chest.setInventorySlotContents(g, itemstack);
							b.getStackInSlot(slot).setCount(b.getStackInSlot(slot).getCount() + a[i].getCount());
							a[i] = ItemStack.EMPTY;
							break;
						}
						else {
							b.getStackInSlot(slot).setCount(b.getInventoryStackLimit());
							a[i].setCount(a[i].getCount() - maxTransfer);
						}
					}
				}

				if(!a[i].isEmpty() && firstEmtpySlot != -1) {
					b.setInventorySlotContents(firstEmtpySlot, a[i].copy());
					a[i] = ItemStack.EMPTY;
				}
			}
		}
	}

	public static void mergeInventory(@Nonnull ItemStack a, IInventory b) {
		int firstEmtpySlot = -1;
		int slot;

		if(!a.isEmpty()) {
			for(slot = 0; slot < b.getSizeInventory(); slot++) {

				if(b.getStackInSlot(slot).isEmpty()) {
					if(firstEmtpySlot == -1)
						firstEmtpySlot = slot;
				}
				else if(b.getStackInSlot(slot).isItemEqual(a)) //b.isItemValidForSlot(slot, a[i]))//
				{
					int maxTransfer = b.getInventoryStackLimit() - b.getStackInSlot(slot).getCount();

					if(a.getCount() < maxTransfer) {
						//chest.setInventorySlotContents(g, itemstack);
						b.getStackInSlot(slot).setCount(b.getStackInSlot(slot).getCount() + a.getCount());
						a = ItemStack.EMPTY;
						break;
					}
					else {
						b.getStackInSlot(slot).setCount( b.getInventoryStackLimit());
						a.setCount( a.getCount() - maxTransfer);
					}
				}
			}

			if(!a.isEmpty() && firstEmtpySlot != -1) {
				if(a.getCount() != 0)
					b.setInventorySlotContents(firstEmtpySlot, a.copy());
				a =  ItemStack.EMPTY;
			}
		}
	}

	@Nonnull
	public static ItemStack getFirstItemInInv(@Nonnull ItemStack[] i) {
		for(ItemStack stack : i)
			if(!stack.isEmpty()) return stack;
		return ItemStack.EMPTY;
	}

	public static int getFirstFilledSlotIndex(IInventory inv) {
		for(int i = 0; i < inv.getSizeInventory(); i++)
			if(inv.getStackInSlot(i) != ItemStack.EMPTY) return i;
		return inv.getSizeInventory();
	}

	public static int getContinuousBlockLength(World world, EnumFacing direction, BlockPos pos, int maxDist, Block block) {
		int dist = 0;
		for(int i = 0; i < maxDist; i++) {
			if(world.getBlockState(new BlockPos(pos.add((i*direction.getFrontOffsetX()), (i*direction.getFrontOffsetY()), (i*direction.getFrontOffsetZ())))).getBlock() != block) 
				break;

			dist = i+1;
		}

		return dist;
	}
	
	public static int getContinuousBlockLength(World world, EnumFacing direction, BlockPos pos, int maxDist, Block[] blocks) {
		int dist = 0;
		for(int i = 0; i < maxDist; i++) {
			Block blockchecked = world.getBlockState(new BlockPos(pos.add((i*direction.getFrontOffsetX()), (i*direction.getFrontOffsetY()), (i*direction.getFrontOffsetZ())))).getBlock();
			boolean exists = false;
			for( Block b : blocks ) {
				if(blockchecked == b) {
					exists = true;
					break;
				}
			}
			
			if(!exists) 
				break;

			dist = i+1;
		}

		return dist;
	}

	public static boolean areOresSameTypeOreDict(@Nonnull ItemStack stack1, @Nonnull ItemStack stack2) {
		int[] stack1Id = OreDictionary.getOreIDs(stack1);
		int[] stack2Id = OreDictionary.getOreIDs(stack2);

		for(int i : stack1Id) {
			for(int j : stack2Id) {
				if(i == j)
					return true;
			}
		}

		return false;
	}

	public static boolean isItemInOreDict(@Nonnull ItemStack stack, String oreDictEntry) {
		List<ItemStack> itemStacks = OreDictionary.getOres(oreDictEntry);
		for(ItemStack stack1 : itemStacks)
			if(OreDictionary.itemMatches(stack1, stack, false))
				return true;
		return false;
	}
}
