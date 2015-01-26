package mcp.mobius.betterbarrels.bspace;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mcp.mobius.betterbarrels.common.items.upgrades.UpgradeCore;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class BBEventHandler {

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if ((!event.world.field_72995_K) && (event.world.field_73011_w.field_76574_g == 0)) {
            BSpaceStorageHandler.instance().loadFromFile();
        }
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event)
    {
        if ((event.itemStack.func_77973_b() instanceof ItemUpgradeCore)) {
            event.toolTip.add(1, StatCollector.func_74838_a("text.jabba.tooltip.slots.used") + UpgradeCore.values()[event.itemStack.func_77960_j()].slotsUsed);
        }
        if ((event.itemStack.func_77973_b() instanceof ItemUpgradeStructural))
        {
            int nslots = 0;
            for (int i = 0; i < event.itemStack.func_77960_j() + 1; i++) {
                nslots += MathHelper.func_76128_c(Math.pow(2.0D, i));
            }
            event.toolTip.add(1, StatCollector.func_74838_a("text.jabba.tooltip.slots.provided") + nslots);
        }
        if ((event.itemStack.func_77973_b() instanceof ItemBarrelMover)) {
            if ((event.itemStack.func_77942_o()) && (event.itemStack.func_77978_p().func_74764_b("Container")))
            {
                NBTTagCompound tag = event.itemStack.func_77978_p().func_74775_l("Container");
                Block storedBlock;
                if (tag.func_74764_b("ID")) {
                    storedBlock = Block.func_149729_e(tag.func_74762_e("ID"));
                } else {
                    storedBlock = Block.func_149684_b(tag.func_74779_i("Block"));
                }
                int meta = tag.func_74762_e("Meta");
                ItemStack stack = new ItemStack(storedBlock, 0, meta);
                event.toolTip.add(1, stack.func_82833_r());
            }
            else
            {
                event.toolTip.add(1, StatCollector.func_74838_a("text.jabba.tooltip.empty"));
            }
        }
    }

}
