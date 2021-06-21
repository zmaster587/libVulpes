package zmaster587.libVulpes.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class RotatableBlock extends Block {

	public static final PropertyEnum<EnumFacing> FACING =  BlockHorizontal.FACING;

	public RotatableBlock(Material par2Material) {
		super(par2Material);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		if(meta > 1)
			return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta & 7]);
		else
			return this.getDefaultState().withProperty(FACING, EnumFacing.NORTH);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	
	
	@Override
	@Nonnull
	public IBlockState getStateForPlacement(World world, BlockPos pos,
			EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer, EnumHand hand) {
		return  getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());//super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
	}
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, @Nonnull ItemStack stack) {

		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
	}

	public static EnumFacing getFront(IBlockState state) {
		if(state.getBlock() instanceof RotatableBlock)
			return state.getValue(FACING);
		return EnumFacing.UP;
	}
}
