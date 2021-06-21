package zmaster587.libVulpes.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import zmaster587.libVulpes.util.InputSyncHandler;

public class PacketChangeKeyState extends BasePacket {

	int key;
	boolean state;
	
	public PacketChangeKeyState(int key, boolean state) {
		this.key = key;
		this.state = state;
	}
	
	public PacketChangeKeyState() {}
	
	@Override
	public void write(ByteBuf out) {
		out.writeInt(key);
		out.writeBoolean(state);
	}

	@Override
	public void readClient(ByteBuf in) {
		in.readInt();
		in.readBoolean();
	}

	@Override
	public void read(ByteBuf in) {
		key = in.readInt();
		state = in.readBoolean();
	}

	@Override
	public void executeClient(EntityPlayer thePlayer) {
		
	}

	@Override
	public void executeServer(EntityPlayerMP player) {
		InputSyncHandler.updateKeyPress(player, key, state);
	}

}
