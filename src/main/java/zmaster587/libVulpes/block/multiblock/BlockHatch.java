package zmaster587.libVulpes.block.multiblock;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInputHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInventoryHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileOutputHatch;
import zmaster587.libVulpes.util.FluidUtils;

import java.util.Random;

public class BlockHatch extends BlockMultiblockStructure {

	private final Random random = new Random();

	public BlockHatch(Material material) {
		super(material);
		isBlockContainer = true;
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT,0));
	}

	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, VARIANT);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public void getSubBlocks(CreativeTabs tab,
			NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
		list.add(new ItemStack(this, 1, 2));
		list.add(new ItemStack(this, 1, 3));
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		//TODO: multiple sized Hatches
		int metadata = state.getValue(VARIANT);
		if((metadata & 7) == 0)
			return new TileInputHatch(4);
		else if((metadata & 7) == 1)
			return new TileOutputHatch(4);
		else if((metadata & 7) == 2)
			return new TileFluidHatch(false);	
		else if((metadata & 7) == 3)
			return new TileFluidHatch(true);	

		return null;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {

		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof IInventory) {
			IInventory inventory = (IInventory)tile;
			for(int i = 0; i < inventory.getSizeInventory(); i++) {
				ItemStack stack = inventory.getStackInSlot(i);

				if(stack.isEmpty())
					continue;

				EntityItem entityitem = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);

				float mult = 0.05F;

				entityitem.motionX = (float)this.random.nextGaussian() * mult;
				entityitem.motionY = (float)this.random.nextGaussian() * mult + 0.2F;
				entityitem.motionZ = (float)this.random.nextGaussian() * mult;

				world.spawnEntity(entityitem);
			}
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state)
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		TileEntity tile = worldIn.getTileEntity(pos);
		//Fluid comparator size
		if (tile instanceof TileFluidHatch)
			return (15 * ((TileFluidHatch) tile).getFluidTank().getFluidAmount())/((TileFluidHatch) tile).getFluidTank().getCapacity();
		//Otherwise, try if it's an inventory - this code is adapted from how chests do it because we don't extend BlockContainer
		else if (tile instanceof TileInventoryHatch) {
			TileInventoryHatch tile2 = ((TileInventoryHatch) tile);
			int i = 0;
			float f = 0.0F;

			for (int j = 0; j < tile2.getSizeInventory(); ++j) {
				ItemStack itemstack = tile2.getStackInSlot(j);

				if (!itemstack.isEmpty()) {
					f += (float) itemstack.getCount() / (float) Math.min(tile2.getInventoryStackLimit(), itemstack.getMaxStackSize());
					i++;
				}
			}
			f = f / (float) tile2.getSizeInventory();
			return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
		}
		//Failing that, we do nothing
		return 0;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState blockState,
			IBlockAccess blockAccess, BlockPos pos, EnumFacing direction) {


		boolean isPointer = blockAccess.getTileEntity(pos.offset(direction.getOpposite())) instanceof TilePointer;
		if(isPointer)
			isPointer = !(((TilePointer)blockAccess.getTileEntity(pos.offset(direction.getOpposite()))).getMasterBlock() instanceof TileMultiBlock);

		return blockState.getValue(VARIANT) < 8;

	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		int meta = worldIn.getBlockState(pos).getValue(VARIANT);
		TileEntity tile = worldIn.getTileEntity(pos);
		ItemStack heldItem = playerIn.getHeldItem(hand);

		//Do some fancy fluid stuff
		//This code is modified from the Forge fluid handler that does both actions at once, as we want only one that corresponds to the correct hatch type
		if (FluidUtils.containsFluid(playerIn.getHeldItem(hand)) && tile instanceof TileFluidHatch) {
			IItemHandler playerInventory = playerIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			if (((TileFluidHatch)tile).isOutputOnly()) {
				FluidActionResult fluidActionResult = FluidUtil.tryFillContainerAndStow(heldItem, ((TileFluidHatch)tile).getFluidTank(), playerInventory, Integer.MAX_VALUE, playerIn, true);
				if (fluidActionResult.isSuccess())
				    playerIn.setHeldItem(hand, fluidActionResult.getResult());
			} else {
				FluidActionResult fluidActionResult = FluidUtil.tryEmptyContainerAndStow(heldItem, ((TileFluidHatch)tile).getFluidTank(), playerInventory, Integer.MAX_VALUE, playerIn, true);
				if (fluidActionResult.isSuccess())
					playerIn.setHeldItem(hand, fluidActionResult.getResult());
			}

		}
		//Handle gui through modular system
		 else if((meta & 7) < 8 && !worldIn.isRemote)
			playerIn.openGui(LibVulpes.instance, GuiHandler.guiId.MODULAR.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());

		return true;
	}
}
