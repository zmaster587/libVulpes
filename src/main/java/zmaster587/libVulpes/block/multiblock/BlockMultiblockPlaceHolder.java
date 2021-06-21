package zmaster587.libVulpes.block.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TilePlaceholder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.List;

/**
 * Invisible block used to store blocks that are part of a completed multi-block structure
 * 
 */
public class BlockMultiblockPlaceHolder extends BlockContainer {

	public BlockMultiblockPlaceHolder() {
		super(Material.IRON);
	}

	//Make invisible
	@Override
	@ParametersAreNullableByDefault
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) { return false; }
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	//Make sure to get the block this one is storing rather than the placeholder itself
	@Override
	@Nonnull
	public ItemStack getPickBlock(@Nullable IBlockState state, RayTraceResult target,
			World world, @Nonnull BlockPos pos, EntityPlayer player) {
		
		TilePlaceholder tile = (TilePlaceholder)world.getTileEntity(pos);
		if(tile != null && tile.getReplacedState() != null)
			return new ItemStack(tile.getReplacedState().getBlock(), 1, tile.getReplacedMeta());
		else return ItemStack.EMPTY;
	}
	
	

	@Override
	public void getDrops(NonNullList list, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {}
	
	@Override
	public void onBlockHarvested(World world, BlockPos pos,
			IBlockState state, EntityPlayer player) {
		
		super.onBlockHarvested(world, pos, state, player);

		if(!world.isRemote && !player.capabilities.isCreativeMode) {
			TilePlaceholder tile = (TilePlaceholder)world.getTileEntity(pos);

			IBlockState newBlockState = tile.getReplacedState();
			Block newBlock = newBlockState.getBlock();

			if(newBlock != Blocks.AIR && player.canHarvestBlock(newBlockState)) {
				List<ItemStack> stackList = newBlock.getDrops(world, pos, newBlockState, 0);

				for(ItemStack stack : stackList) {
					EntityItem entityItem = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
					world.spawnEntity(entityItem);

				}
			}
		}
	}

	@Override
	@ParametersAreNonnullByDefault
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity tile = world.getTileEntity(pos);

		if(tile instanceof TilePlaceholder) {
			tile = ((TilePlaceholder)tile).getMasterBlock();
			if(tile instanceof TileMultiBlock)
				((TileMultiBlock)tile).deconstructMultiBlock(world, pos, true, world.getBlockState(tile.getPos()));
		}
		
		super.breakBlock(world, pos, state);
	}

	@Override
	@ParametersAreNullableByDefault
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TilePlaceholder();
	}

	@Override
	public TileEntity createNewTileEntity(@Nullable World worldIn, int meta) {
		return new TilePlaceholder();
	}
}
