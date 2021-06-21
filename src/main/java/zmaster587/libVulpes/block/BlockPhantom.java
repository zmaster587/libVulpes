package zmaster587.libVulpes.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import zmaster587.libVulpes.tile.TileSchematic;
import zmaster587.libVulpes.tile.multiblock.TilePlaceholder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;

public class BlockPhantom extends Block {

	public BlockPhantom(Material mat) {
		super(mat);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) { return false; }
	
	@Override
	public void getDrops(NonNullList list, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) { }
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState();
	}
	
	
	@Override
	@ParametersAreNullableByDefault
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileSchematic();
	}
	
	@Override
	@Nonnull
	@ParametersAreNonnullByDefault
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target,
			World world, BlockPos pos, EntityPlayer player) {
		TileEntity tile = world.getTileEntity(pos);
		
		if(tile instanceof TilePlaceholder && ((TilePlaceholder) tile).getReplacedState() != null) {
			Block block = ((TilePlaceholder)tile).getReplacedState().getBlock();


			return ((TilePlaceholder)tile).getReplacedState().getBlock().getPickBlock(((TilePlaceholder)tile).getReplacedState(), target, world, pos, player);
		}
		return super.getPickBlock(state, target, world, pos, player);
	}

	@Override
	@Nonnull
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	@ParametersAreNullableByDefault
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, @Nullable BlockPos pos) {
		return true;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		// TODO Auto-generated method stub
		return null;
	}
}
