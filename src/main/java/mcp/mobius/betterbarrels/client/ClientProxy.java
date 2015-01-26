package mcp.mobius.betterbarrels.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import mcp.mobius.betterbarrels.BetterBarrels;
import mcp.mobius.betterbarrels.Utils;
import mcp.mobius.betterbarrels.bspace.BBEventHandler;
import mcp.mobius.betterbarrels.common.BaseProxy;
import mcp.mobius.betterbarrels.common.StructuralLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraftforge.common.MinecraftForge;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class ClientProxy extends BaseProxy {

    public static Map<Integer, ISimpleBlockRenderingHandler> blockRenderers;

    public void registerRenderers()
    {
        try
        {
            Field blockRendererField = RenderingRegistry.class.getDeclaredField("blockRenderers");
            blockRendererField.setAccessible(true);
            blockRenderers = (Map)blockRendererField.get(RenderingRegistry.instance());
        }
        catch (Throwable t) {}
        BetterBarrels.blockBarrelRendererID = RenderingRegistry.getNextAvailableRenderId();
        while (blockRenderers.containsKey(Integer.valueOf(BetterBarrels.blockBarrelRendererID))) {
            BetterBarrels.blockBarrelRendererID = RenderingRegistry.getNextAvailableRenderId();
        }
        RenderingRegistry.registerBlockHandler(BetterBarrels.blockBarrelRendererID, new BlockBarrelRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBarrel.class, new TileEntityBarrelRenderer());
    }

    public void checkRenderers()
    {
        ISimpleBlockRenderingHandler renderer = (ISimpleBlockRenderingHandler)blockRenderers.get(Integer.valueOf(BetterBarrels.blockBarrelRendererID));
        if (!(renderer instanceof BlockBarrelRenderer)) {
            throw new RuntimeException(String.format("Wrong renderer found ! %s found while looking up the Jabba Barrel renderer.", new Object[] { renderer.getClass().getCanonicalName() }));
        }
    }

    public void registerEventHandler()
    {
        MinecraftForge.EVENT_BUS.register(new BBEventHandler());
    }

    public void postInit()
    {
        ((IReloadableResourceManager) Minecraft.func_71410_x().func_110442_L()).func_110542_a(new IResourceManagerReloadListener()
        {
            private boolean ranOnce = false;

            public void func_110549_a(IResourceManager resourcemanager)
            {
                if (!this.ranOnce)
                {
                    this.ranOnce = true;
                    return;
                }
                StructuralLevelClientData.loadBaseTextureData();
                if (StructuralLevel.LEVELS != null) {
                    for (StructuralLevel level : StructuralLevel.LEVELS) {
                        if (level.levelNum != 0)
                        {
                            level.clientData.generateIcons();
                            StringTranslate.inject(new ByteArrayInputStream(("item.upgrade.structural." + String.valueOf(level.levelNum) + ".name=" + StatCollector.func_74838_a("item.upgrade.structural") + " " + Utils.romanNumeral(level.levelNum) + " (" + level.clientData.getMaterialName() + ")").getBytes()));
                        }
                    }
                }
                StructuralLevelClientData.unloadBaseTextureData();
            }
        });
    }

    public void initialiseClientData(int[] overrideColorData)
    {
        for (StructuralLevel level : StructuralLevel.LEVELS) {
            level.clientData = new StructuralLevelClientData(level);
        }
        if (overrideColorData != null) {
            if (overrideColorData.length % 2 == 0) {
                for (int i = 0; i < overrideColorData.length; i += 2) {
                    if (overrideColorData[i] != 0) {
                        if ((overrideColorData[i] > 0) && (overrideColorData[i] < StructuralLevel.LEVELS.length)) {
                            StructuralLevel.LEVELS[overrideColorData[i]].clientData.setColorOverride(0xFF000000 | overrideColorData[(i + 1)]);
                        } else {
                            BetterBarrels.log.warn("Attempting to override the structural tier color for non existant tier: " + overrideColorData[i]);
                        }
                    }
                }
            } else {
                BetterBarrels.log.warn("Color override list is not formatted in pairs, ignoring");
            }
        }
    }

}
