package mcp.mobius.betterbarrels.client.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import mcp.mobius.betterbarrels.BetterBarrels;
import mcp.mobius.betterbarrels.common.StructuralLevel;
import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class BlockBarrelRenderer implements ISimpleBlockRenderingHandler {

    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        Tessellator tessellator = Tessellator.field_78398_a;


        IIcon iconSide = StructuralLevel.LEVELS[0].clientData.getIconSide();
        IIcon iconTop = StructuralLevel.LEVELS[0].clientData.getIconTop();
        IIcon iconLabel = StructuralLevel.LEVELS[0].clientData.getIconLabel();

        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        tessellator.func_78382_b();
        tessellator.func_78375_b(0.0F, -1.0F, 0.0F);
        renderer.func_147768_a(block, 0.0D, 0.0D, 0.0D, iconTop);
        tessellator.func_78375_b(0.0F, 1.0F, 0.0F);
        renderer.func_147806_b(block, 0.0D, 0.0D, 0.0D, iconTop);
        tessellator.func_78375_b(0.0F, 0.0F, -1.0F);
        renderer.func_147761_c(block, 0.0D, 0.0D, 0.0D, iconSide);
        tessellator.func_78375_b(0.0F, 0.0F, 1.0F);
        renderer.func_147734_d(block, 0.0D, 0.0D, 0.0D, iconSide);
        tessellator.func_78375_b(-1.0F, 0.0F, 0.0F);
        renderer.func_147798_e(block, 0.0D, 0.0D, 0.0D, iconSide);
        tessellator.func_78375_b(1.0F, 0.0F, 0.0F);
        renderer.func_147764_f(block, 0.0D, 0.0D, 0.0D, iconLabel);
        tessellator.func_78381_a();
    }

    private static int[][] forgeFacingtoMCTopBottomRotate = { { 0, 0, 0, 3, 1, 2, 0 }, { 0, 0, 3, 0, 1, 2, 0 } };

    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block tile, int modelId, RenderBlocks renderer)
    {
        TileEntityBarrel barrel = (TileEntityBarrel)world.func_147438_o(x, y, z);

        renderer.field_147865_v = forgeFacingtoMCTopBottomRotate[0][barrel.rotation.ordinal()];
        renderer.field_147867_u = forgeFacingtoMCTopBottomRotate[1][barrel.rotation.ordinal()];

        barrel.overlaying = false;
        boolean renderedBarrel = renderer.func_147784_q(tile, x, y, z);
        barrel.overlaying = true;
        boolean renderedOverlay = renderer.func_147784_q(tile, x, y, z);
        barrel.overlaying = false;

        renderer.field_147865_v = 0;
        renderer.field_147867_u = 0;

        return (renderedBarrel) || (renderedOverlay);
    }

    public boolean shouldRender3DInInventory(int modelID)
    {
        return true;
    }

    public int getRenderId()
    {
        return BetterBarrels.blockBarrelRendererID;
    }

}
