package mcp.mobius.betterbarrels.client.render;

import mcp.mobius.betterbarrels.BetterBarrels;
import mcp.mobius.betterbarrels.common.StructuralLevel;
import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;
import mcp.mobius.betterbarrels.common.blocks.logic.Coordinates;
import mcp.mobius.betterbarrels.common.items.IOverlayItem;
import mcp.mobius.betterbarrels.common.items.upgrades.UpgradeCore;
import mcp.mobius.betterbarrels.common.items.upgrades.UpgradeSide;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class TileEntityBarrelRenderer extends TileEntityBaseRenderer {

    public static TileEntityBarrelRenderer _instance = null;
    protected static ItemStack coreStorage = new ItemStack(BetterBarrels.itemUpgradeCore, 0, 0);
    protected static ItemStack coreEnder = new ItemStack(BetterBarrels.itemUpgradeCore, 0, 1);
    protected static ItemStack coreRedstone = new ItemStack(BetterBarrels.itemUpgradeCore, 0, 2);
    protected static ItemStack coreHopper = new ItemStack(BetterBarrels.itemUpgradeCore, 0, 3);
    protected static ItemStack coreVoid = new ItemStack(BetterBarrels.itemUpgradeCore, 0, UpgradeCore.VOID.ordinal());
    protected static ItemStack coreCreative = new ItemStack(BetterBarrels.itemUpgradeCore, 0, UpgradeCore.CREATIVE.ordinal());

    public static TileEntityBarrelRenderer instance()
    {
        if (_instance == null) {
            _instance = new TileEntityBarrelRenderer();
        }
        return _instance;
    }

    public void func_147500_a(TileEntity tileEntity, double xpos, double ypos, double zpos, float var8)
    {
        if ((tileEntity instanceof TileEntityBarrel))
        {
            saveBoundTexture();




            int[][] savedGLState = modifyGLState(new int[] { 3042, 2896 }, null);

            ForgeDirection orientation = ((TileEntityBarrel)tileEntity).orientation;
            ForgeDirection rotation = ((TileEntityBarrel)tileEntity).rotation;
            TileEntityBarrel barrelEntity = (TileEntityBarrel)tileEntity;
            Coordinates barrelPos = new Coordinates(0, xpos, ypos, zpos);

            boolean isHammer = (this.mc.field_71439_g.func_70694_bm().func_77973_b() instanceof IOverlayItem);
            boolean hasItem = barrelEntity.getStorage().hasItem();

            int color = StructuralLevel.LEVELS[barrelEntity.coreUpgrades.levelStructural].clientData.getTextColor();
            for (ForgeDirection forgeSide : ForgeDirection.VALID_DIRECTIONS)
            {
                boolean isTopBottom = (forgeSide == ForgeDirection.DOWN) || (forgeSide == ForgeDirection.UP);
                if ((hasItem) && (isItemDisplaySide(barrelEntity, forgeSide)))
                {
                    setLight(barrelEntity, forgeSide);

                    renderStackOnBlock(barrelEntity.getStorage().getItemForRender(), forgeSide, isTopBottom ? rotation : orientation, barrelPos, 8.0F, 65.0D, isTopBottom ? 64.0D : 75.0D);
                    String barrelString = getBarrelString(barrelEntity);
                    renderTextOnBlock(barrelString, forgeSide, isTopBottom ? rotation : orientation, barrelPos, 2.0F, 128.0D, 10.0D, color, TileEntityBaseRenderer.ALIGNCENTER);
                }
            }
            if (isHammer) {
                for (ForgeDirection forgeSide : ForgeDirection.VALID_DIRECTIONS)
                {
                    boolean isTopBottom = (forgeSide == ForgeDirection.DOWN) || (forgeSide == ForgeDirection.UP);
                    setLight(barrelEntity, forgeSide);
                    if (barrelEntity.sideUpgrades[forgeSide.ordinal()] == UpgradeSide.REDSTONE)
                    {
                        int index = barrelEntity.sideMetadata[forgeSide.ordinal()] + 32;
                        renderIconOnBlock(index, forgeSide, isTopBottom ? rotation : orientation, barrelPos, 2.0F, 224.0D, 0.0D, -0.009999999776482582D);
                    }
                    else if (barrelEntity.sideUpgrades[forgeSide.ordinal()] == UpgradeSide.HOPPER)
                    {
                        int index = barrelEntity.sideMetadata[forgeSide.ordinal()] + 32;
                        renderIconOnBlock(index, forgeSide, isTopBottom ? rotation : orientation, barrelPos, 2.0F, 224.0D, 0.0D, -0.009999999776482582D);
                    }
                    else if (isItemDisplaySide(barrelEntity, forgeSide))
                    {
                        int offsetY = 224;
                        if (barrelEntity.coreUpgrades.levelStructural > 0)
                        {
                            renderIconOnBlock(StructuralLevel.LEVELS[barrelEntity.coreUpgrades.levelStructural].clientData.getIconItem(), 1, forgeSide, isTopBottom ? rotation : orientation, barrelPos, 2.0F, 0.0D, 0.0D, -0.001000000047497451D);
                            renderTextOnBlock("x" + String.valueOf(barrelEntity.coreUpgrades.levelStructural), forgeSide, isTopBottom ? rotation : orientation, barrelPos, 2.0F, 37.0D, 15.0D, color, TileEntityBaseRenderer.ALIGNLEFT);
                            if (barrelEntity.coreUpgrades.getFreeSlots() > 0)
                            {
                                String freeSlots = String.valueOf(barrelEntity.coreUpgrades.getFreeSlots());
                                if (freeSlots.length() < 4) {
                                    renderTextOnBlock(freeSlots, forgeSide, isTopBottom ? rotation : orientation, barrelPos, 2.0F, 254.0D, 127.0D, color, TileEntityBaseRenderer.ALIGNRIGHT);
                                } else {
                                    renderTextOnBlock(freeSlots, forgeSide, isTopBottom ? rotation : orientation, barrelPos, 2.0F, 248.0D, 134.0D, 90.0F, color, TileEntityBaseRenderer.ALIGNCENTER);
                                }
                            }
                        }
                        if (barrelEntity.coreUpgrades.nStorageUpg > 0)
                        {
                            renderStackOnBlock(coreStorage, forgeSide, isTopBottom ? rotation : orientation, barrelPos, 2.0F, 224.0D, 0.0D);
                            renderTextOnBlock(String.valueOf(barrelEntity.coreUpgrades.nStorageUpg) + "x", forgeSide, isTopBottom ? rotation : orientation, barrelPos, 2.0F, 224.0D, 15.0D, color, TileEntityBaseRenderer.ALIGNRIGHT);
                        }
                        if (barrelEntity.coreUpgrades.hasRedstone)
                        {
                            renderStackOnBlock(coreRedstone, forgeSide, isTopBottom ? rotation : orientation, barrelPos, 2.0F, 0.0D, offsetY);
                            offsetY -= 35;
                        }
                        if (barrelEntity.coreUpgrades.hasHopper)
                        {
                            renderStackOnBlock(coreHopper, forgeSide, isTopBottom ? rotation : orientation, barrelPos, 2.0F, 0.0D, offsetY);
                            offsetY -= 35;
                        }
                        if (barrelEntity.coreUpgrades.hasEnder)
                        {
                            renderStackOnBlock(coreEnder, forgeSide, isTopBottom ? rotation : orientation, barrelPos, 2.0F, 0.0D, offsetY);
                            offsetY -= 35;
                        }
                        if (barrelEntity.coreUpgrades.hasVoid)
                        {
                            renderStackOnBlock(coreVoid, forgeSide, isTopBottom ? rotation : orientation, barrelPos, 2.0F, 0.0D, offsetY);
                            offsetY -= 35;
                        }
                        if (barrelEntity.coreUpgrades.hasCreative)
                        {
                            renderStackOnBlock(coreCreative, forgeSide, isTopBottom ? rotation : orientation, barrelPos, 2.0F, 0.0D, offsetY);
                            offsetY -= 35;
                        }
                    }
                }
            }
            restoreGlState(savedGLState);
            loadBoundTexture();
        }
    }

    protected String getBarrelString(TileEntityBarrel barrel)
    {
        String outstring = null;
        if (!barrel.getStorage().hasItem()) {
            return "";
        }
        int maxstacksize = barrel.getStorage().getItem().func_77976_d();

        int amount = barrel.getStorage().getAmount();
        if (barrel.coreUpgrades.hasCreative)
        {
            outstring = "-";
        }
        else if (maxstacksize != 1)
        {
            int nstacks = amount / maxstacksize;
            int remains = amount % maxstacksize;
            if ((nstacks > 0) && (remains > 0)) {
                outstring = String.format("%s*%s + %s", new Object[] { Integer.valueOf(nstacks), Integer.valueOf(maxstacksize), Integer.valueOf(remains) });
            } else if ((nstacks == 0) && (remains > 0)) {
                outstring = String.format("%s", new Object[] { Integer.valueOf(remains) });
            } else if ((nstacks > 0) && (remains == 0)) {
                outstring = String.format("%s*%s", new Object[] { Integer.valueOf(nstacks), Integer.valueOf(maxstacksize) });
            } else if (amount == 0) {
                outstring = "0";
            }
        }
        else if (maxstacksize == 1)
        {
            outstring = String.format("%s", new Object[] { Integer.valueOf(amount) });
        }
        else
        {
            outstring = "";
        }
        return outstring;
    }

    protected boolean isItemDisplaySide(TileEntityBarrel barrel, ForgeDirection forgeSide)
    {
        if (barrel.sideUpgrades[forgeSide.ordinal()] == UpgradeSide.NONE) {
            return false;
        }
        if (barrel.sideUpgrades[forgeSide.ordinal()] == UpgradeSide.FRONT) {
            return true;
        }
        if (barrel.sideUpgrades[forgeSide.ordinal()] == UpgradeSide.STICKER) {
            return true;
        }
        return false;
    }

}
