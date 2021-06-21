package zmaster587.libVulpes.block.multiblock;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.tile.IComparatorOverride;
import zmaster587.libVulpes.tile.energy.TilePlugBase;

public class BlockMultiMachineBattery extends BlockMultiblockStructure {

	protected Class<? extends TileEntity> tileClass;
	protected int guiId;
	
	public BlockMultiMachineBattery(Material material, Class<? extends TilePlugBase> tileClass, int guiId) {
		super(material);
		this.tileClass = tileClass;
		this.guiId = guiId;
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer playerIn, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!worldIn.isRemote)
			playerIn.openGui(LibVulpes.instance, guiId, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		try {
			TilePlugBase tile = (TilePlugBase) tileClass.newInstance();
			tile.setTeir(1);
			return tile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
