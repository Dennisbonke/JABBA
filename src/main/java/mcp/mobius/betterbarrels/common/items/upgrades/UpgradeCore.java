package mcp.mobius.betterbarrels.common.items.upgrades;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.betterbarrels.BetterBarrels;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public enum UpgradeCore {

    STORAGE(Type.STORAGE, 1),  ENDER(Type.ENDER, 2),  REDSTONE(Type.REDSTONE, 1),  HOPPER(Type.HOPPER, 1),  STORAGE3(Type.STORAGE, 3),  STORAGE9(Type.STORAGE, 9),  STORAGE27(Type.STORAGE, 27),  VOID(Type.VOID, 2),  STORAGE81(Type.STORAGE, 81),  STORAGE243(Type.STORAGE, 243),  CREATIVE(Type.CREATIVE, 1);

    public final Type type;
    public final int slotsUsed;
    public final String translationKey;
    @SideOnly(Side.CLIENT)
    public IIcon icon;

    public static enum Type
    {
        STORAGE,  ENDER,  REDSTONE,  HOPPER,  VOID,  CREATIVE;

        private Type() {}
    }

    private UpgradeCore(Type type, int slots)
    {
        this.type = type;
        this.slotsUsed = slots;
        this.translationKey = ("item.upgrade.core." + name().toLowerCase());
    }

    @SideOnly(Side.CLIENT)
    public String description()
    {
        String key = "text.jabba.ubgrade.core." + this.type.name().toLowerCase();
        if (this.type == Type.STORAGE) {
            return StatCollector.func_74837_a(key, new Object[] { Integer.valueOf(this.slotsUsed * BetterBarrels.stacksSize) });
        }
        return StatCollector.func_74838_a(key);
    }

}
