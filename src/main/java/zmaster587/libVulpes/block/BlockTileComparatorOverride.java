package zmaster587.libVulpes.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.libVulpes.tile.IComparatorOverride;

public class BlockTileComparatorOverride extends BlockTile {

	public BlockTileComparatorOverride(Class<? extends TileEntity> tileClass, int guiId) {
		super(tileClass, guiId);
	}

	public BlockTileComparatorOverride(Class<? extends TileEntity> tileClass, int guiId, Material material) {
		super(tileClass, guiId, material);
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state)
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof IComparatorOverride) {
        	return ((IComparatorOverride) tile).getComparatorOverride();
		}
        return 0;
	}
}
