package mcp.mobius.betterbarrels;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class RecipeHandler {

    public static RecipeHandler _instance = new RecipeHandler();

    public static RecipeHandler instance()
    {
        return _instance;
    }

    public void registerOres()
    {
        OreDictionary.registerOre("ingotIron", Items.field_151042_j);
        OreDictionary.registerOre("ingotGold", Items.field_151043_k);
        OreDictionary.registerOre("slimeball", Items.field_151123_aH);
        OreDictionary.registerOre("gemDiamond", Items.field_151045_i);
        OreDictionary.registerOre("gemEmerald", Items.field_151166_bC);
        OreDictionary.registerOre("chestWood", Blocks.field_150486_ae);
        OreDictionary.registerOre("stickWood", Items.field_151055_y);
        OreDictionary.registerOre("obsidian", Blocks.field_150343_Z);
        OreDictionary.registerOre("whiteStone", Blocks.field_150377_bs);
        OreDictionary.registerOre("transdimBlock", Blocks.field_150477_bB);

        Block CBEnderChest = Block.func_149684_b("EnderStorage:enderChest");
        if (CBEnderChest != null) {
            for (int meta = 0; meta < 4096; meta++) {
                OreDictionary.registerOre("transdimBlock", new ItemStack(CBEnderChest, 1, meta));
            }
        }
    }

    private ItemStack upgradeItem = null;

    public void registerRecipes()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.blockBarrel), new Object[] { "W-W", "WCW", "WWW",

                Character.valueOf('C'), "chestWood",
                Character.valueOf('W'), "logWood",
                Character.valueOf('-'), "slabWood" }));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.itemMover, 1, 0), new Object[] { "  X", " PX", "XXX",

                Character.valueOf('X'), "ingotIron",
                Character.valueOf('P'), "plankWood" }));
        if (BetterBarrels.diamondDollyActive) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.itemMoverDiamond, 1, 0), new Object[] { "   ", " P ", "XXX",

                    Character.valueOf('X'), "gemDiamond",
                    Character.valueOf('P'), BetterBarrels.itemMover }));
        }
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.itemHammer, 1, 0), new Object[] { "III", "ISI", " S ",

                Character.valueOf('I'), "ingotIron",
                Character.valueOf('S'), "stickWood" }));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.itemTuningFork, 1, 0), new Object[] { " P ", " EP", "P  ",

                Character.valueOf('P'), "ingotIron",
                Character.valueOf('E'), Items.field_151079_bi }));


        addCoreUpgrade(0, BetterBarrels.blockBarrel);
        addCoreUpgrade(1, "transdimBlock");
        addCoreUpgrade(2, Blocks.field_150451_bX);
        addCoreUpgrade(3, Blocks.field_150438_bZ);
        addCoreUpgrade(UpgradeCore.VOID.ordinal(), Blocks.field_150343_Z);


        addSideUpgrade(0, "slimeball", Items.field_151121_aF);
        addSideUpgrade(1, Blocks.field_150438_bZ, "plankWood");
        addSideUpgrade(2, Items.field_151137_ax, "plankWood");


        UpgradeCore prevStorage = UpgradeCore.STORAGE;
        for (UpgradeCore core : UpgradeCore.values()) {
            if ((core.type == UpgradeCore.Type.STORAGE) && (core.slotsUsed > 1))
            {
                if (core.slotsUsed > StructuralLevel.LEVELS[BetterBarrels.maxCraftableTier].getMaxCoreSlots()) {
                    break;
                }
                addCoreUpgradeUpgrade(core.ordinal(), prevStorage.ordinal());
                prevStorage = core;
            }
        }
    }

    public void registerLateRecipes()
    {
        try
        {
            Utils.Material mat = new Utils.Material(BetterBarrels.upgradeItemStr);
            this.upgradeItem = mat.getStack();
        }
        catch (Throwable t)
        {
            BetterBarrels.log.error("Requested item with id " + BetterBarrels.upgradeItemStr + " for tier upgrade recipes was not found, using the default of vanilla fence");
            this.upgradeItem = new ItemStack(Blocks.field_150422_aJ);
        }
        int i = 0;
        for (int max = Math.min(StructuralLevel.LEVELS.length - 1, BetterBarrels.maxCraftableTier); i < max; i++) {
            addStructuralUpgrade(i, StructuralLevel.LEVELS[(i + 1)].material.getStack());
        }
    }

    private void addCoreUpgradeUpgrade(int resultMeta, int sourceMeta)
    {
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BetterBarrels.itemUpgradeCore, 1, resultMeta), new Object[] { new ItemStack(BetterBarrels.itemUpgradeCore, 1, sourceMeta), new ItemStack(BetterBarrels.itemUpgradeCore, 1, sourceMeta), new ItemStack(BetterBarrels.itemUpgradeCore, 1, sourceMeta) }));



        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BetterBarrels.itemUpgradeCore, 3, sourceMeta), new Object[] { new ItemStack(BetterBarrels.itemUpgradeCore, 1, resultMeta) }));
    }

    private void addStructuralUpgrade(int level, Object variableComponent)
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.itemUpgradeStructural, 1, level), new Object[] { "PBP", "B B", "PBP",

                Character.valueOf('P'), this.upgradeItem,
                Character.valueOf('B'), variableComponent }));
    }

    private void addCoreUpgrade(int meta, Object variableComponent)
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.itemUpgradeCore, 1, meta), new Object[] { " P ", " B ", " P ",

                Character.valueOf('P'), Blocks.field_150331_J,
                Character.valueOf('B'), variableComponent }));
    }

    private void addSideUpgrade(int meta, Object center, Object border)
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.itemUpgradeSide, 4, meta), new Object[]{" P ", "PBP", " P ",

                Character.valueOf('P'), border,
                Character.valueOf('B'), center}));
    }

}
