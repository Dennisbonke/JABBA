package mcp.mobius.betterbarrels.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;
import net.minecraft.client.Minecraft;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class Message0x03SideupgradeUpdate extends SimpleChannelInboundHandler<Message0x03SideupgradeUpdate> implements IBarrelMessage {

    public int x;
    public int y;
    public int z;
    public int[] sideUpgrades = new int[6];
    public int[] sideMetadata = new int[6];

    public Message0x03SideupgradeUpdate() {}

    public Message0x03SideupgradeUpdate(TileEntityBarrel barrel)
    {
        this.x = barrel.field_145851_c;
        this.y = barrel.field_145848_d;
        this.z = barrel.field_145849_e;
        for (int i = 0; i < 6; i++) {
            this.sideUpgrades[i] = barrel.sideUpgrades[i];
        }
        for (int i = 0; i < 6; i++) {
            this.sideMetadata[i] = barrel.sideMetadata[i];
        }
    }

    public void encodeInto(ChannelHandlerContext ctx, IBarrelMessage msg, ByteBuf target)
            throws Exception
    {
        target.writeInt(this.x);
        target.writeInt(this.y);
        target.writeInt(this.z);
        for (int i = 0; i < 6; i++) {
            target.writeInt(this.sideUpgrades[i]);
        }
        for (int i = 0; i < 6; i++) {
            target.writeInt(this.sideMetadata[i]);
        }
    }

    public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IBarrelMessage rawmsg)
    {
        Message0x03SideupgradeUpdate msg = (Message0x03SideupgradeUpdate)rawmsg;
        msg.x = dat.readInt();
        msg.y = dat.readInt();
        msg.z = dat.readInt();
        for (int i = 0; i < 6; i++) {
            msg.sideUpgrades[i] = dat.readInt();
        }
        for (int i = 0; i < 6; i++) {
            msg.sideMetadata[i] = dat.readInt();
        }
    }

    protected void channelRead0(ChannelHandlerContext ctx, Message0x03SideupgradeUpdate msg)
            throws Exception
    {
        TileEntityBarrel barrel = (TileEntityBarrel)Minecraft.func_71410_x().field_71441_e.func_147438_o(msg.x, msg.y, msg.z);
        if (barrel != null)
        {
            barrel.sideUpgrades = msg.sideUpgrades;
            barrel.sideMetadata = msg.sideMetadata;
            Minecraft.func_71410_x().field_71441_e.func_147471_g(msg.x, msg.y, msg.z);
        }
    }

}
