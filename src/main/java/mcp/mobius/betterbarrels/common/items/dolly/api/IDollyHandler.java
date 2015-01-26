package mcp.mobius.betterbarrels.common.items.dolly.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public abstract interface IDollyHandler {

    public abstract void onContainerPickup(World paramWorld, int paramInt1, int paramInt2, int paramInt3, TileEntity paramTileEntity);

}
