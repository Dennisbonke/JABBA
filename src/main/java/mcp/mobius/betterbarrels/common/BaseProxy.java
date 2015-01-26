package mcp.mobius.betterbarrels.common;

import mcp.mobius.betterbarrels.bspace.BBEventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class BaseProxy {

    public void registerEventHandler()
    {
        MinecraftForge.EVENT_BUS.register(new BBEventHandler());
    }

    public void registerRenderers() {}

    public void updatePlayerInventory(EntityPlayer player)
    {
        if ((player instanceof EntityPlayerMP))
        {
            EntityPlayerMP playerMP = (EntityPlayerMP)player;
            playerMP.func_71120_a(playerMP.field_71069_bz);
        }
    }

    public void postInit() {}

    public void checkRenderers() {}

    public void initialiseClientData(int[] overrideColorData) {}

}
