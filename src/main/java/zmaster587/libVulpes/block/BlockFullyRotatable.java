package zmaster587.libVulpes.block;

import net.minecraft.block.Block;
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

public class BlockFullyRotatable extends Block {
	public static final PropertyEnum<EnumFacing> FACING =  PropertyEnum.create("facing", EnumFacing.class);

	public BlockFullyRotatable(Material par2Material) {
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
		return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta]);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos,
			EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer, EnumHand hand) {
		if(Math.abs(placer.rotationPitch) > 60)
			return getDefaultState().withProperty(FACING, placer.rotationPitch > 0 ? EnumFacing.UP : EnumFacing.DOWN);
		return  getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());//super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, @Nonnull ItemStack stack) {

		if(Math.abs(placer.rotationPitch) > 60) {
			world.setBlockState(pos, state.withProperty(FACING, placer.rotationPitch > 0 ? EnumFacing.UP : EnumFacing.DOWN), 2);
		}
		else
			world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
	}

	public static EnumFacing getFront(IBlockState state) {
		return state.getValue(FACING);
	}
}
