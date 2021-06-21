package zmaster587.libVulpes.block;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.GuiHandler;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class RotatableMachineBlock extends RotatableBlock {
	protected final Random random = new Random();
	
	static  PropertyBool STATE = PropertyBool.create("state");

	public RotatableMachineBlock(Material par2Material) {
		super(par2Material);
		this.isBlockContainer = true;
	}
	
	public int getMetaFromState(IBlockState state) {
		int flag = state.getValue(STATE) ? 8 : 0;
		return state.getValue(FACING).getIndex() | flag;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		boolean state = meta >= 8;
		if(meta > 1)
			return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta & 7]).withProperty(STATE, state);
		else
			return this.getDefaultState().withProperty(FACING, EnumFacing.NORTH).withProperty(STATE, state);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, STATE);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		tooltip.add(ChatFormatting.ITALIC + LibVulpes.proxy.getLocalizedString("machine.tooltip.multiblock"));
	}

    /**
     * Called on server worlds only when the block has been replaced by a different block ID, or the same block with a
     * different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old
     * metadata
     */
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		IInventory tileentitychest = (IInventory)world.getTileEntity(pos);

		if (tileentitychest != null)
		{
			for (int j1 = 0; j1 < tileentitychest.getSizeInventory(); ++j1)
			{
				ItemStack itemstack = tileentitychest.getStackInSlot(j1);

				if (!itemstack.isEmpty())
				{
					float f = this.random.nextFloat() * 0.8F + 0.1F;
					float f1 = this.random.nextFloat() * 0.8F + 0.1F;
					EntityItem entityitem;

					for (float f2 = this.random.nextFloat() * 0.8F + 0.1F; itemstack.getCount() > 0; world.spawnEntity(entityitem))
					{
						int k1 = this.random.nextInt(21) + 10;

						if (k1 > itemstack.getCount())
						{
							k1 = itemstack.getCount();
						}

						itemstack.setCount(itemstack.getCount() - k1);

						entityitem = new EntityItem(world, (float)pos.getX() + f, (float)pos.getY() + f1, (float)pos.getZ() + f2, new ItemStack(itemstack.getItem(), k1, itemstack.getItemDamage()));
						float f3 = 0.05F;
						entityitem.motionX = (float)this.random.nextGaussian() * f3;
						entityitem.motionY = (float)this.random.nextGaussian() * f3 + 0.2F;
						entityitem.motionZ = (float)this.random.nextGaussian() * f3;

						if (itemstack.hasTagCompound())
						{
							NBTTagCompound tag = itemstack.getTagCompound();
							entityitem.getItem().setTagCompound(tag == null ? null : tag.copy());
						}
					}
				}
			}
		}

		super.breakBlock(world,pos, state);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos,
			IBlockState state, EntityPlayer playerIn, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		//Handle gui through modular system
		if(!worldIn.isRemote )
			playerIn.openGui(LibVulpes.instance, GuiHandler.guiId.MODULAR.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());

		return true;
	}
	
	/**
	 * Called when the block receives a BlockEvent - see World.addBlockEvent. By default, passes it on to the tile
	 * entity at this location. Args: world, x, y, z, blockID, EventID, event parameter
	 */
	/*public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6)
	{
		super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
		TileEntity tileentity = par1World.getTileEntity(par2, par3, par4);
		return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
	}*/
}
