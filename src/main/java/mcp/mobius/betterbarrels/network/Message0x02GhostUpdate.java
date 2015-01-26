package mcp.mobius.betterbarrels.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;
import net.minecraft.client.Minecraft;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class Message0x02GhostUpdate extends SimpleChannelInboundHandler<Message0x02GhostUpdate> implements IBarrelMessage {

    public int x;
    public int y;
    public int z;
    public boolean locked;

    public Message0x02GhostUpdate() {}

    public Message0x02GhostUpdate(TileEntityBarrel barrel)
    {
        this.x = barrel.field_145851_c;
        this.y = barrel.field_145848_d;
        this.z = barrel.field_145849_e;
        this.locked = barrel.getStorage().isGhosting();
    }

    public void encodeInto(ChannelHandlerContext ctx, IBarrelMessage msg, ByteBuf target)
            throws Exception
    {
        target.writeInt(this.x);
        target.writeInt(this.y);
        target.writeInt(this.z);
        target.writeBoolean(this.locked);
    }

    public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IBarrelMessage rawmsg)
    {
        Message0x02GhostUpdate msg = (Message0x02GhostUpdate)rawmsg;
        msg.x = dat.readInt();
        msg.y = dat.readInt();
        msg.z = dat.readInt();
        msg.locked = dat.readBoolean();
    }

    protected void channelRead0(ChannelHandlerContext ctx, Message0x02GhostUpdate msg)
            throws Exception
    {
        TileEntityBarrel barrel = (TileEntityBarrel)Minecraft.func_71410_x().field_71441_e.func_147438_o(msg.x, msg.y, msg.z);
        if (barrel != null)
        {
            barrel.getStorage().setGhosting(msg.locked);
            Minecraft.func_71410_x().field_71441_e.func_147471_g(msg.x, msg.y, msg.z);
        }
    }

}
