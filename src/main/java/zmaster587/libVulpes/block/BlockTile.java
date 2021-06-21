package zmaster587.libVulpes.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.util.IAdjBlockUpdate;

public class BlockTile extends RotatableBlock {

	protected Class<? extends TileEntity> tileClass;
	protected int guiId;
	public static final PropertyBool STATE = PropertyBool.create("state");

	public BlockTile(Class<? extends TileEntity> tileClass, int guiId) {
		super(Material.IRON);
		this.tileClass = tileClass;
		this.guiId = guiId;
		this.setDefaultState(this.blockState.getBaseState().withProperty(STATE, false));
	}

	public BlockTile(Class<? extends TileEntity> tileClass, int guiId, Material material) {
		super(material);
		this.tileClass = tileClass;
		this.guiId = guiId;
		this.setDefaultState(this.blockState.getBaseState().withProperty(STATE, false));
	}

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, STATE);
    }
	
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn,
    		BlockPos pos) {
    	return super.getActualState(state, worldIn, pos);
    }
    
    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
    	return state.getValue(FACING).getIndex() | (state.getValue(STATE) ? 8 : 0);
    }
    
	public void setBlockState(World world, IBlockState state, BlockPos pos, boolean newState) {
		world.setBlockState(pos, state.withProperty(STATE, newState), 2);
		world.markBlockRangeForRenderUpdate(pos, pos);
	}
    
	@Override
	public boolean hasTileEntity(IBlockState state) { return true; }

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		try {
			return tileClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!world.isRemote)
			player.openGui(LibVulpes.instance, guiId, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(world, pos,
				neighbor);
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof IAdjBlockUpdate)
			((IAdjBlockUpdate)tile).onAdjacentBlockUpdated();

	}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntity tile = world.getTileEntity(pos);

		//This code could use some optimization -Dark
		if (!world.isRemote && tile instanceof IInventory)
		{
			IInventory inventory = (IInventory)tile;
			for (int i1 = 0; i1 < inventory.getSizeInventory(); ++i1)
			{
				ItemStack itemstack = inventory.getStackInSlot(i1);

				if (!itemstack.isEmpty())
				{
					float f = world.rand.nextFloat() * 0.8F + 0.1F;
					float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
					EntityItem entityitem;

					for (float f2 = world.rand.nextFloat() * 0.8F + 0.1F; itemstack.getCount() > 0; world.spawnEntity(entityitem))
					{
						int j1 = world.rand.nextInt(21) + 10;

						if (j1 > itemstack.getCount())
						{
							j1 = itemstack.getCount();
						}
						Item oldItem = itemstack.getItem();
						ItemStack oldStack = itemstack.copy();
						itemstack.setCount(itemstack.getCount() - j1);
						entityitem = new EntityItem(world, (float)pos.getX() + f, (float)pos.getY() + f1, (float)pos.getZ() + f2, new ItemStack(oldItem, j1, itemstack.getItemDamage()));
						float f3 = 0.05F;
						entityitem.motionX = (float)world.rand.nextGaussian() * f3;
						entityitem.motionY = (float)world.rand.nextGaussian() * f3 + 0.2F;
						entityitem.motionZ = (float)world.rand.nextGaussian() * f3;

						if (oldStack.hasTagCompound())
						{
							NBTTagCompound tag = oldStack.getTagCompound();
							entityitem.getItem().setTagCompound(tag == null ? null : tag.copy());
						}
						world.spawnEntity(entityitem);
					}
				}
			}

		}

		super.breakBlock(world, pos, state);
	}
}
