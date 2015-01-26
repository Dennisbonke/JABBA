package mcp.mobius.betterbarrels.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class Message0x01ContentUpdate extends SimpleChannelInboundHandler<Message0x01ContentUpdate> implements IBarrelMessage {

    public int x;
    public int y;
    public int z;
    public int amount;
    public ItemStack stack = null;

    public Message0x01ContentUpdate() {}

    public Message0x01ContentUpdate(TileEntityBarrel barrel)
    {
        this.x = barrel.field_145851_c;
        this.y = barrel.field_145848_d;
        this.z = barrel.field_145849_e;
        this.amount = barrel.getStorage().getAmount();
        this.stack = barrel.getStorage().getItem();
    }

    public void encodeInto(ChannelHandlerContext ctx, IBarrelMessage msg, ByteBuf target)
            throws Exception
    {
        target.writeInt(this.x);
        target.writeInt(this.y);
        target.writeInt(this.z);
        target.writeInt(this.amount);
        BarrelPacketHandler.INSTANCE.writeItemStackToBuffer(target, this.stack);
    }

    public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IBarrelMessage rawmsg)
    {
        Message0x01ContentUpdate msg = (Message0x01ContentUpdate)rawmsg;
        msg.x = dat.readInt();
        msg.y = dat.readInt();
        msg.z = dat.readInt();
        msg.amount = dat.readInt();
        try
        {
            msg.stack = BarrelPacketHandler.INSTANCE.readItemStackFromBuffer(dat);
        }
        catch (Exception e) {}
    }

    protected void channelRead0(ChannelHandlerContext ctx, Message0x01ContentUpdate msg)
            throws Exception
    {
        TileEntityBarrel barrel = (TileEntityBarrel) Minecraft.func_71410_x().field_71441_e.func_147438_o(msg.x, msg.y, msg.z);
        if (barrel != null) {
            barrel.getStorage().setStoredItemType(msg.stack, msg.amount);
        }
    }

}
