package mcp.mobius.betterbarrels;

import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class BBWailaProvider implements IWailaDataProvider {

    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }

    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        TileEntityBarrel tebarrel = (TileEntityBarrel)accessor.getTileEntity();
        ItemStack barrelStack = tebarrel.getStorage().getItem();

        currenttip.add(StatCollector.translateToLocalFormatted("text.jabba.waila.structural", new Object[]{Integer.valueOf(tebarrel.coreUpgrades.levelStructural)}));
        currenttip.add(StatCollector.translateToLocalFormatted("text.jabba.waila.upgrades", new Object[]{Integer.valueOf(tebarrel.coreUpgrades.getFreeSlots()), Integer.valueOf(tebarrel.coreUpgrades.getMaxUpgradeSlots())}));
        if (barrelStack != null)
        {
            if (config.getConfig("bb.itemtype")) {
                currenttip.add(barrelStack.getDisplayName());
            }
            if (config.getConfig("bb.itemnumb")) {
                currenttip.add(StatCollector.translateToLocalFormatted("text.jabba.waila.items", new Object[]{Integer.valueOf(tebarrel.getStorage().getAmount()), Integer.valueOf(tebarrel.getStorage().getItem().getMaxStackSize() * tebarrel.getStorage().getMaxStacks())}));
            }
            if (config.getConfig("bb.space")) {
                currenttip.add(StatCollector.translateToLocalFormatted("text.jabba.waila.stacks", new Object[]{Integer.valueOf(tebarrel.getStorage().getMaxStacks())}));
            }
        }
        else
        {
            if (config.getConfig("bb.itemtype")) {
                currenttip.add(StatCollector.translateToLocal("text.jabba.waila.empty"));
            }
            if (config.getConfig("bb.space")) {
                currenttip.add(StatCollector.translateToLocalFormatted("text.jabba.waila.stacks", new Object[]{Integer.valueOf(tebarrel.getStorage().getMaxStacks())}));
            }
        }
        return currenttip;
    }

    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP entityPlayerMP, TileEntity tileEntity, NBTTagCompound nbtTagCompound, World world, int i, int i1, int i2) {
        return null;
    }

    public static void callbackRegister(IWailaRegistrar registrar)
    {
        registrar.addConfig(StatCollector.translateToLocal("itemGroup.jabba"), "bb.itemtype", StatCollector.translateToLocal("text.jabba.waila.key.content"));
        registrar.addConfig(StatCollector.translateToLocal("itemGroup.jabba"), "bb.itemnumb", StatCollector.translateToLocal("text.jabba.waila.key.quantity"));
        registrar.addConfig(StatCollector.translateToLocal("itemGroup.jabba"), "bb.space", StatCollector.translateToLocal("text.jabba.waila.key.stacks"));
        registrar.registerBodyProvider(new BBWailaProvider(), TileEntityBarrel.class);
    }

}
