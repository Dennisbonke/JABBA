package mcp.mobius.betterbarrels.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public abstract interface IBarrelMessage {

    public abstract void encodeInto(ChannelHandlerContext paramChannelHandlerContext, IBarrelMessage paramIBarrelMessage, ByteBuf paramByteBuf)
            throws Exception;

    public abstract void decodeInto(ChannelHandlerContext paramChannelHandlerContext, ByteBuf paramByteBuf, IBarrelMessage paramIBarrelMessage);

}
