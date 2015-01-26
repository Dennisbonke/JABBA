package mcp.mobius.betterbarrels.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;
import mcp.mobius.betterbarrels.common.items.upgrades.UpgradeCore;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class Message0x05CoreUpdate extends SimpleChannelInboundHandler<Message0x05CoreUpdate> implements IBarrelMessage {

    public int x;
    public int y;
    public int z;
    public int nStorageUpg = 0;
    public boolean hasRedstone = false;
    public boolean hasHopper = false;
    public boolean hasEnder = false;
    public boolean hasVoid = false;
    public boolean hasCreative = false;
    public ArrayList<UpgradeCore> upgrades = new ArrayList();

    public Message0x05CoreUpdate() {}

    public Message0x05CoreUpdate(TileEntityBarrel barrel)
    {
        this.x = barrel.field_145851_c;
        this.y = barrel.field_145848_d;
        this.z = barrel.field_145849_e;
        this.nStorageUpg = barrel.coreUpgrades.nStorageUpg;
        this.hasRedstone = barrel.coreUpgrades.hasRedstone;
        this.hasHopper = barrel.coreUpgrades.hasHopper;
        this.hasEnder = barrel.coreUpgrades.hasEnder;
        this.hasVoid = barrel.coreUpgrades.hasVoid;
        this.hasCreative = barrel.coreUpgrades.hasCreative;
        for (UpgradeCore i : barrel.coreUpgrades.upgradeList) {
            this.upgrades.add(i);
        }
    }

    public void encodeInto(ChannelHandlerContext ctx, IBarrelMessage msg, ByteBuf target)
            throws Exception
    {
        target.writeInt(this.x);
        target.writeInt(this.y);
        target.writeInt(this.z);
        target.writeInt(this.upgrades.size());
        for (UpgradeCore i : this.upgrades) {
            target.writeInt(i.ordinal());
        }
    }

    public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IBarrelMessage rawmsg)
    {
        Message0x05CoreUpdate msg = (Message0x05CoreUpdate)rawmsg;
        msg.x = dat.readInt();
        msg.y = dat.readInt();
        msg.z = dat.readInt();
        int size = dat.readInt();
        for (int i = 0; i < size; i++) {
            msg.upgrades.add(UpgradeCore.values()[dat.readInt()]);
        }
        for (UpgradeCore i : msg.upgrades) {
            if (i.type == UpgradeCore.Type.STORAGE) {
                msg.nStorageUpg += i.slotsUsed;
            } else if (i == UpgradeCore.ENDER) {
                msg.hasEnder = true;
            } else if (i == UpgradeCore.HOPPER) {
                msg.hasHopper = true;
            } else if (i == UpgradeCore.REDSTONE) {
                msg.hasRedstone = true;
            } else if (i == UpgradeCore.VOID) {
                msg.hasVoid = true;
            } else if (i == UpgradeCore.CREATIVE) {
                msg.hasCreative = true;
            }
        }
    }

    protected void channelRead0(ChannelHandlerContext ctx, Message0x05CoreUpdate msg)
            throws Exception
    {
        TileEntityBarrel barrel = (TileEntityBarrel) Minecraft.func_71410_x().field_71441_e.func_147438_o(msg.x, msg.y, msg.z);
        if (barrel != null)
        {
            barrel.coreUpgrades.upgradeList = msg.upgrades;
            barrel.coreUpgrades.hasRedstone = msg.hasRedstone;
            barrel.coreUpgrades.hasHopper = msg.hasHopper;
            barrel.coreUpgrades.hasEnder = msg.hasEnder;
            barrel.coreUpgrades.nStorageUpg = msg.nStorageUpg;
            barrel.setVoid(msg.hasVoid);
            barrel.setCreative(msg.hasCreative);
        }
    }

}
