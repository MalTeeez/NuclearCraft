package nc.packet;

import io.netty.buffer.ByteBuf;
import nc.NuclearCraft;
import nc.tile.machine.TileAssembler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketAssemblerState implements IMessage {

	private int x;
	private int y;
	private int z;

	private TileAssembler.Mode mode;

	public PacketAssemblerState() {}

	public PacketAssemblerState(TileAssembler tile) {
		x = tile.xCoord;
		y = tile.yCoord;
		z = tile.zCoord;
		mode = tile.getMode();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeShort(mode.ordinal());
	}

	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		short ordinal = buf.readShort();
		TileAssembler.Mode[] modes = TileAssembler.Mode.values();
		mode = ordinal >= 0 && ordinal < modes.length ? modes[ordinal] : null;
	}

	public static class Handler implements IMessageHandler<PacketAssemblerState, IMessage> {
		public IMessage onMessage(PacketAssemblerState message, MessageContext ctx) {
			EntityPlayer player = NuclearCraft.NCProxy.getPlayerEntity(ctx);
			// The position comes straight from the client, only accept loaded tiles within reach
			if (message.mode == null || !player.worldObj.blockExists(message.x, message.y, message.z) || player.getDistanceSq(message.x + 0.5D, message.y + 0.5D, message.z + 0.5D) > 64) return null;
			TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
			if(te instanceof TileAssembler) {
				TileAssembler me = (TileAssembler) te;
				me.setMode(message.mode);
				player.worldObj.markBlockForUpdate(message.x, message.y, message.z);
			}
			return null;
		}
	}
}