package mcp.mobius.betterbarrels.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mcp.mobius.betterbarrels.common.blocks.StorageLocal;
import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class Message0x06FullStorage extends SimpleChannelInboundHandler<Message0x06FullStorage> implements IBarrelMessage {

    public int x;
    public int y;
    public int z;
    public NBTTagCompound storageTag = new NBTTagCompound();

    public Message0x06FullStorage() {}

    public Message0x06FullStorage(TileEntityBarrel barrel)
    {
        this.x = barrel.field_145851_c;
        this.y = barrel.field_145848_d;
        this.z = barrel.field_145849_e;
        this.storageTag = barrel.getStorage().writeTagCompound();
    }

    public void encodeInto(ChannelHandlerContext ctx, IBarrelMessage msg, ByteBuf target)
            throws Exception
    {
        target.writeInt(this.x);
        target.writeInt(this.y);
        target.writeInt(this.z);
        BarrelPacketHandler.INSTANCE.writeNBTTagCompoundToBuffer(target, this.storageTag);
    }

    public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IBarrelMessage rawmsg)
    {
        Message0x06FullStorage msg = (Message0x06FullStorage)rawmsg;
        msg.x = dat.readInt();
        msg.y = dat.readInt();
        msg.z = dat.readInt();
        try
        {
            msg.storageTag = BarrelPacketHandler.INSTANCE.readNBTTagCompoundFromBuffer(dat);
        }
        catch (Exception e) {}
    }

    protected void channelRead0(ChannelHandlerContext ctx, Message0x06FullStorage msg)
            throws Exception
    {
        TileEntityBarrel barrel = (TileEntityBarrel) Minecraft.func_71410_x().field_71441_e.func_147438_o(msg.x, msg.y, msg.z);
        if (barrel != null)
        {
            StorageLocal storage = new StorageLocal();
            storage.readTagCompound(msg.storageTag);
            barrel.setStorage(storage);
        }
    }

}
