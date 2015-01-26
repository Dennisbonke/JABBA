package mcp.mobius.betterbarrels;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

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

        currenttip.add(StatCollector.func_74837_a("text.jabba.waila.structural", new Object[] { Integer.valueOf(tebarrel.coreUpgrades.levelStructural) }));
        currenttip.add(StatCollector.func_74837_a("text.jabba.waila.upgrades", new Object[] { Integer.valueOf(tebarrel.coreUpgrades.getFreeSlots()), Integer.valueOf(tebarrel.coreUpgrades.getMaxUpgradeSlots()) }));
        if (barrelStack != null)
        {
            if (config.getConfig("bb.itemtype")) {
                currenttip.add(barrelStack.func_82833_r());
            }
            if (config.getConfig("bb.itemnumb")) {
                currenttip.add(StatCollector.func_74837_a("text.jabba.waila.items", new Object[] { Integer.valueOf(tebarrel.getStorage().getAmount()), Integer.valueOf(tebarrel.getStorage().getItem().func_77976_d() * tebarrel.getStorage().getMaxStacks()) }));
            }
            if (config.getConfig("bb.space")) {
                currenttip.add(StatCollector.func_74837_a("text.jabba.waila.stacks", new Object[] { Integer.valueOf(tebarrel.getStorage().getMaxStacks()) }));
            }
        }
        else
        {
            if (config.getConfig("bb.itemtype")) {
                currenttip.add(StatCollector.func_74838_a("text.jabba.waila.empty"));
            }
            if (config.getConfig("bb.space")) {
                currenttip.add(StatCollector.func_74837_a("text.jabba.waila.stacks", new Object[] { Integer.valueOf(tebarrel.getStorage().getMaxStacks()) }));
            }
        }
        return currenttip;
    }

    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

    public static void callbackRegister(IWailaRegistrar registrar)
    {
        registrar.addConfig(StatCollector.func_74838_a("itemGroup.jabba"), "bb.itemtype", StatCollector.func_74838_a("text.jabba.waila.key.content"));
        registrar.addConfig(StatCollector.func_74838_a("itemGroup.jabba"), "bb.itemnumb", StatCollector.func_74838_a("text.jabba.waila.key.quantity"));
        registrar.addConfig(StatCollector.func_74838_a("itemGroup.jabba"), "bb.space", StatCollector.func_74838_a("text.jabba.waila.key.stacks"));
        registrar.registerBodyProvider(new BBWailaProvider(), TileEntityBarrel.class);
    }

}
