package zmaster587.libVulpes.items;

import com.mojang.realmsclient.gui.ChatFormatting;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.INetworkItem;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketItemModifcation;
import zmaster587.libVulpes.tile.TileSchematic;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TilePlaceholder;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.Vector3F;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.Map.Entry;

public class ItemProjector extends Item implements IModularInventory, IButtonInventory, INetworkItem {

	private ArrayList<TileMultiBlock> machineList;
	private ArrayList<BlockTile> blockList;
	private ArrayList<String> descriptionList;

	private static final String IDNAME = "machineId";

	public ItemProjector() {
		machineList = new ArrayList<>();
		blockList = new ArrayList<>();
		descriptionList = new ArrayList<>();
	}

	public void registerMachine(TileMultiBlock multiblock, BlockTile mainBlock) {
		machineList.add(multiblock);
		blockList.add(mainBlock);
		HashMap<Object, Integer> map = new HashMap<>();

		Object[][][] structure = multiblock.getStructure();

		for (Object[][] objects2d : structure) {
			for (Object[] objects : objects2d) {
				for (Object object : objects) {
					if (!map.containsKey(object)) {
						map.put(object, 1);
					} else
						map.put(object, map.get(object) + 1);
				}
			}
		}

		StringBuilder str = new StringBuilder(Item.getItemFromBlock(mainBlock).getItemStackDisplayName(new ItemStack(mainBlock)) + " x1\n");

		for(Entry<Object, Integer> entry : map.entrySet()) {

			List<BlockMeta> blockMeta = multiblock.getAllowableBlocks(entry.getKey());

			if(blockMeta.isEmpty() || Item.getItemFromBlock(blockMeta.get(0).getBlock()) == Items.AIR || blockMeta.get(0).getBlock() == Blocks.AIR )
				continue;
			for (BlockMeta meta : blockMeta) {
				String itemStr = Item.getItemFromBlock(meta.getBlock()).getItemStackDisplayName(new ItemStack(meta.getBlock(), 1, meta.getMeta()));
				if (!itemStr.contains("tile.")) {
					str.append(itemStr);
					str.append(" or ");
				}
			}
			
			if(str.toString().endsWith(" or ")) {
				str = new StringBuilder(str.substring(0, str.length() - 4));
			}
			str.append(" x").append(entry.getValue()).append("\n");
		}

		descriptionList.add(str.toString());
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void mouseEvent(MouseEvent event) {
		if(Minecraft.getMinecraft().player.isSneaking() && event.getDwheel() != 0) {
			ItemStack stack = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);

			if(!stack.isEmpty() && stack.getItem() == this && getMachineId(stack) != -1) {
				if(event.getDwheel() < 0) {
					setYLevel(stack, getYLevel(stack) + 1);
				}
				else
					setYLevel(stack, getYLevel(stack) - 1);
				event.setCanceled(true);

				PacketHandler.sendToServer(new PacketItemModifcation(this, Minecraft.getMinecraft().player, (byte)1));
			}
		}
	}

	private void clearStructure(World world, TileMultiBlock tile, @Nonnull ItemStack stack) {

		int id = getMachineId(stack);
		EnumFacing direction = EnumFacing.getFront(getDirection(stack));

		TileMultiBlock multiblock = machineList.get(id);

		int prevMachineId = getPrevMachineId(stack);
		Object[][][] structure;
		if(prevMachineId >= 0 && prevMachineId < machineList.size()) {
			structure = machineList.get(prevMachineId).getStructure();

			Vector3F<Integer> basepos = getBasePosition(stack);

			for(int y = 0; y < structure.length; y++) {
				for(int z=0 ; z < structure[0].length; z++) {
					for(int x=0; x < structure[0][0].length; x++) {

						int globalX = basepos.x - x*direction.getFrontOffsetZ() + z*direction.getFrontOffsetX();
						int globalZ = basepos.z + (x* direction.getFrontOffsetX()) + (z*direction.getFrontOffsetZ());
						BlockPos pos = new BlockPos(globalX, basepos.y + y, globalZ);
						if(world.getBlockState(pos).getBlock() == LibVulpesBlocks.blockPhantom) 
							world.setBlockToAir(pos);
					}
				}
			}
		}
	}

	private void RebuildStructure(World world, TileMultiBlock tile, @Nonnull ItemStack stack, int posX, int posY, int posZ, EnumFacing orientation) {

		int id = getMachineId(stack);
		EnumFacing direction = EnumFacing.getFront(getDirection(stack));

		TileMultiBlock multiblock = machineList.get(id);
		Object[][][] structure;

		clearStructure(world, tile, stack);

		structure = multiblock.getStructure();
		direction = orientation;

		int y = getYLevel(stack);
		int endNumber, startNumber;

		if(y == -1) {
			startNumber = 0;
			endNumber = structure.length;
		}
		else {
			startNumber = y;
			endNumber = y + 1;
		}
		for(y=startNumber; y < endNumber; y++) {
			for(int z=0 ; z < structure[0].length; z++) {
				for(int x=0; x < structure[0][0].length; x++) {
					List<BlockMeta> block;
					if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'c') {
						block = new ArrayList<>();
						block.add(new BlockMeta(blockList.get(id), orientation.getOpposite().ordinal()));
					}
					else if(multiblock.getAllowableBlocks(structure[y][z][x]).isEmpty())
						continue;
					else
						block = multiblock.getAllowableBlocks(structure[y][z][x]);

					int globalX = posX - x*direction.getFrontOffsetZ() + z*direction.getFrontOffsetX();
					int globalZ = posZ + (x* direction.getFrontOffsetX())  + (z*direction.getFrontOffsetZ());
					int globalY = -y + structure.length + posY - 1;
					BlockPos pos = new BlockPos(globalX, globalY, globalZ);

					if((world.isAirBlock(pos) || world.getBlockState(pos).getBlock().isReplaceable(world, pos)) && block.get(0).getBlock() != Blocks.AIR) {
						//block = (Block)structure[y][z][x];
						world.setBlockState(pos,  LibVulpesBlocks.blockPhantom.getStateFromMeta(block.get(0).getMeta()));
						TileEntity newTile = world.getTileEntity(pos);

						//TODO: compatibility fixes with the tile entity not reflecting current block
						if(newTile instanceof TilePlaceholder) {
							((TileSchematic)newTile).setReplacedBlock(block);

							((TilePlaceholder)newTile).setReplacedTileEntity(block.get(0).getBlock().createTileEntity(world, block.get(0).getBlock().getDefaultState()));
						}
					}
				}
			}
		}
		this.setPrevMachineId(stack, id);
		this.setBasePosition(stack, posX, posY, posZ);
		this.setDirection(stack, orientation.ordinal());
	}
	
	@Override
	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		
		if( player.isSneaking()) {
			if(!world.isRemote)
				player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULARNOINV.ordinal(), world, -1, -1, 0);
			return super.onItemRightClick(world, player, hand);
		}
		return super.onItemRightClick(world, player, hand);
	}

	@Override
	@Nonnull
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world,
			BlockPos blockPos, EnumFacing side, float hitX, float hitY, float hitZ,
			EnumHand hand) {


		ItemStack stack = player.getHeldItem(hand);
		
		int id = getMachineId(stack);
		if(!player.isSneaking() && id != -1 && world.isRemote) {
			EnumFacing dir = EnumFacing.getFront(ZUtils.getDirectionFacing(player.rotationYaw - 180));
			TileMultiBlock tile = machineList.get(getMachineId(stack));


			int x = tile.getStructure()[0][0].length;
			int z = tile.getStructure()[0].length;

			int globalX = (-x*dir.getFrontOffsetZ() + z*dir.getFrontOffsetX())/2;
			int globalZ = ((x* dir.getFrontOffsetX())  + (z*dir.getFrontOffsetZ()))/2;

			RayTraceResult pos = Minecraft.getMinecraft().objectMouseOver;

			TileEntity tile2;
			if((tile2 = world.getTileEntity(pos.getBlockPos())) instanceof TileMultiBlock) {
				for(TileMultiBlock tiles:  machineList) {
					if(tile2.getClass() == tiles.getClass()) {

						setMachineId(stack, machineList.indexOf(tiles));
						Object[][][] structure = tiles.getStructure();

						HashedBlockPosition controller = getControllerOffset(structure);
						dir = BlockMultiblockMachine.getFront(world.getBlockState(tile2.getPos())).getOpposite();

						controller.y = (short) (structure.length - controller.y);

						globalX = (-controller.x*dir.getFrontOffsetZ() + controller.z*dir.getFrontOffsetX());
						globalZ = ((controller.x* dir.getFrontOffsetX())  + (controller.z*dir.getFrontOffsetZ()));

						setDirection(stack, dir.ordinal());

						setBasePosition(stack, pos.getBlockPos().getX() - globalX, pos.getBlockPos().getY() - controller.y  + 1, pos.getBlockPos().getZ() - globalZ);
						PacketHandler.sendToServer(new PacketItemModifcation(this, player, (byte)0));
						PacketHandler.sendToServer(new PacketItemModifcation(this, player, (byte)2));
						return super.onItemUseFirst(player, world, blockPos, side, hitX, hitY, hitZ, hand);
					}
				}
			}

			if(pos.sideHit == EnumFacing.DOWN)
				setBasePosition(stack, pos.getBlockPos().getX() - globalX, pos.getBlockPos().getY() - tile.getStructure().length, pos.getBlockPos().getZ() - globalZ);
			else
				setBasePosition(stack, pos.getBlockPos().getX() - globalX, pos.getBlockPos().getY()+1, pos.getBlockPos().getZ() - globalZ);
			setDirection(stack, dir.ordinal());

			PacketHandler.sendToServer(new PacketItemModifcation(this, player, (byte)2));
		}

		return super.onItemUseFirst(player, world, blockPos, side, hitX, hitY, hitZ, hand);
	}

	protected HashedBlockPosition getControllerOffset(Object[][][] structure) {
		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {
					if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'c')
						return new HashedBlockPosition(x, y, z);
				}
			}
		}
		return null;
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<>();
		List<ModuleBase> btns = new LinkedList<>();

		for(int i = 0; 	i <	machineList.size(); i++) {
			TileMultiBlock multiblock = machineList.get(i);
			btns.add(new ModuleButton(60, 4 + i*24, i, LibVulpes.proxy.getLocalizedString(multiblock.getMachineName()), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild));
		}

		ModuleContainerPan panningContainer = new ModuleContainerPan(5, 20, btns, new LinkedList<>(), TextureResources.starryBG, 160, 100, 0, 500);
		modules.add(panningContainer);
		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "item.holoProjector.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return entity != null && !entity.isDead && !entity.getHeldItem(EnumHand.MAIN_HAND).isEmpty() && entity.getHeldItem(EnumHand.MAIN_HAND).getItem() == this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onInventoryButtonPressed(int buttonId) {
		//PacketHandler.sendToServer(new PacketItemModifcation(this, Minecraft.getMinecraft().thePlayer, (byte)buttonId));
		ItemStack stack = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);
		if(!stack.isEmpty() && stack.getItem() == this) {
			setMachineId(stack, buttonId);
			PacketHandler.sendToServer(new PacketItemModifcation(this, Minecraft.getMinecraft().player, (byte)0));
		}
	}

	private void setMachineId(@Nonnull ItemStack stack, int id) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else 
			nbt = new NBTTagCompound();

		nbt.setInteger(IDNAME, id);
		stack.setTagCompound(nbt);
	}

	private int getMachineId(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound()) {
			return stack.getTagCompound().getInteger(IDNAME);
		}
		else
			return -1;
	}

	private void setYLevel(@Nonnull ItemStack stack, int level) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else 
			nbt = new NBTTagCompound();

		TileMultiBlock machine = machineList.get(getMachineId(stack));

		if(level == -2)
			level = machine.getStructure().length-1;
		else if(level == machine.getStructure().length)
			level = -1;
		nbt.setInteger("yOffset", level);
		stack.setTagCompound(nbt);
	}

	private int getYLevel(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound()) {
			return stack.getTagCompound().getInteger("yOffset");
		}
		else
			return -1;
	}

	private void setPrevMachineId(@Nonnull ItemStack stack, int id) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else 
			nbt = new NBTTagCompound();

		nbt.setInteger(IDNAME + "Prev", id);
		stack.setTagCompound(nbt);
	}

	private int getPrevMachineId(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound()) {
			return stack.getTagCompound().getInteger(IDNAME + "Prev");
		}
		else
			return -1;
	}

	private Vector3F<Integer> getBasePosition(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			return new Vector3F<>(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
		}
		else
			return null;
	}

	private void setBasePosition(@Nonnull ItemStack stack, int x, int y, int z) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else
			nbt = new NBTTagCompound();

		nbt.setInteger("x", x);
		nbt.setInteger("y", y);
		nbt.setInteger("z", z);

		stack.setTagCompound(nbt);
	}

	public int getDirection(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound()) {
			return stack.getTagCompound().getInteger("dir");
		}
		else
			return -1;
	}

	public void setDirection(@Nonnull ItemStack stack, int dir) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else
			nbt = new NBTTagCompound();

		nbt.setInteger("dir", dir);

		stack.setTagCompound(nbt);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, World player,
			List<String> list, ITooltipFlag bool) {
		super.addInformation(stack, player, list, bool);

		list.add("Shift right-click: opens machine selection interface");
		list.add("Shift-scroll: moves cross-section");

		int id = getMachineId(stack);
		if(id != -1) {
			list.add("");
			list.add(ChatFormatting.GREEN + LibVulpes.proxy.getLocalizedString(machineList.get(id).getMachineName()));
			String str = descriptionList.get(id);

			String[] strList = str.split("\n");

			list.addAll(Arrays.asList(strList));
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id, @Nonnull ItemStack stack) {
		if(id == 0) {
			out.writeInt(getMachineId(stack));
		}
		else if(id == 1)
			out.writeInt(getYLevel(stack));
		else if(id == 2) {

			Vector3F<Integer> pos = getBasePosition(stack);
			out.writeInt(pos.x);
			out.writeInt(pos.y);
			out.writeInt(pos.z);
			out.writeInt(getDirection(stack));
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId, NBTTagCompound nbt, @Nonnull ItemStack stack) {
		if(packetId == 0) {
			nbt.setInteger(IDNAME, in.readInt());
		}
		else if(packetId == 1)
			nbt.setInteger("yLevel", in.readInt());
		else if(packetId == 2) {
			nbt.setInteger("x", in.readInt());
			nbt.setInteger("y", in.readInt());
			nbt.setInteger("z", in.readInt());
			nbt.setInteger("dir", in.readInt());
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt, @Nonnull ItemStack stack) {
		if(id == 0) {
			int machineId = nbt.getInteger(IDNAME);
			setMachineId(stack, nbt.getInteger(IDNAME));
			TileMultiBlock tile = machineList.get(machineId);
			setYLevel(stack, tile.getStructure().length-1);
		}
		else if(id == 1) {
			setYLevel(stack, nbt.getInteger("yLevel"));
			Vector3F<Integer> vec = getBasePosition(stack);
			RebuildStructure(player.world, this.machineList.get(getMachineId(stack)), stack, vec.x, vec.y, vec.z, EnumFacing.getFront(getDirection(stack)));
		}
		else if(id == 2) {
			int x = nbt.getInteger("x");
			int y = nbt.getInteger("y");
			int z = nbt.getInteger("z");
			int dir = nbt.getInteger("dir");

			if(getMachineId(stack) != -1)
				RebuildStructure(player.world, this.machineList.get(getMachineId(stack)), stack, x, y, z, EnumFacing.getFront(dir));
		}
	}
}