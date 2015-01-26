package mcp.mobius.betterbarrels.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import mcp.mobius.betterbarrels.common.LocalizedChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;
import java.util.EnumMap;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public enum BarrelPacketHandler {

    INSTANCE;

    public EnumMap<Side, FMLEmbeddedChannel> channels;

    private BarrelPacketHandler()
    {
        this.channels = NetworkRegistry.INSTANCE.newChannel("JABBA", new ChannelHandler[] { new BarrelCodec() });
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            addClientHandlers();
        }
    }

    private void addClientHandlers()
    {
        FMLEmbeddedChannel channel = (FMLEmbeddedChannel)this.channels.get(Side.CLIENT);
        String codec = channel.findChannelHandlerNameForType(BarrelCodec.class);

        channel.pipeline().addAfter(codec, "ClientHandler", new Message0x00FulleTileEntityNBT());
        channel.pipeline().addAfter("ClientHandler", "ContentUpdate", new Message0x01ContentUpdate());
        channel.pipeline().addAfter("ContentUpdate", "GhostUpdate", new Message0x02GhostUpdate());
        channel.pipeline().addAfter("GhostUpdate", "Sideupgradeupdate", new Message0x03SideupgradeUpdate());
        channel.pipeline().addAfter("Sideupgradeupdate", "Structuralupdate", new Message0x04Structuralupdate());
        channel.pipeline().addAfter("Structuralupdate", "CoreUpdate", new Message0x05CoreUpdate());
        channel.pipeline().addAfter("CoreUpdate", "FullStorage", new Message0x06FullStorage());
        channel.pipeline().addAfter("FullStorage", "ForceRender", new Message0x07ForceRender());
        channel.pipeline().addAfter("ForceRender", "LinkUpdate", new Message0x08LinkUpdate());
        channel.pipeline().addAfter("LinkUpdate", "LocalizedChat", new Message0x09LocalizedChat());
    }

    private class BarrelCodec
            extends FMLIndexedMessageToMessageCodec<IBarrelMessage>
    {
        public BarrelCodec()
        {
            addDiscriminator(0, Message0x00FulleTileEntityNBT.class);
            addDiscriminator(1, Message0x01ContentUpdate.class);
            addDiscriminator(2, Message0x02GhostUpdate.class);
            addDiscriminator(3, Message0x03SideupgradeUpdate.class);
            addDiscriminator(4, Message0x04Structuralupdate.class);
            addDiscriminator(5, Message0x05CoreUpdate.class);
            addDiscriminator(6, Message0x06FullStorage.class);
            addDiscriminator(7, Message0x07ForceRender.class);
            addDiscriminator(8, Message0x08LinkUpdate.class);
            addDiscriminator(9, Message0x09LocalizedChat.class);
        }

        public void encodeInto(ChannelHandlerContext ctx, IBarrelMessage msg, ByteBuf target)
                throws Exception
        {
            msg.encodeInto(ctx, msg, target);
        }

        public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IBarrelMessage msg)
        {
            msg.decodeInto(ctx, dat, msg);
        }
    }

    public void sendTo(IBarrelMessage message, EntityPlayerMP player)
    {
        ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToDimension(IBarrelMessage message, int dimensionId)
    {
        ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(Integer.valueOf(dimensionId));
        ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToAllAround(IBarrelMessage message, NetworkRegistry.TargetPoint point)
    {
        ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
        ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public static void sendLocalizedChat(EntityPlayer player, LocalizedChat message, Object... extraNumbers)
    {
        if ((player instanceof EntityPlayerMP)) {
            INSTANCE.sendTo(new Message0x09LocalizedChat(message, extraNumbers), (EntityPlayerMP)player);
        }
    }

    public void writeNBTTagCompoundToBuffer(ByteBuf target, NBTTagCompound tag)
            throws IOException
    {
        if (tag == null)
        {
            target.writeShort(-1);
        }
        else
        {
            byte[] abyte = CompressedStreamTools.func_74798_a(tag);
            target.writeShort((short)abyte.length);
            target.writeBytes(abyte);
        }
    }

    public NBTTagCompound readNBTTagCompoundFromBuffer(ByteBuf dat)
            throws IOException
    {
        short short1 = dat.readShort();
        if (short1 < 0) {
            return null;
        }
        byte[] abyte = new byte[short1];
        dat.readBytes(abyte);
        return CompressedStreamTools.func_152457_a(abyte, NBTSizeTracker.field_152451_a);
    }

    public void writeItemStackToBuffer(ByteBuf target, ItemStack stack)
            throws IOException
    {
        if (stack == null)
        {
            target.writeShort(-1);
        }
        else
        {
            target.writeShort(Item.func_150891_b(stack.func_77973_b()));
            target.writeByte(stack.field_77994_a);
            target.writeShort(stack.func_77960_j());
            NBTTagCompound nbttagcompound = null;
            if ((stack.func_77973_b().func_77645_m()) || (stack.func_77973_b().func_77651_p())) {
                nbttagcompound = stack.field_77990_d;
            }
            writeNBTTagCompoundToBuffer(target, nbttagcompound);
        }
    }

    public ItemStack readItemStackFromBuffer(ByteBuf dat)
            throws IOException
    {
        ItemStack itemstack = null;
        short short1 = dat.readShort();
        if (short1 >= 0)
        {
            byte b0 = dat.readByte();
            short short2 = dat.readShort();
            itemstack = new ItemStack(Item.func_150899_d(short1), b0, short2);
            itemstack.field_77990_d = readNBTTagCompoundFromBuffer(dat);
        }
        return itemstack;
    }

}
