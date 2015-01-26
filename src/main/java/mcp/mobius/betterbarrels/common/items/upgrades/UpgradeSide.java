package mcp.mobius.betterbarrels.common.items.upgrades;

import mcp.mobius.betterbarrels.BetterBarrels;
import net.minecraft.item.Item;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class UpgradeSide {

    public static int NONE = 0;
    public static int FRONT = 1;
    public static int STICKER = 2;
    public static int HOPPER = 3;
    public static int REDSTONE = 4;
    public static int RS_FULL = 0;
    public static int RS_EMPT = 1;
    public static int RS_PROP = 2;
    public static Item[] mapItem = { null, null, BetterBarrels.itemUpgradeSide, BetterBarrels.itemUpgradeSide, BetterBarrels.itemUpgradeSide };
    public static int[] mapMeta = { 0, 0, 0, 1, 2 };
    public static int[] mapRevMeta = { STICKER, HOPPER, REDSTONE };
    public static int[] mapReq = { -1, -1, -1, UpgradeCore.Type.HOPPER



            .ordinal(), UpgradeCore.Type.REDSTONE
            .ordinal() };

}
