package zmaster587.libVulpes.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.interfaces.ILinkableTile;

import javax.annotation.Nonnull;

public class TilePointer extends TileEntity implements IMultiblock, ILinkableTile {
	BlockPos masterBlockPos;

	TileEntity masterBlock;

	public TilePointer() {
		super();
		masterBlockPos = null;
	}

	public TilePointer(BlockPos pos) {
		super();
		masterBlockPos = pos;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos,
			IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public boolean onLinkStart(@Nonnull ItemStack item, TileEntity entity, EntityPlayer player, World world) {
		if(hasMaster()) {
			TileEntity master = this.getMasterBlock();
			if(master instanceof ILinkableTile) {
				return ((ILinkableTile)master).onLinkStart(item, master, player, world);
			}
		}
		return false;
	}
	public boolean onLinkComplete(@Nonnull ItemStack item, TileEntity entity, EntityPlayer player, World world) {
		if(hasMaster()) {
			TileEntity master = this.getMasterBlock();
			if(master instanceof ILinkableTile) {
				return ((ILinkableTile)master).onLinkComplete(item, master, player, world);
			}
		}
		return false;
	}

	public boolean allowRedstoneOutputOnSide(EnumFacing facing) {
		return true;
	}
	
	public boolean isSet() { return masterBlockPos != null;}

	public BlockPos getMasterPos() {
		return masterBlockPos;
	}

	public void setBlockPos(BlockPos pos) {
		masterBlockPos = pos;
	}

	@Override
	public void setMasterBlock(BlockPos pos) {
		setComplete(pos);
	}

	public TileEntity getFinalPointedTile() {
		TileEntity pointedTile;

		try  {
			if(world.isAreaLoaded(masterBlockPos, 1))
				pointedTile = this.world.getTileEntity(masterBlockPos);
			else
				pointedTile = null;
		} catch(NullPointerException e) {
			return null;
		}

		if(pointedTile == null)
			return null;
		else if(pointedTile instanceof TilePointer)
			try {
				return ((TilePointer)pointedTile).getFinalPointedTile();
			} catch (StackOverflowError e) {
				LibVulpes.logger.warn("Stack overflow has occured with tile at location " + getPos() + ".  The game has been prevented from crashing (in theory)");
				return null;
			}
		else
			return pointedTile;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		this.readFromNBT(tag);
	}
	
	@Override
	public boolean hasMaster() {
		return isSet();
	}

	@Override
	public TileEntity getMasterBlock() {
		if(hasMaster()) {
			if(masterBlock == null || masterBlock.isInvalid())
				masterBlock = this.world.getTileEntity(masterBlockPos);
			return masterBlock;
		}
		return null;
	}

	public boolean canUpdate() {return false;}

	@Override
	public void setComplete(BlockPos pos) {
		masterBlockPos = pos;
	}

	@Override
	public void setIncomplete() {
		masterBlockPos = null;
		masterBlock = null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);

		return writeToNBTHelper(nbtTagCompound);
	}

	protected NBTTagCompound writeToNBTHelper(NBTTagCompound nbtTagCompound) {
		if(masterBlockPos != null) {
			nbtTagCompound.setInteger("masterX", masterBlockPos.getX());
			nbtTagCompound.setInteger("masterY", masterBlockPos.getY());
			nbtTagCompound.setInteger("masterZ", masterBlockPos.getZ());
		}
		return nbtTagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);

		readFromNBTHelper(nbtTagCompound);
	}

	protected void readFromNBTHelper(NBTTagCompound nbtTagCompound) {
		if(nbtTagCompound.hasKey("masterX")) {
			masterBlockPos = new BlockPos(nbtTagCompound.getInteger("masterX"),
					nbtTagCompound.getInteger("masterY"),
					nbtTagCompound.getInteger("masterZ"));
		}
		else
			masterBlockPos = null;
	}
}
