package zmaster587.libVulpes.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;


public interface INetworkMachine {
	
	//Writes data to the network given an id of what type of packet to write
	void writeDataToNetwork(ByteBuf out, byte id);
	
	//Reads data, stores read data to nbt to be passed to useNetworkData
	void readDataFromNetwork(ByteBuf in, byte packetId, NBTTagCompound nbt);
	
	//Applies changes from network
	void useNetworkData(EntityPlayer player, Side side, byte id, NBTTagCompound nbt);
}
