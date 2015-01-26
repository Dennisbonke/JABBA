package mcp.mobius.betterbarrels.client.render;

import mcp.mobius.betterbarrels.common.blocks.logic.Coordinates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public abstract class TileEntityBaseRenderer extends TileEntitySpecialRenderer {

    protected float scale = 0.0039063F;
    protected RenderBlocks renderBlocks = new RenderBlocks();
    protected RenderItem renderItem = new RenderItem();
    protected Minecraft mc = Minecraft.getMinecraft();
    protected TextureManager texManager = this.mc.field_71446_o;
    protected FontRenderer renderFont = this.mc.field_71466_p;
    protected static ResourceLocation itemsSheetRes = new ResourceLocation("jabba", "textures/sheets/items.png");
    protected int boundTexIndex;
    protected static byte ALIGNLEFT = 0;
    protected static byte ALIGNCENTER = 1;
    protected static byte ALIGNRIGHT = 2;

    protected void setLight(TileEntity tileEntity, ForgeDirection side)
    {
        int ambientLight = tileEntity.func_145831_w().func_72802_i(tileEntity.field_145851_c + side.offsetX, tileEntity.field_145848_d + side.offsetY, tileEntity.field_145849_e + side.offsetZ, 0);
        int var6 = ambientLight % 65536;
        int var7 = ambientLight / 65536;
        float var8 = 1.0F;
        OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, var6 * var8, var7 * var8);
    }

    protected void renderTextOnBlock(String renderString, ForgeDirection side, ForgeDirection orientation, Coordinates barrelPos, float size, double posx, double posy, int red, int green, int blue, int alpha, byte align)
    {
        int color = alpha << 24 | red << 16 | blue << 8 | green;
        renderTextOnBlock(renderString, side, orientation, barrelPos, size, posx, posy, color, align);
    }

    protected void renderTextOnBlock(String renderString, ForgeDirection side, ForgeDirection orientation, Coordinates barrelPos, float size, double posx, double posy, int color, byte align)
    {
        renderTextOnBlock(renderString, side, orientation, barrelPos, size, posx, posy, 0.0F, color, align);
    }

    protected void renderTextOnBlock(String renderString, ForgeDirection side, ForgeDirection orientation, Coordinates barrelPos, float size, double posx, double posy, float angle, int color, byte align)
    {
        if ((renderString == null) || (renderString.equals(""))) {
            return;
        }
        int stringWidth = func_147498_b().func_78256_a(renderString);

        GL11.glPushMatrix();

        alignRendering(side, orientation, barrelPos);
        moveRendering(size, posx, posy, -0.001D);

        GL11.glRotatef(angle, 0.0F, 0.0F, 1.0F);

        GL11.glDepthMask(false);
        GL11.glDisable(2896);
        switch (align)
        {
            case 0:
                func_147498_b().func_78276_b(renderString, 0, 0, color);
                break;
            case 1:
                func_147498_b().func_78276_b(renderString, -stringWidth / 2, 0, color);
                break;
            case 2:
                func_147498_b().func_78276_b(renderString, -stringWidth, 0, color);
        }
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected void renderStackOnBlock(ItemStack stack, ForgeDirection side, ForgeDirection orientation, Coordinates barrelPos, float size, double posx, double posy)
    {
        if (stack == null) {
            return;
        }
        int[][] savedGLState = saveGLState(new int[] { 3008, 2896 });
        GL11.glPushMatrix();

        alignRendering(side, orientation, barrelPos);
        moveRendering(size, posx, posy, -0.001D);
        if (!ForgeHooksClient.renderInventoryItem(this.renderBlocks, this.texManager, stack, true, 0.0F, 0.0F, 0.0F)) {
            this.renderItem.func_77015_a(this.renderFont, this.texManager, stack, 0, 0);
        }
        GL11.glPopMatrix();
        restoreGlState(savedGLState);
    }

    protected void renderIconOnBlock(IIcon icon, int sheet, ForgeDirection side, ForgeDirection orientation, Coordinates barrelPos, float size, double posx, double posy, double zdepth)
    {
        if (icon == null) {
            return;
        }
        int[][] savedGLState = modifyGLState(new int[] { 2896 }, new int[] { 3008 });
        GL11.glPushMatrix();

        alignRendering(side, orientation, barrelPos);
        moveRendering(size, posx, posy, zdepth);

        this.texManager.func_110577_a(sheet == 0 ? TextureMap.field_110575_b : TextureMap.field_110576_c);
        drawIcon(0, 0, icon, side);

        GL11.glPopMatrix();
        restoreGlState(savedGLState);
    }

    protected void renderIconOnBlock(int index, ForgeDirection side, ForgeDirection orientation, Coordinates barrelPos, float size, double posx, double posy, double zdepth)
    {
        GL11.glPushMatrix();

        alignRendering(side, orientation, barrelPos);
        moveRendering(size / 2.0F, posx, posy, zdepth);

        this.texManager.func_110577_a(itemsSheetRes);
        drawTexturedModalRect(0, 0, 32 * (index % 16), 32 * (index / 16), 32, 32);

        GL11.glPopMatrix();
    }

    protected void alignRendering(ForgeDirection side, ForgeDirection orientation, Coordinates position)
    {
        GL11.glTranslated(position.x + 0.5D, position.y + 0.5D, position.z + 0.5D);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(getRotationYForSide(side, orientation), 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(getRotationXForSide(side), 1.0F, 0.0F, 0.0F);
        GL11.glTranslated(-0.5D, -0.5D, -0.5D);
    }

    protected void moveRendering(float size, double posX, double posY, double posz)
    {
        GL11.glTranslated(0.0D, 0.0D, posz);
        GL11.glScalef(this.scale, this.scale, -1.0E-004F);
        GL11.glTranslated(posX, posY, 0.0D);
        GL11.glScalef(size, size, 1.0F);
    }

    static final int[] orientRotation = { 0, 0, 0, 2, 3, 1, 0 };

    protected float getRotationYForSide(ForgeDirection side, ForgeDirection orientation)
    {
        int[] sideRotation = { orientRotation[orientation.ordinal()], orientRotation[orientation.ordinal()], 0, 2, 3, 1 };
        return sideRotation[side.ordinal()] * 90.0F;
    }

    static final int[] sideRotation = { 1, 3, 0, 0, 0, 0 };

    protected float getRotationXForSide(ForgeDirection side)
    {
        return sideRotation[side.ordinal()] * 90.0F;
    }

    protected void drawIcon(int posX, int posY, IIcon icon, ForgeDirection side)
    {
        float minU = icon.func_94209_e();
        float minV = icon.func_94206_g();
        float maxU = icon.func_94212_f();
        float maxV = icon.func_94210_h();
        int sizeX = 16;
        int sizeY = 16;
        Tessellator var9 = Tessellator.field_78398_a;
        var9.func_78382_b();

        var9.func_78374_a(posX + 0, posY + sizeY, 0.0D, minU, maxV);
        var9.func_78374_a(posX + sizeX, posY + sizeY, 0.0D, maxU, maxV);
        var9.func_78374_a(posX + sizeX, posY + 0, 0.0D, maxU, minV);
        var9.func_78374_a(posX + 0, posY + 0, 0.0D, minU, minV);
        var9.func_78381_a();
    }

    protected void drawTexturedModalRect(int posX, int posY, int textureX, int textureY, int sizeX, int sizeY)
    {
        float scaleX = 0.0039063F;
        float scaleY = 0.0039063F;
        float zLevel = 0.0F;
        Tessellator var9 = Tessellator.field_78398_a;
        var9.func_78382_b();
        var9.func_78374_a(posX + 0, posY + sizeY, zLevel, (textureX + 0) * scaleX, (textureY + sizeY) * scaleY);
        var9.func_78374_a(posX + sizeX, posY + sizeY, zLevel, (textureX + sizeX) * scaleX, (textureY + sizeY) * scaleY);
        var9.func_78374_a(posX + sizeX, posY + 0, zLevel, (textureX + sizeX) * scaleX, (textureY + 0) * scaleY);
        var9.func_78374_a(posX + 0, posY + 0, zLevel, (textureX + 0) * scaleX, (textureY + 0) * scaleY);
        var9.func_78381_a();
    }

    protected void saveBoundTexture()
    {
        this.boundTexIndex = GL11.glGetInteger(32873);
    }

    protected void loadBoundTexture()
    {
        GL11.glBindTexture(3553, this.boundTexIndex);
    }

    protected int[][] saveGLState(int[] bitsToSave)
    {
        if (bitsToSave == null) {
            return (int[][])null;
        }
        int[][] savedGLState = new int[bitsToSave.length][2];
        int count = 0;
        for (int glBit : bitsToSave)
        {
            savedGLState[count][0] = glBit;
            savedGLState[(count++)][1] = (GL11.glIsEnabled(glBit) ? 1 : 0);
        }
        return savedGLState;
    }

    protected int[][] modifyGLState(int[] bitsToDisable, int[] bitsToEnable)
    {
        return modifyGLState(bitsToDisable, bitsToEnable, null);
    }

    protected int[][] modifyGLState(int[] bitsToDisable, int[] bitsToEnable, int[] bitsToSave)
    {
        if ((bitsToDisable == null) && (bitsToEnable == null) && (bitsToSave == null)) {
            return (int[][])null;
        }
        int[][] savedGLState = new int[(bitsToDisable != null ? bitsToDisable.length : 0) + (bitsToEnable != null ? bitsToEnable.length : 0) + (bitsToSave != null ? bitsToSave.length : 0)][2];
        int count = 0;
        if (bitsToDisable != null) {
            for (int glBit : bitsToDisable)
            {
                savedGLState[count][0] = glBit;
                savedGLState[(count++)][1] = (GL11.glIsEnabled(glBit) ? 1 : 0);
                GL11.glDisable(glBit);
            }
        }
        if (bitsToEnable != null) {
            for (int glBit : bitsToEnable)
            {
                savedGLState[count][0] = glBit;
                savedGLState[(count++)][1] = (GL11.glIsEnabled(glBit) ? 1 : 0);
                GL11.glEnable(glBit);
            }
        }
        if (bitsToSave != null) {
            for (int glBit : bitsToSave)
            {
                savedGLState[count][0] = glBit;
                savedGLState[(count++)][1] = (GL11.glIsEnabled(glBit) ? 1 : 0);
            }
        }
        return savedGLState;
    }

    protected void restoreGlState(int[][] savedGLState)
    {
        if (savedGLState == null) {
            return;
        }
        for (int[] glBit : savedGLState) {
            if (glBit[1] == 1) {
                GL11.glEnable(glBit[0]);
            } else {
                GL11.glDisable(glBit[0]);
            }
        }
    }

}
