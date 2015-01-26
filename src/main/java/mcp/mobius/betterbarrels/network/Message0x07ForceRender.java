package mcp.mobius.betterbarrels.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class Message0x07ForceRender extends SimpleChannelInboundHandler<Message0x07ForceRender> implements IBarrelMessage {

    public int x;
    public int y;
    public int z;

    public Message0x07ForceRender() {}

    public Message0x07ForceRender(int xCoord, int yCoord, int zCoord)
    {
        this.x = xCoord;
        this.y = yCoord;
        this.z = zCoord;
    }

    public void encodeInto(ChannelHandlerContext ctx, IBarrelMessage msg, ByteBuf target)
            throws Exception
    {
        target.writeInt(this.x);
        target.writeInt(this.y);
        target.writeInt(this.z);
    }

    public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IBarrelMessage rawmsg)
    {
        Message0x07ForceRender msg = (Message0x07ForceRender)rawmsg;
        msg.x = dat.readInt();
        msg.y = dat.readInt();
        msg.z = dat.readInt();
    }

    protected void channelRead0(ChannelHandlerContext ctx, Message0x07ForceRender msg)
            throws Exception
    {
        Minecraft.func_71410_x().field_71441_e.func_147471_g(msg.x, msg.y, msg.z);
    }

}
