package zmaster587.libVulpes.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;

public interface INetworkItem {
	
	//Writes data to the network given an id of what type of packet to write
	void writeDataToNetwork(ByteBuf out, byte id, @Nonnull ItemStack stack);
	
	//Reads data, stores read data to nbt to be passed to useNetworkData
	void readDataFromNetwork(ByteBuf in, byte packetId, NBTTagCompound nbt, @Nonnull ItemStack stack);
	
	//Applies changes from network
	void useNetworkData(EntityPlayer player, Side side, byte id, NBTTagCompound nbt, @Nonnull ItemStack stack);
}
