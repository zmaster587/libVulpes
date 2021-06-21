package zmaster587.libVulpes.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BasePacket implements IMessage {
	public static final String CHANNEL = "advancedRocketry";
	private static final BiMap<Integer,Class<? extends BasePacket>> idMap;

	static {
		ImmutableBiMap.Builder<Integer, Class<? extends BasePacket>> builder = ImmutableBiMap.builder();
		//builder.put(Integer.valueOf(0), PacketMachine.class);
		idMap = builder.build();
	}

	public static BasePacket constructPacket(int packetId) throws ProtocolException, InstantiationException, IllegalAccessException {
		Class<? extends BasePacket> clazz = idMap.get(packetId);
		if(clazz == null){
			throw new ProtocolException("Protocol Exception!  Unknown Packet Id!");
		} else {
			return clazz.newInstance();
		}
	}




	public static class ProtocolException extends Exception {

		public ProtocolException() {
		}

		public ProtocolException(String message, Throwable cause) {
			super(message, cause);
		}

		public ProtocolException(String message) {
			super(message);
		}

		public ProtocolException(Throwable cause) {
			super(cause);
		}
	}

	public final int getPacketId() {
		if(idMap.inverse().containsKey(getClass())) {
			return idMap.inverse().get(getClass());
		} else {
			throw new RuntimeException("Packet " + getClass().getSimpleName() + " is a missing mapping!");
		}
	}

	public abstract void write(ByteBuf out);

	public abstract void readClient(ByteBuf in);

	public abstract void read(ByteBuf in);

	@SideOnly(Side.CLIENT)
	public abstract void executeClient(EntityPlayer thePlayer);

	public abstract void executeServer(EntityPlayerMP player);

	@Override
	public void fromBytes(ByteBuf buf) {
		switch(FMLCommonHandler.instance().getSide()) {
		case CLIENT:
			readClient(buf);
			break;
		case SERVER:
			read(buf);
			break;
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		write(buf);
	}

	public static class BasePacketHandlerServer implements IMessageHandler<BasePacket, IMessage> {
		@Override
		public IMessage onMessage(BasePacket message, MessageContext ctx) {

			((WorldServer) ctx.getServerHandler().player.world).addScheduledTask(new executor(message, ctx.getServerHandler().player, ctx.side));



			return null;
		}

		public static class executor implements Runnable {

			final Side side;
			final BasePacket packet;
			final EntityPlayer player;

			public executor(BasePacket packet, EntityPlayer player, Side side) {
				this.packet = packet;
				this.player = player;
				this.side = side;
			}

			@Override
			public void run() {
					packet.executeServer((EntityPlayerMP) player);
			}
		}
	}

	public static class BasePacketHandlerClient implements IMessageHandler<BasePacket, IMessage> {
		@Override
		public IMessage onMessage(BasePacket message, MessageContext ctx) {
			switch(ctx.side) {
			case CLIENT:
				Minecraft.getMinecraft().addScheduledTask(new executor(message, Minecraft.getMinecraft().player, ctx.side));
				break;
			case SERVER:
				((WorldServer) ctx.getServerHandler().player.world).addScheduledTask(new executor(message, ctx.getServerHandler().player, ctx.side));
				break;
			}

			return null;
		}

		public static class executor implements Runnable {

			final Side side;
			final BasePacket packet;
			final EntityPlayer player;

			public executor(BasePacket packet, EntityPlayer player, Side side) {
				this.packet = packet;
				this.player = player;
				this.side = side;
			}

			@Override
			public void run() {
				if(side.isClient()) {
					packet.executeClient(player);
				}
				else
					packet.executeServer((EntityPlayerMP) player);
			}
		}
	}
}
