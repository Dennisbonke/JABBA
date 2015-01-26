package mcp.mobius.betterbarrels.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.betterbarrels.BetterBarrels;
import mcp.mobius.betterbarrels.common.JabbaCreativeTab;
import mcp.mobius.betterbarrels.common.LocalizedChat;
import mcp.mobius.betterbarrels.common.blocks.BlockBarrel;
import mcp.mobius.betterbarrels.network.BarrelPacketHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class ItemBarrelHammer extends Item implements IOverlayItem {

    public static enum HammerMode
    {
        NORMAL,  BSPACE,  REDSTONE,  HOPPER,  STORAGE,  STRUCTURAL,  VOID,  CREATIVE;

        public final LocalizedChat message;
        public IIcon icon;

        private HammerMode()
        {
            this.message = LocalizedChat.valueOf("HAMMER_" + name().toUpperCase());
        }

        public static ItemStack setNextMode(ItemStack item, EntityPlayer player)
        {
            int next_mode = item.func_77960_j() + 1;
            if ((!player.field_71075_bZ.field_75098_d) && (next_mode == CREATIVE.ordinal())) {
                next_mode++;
            }
            if (next_mode >= values().length) {
                next_mode = 0;
            }
            item.func_77964_b(next_mode);
            return item;
        }

        public static HammerMode getMode(ItemStack item)
        {
            int mode = item.func_77960_j();
            if (mode >= values().length) {
                mode = 0;
            }
            return values()[mode];
        }
    }

    public ItemBarrelHammer()
    {
        func_77625_d(1);
        func_77627_a(true);
        func_77655_b("hammer");
        func_77637_a(JabbaCreativeTab.tab);
    }

    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player)
    {
        return world.func_147439_a(x, y, z) == BetterBarrels.blockBarrel;
    }

    public void func_94581_a(IIconRegister par1IconRegister)
    {
        for (HammerMode mode : ) {
            mode.icon = par1IconRegister.func_94245_a("JABBA:hammer_" + mode.name().toLowerCase());
        }
    }

    public IIcon func_77617_a(int dmg)
    {
        if (dmg >= HammerMode.values().length) {
            dmg = 0;
        }
        return HammerMode.values()[dmg].icon;
    }

    public String func_77667_c(ItemStack par1ItemStack)
    {
        return super.func_77658_a() + "." + HammerMode.getMode(par1ItemStack).name().toLowerCase();
    }

    public ItemStack func_77659_a(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if (par3EntityPlayer.func_70093_af())
        {
            par3EntityPlayer.field_71071_by.field_70462_a[par3EntityPlayer.field_71071_by.field_70461_c] = HammerMode.setNextMode(par1ItemStack, par3EntityPlayer);
            if (!par2World.field_72995_K) {
                BarrelPacketHandler.sendLocalizedChat(par3EntityPlayer, HammerMode.getMode(par1ItemStack).message, new Object[0]);
            }
        }
        return par1ItemStack;
    }

    public boolean func_150897_b(Block blockHit)
    {
        if ((blockHit instanceof BlockBarrel)) {
            return true;
        }
        return super.func_150897_b(blockHit);
    }

    public float func_150893_a(ItemStack hammerStack, Block blockHit)
    {
        if (((hammerStack.func_77973_b() instanceof ItemBarrelHammer)) && ((blockHit instanceof BlockBarrel))) {
            return Item.ToolMaterial.IRON.func_77998_b();
        }
        return super.func_150893_a(hammerStack, blockHit);
    }

    @SideOnly(Side.CLIENT)
    public void func_150895_a(Item item, CreativeTabs tabs, List list)
    {
        for (HammerMode mode : ) {
            list.add(new ItemStack(item, 1, mode.ordinal()));
        }
    }

}
