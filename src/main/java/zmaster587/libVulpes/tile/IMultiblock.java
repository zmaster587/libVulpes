package zmaster587.libVulpes.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public interface IMultiblock {
	
	boolean hasMaster();
	
	TileEntity getMasterBlock();
	
	void setComplete(BlockPos pos);
	
	void setIncomplete();
	
	void setMasterBlock(BlockPos pos);
}
