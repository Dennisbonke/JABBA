package mcp.mobius.betterbarrels.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mcp.mobius.betterbarrels.BetterBarrels;
import mcp.mobius.betterbarrels.client.ClientChatUtils;
import mcp.mobius.betterbarrels.common.LocalizedChat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class Message0x09LocalizedChat extends SimpleChannelInboundHandler<Message0x09LocalizedChat> implements IBarrelMessage {

    public int messageID;
    public int extraCount;
    ArrayList<SupportedExtraTypes> extraTypesList = new ArrayList();
    ArrayList<Object> extraValuesList = new ArrayList();
    public Message0x09LocalizedChat() {}

    static enum SupportedExtraTypes
    {
        INT(Integer.class),  STR(String.class),  FLT(Float.class);

        Class clazz;

        private SupportedExtraTypes(Class clazz)
        {
            this.clazz = clazz;
        }

        public static SupportedExtraTypes getType(int i)
        {
            return values()[i];
        }

        public static SupportedExtraTypes getTypeFromObject(Object o)
        {
            for (SupportedExtraTypes supportedType : ) {
                if (o.getClass().isAssignableFrom(supportedType.clazz)) {
                    return supportedType;
                }
            }
            return null;
        }
    }

    public Message0x09LocalizedChat(LocalizedChat message, Object... extraItems)
    {
        this.messageID = message.ordinal();
        for (Object extraObject : extraItems)
        {
            SupportedExtraTypes type = SupportedExtraTypes.getTypeFromObject(extraObject);
            if (type == null)
            {
                BetterBarrels.log.warn("Localized Chat Packet has no support for : " + extraObject.getClass().getCanonicalName());
            }
            else
            {
                this.extraTypesList.add(type);
                this.extraValuesList.add(extraObject);
            }
        }
        this.extraCount = this.extraTypesList.size();
    }

    public void encodeInto(ChannelHandlerContext ctx, IBarrelMessage msg, ByteBuf target)
            throws Exception
    {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(byteStream);

        outStream.writeInt(this.messageID);
        outStream.writeInt(this.extraCount);
        for (int i = 0; i < this.extraCount; i++)
        {
            SupportedExtraTypes type = (SupportedExtraTypes)this.extraTypesList.get(i);

            outStream.writeByte(type.ordinal());
            switch (type.ordinal())
            {
                case 1:
                    outStream.writeInt(((Integer)this.extraValuesList.get(i)).intValue());
                    break;
                case 2:
                    outStream.writeUTF((String)this.extraValuesList.get(i));
                    break;
                case 3:
                    outStream.writeFloat(((Float)this.extraValuesList.get(i)).floatValue());
            }
        }
        outStream.close();
        target.writeBytes(byteStream.toByteArray());
    }

    public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IBarrelMessage rawmsg)
    {
        DataInputStream inStream = new DataInputStream(new ByteArrayInputStream(dat.array(), dat.arrayOffset(), dat.capacity()));
        Message0x09LocalizedChat msg = (Message0x09LocalizedChat)rawmsg;
        try
        {
            msg.messageID = inStream.readInt();
            msg.extraCount = inStream.readInt();
            for (int i = 0; i < this.extraCount; i++)
            {
                SupportedExtraTypes type = SupportedExtraTypes.getType(inStream.readByte());

                msg.extraTypesList.add(type);
                switch (type.ordinal())
                {
                    case 1:
                        msg.extraValuesList.add(Integer.valueOf(inStream.readInt()));
                        break;
                    case 2:
                        msg.extraValuesList.add(inStream.readUTF());
                        break;
                    case 3:
                        msg.extraValuesList.add(Float.valueOf(inStream.readFloat()));
                }
            }
        }
        catch (Throwable t) {}
    }

    protected void channelRead0(ChannelHandlerContext ctx, Message0x09LocalizedChat msg)
            throws Exception
    {
        ClientChatUtils.printLocalizedMessage(LocalizedChat.values()[msg.messageID].localizationKey, msg.extraValuesList.toArray());
    }

}
