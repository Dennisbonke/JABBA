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

    public ItemStack func_151244_d()
    {
        return new ItemStack(BetterBarrels.blockBarrel);
    }

    public Item func_78016_d()
    {
        return Item.func_150898_a(BetterBarrels.blockBarrel);
    }

}
