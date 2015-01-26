package mcp.mobius.betterbarrels;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;
import mcp.mobius.betterbarrels.common.BaseProxy;
import mcp.mobius.betterbarrels.common.StructuralLevel;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
@Mod(modid="JABBA", name="JABBA", version="1.2.0a", dependencies="after:Waila;after:NotEnoughItems")
public class BetterBarrels {

    private static boolean DEBUG = Boolean.parseBoolean(System.getProperty("mcp.mobius.debugJabba", "false"));
    public static final String modid = "JABBA";

    public static void debug(String msg)
    {
        if (DEBUG) {
            log.log(Level.WARN, msg);
        }
    }

    public static Logger log = LogManager.getLogger("JABBA");
    @Mod.Instance("JABBA")
    public static BetterBarrels instance;
    @SidedProxy(clientSide="mcp.mobius.betterbarrels.client.ClientProxy", serverSide="mcp.mobius.betterbarrels.common.BaseProxy")
    public static BaseProxy proxy;
    private static Configuration config = null;
    public static boolean fullBarrelTexture = true;
    public static boolean highRezTexture = true;
    public static boolean showUpgradeSymbols = true;
    public static boolean diamondDollyActive = true;
    public static int stacksSize = 64;
    public static int maxCraftableTier = StructuralLevel.defaultUpgradeMaterialsList.length;
    public static String upgradeItemStr = "minecraft:fence";
    public static Block blockBarrel = null;
    public static Block blockMiniBarrel = null;
    public static Block blockBarrelShelf = null;
    public static Item itemUpgradeStructural = null;
    public static Item itemUpgradeCore = null;
    public static Item itemUpgradeSide = null;
    public static Item itemMover = null;
    public static Item itemMoverDiamond = null;
    public static Item itemTuningFork = null;
    public static Item itemLockingPlanks = null;
    public static Item itemHammer = null;
    public static long limiterDelay = 500L;
    public static int blockBarrelRendererID = -1;
    public static boolean allowVerticalPlacement = true;
    public static float verticalPlacementRange = 1.0F;
    public static boolean exposeFullStorageSize = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        config = new Configuration(event.getSuggestedConfigurationFile());
        try
        {
            config.load();

            diamondDollyActive = config.get("general", "diamondDollyActive", true).getBoolean(true);
            limiterDelay = config.get("general", "packetLimiterDelay", 500, "Controls the minimum delay (in ms) between two server/client sync. Lower values mean closer to realtime, and more network usage.").getInt();

            String[] materialsList = config.get("general", "materialList", StructuralLevel.defaultUpgradeMaterialsList, "A structural tier will be created for each material in this list, even if not craftable").getStringList();
            if (materialsList.length > 18)
            {
                String[] trimedList = new String[18];
                for (int i = 0; i < 18; i++) {
                    trimedList[i] = materialsList[i];
                }
                materialsList = trimedList;
                config.get("general", "materialList", trimedList).set(trimedList);
            }
            debug("00 - Loaded materials list: " + Arrays.toString(materialsList));
            StructuralLevel.createLevelArray(materialsList);
            maxCraftableTier = Math.min(18, Math.min(materialsList.length, config.get("general", "maxCraftableTier", materialsList.length, "Maximum tier to generate crafting recipes for").getInt()));
            debug("01 - Max craftable tier: " + maxCraftableTier);
            proxy.initialiseClientData(config.get("general", "colorOverrides", new int[] { 0, 0 }, "This list contains paired numbers: first is the tier level this color applies to, second is the color. The color value is the RGB color as a single int").getIntList());

            stacksSize = config.get("general", "stacksSize", stacksSize, "How many stacks the base barrel and each upgrade will provide").getInt();
            upgradeItemStr = config.get("general", "tierUpgradeItem", upgradeItemStr, "The name of the item to use for the strutural tier upgrade recipes. Default is \"minecraft:fence\" for Vanilla Fence. The format is Ore.name for an ore dictionary lookup, or itemDomain:itemname[:meta] for a direct item, not this is case-sensitive.").getString();

            allowVerticalPlacement = config.getBoolean("allowVerticalPlacement", "general", true, "If true, barrels can be initially placed and dollyed so that their front side can be on the top or bottom. The front side is the side with the initial sticker applied.");
            verticalPlacementRange = config.getFloat("verticalPlacementRange", "general", 0.79F, 0.0F, 1.0F, "This is used when testing a players aim for block placement.  If the aim value is greater than or equal to this setting, it is determined you are attempting to place a block facing down.  The reverse is true for placing blocks facing up. 0 = dead ahead, 1 = directly above.");

            exposeFullStorageSize = config.getBoolean("exposeFullStorageSize", "experimental", false, "If true, barrels will expose their full contents through the standard MC inventory interfaces. This will allow mods that do not support the DSU to see the full contents of the barrel. *** WARNING *** This will allow mods that do not properly handle inventories to empty out a barrel in one go. Use at your own risk. If you do find such a game breaking mod, please report to that mods' author and ask them to handle inventories better. Otherwise, please enjoy this experimental feature ^_^");
        }
        catch (Exception e)
        {
            FMLLog.log(Level.ERROR, e, "BlockBarrel has a problem loading it's configuration", new Object[0]);
            FMLLog.severe(e.getMessage(), new Object[0]);
        }
        finally
        {
            if (config.hasChanged()) {
                config.save();
            }
        }
        proxy.registerEventHandler();


        blockBarrel = new BlockBarrel();
        itemUpgradeStructural = new ItemUpgradeStructural();
        itemUpgradeCore = new ItemUpgradeCore();
        itemUpgradeSide = new ItemUpgradeSide();
        itemMover = new ItemBarrelMover();
        itemMoverDiamond = new ItemDiamondMover();
        itemHammer = new ItemBarrelHammer();
        itemTuningFork = new ItemTuningFork();

        GameRegistry.registerBlock(blockBarrel, "barrel");





        GameRegistry.registerItem(itemUpgradeStructural, "upgradeStructural");
        GameRegistry.registerItem(itemUpgradeCore, "upgradeCore");
        GameRegistry.registerItem(itemUpgradeSide, "upgradeSide");
        GameRegistry.registerItem(itemMover, "mover");
        GameRegistry.registerItem(itemMoverDiamond, "moverDiamond");
        GameRegistry.registerItem(itemHammer, "hammer");
        GameRegistry.registerItem(itemTuningFork, "tuningFork");

        BarrelPacketHandler.INSTANCE.ordinal();
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
        RecipeHandler.instance().registerRecipes();
        GameRegistry.registerTileEntity(TileEntityBarrel.class, "TileEntityBarrel");
        FMLCommonHandler.instance().bus().register(ServerTickHandler.INSTANCE);
        proxy.registerRenderers();
        FMLInterModComms.sendMessage("Waila", "register", "mcp.mobius.betterbarrels.BBWailaProvider.callbackRegister");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        RecipeHandler.instance().registerOres();
        RecipeHandler.instance().registerLateRecipes();
        proxy.postInit();
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
        BSpaceStorageHandler.instance().writeToFile();
    }

}
