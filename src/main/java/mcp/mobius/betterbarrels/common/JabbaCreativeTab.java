package mcp.mobius.betterbarrels.common;

import mcp.mobius.betterbarrels.BetterBarrels;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class JabbaCreativeTab extends CreativeTabs {

    public static JabbaCreativeTab tab = new JabbaCreativeTab();

    public JabbaCreativeTab()
    {
        super("jabba");
    }

    public ItemStack getIconItemStack()
    {
        return new ItemStack(BetterBarrels.blockBarrel);
    }

    public Item getTabIconItem()
    {
        return Item.getItemFromBlock(BetterBarrels.blockBarrel);
    }

}
