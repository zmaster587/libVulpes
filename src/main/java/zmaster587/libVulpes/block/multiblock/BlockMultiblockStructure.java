package zmaster587.libVulpes.block.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;

/**
 * Block which is integrated into the multiblock structure.  When a structure is formed the block
 * continues to render and when broken will alert the master block
 * most significant damage bit indicates if the block is fully formed
 */
public class BlockMultiblockStructure extends Block {
	
	public static final PropertyInteger VARIANT = PropertyInteger.create("varient", 0, 15);
	protected BlockMultiblockStructure(Material material) {
		super(material);
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT,0));
	}
	
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, VARIANT);
    }
	
    @Override
    public int getMetaFromState(IBlockState state) {
    	return state.getValue(VARIANT);
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
    	return this.blockState.getBaseState().withProperty(VARIANT,meta);
    }
    
	/**
	 * Turns the block invisible or in the case of BlockMultiBlockComponentVisible makes it create a tileEntity
	 * @param world
	 * @param pos
	 * @param state
	 */
	public void hideBlock(World world, BlockPos pos, IBlockState state) {
		world.setBlockState(pos, state.withProperty(VARIANT, state.getValue(VARIANT) | 8));
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(VARIANT) & 7;
	}
	
	public void completeStructure(World world, BlockPos pos, IBlockState state) {
		
	}

	public void destroyStructure(World world, BlockPos pos, IBlockState state) {
		world.setBlockState(pos, state.withProperty(VARIANT, state.getValue(VARIANT) & 7));
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState blockState,
			IBlockAccess blockAccess, BlockPos pos, EnumFacing direction) {
		
		return super.shouldSideBeRendered(blockState, blockAccess, pos, direction) && blockState.getValue(VARIANT) < 8;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos,
			IBlockState state) {
		
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof IMultiblock) {
			IMultiblock tileMulti = (IMultiblock)tile;
			
			if(tileMulti.hasMaster()) {
				if(tileMulti.getMasterBlock() instanceof TileMultiBlock)
					((TileMultiBlock)tileMulti.getMasterBlock()).deconstructMultiBlock(world, pos,true, world.getBlockState(tileMulti.getMasterBlock().getPos()));
			}
		}
		super.breakBlock(world, pos, state);
	}
}
