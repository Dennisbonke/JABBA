package mcp.mobius.betterbarrels.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;
import net.minecraft.client.Minecraft;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class Message0x04Structuralupdate extends SimpleChannelInboundHandler<Message0x04Structuralupdate> implements IBarrelMessage {

    public int x;
    public int y;
    public int z;
    public int level;

    public Message0x04Structuralupdate() {}

    public Message0x04Structuralupdate(TileEntityBarrel barrel)
    {
        this.x = barrel.field_145851_c;
        this.y = barrel.field_145848_d;
        this.z = barrel.field_145849_e;
        this.level = barrel.coreUpgrades.levelStructural;
    }

    public void encodeInto(ChannelHandlerContext ctx, IBarrelMessage msg, ByteBuf target)
            throws Exception
    {
        target.writeInt(this.x);
        target.writeInt(this.y);
        target.writeInt(this.z);
        target.writeInt(this.level);
    }

    public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IBarrelMessage rawmsg)
    {
        Message0x04Structuralupdate msg = (Message0x04Structuralupdate)rawmsg;
        msg.x = dat.readInt();
        msg.y = dat.readInt();
        msg.z = dat.readInt();
        msg.level = dat.readInt();
    }

    protected void channelRead0(ChannelHandlerContext ctx, Message0x04Structuralupdate msg)
            throws Exception
    {
        TileEntityBarrel barrel = (TileEntityBarrel)Minecraft.func_71410_x().field_71441_e.func_147438_o(msg.x, msg.y, msg.z);
        if (barrel != null)
        {
            barrel.coreUpgrades.levelStructural = msg.level;
            Minecraft.func_71410_x().field_71441_e.func_147471_g(msg.x, msg.y, msg.z);
        }
    }

}
