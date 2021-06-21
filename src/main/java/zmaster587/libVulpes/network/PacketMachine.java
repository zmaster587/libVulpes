package zmaster587.libVulpes.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.libVulpes.util.INetworkMachine;

public class PacketMachine extends BasePacket {

	INetworkMachine machine;

	NBTTagCompound nbt;

	byte packetId;

	public PacketMachine() {
		nbt = new NBTTagCompound();
	}

	public PacketMachine(INetworkMachine machine, byte packetId) {
		this();
		this.machine = machine;
		this.packetId = packetId;
	}


	@Override
	public void write(ByteBuf outline) {
		outline.writeInt(((TileEntity)machine).getWorld().provider.getDimension());
		outline.writeInt(((TileEntity)machine).getPos().getX());
		outline.writeInt(((TileEntity)machine).getPos().getY());
		outline.writeInt(((TileEntity)machine).getPos().getZ());

		outline.writeByte(packetId);

		machine.writeDataToNetwork(outline, packetId);
		//ByteBuf buffer
		//outline.writeBytes(out.toByteArray());
		//outline.
		//outline. =  (ByteBuf)out.toByteArray();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readClient(ByteBuf in) {
		//DEBUG:
		in.readInt();
		World world = Minecraft.getMinecraft().world;
		int x = in.readInt();
		int y = in.readInt();
		int z = in.readInt();
		packetId = in.readByte();

		TileEntity ent = world.getTileEntity(new BlockPos(x, y, z));

		if(ent instanceof INetworkMachine) {
			machine = (INetworkMachine)ent;
			machine.readDataFromNetwork(in, packetId, nbt);
		}
		else {
			//Error
		}
	}

	@Override
	public void read(ByteBuf in) {
		//DEBUG:
		int temp = in.readInt();
		World world = DimensionManager.getWorld(temp);

		int x = in.readInt();
		int y = in.readInt();
		int z = in.readInt();
		packetId = in.readByte();

		BlockPos pos = new BlockPos(x,y,z);
		Chunk chunk = world.getChunkFromBlockCoords(pos);

		if(chunk.isLoaded()) {
			TileEntity ent = world.getTileEntity(pos);

			if(ent instanceof INetworkMachine) {
				machine = (INetworkMachine)ent;
				machine.readDataFromNetwork(in, packetId, nbt);
			}
			else {
				//Error
			}
		}
	}

	public void executeClient(EntityPlayer player) {
		//Machine can be null if not all chunks are loaded
		if(machine != null)
			machine.useNetworkData(player, Side.CLIENT, packetId, nbt);
	}

	public void executeServer(EntityPlayerMP player) {
		if(machine != null)
			machine.useNetworkData(player, Side.SERVER, packetId, nbt);
	}

	public void execute(EntityPlayer player, Side side) {
		if(machine != null)
			machine.useNetworkData(player, side, packetId, nbt);
	}

}
