package mcp.mobius.betterbarrels.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;
import net.minecraft.client.Minecraft;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class Message0x08LinkUpdate extends SimpleChannelInboundHandler<Message0x08LinkUpdate> implements IBarrelMessage {

    public int x;
    public int y;
    public int z;
    public boolean isLinked;

    public Message0x08LinkUpdate() {}

    public Message0x08LinkUpdate(TileEntityBarrel barrel)
    {
        this.x = barrel.field_145851_c;
        this.y = barrel.field_145848_d;
        this.z = barrel.field_145849_e;
        this.isLinked = barrel.isLinked;
    }

    public void encodeInto(ChannelHandlerContext ctx, IBarrelMessage msg, ByteBuf target)
            throws Exception
    {
        target.writeInt(this.x);
        target.writeInt(this.y);
        target.writeInt(this.z);
        target.writeBoolean(this.isLinked);
    }

    public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IBarrelMessage rawmsg)
    {
        Message0x08LinkUpdate msg = (Message0x08LinkUpdate)rawmsg;
        msg.x = dat.readInt();
        msg.y = dat.readInt();
        msg.z = dat.readInt();
        msg.isLinked = dat.readBoolean();
    }

    protected void channelRead0(ChannelHandlerContext ctx, Message0x08LinkUpdate msg)
            throws Exception
    {
        TileEntityBarrel barrel = (TileEntityBarrel)Minecraft.func_71410_x().field_71441_e.func_147438_o(msg.x, msg.y, msg.z);
        if (barrel != null)
        {
            barrel.isLinked = msg.isLinked;
            Minecraft.func_71410_x().field_71441_e.func_147471_g(msg.x, msg.y, msg.z);
        }
    }

}
