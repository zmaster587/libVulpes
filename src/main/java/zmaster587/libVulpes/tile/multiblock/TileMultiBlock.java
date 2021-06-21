package zmaster587.libVulpes.tile.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.block.multiblock.BlockMultiBlockComponentVisible;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockStructure;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.tile.TileSchematic;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInputHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileOutputHatch;
import zmaster587.libVulpes.util.IFluidHandlerInternal;
import zmaster587.libVulpes.util.Vector3F;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TileMultiBlock extends TileEntity {

	/*CanRender must be seperate from incomplete because some multiblocks must be completed on the client but
	because chunks on the client.  It is also used to determine if the block on the server has ever been complete */
	protected boolean completeStructure, canRender;
	protected byte timeAlive = 0;

	protected LinkedList<IInventory> itemInPorts = new LinkedList<>();
	protected LinkedList<IInventory> itemOutPorts = new LinkedList<>();

	protected LinkedList<IFluidHandlerInternal> fluidInPorts = new LinkedList<>();
	protected LinkedList<IFluidHandlerInternal> fluidOutPorts = new LinkedList<>();

	protected static HashMap<Character, List<BlockMeta>> charMapping = new HashMap<>();

	public TileMultiBlock() {
		completeStructure = false;
		canRender = false;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos,
			IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public static void addMapping(char character, List<BlockMeta> listToAdd) {
		if(charMapping.containsKey(character))
			LibVulpes.logger.warn("Overwritting Multiblock mapping of \"" + character + "\"");
		charMapping.put(character, listToAdd);
	}

	public static List<BlockMeta> getMapping(char character) {
		return charMapping.get(character);
	}
	
	public List<IInventory> getItemInPorts() {
		for(int i = 0; i < itemInPorts.size(); i++) {
			if(itemInPorts.get(i) instanceof TileEntity) {
				TileEntity newTile = world.getTileEntity(((TileEntity)itemInPorts.get(i)).getPos());
				if (newTile instanceof IInventory)
					itemInPorts.set(i, (IInventory)newTile);
			}
		}
		return itemInPorts;
	}
	
	public List<IInventory> getItemOutPorts() {
		for(int i = 0; i < itemOutPorts.size(); i++) {
			if(itemOutPorts.get(i) instanceof TileEntity) {
				TileEntity newTile = world.getTileEntity(((TileEntity)itemOutPorts.get(i)).getPos());
				if (newTile instanceof IInventory)
					itemOutPorts.set(i, (IInventory)newTile);
			}
		}
		return itemOutPorts;
	}

	/**
	 * Note: it may be true on the server but not the client.  This is because the client needs to form the multiblock
	 * so the tile has references to other blocks in its structure for gui display etc
	 * @return true if the structure is complete
	 */
	public boolean isComplete() {
		return completeStructure;
	}

	/**
	 * 
	 * @return true if the block should be rendered as complete
	 */
	@SideOnly(Side.CLIENT)
	public boolean canRender() {
		return canRender;
	}

	/**
	 * @return the unlocalized name of the machine
	 */
	public String getMachineName() {
		return "";
	}

	public void setMachineRunning(boolean running) {
		world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockTile.STATE, running), 2);
	}

	public boolean isUsableByPlayer(EntityPlayer player) {
		return player.getDistance(this.pos.getX(), this.pos.getY(), this.pos.getZ()) < 64;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("canRender", canRender);
		writeNetworkData(nbt);
		return new SPacketUpdateTileEntity(this.pos, 0, nbt);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("canRender", canRender);
		writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound nbt) {

		canRender = nbt.getBoolean("canRender");
		readNetworkData(nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.getNbtCompound();

		canRender = nbt.getBoolean("canRender");
		readNetworkData(nbt);
	}

	public void invalidateComponent(TileEntity tile) {
		setComplete(false);
	}

	/**Called by inventory blocks that are part of the structure
	 ** This includes recipe management etc
	 **/
	public void onInventoryUpdated() {
	}

	/**
	 * @param world world
	 * @param destroyedPos coords of destroyed block
	 * @param blockBroken set true if the block is being broken, otherwise some other means is being used to disassemble the machine
	 */
	public void deconstructMultiBlock(World world, BlockPos destroyedPos, boolean blockBroken, IBlockState state) {
		canRender = completeStructure = false;
		if(this.pos.compareTo(destroyedPos) != 0) 
			world.setBlockState(this.pos, world.getBlockState(pos).withProperty(BlockTile.STATE, false));



		//UNDO all the placeholder blocks
		EnumFacing front = getFrontDirection(state);

		Object[][][] structure = getStructure();
		Vector3F<Integer> offset = getControllerOffset(structure);


		//Mostly to make sure IMultiblocks lose their choke-hold on this machines and to revert placeholder blocks
		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {

					int globalX = pos.getX() + (x - offset.x)*front.getFrontOffsetZ() - (z-offset.z)*front.getFrontOffsetX();
					int globalY = pos.getY() - y + offset.y;
					int globalZ = pos.getZ() - (x - offset.x)*front.getFrontOffsetX()  - (z-offset.z)*front.getFrontOffsetZ();


					TileEntity tile = world.getTileEntity(new BlockPos(globalX, globalY, globalZ));
					//This block is being broken anyway so don't bother
					if(blockBroken 
					&& globalX == destroyedPos.getX() 
					&& globalY == destroyedPos.getY() 
					&& globalZ == destroyedPos.getZ()) {
						if(tile instanceof IMultiblock) {
							((IMultiblock)tile).setIncomplete();
						}
						tile.invalidate();
						continue;
					}
					Block block = world.getBlockState(new BlockPos(globalX, globalY, globalZ)).getBlock();

					destroyBlockAt(new BlockPos(globalX, globalY, globalZ), block, tile);

				}
			}
		}

		resetCache();
		this.markDirty();
		world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
	}

	/**
	 * Called when the multiblock is being deconstructed.  This is called for each block in the structure.
	 * Provided in case of special handling
	 * @param destroyedPos coords of destroyed block
	 * @param block
	 * @param tile
	 */
	protected void destroyBlockAt(BlockPos destroyedPos, Block block, TileEntity tile) {

		//if it's an instance of multiblock structure call its destroyStructure method
		if(block instanceof BlockMultiblockStructure) {
			((BlockMultiblockStructure)block).destroyStructure(world, destroyedPos, world.getBlockState(destroyedPos));
		}

		//If the the tile is a placeholder then make sure to replace it with its original block and tile
		if(tile instanceof TilePlaceholder && !(tile instanceof TileSchematic)) {
			TilePlaceholder placeholder = (TilePlaceholder)tile;

			//Must set incomplete BEFORE changing the block to prevent stack overflow!
			placeholder.setIncomplete();

			world.setBlockState(destroyedPos, placeholder.getReplacedState());

			//Dont try to set a tile if none existed
			if(placeholder.getReplacedTileEntity() != null) {
				NBTTagCompound nbt = new NBTTagCompound();
				placeholder.getReplacedTileEntity().writeToNBT(nbt);

				world.getTileEntity(destroyedPos).readFromNBT(nbt);
			} else if (world.getTileEntity(destroyedPos) != null)
				world.getTileEntity(destroyedPos).invalidate();
		}
		//Make all pointers incomplete
		else if(tile instanceof IMultiblock) {
			((IMultiblock)tile).setIncomplete();
			tile.invalidate();
		}
	}

	public EnumFacing getFrontDirection(IBlockState state) {
		return RotatableBlock.getFront(state);
	}

	public Object[][][] getStructure() {
		return null;
	}

	public boolean attemptCompleteStructure(IBlockState state) {
		//if(!completeStructure)
		canRender = completeStructure = completeStructure(state);
		return completeStructure;
	}

	public void setComplete(boolean complete) {
		completeStructure = complete;
	}

	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> list = new ArrayList<>();
		return list;
	}

	/**
	 * Called when cached Tiles need to be cleared (batteries/IO/etc)
	 */
	public void resetCache() {
		itemInPorts.clear();
		itemOutPorts.clear();
		fluidInPorts.clear();
		fluidOutPorts.clear();
	}


	/**
	 * Use '*' to allow any kind of Hatch, or energy device or anything returned by getAllowableWildcards
	 * Use 'L' for liquid input hatches
	 * Use 'l' for liquid output hatches
	 * Use 'I' for input hatch
	 * Use 'O' for output hatch
	 * Use 'P' for power input
	 * Use 'p' for power output
	 * Use 'D' for data hatch
	 * Use 'c' for the main Block, there can only be one
	 * Use null for anything
	 * Use a Block to force the user to place that block there
	 * @return true if the structure is valid
	 */
	protected boolean completeStructure(IBlockState state) {

		//Make sure the environment is clean
		resetCache();

		Object[][][] structure = getStructure();

		Vector3F<Integer> offset = getControllerOffset(structure);
		List<BlockPos> replacableBlocks = new LinkedList<>();

		EnumFacing front = getFrontDirection(state);

		//Store tile entities for later processing so we don't risk the check failing halfway through leaving half the multiblock assigned
		LinkedList<TileEntity> tiles = new LinkedList<>();

		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {

					//Ignore nulls
					if(structure[y][z][x] == null)
						continue;

					int globalX = pos.getX() + (x - offset.x)*front.getFrontOffsetZ() - (z-offset.z)*front.getFrontOffsetX();
					int globalY = pos.getY() - y + offset.y;
					int globalZ = pos.getZ() - (x - offset.x)*front.getFrontOffsetX()  - (z-offset.z)*front.getFrontOffsetZ();
					BlockPos globalPos = new BlockPos(globalX, globalY, globalZ);

					if(!world.getChunkFromBlockCoords(globalPos).isLoaded())
						return false;

					TileEntity tile = world.getTileEntity(globalPos);
					IBlockState blockState = world.getBlockState(globalPos);
					Block block = blockState.getBlock();
					int meta = block.getMetaFromState(blockState);

					if(block == LibVulpesBlocks.blockPhantom)
						return false;

					if(tile != null)
						tiles.add(tile);

					//If the other block already thinks it's complete just assume valid
					if(tile instanceof TilePointer) {
						TileEntity masterBlock;
						if(((IMultiblock)tile).hasMaster() && (masterBlock = ((IMultiblock)tile).getMasterBlock()) != this) {
							
							//This ~should~ only occur with world edits and being moved to space stations and such
							if(masterBlock == null)
								((IMultiblock)tile).setMasterBlock(this.pos);
							
							if(((IMultiblock)tile).getMasterBlock().getPos().equals(getPos())) {
								((IMultiblock)tile).setMasterBlock(getPos());
								continue;
							}
						}
						else if(((IMultiblock)tile).getMasterBlock() == this) 
							continue;
					}
					//Make sure the structure is valid
					if(!(structure[y][z][x] instanceof Character
					&& (Character)structure[y][z][x] == 'c')
					&& !(structure[y][z][x] instanceof Block
					&& structure[y][z][x] == Blocks.AIR
					&& world.isAirBlock(globalPos))
					&& !getAllowableBlocks(structure[y][z][x]).contains(new BlockMeta(block,meta))) {

						//Can it be replaced?
						if(block.isReplaceable(world, globalPos)
						&& structure[y][z][x] instanceof Block
						&& structure[y][z][x] == Blocks.AIR)
							replacableBlocks.add(globalPos);
						else {
							LibVulpes.proxy.spawnParticle("errorBox", world, globalX, globalY, globalZ, 0, 0, 0);
							return false;
						}
					}
				}
			}
		}

		//Notify all blocks in the structure that it's being build and assimilate them
		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {

					int globalX = pos.getX() + (x - offset.x)*front.getFrontOffsetZ() - (z-offset.z)*front.getFrontOffsetX();
					int globalY = pos.getY() - y + offset.y;
					int globalZ = pos.getZ() - (x - offset.x)*front.getFrontOffsetX()  - (z-offset.z)*front.getFrontOffsetZ();
					BlockPos globalPos = new BlockPos(globalX, globalY, globalZ);


					TileEntity tile = world.getTileEntity(globalPos);
					IBlockState blockState = world.getBlockState(globalPos);
					Block block = blockState.getBlock();
					int meta = block.getMetaFromState(blockState);

					if(block instanceof BlockMultiBlockComponentVisible) {
						((BlockMultiBlockComponentVisible)block).hideBlock(world, globalPos, blockState);

						tile = world.getTileEntity(globalPos);

						if(tile instanceof IMultiblock)
							((IMultiblock)tile).setComplete(globalPos);
					}
					else if(block instanceof BlockMultiblockStructure) {
						if(shouldHideBlock(world, globalPos, blockState))
							((BlockMultiblockStructure)block).hideBlock(world, globalPos, blockState);
					}

					if(structure[y][z][x] != null && !block.isAir(blockState, world, globalPos) && !(tile instanceof IMultiblock) && !(tile instanceof TileMultiBlock)) {
						replaceStandardBlock(globalPos, blockState, tile);
					}
				}
			}
		}

		//Now that we know the multiblock is valid we can assign
		for(TileEntity tile : tiles) {
			integrateTile(tile);
		}
		
		//Replace and drop replaceable blocks
		for(BlockPos pos : replacableBlocks) {
			world.setBlockToAir(pos);
		}
		
		markDirty();
		world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
		return true;
	}


	public List<BlockMeta> getAllowableBlocks(Object input) {
		if(input instanceof Character && (Character)input == '*') {
			return getAllowableWildCardBlocks();
		}
		else if(input instanceof Character  && charMapping.containsKey(input)) {
			return charMapping.get(input);
		}
		else if(input instanceof String) { //OreDict entry
			List<ItemStack> stacks = OreDictionary.getOres((String)input);
			List<BlockMeta> list = new LinkedList<>();
			for(ItemStack stack : stacks) {
				//stack.get
				Block block = Block.getBlockFromItem(stack.getItem());
				list.add(new BlockMeta(block, stack.getItem().getMetadata(stack.getItemDamage())));
			}
			return list;
		}
		else if(input instanceof Block) {
			List<BlockMeta> list = new ArrayList<>();
			list.add(new BlockMeta((Block) input, BlockMeta.WILDCARD));
			return list;
		}
		else if(input instanceof BlockMeta) {
			List<BlockMeta> list = new ArrayList<>();
			list.add((BlockMeta) input);
			return list;
		}
		else if(input instanceof Block[]) {
			List<BlockMeta> list = new ArrayList<>();
			for(Block b : (Block[])input) list.add(new BlockMeta(b));
			return list;
		}
		else if(input instanceof List) {
			return (List<BlockMeta>)input;
		}
		return new ArrayList<>();
	}

	public boolean shouldHideBlock(World world, BlockPos pos, IBlockState tile) {
		return false;
	}

	/**
	 * Called when replacing a block that is not specifically designed to be compatible with the multiblocks.  Eg iron black
	 * Most multiblocks have a renderer and so these blocks are converted to an invisible pointer
	 * @return
	 */
	protected void replaceStandardBlock(BlockPos newPos, IBlockState state, TileEntity tile) {

		world.setBlockState(newPos, LibVulpesBlocks.blockPlaceHolder.getDefaultState());
		TilePlaceholder newTile = (TilePlaceholder)world.getTileEntity(newPos);

		newTile.setReplacedBlockState(state);
		newTile.setReplacedTileEntity(tile);
		newTile.setMasterBlock(pos);
	}

	/**
	 * This is used so classes extending this one can have their own handling of tiles without overriding the method
	 * @param tile Current tile in multiblock
	 */
	protected void integrateTile(TileEntity tile) {
		if(tile instanceof IMultiblock)
			((IMultiblock) tile).setComplete(pos);

		if(tile instanceof TileInputHatch)
			itemInPorts.add((IInventory) tile);
		else if(tile instanceof TileOutputHatch) 
			itemOutPorts.add((IInventory) tile);
		else if(tile instanceof TileFluidHatch) {
			TileFluidHatch liquidHatch = (TileFluidHatch)tile;
			if(liquidHatch.isOutputOnly())
				fluidOutPorts.add(liquidHatch);
			else
				fluidInPorts.add(liquidHatch);
		}
	}

	protected Vector3F<Integer> getControllerOffset(Object[][][] structure) {
		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {
					if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'c')
						return new Vector3F<>(x, y, z);
				}
			}
		}
		return null;
	}

	protected void writeNetworkData(NBTTagCompound nbt) {

	}

	protected void readNetworkData(NBTTagCompound nbt) {

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		writeNetworkData(nbt);
		nbt.setBoolean("completeStructure", completeStructure);
		nbt.setBoolean("canRender", canRender);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		readNetworkData(nbt);
		completeStructure = nbt.getBoolean("completeStructure");
		canRender = nbt.getBoolean("canRender");
	}
}
