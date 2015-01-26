package mcp.mobius.betterbarrels.client;

import cpw.mods.fml.common.registry.LanguageRegistry;
import mcp.mobius.betterbarrels.BetterBarrels;
import mcp.mobius.betterbarrels.Utils;
import mcp.mobius.betterbarrels.common.StructuralLevel;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.compress.archivers.dump.DumpArchiveEntry;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;
import scala.tools.cmd.gen.AnyVals;

import java.lang.reflect.Method;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class StructuralLevelClientData {

    private AccessibleTextureAtlasSprite iconBlockSide;
    private AccessibleTextureAtlasSprite iconBlockLabel;
    private AccessibleTextureAtlasSprite iconBlockTop;
    private AccessibleTextureAtlasSprite iconBlockTopLabel;
    private AccessibleTextureAtlasSprite iconItem;
    private ItemStack materialStack;
    private StructuralLevel level;
    private int textColor = -1;
    private String name;
    private int colorOverride = -1;
    private static BaseTextures baseTexturePixels;

    public StructuralLevelClientData(StructuralLevel inlevel)
    {
        this.level = inlevel;
    }

    public IIcon getIconSide()
    {
        return this.iconBlockSide;
    }

    public IIcon getIconTop()
    {
        return this.iconBlockTop;
    }

    public IIcon getIconLabel()
    {
        return this.iconBlockLabel;
    }

    public IIcon getIconLabelTop()
    {
        return this.iconBlockTopLabel;
    }

    public IIcon getIconItem()
    {
        return this.iconItem;
    }

    public int getTextColor()
    {
        return this.textColor;
    }

    public String getMaterialName()
    {
        return this.name;
    }

    public void setColorOverride(int override)
    {
        this.colorOverride = override;
    }

    public void cacheStackAndName()
    {
        BetterBarrels.debug("15 - Looking up user friendly name for " + (this.level.material.isOreDict() ? this.level.material.name : new StringBuilder().append(this.level.material.modDomain).append(":").append(this.level.material.name).append(":").append(this.level.material.meta).toString()));
        this.materialStack = this.level.material.getStack();
        this.name = this.materialStack.func_82833_r();
        if (this.name.indexOf(".name") > 0) {
            this.name = LanguageRegistry.instance().getStringLocalization(this.name);
        }
        BetterBarrels.debug("16 - Found: " + this.name);
    }

    public static void loadBaseTextureData()
    {
        BetterBarrels.debug("08 - Pre-loading component texture data.");
        baseTexturePixels = new BaseTextures(null);

        baseTexturePixels.labelBorder = getPixelsForTexture(false, "JABBA:barrel_label_border");
        baseTexturePixels.labelBackground = getPixelsForTexture(false, "JABBA:barrel_label_background");
        baseTexturePixels.topBorder = getPixelsForTexture(false, "JABBA:barrel_top_border");
        baseTexturePixels.topBackground = getPixelsForTexture(false, "JABBA:barrel_top_background");
        baseTexturePixels.topLabel = getPixelsForTexture(false, "JABBA:barrel_top_label");
        baseTexturePixels.sideBorder = getPixelsForTexture(false, "JABBA:barrel_side_border");
        baseTexturePixels.sideBackground = getPixelsForTexture(false, "JABBA:barrel_side_background");
        baseTexturePixels.item = getPixelsForTexture(true, "JABBA:capaupg_base");
        baseTexturePixels.itemArrow = getPixelsForTexture(true, "JABBA:capaupg_color");
    }

    public static void unloadBaseTextureData()
    {
        BetterBarrels.debug("39 - Unloading preloaded texture data");
        baseTexturePixels = null;
    }

    private static class BaseTextures
    {
        public int[] labelBackground;
        public int[] labelBorder;
        public int[] topBackground;
        public int[] topBorder;
        public int[] topLabel;
        public int[] sideBackground;
        public int[] sideBorder;
        public int[] item;
        public int[] itemArrow;
    }

    private static class AccessibleTextureAtlasSprite
            extends TextureAtlasSprite
    {
        protected int textureType;

        AccessibleTextureAtlasSprite(String par1Str, int textype)
        {
            super();
            this.textureType = textype;
        }

        private static Method fixPixels = Utils.ReflectionHelper.getMethod(TextureAtlasSprite.class, new String[] { "a", "func_147961_a", "fixTransparentPixels" }, new Class[] { [[AnyVals.I.class }, Level.ERROR, "Unable to locate required method 'fixTransparentPixels' for texture generation.  Please post this error at the error tracker along with a copy of your ForgeModLoader-client-0.log.");
    private static Method setupAnisotropic = Utils.ReflectionHelper.getMethod(TextureAtlasSprite.class, new String[] { "a", "func_147960_a", "prepareAnisotropicFiltering" }, new Class[] { [[AnyVals.I.class, Integer.DumpArchiveEntry.TYPE, Integer.TYPE }, Level.ERROR, "Unable to locate required method 'prepareAnisotropicFiltering' for texture generation.  Please post this error at the error tracker along with a copy of your ForgeModLoader-client-0.log.");
private static Field useAnisotropic = Utils.ReflectionHelper.getField(TextureAtlasSprite.class, new String[] { "k", "field_147966_k", "useAnisotropicFiltering" }, Level.ERROR, "Unable to locate required field 'useAnisotropicFiltering' for texture generation.  Please post this error at the error tracker along with a copy of your ForgeModLoader-client-0.log.");
private static Field texmapMipMapLevels = Utils.ReflectionHelper.getField(TextureMap.class, new String[] { "j", "field_147636_j", "mipmapLevels" });
private static Field texmapAnisotropic = Utils.ReflectionHelper.getField(TextureMap.class, new String[] { "k", "field_147637_k", "anisotropicFiltering" });

public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location)
        {
        if ((this.textureType == 1) || (location.func_110623_a().endsWith("0"))) {
        return false;
        }
        return true;
        }

public boolean load(IResourceManager manager, ResourceLocation location)
        {
        try
        {
        boolean useanisotropicFiltering = texmapAnisotropic.getInt(Minecraft.func_71410_x().func_147117_R()) > 1.0F;
        int mipmapLevels = this.textureType == 0 ? texmapMipMapLevels.getInt(Minecraft.func_71410_x().func_147117_R()) : 0;

        BufferedImage[] abufferedimage = new BufferedImage[1 + mipmapLevels];
        abufferedimage[0] = ImageIO.read(manager.func_110536_a(new ResourceLocation(location.func_110624_b(), this.textureType == 0 ? "textures/blocks/barrel_top_border.png" : "textures/items/capaupg_base.png")).func_110527_b());

        func_147964_a(abufferedimage, null, useanisotropicFiltering);

        return false;
        }
        catch (Throwable t)
        {
        BetterBarrels.log.error(t);
        }
        return true;
        }

public void replaceTextureData(int[] pixels, int mipmapLevels)
        throws Exception
        {
        BetterBarrels.debug("37p1 - entering texture replacement with " + mipmapLevels + " mipmap levels.");
        int[][] aint = new int[1 + mipmapLevels][];
        aint[0] = pixels;
        fixPixels.invoke(this, new Object[] { aint });
        boolean useAnisotropic = useAnisotropic.getBoolean(this);
        aint = (int[][])setupAnisotropic.invoke(this, new Object[] { aint, Integer.valueOf(useAnisotropic ? this.field_130223_c - 16 : this.field_130223_c), Integer.valueOf(useAnisotropic ? this.field_130224_d - 16 : this.field_130224_d) });
        aint = TextureUtil.func_147949_a(mipmapLevels, this.field_130223_c, aint);
        BetterBarrels.debug("37 - Attempting to replace texture for [" + func_94215_i() + "] with an array of [" + (aint != null ? Integer.valueOf(aint[0].length) : "(null)") + "] pixels, current texture dims are [" + this.field_130223_c + "x" + this.field_130224_d + "] for a total size of " + this.field_130223_c * this.field_130224_d);
        BetterBarrels.debug(toString());
        if (aint[0].length != this.field_130224_d * this.field_130223_c) {
        throw new Exception("Attempting to replace texture image data with " + (aint[0].length > this.field_130224_d * this.field_130223_c ? "too much" : "too little") + " data.");
        }
        BetterBarrels.debug("38 - Calling Minecraft Texture upload utility method");
        TextureUtil.func_147955_a(aint, this.field_130223_c, this.field_130224_d, this.field_110975_c, this.field_110974_d, false, false);
        func_130103_l();
        }
        }

private static AccessibleTextureAtlasSprite registerIcon(IIconRegister par1IconRegister, String key)
        {
        TextureMap texmap = (TextureMap)par1IconRegister;
        AccessibleTextureAtlasSprite ret = new AccessibleTextureAtlasSprite(key, texmap.func_130086_a());
        if (texmap.setTextureEntry(key, ret)) {
        return ret;
        }
        return (AccessibleTextureAtlasSprite)texmap.getTextureExtry(key);
        }

public void registerItemIcon(IIconRegister par1IconRegister, int ordinal)
        {
        this.iconItem = registerIcon(par1IconRegister, "JABBA:blanks/capacity/" + String.valueOf(ordinal));
        }

public void registerBlockIcons(IIconRegister par1IconRegister, int ordinal)
        {
        this.iconBlockSide = registerIcon(par1IconRegister, "JABBA:barrel_side_" + String.valueOf(ordinal));
        this.iconBlockTop = registerIcon(par1IconRegister, "JABBA:barrel_top_" + String.valueOf(ordinal));
        this.iconBlockLabel = registerIcon(par1IconRegister, "JABBA:barrel_label_" + String.valueOf(ordinal));
        this.iconBlockTopLabel = registerIcon(par1IconRegister, "JABBA:barrel_labeltop_" + String.valueOf(ordinal));
        }

private class PixelARGB
{
    int A;
    int R;
    int G;
    int B;
    int combined;
    private int addCount = 0;

    PixelARGB(int pixel)
    {
        this.A = (pixel >> 24 & 0xFF);
        this.R = (pixel >> 16 & 0xFF);
        this.G = (pixel >> 8 & 0xFF);
        this.B = (pixel & 0xFF);
        this.combined = pixel;
    }

    PixelARGB(int alpha, int red, int green, int blue)
    {
        this.A = alpha;
        this.R = red;
        this.G = green;
        this.B = blue;
        this.combined = (((this.A & 0xFF) << 24) + ((this.R & 0xFF) << 16) + ((this.G & 0xFF) << 8) + (this.B & 0xFF));
    }

    PixelARGB alphaAdd(PixelARGB add)
    {
        this.addCount += 1;
        this.A += add.A;
        this.R += add.R * add.A / 255;
        this.G += add.G * add.G / 255;
        this.B += add.B * add.B / 255;
        this.combined = (((this.A & 0xFF) << 24) + ((this.R & 0xFF) << 16) + ((this.G & 0xFF) << 8) + (this.B & 0xFF));
        return this;
    }

    PixelARGB normalize()
    {
        if (this.addCount == 0) {
            return this;
        }
        this.R = (this.R * 255 / this.A);
        this.G = (this.G * 255 / this.A);
        this.B = (this.B * 255 / this.A);
        this.A /= this.addCount;
        this.combined = (((this.A & 0xFF) << 24) + ((this.R & 0xFF) << 16) + ((this.G & 0xFF) << 8) + (this.B & 0xFF));
        this.addCount = 0;
        return this;
    }

    PixelARGB addIgnoreAlpha(PixelARGB add)
    {
        this.addCount += 1;
        this.R += add.R;
        this.G += add.G;
        this.B += add.B;
        this.combined = (((this.A & 0xFF) << 24) + ((this.R & 0xFF) << 16) + ((this.G & 0xFF) << 8) + (this.B & 0xFF));
        return this;
    }

    PixelARGB addSkipTransparent(PixelARGB add)
    {
        if (add.A == 0) {
            return this;
        }
        this.addCount += 1;
        this.R += add.R;
        this.G += add.G;
        this.B += add.B;
        this.combined = (((this.A & 0xFF) << 24) + ((this.R & 0xFF) << 16) + ((this.G & 0xFF) << 8) + (this.B & 0xFF));
        return this;
    }

    PixelARGB normalizeIgnoreAlpha()
    {
        if (this.addCount == 0) {
            return this;
        }
        this.R /= this.addCount;
        this.G /= this.addCount;
        this.B /= this.addCount;
        this.combined = (((this.A & 0xFF) << 24) + ((this.R & 0xFF) << 16) + ((this.G & 0xFF) << 8) + (this.B & 0xFF));
        this.addCount = 0;
        return this;
    }

    PixelARGB YIQContrastTextColor()
    {
        int color = (this.R * 299 + this.G * 587 + this.B * 114) / 1000 >= 128 ? 0 : 255;
        return new PixelARGB(StructuralLevelClientData.this, 255, color, color, color);
    }
}

    private void grainMergeArrayWithColor(int[] pixels, PixelARGB color)
    {
        BetterBarrels.debug("35 - Running grain merge on material with color");
        for (int i = 0; i < pixels.length; i++)
        {
            PixelARGB pix = new PixelARGB(pixels[i]);
            if (pix.A == 0) {
                pixels[i] = 0;
            } else {
                pixels[i] = new PixelARGB(255, Math.max(0, Math.min(255, pix.R + color.R - 128)), Math.max(0, Math.min(255, pix.G + color.G - 128)), Math.max(0, Math.min(255, pix.B + color.B - 128))).combined;
            }
        }
        BetterBarrels.debug("36 - sanity check, pixels.length:" + pixels.length);
    }

    private void mergeArraysBasedOnAlpha(int[] target, int[] merge)
    {
        for (int i = 0; i < merge.length; i++)
        {
            PixelARGB targetPixel = new PixelARGB(target[i]);
            PixelARGB mergePixel = new PixelARGB(merge[i]);
            target[i] = (mergePixel.A == 0 ? targetPixel.combined : mergePixel.combined);
        }
    }

    private PixelARGB averageColorFromArray(int[] pixels)
    {
        PixelARGB totals = new PixelARGB(0);
        for (int pixel : pixels) {
            totals.alphaAdd(new PixelARGB(pixel));
        }
        return totals.normalize();
    }

    private PixelARGB averageColorFromArrayB(int[] pixels)
    {
        PixelARGB totals = new PixelARGB(0);
        for (int pixel : pixels) {
            totals.addSkipTransparent(new PixelARGB(pixel));
        }
        return totals.normalizeIgnoreAlpha();
    }

    private static int[] getPixelsForTexture(boolean item, ResourceLocation resourcelocation)
    {
        BetterBarrels.debug("09 - Entering texture load method for texture : " + resourcelocation.toString());
        ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.func_110624_b(), String.format("%s/%s%s", new Object[] { item ? "textures/items" : "textures/blocks", resourcelocation.func_110623_a(), ".png" }));
        BetterBarrels.debug("11 - Modified resource path : " + resourcelocation1.toString());
        int[] pixels = null;
        try
        {
            pixels = TextureUtil.func_110986_a(Minecraft.func_71410_x().func_110442_L(), resourcelocation1);
        }
        catch (Throwable t)
        {
            BetterBarrels.log.warn("JABBA-Debug Problem loading texture: " + resourcelocation);
        }
        BetterBarrels.debug("12 - read texture data of length : " + (pixels != null ? Integer.valueOf(pixels.length) : "(null)"));
        return pixels;
    }

    private static int[] getPixelsForTexture(boolean item, String location)
    {
        return getPixelsForTexture(item, new ResourceLocation(location));
    }

    private static int[] getPixelsForTexture(boolean item, IIcon icon)
    {
        return getPixelsForTexture(item, new ResourceLocation(icon.func_94215_i()));
    }

    public boolean generateIcons()
    {
        BetterBarrels.debug("17 - Entering Texture Generation for Structural Tier: " + this.level);
        int terrainTextureId = Minecraft.func_71410_x().field_71446_o.func_110581_b(TextureMap.field_110575_b).func_110552_b();
        int itemTextureId = Minecraft.func_71410_x().field_71446_o.func_110581_b(TextureMap.field_110576_c).func_110552_b();
        if ((terrainTextureId != 0) && (itemTextureId != 0))
        {
            int previousTextureID = GL11.glGetInteger(32873);


            int[] labelBorderPixels = (int[])baseTexturePixels.labelBorder.clone();
            BetterBarrels.debug("18 - " + labelBorderPixels.length);
            int[] labelBackgroundPixels = (int[])baseTexturePixels.labelBackground.clone();
            BetterBarrels.debug("19 - " + labelBackgroundPixels.length);
            int[] topBorderPixels = (int[])baseTexturePixels.topBorder.clone();
            BetterBarrels.debug("20 - " + topBorderPixels.length);
            int[] topBackgroundPixels = (int[])baseTexturePixels.topBackground.clone();
            BetterBarrels.debug("21 - " + topBackgroundPixels.length);
            int[] topLabelBorderPixels = (int[])baseTexturePixels.topBorder.clone();
            BetterBarrels.debug("22 - " + topLabelBorderPixels.length);
            int[] topLabelBackgroundPixels = (int[])baseTexturePixels.topLabel.clone();
            BetterBarrels.debug("23 - " + topLabelBackgroundPixels.length);
            int[] sideBorderPixels = (int[])baseTexturePixels.sideBorder.clone();
            BetterBarrels.debug("24 - " + sideBorderPixels.length);
            int[] sideBackgroundPixels = (int[])baseTexturePixels.sideBackground.clone();
            BetterBarrels.debug("25 - " + sideBackgroundPixels.length);


            int[] itemBasePixels = (int[])baseTexturePixels.item.clone();
            BetterBarrels.debug("26 - " + itemBasePixels.length);
            int[] itemArrowPixels = (int[])baseTexturePixels.itemArrow.clone();
            BetterBarrels.debug("27 - " + itemArrowPixels.length);
            int[] itemRomanPixels = getPixelsForTexture(true, this.iconItem);
            BetterBarrels.debug("28 - " + itemRomanPixels.length);

            int[] materialPixels = null;
            boolean foundSourceMaterial = false;
            if (this.colorOverride == -1)
            {
                try
                {
                    while (this.level.material.meta >= 0)
                    {
                        cacheStackAndName();
                        Block materialBlock = Block.func_149634_a(this.materialStack.func_77973_b());
                        Item materialItem = this.materialStack.func_77973_b();
                        if ((materialBlock != Blocks.field_150350_a) && (!materialBlock.func_149739_a().equalsIgnoreCase("tile.ForgeFiller")))
                        {
                            BetterBarrels.debug("32 - Block found");
                            materialPixels = getPixelsForTexture(false, materialBlock.func_149691_a(0, this.materialStack.func_77960_j()));
                            foundSourceMaterial = true;
                            BetterBarrels.debug("33 - Loaded texture data for [" + this.name + "]: read an array of length: " + (materialPixels != null ? Integer.valueOf(materialPixels.length) : "(null)"));
                        }
                        else if (materialItem != null)
                        {
                            BetterBarrels.debug("30 - Item found, attempting to load");
                            materialPixels = getPixelsForTexture(true, materialItem.func_77617_a(this.materialStack.func_77960_j()));
                            foundSourceMaterial = true;
                            BetterBarrels.debug("30 - Loaded texture data for [" + this.name + "]: read an array of length: " + (materialPixels != null ? Integer.valueOf(materialPixels.length) : "(null)"));
                        }
                        if ((materialPixels != null) || (!this.level.material.isOreDict())) {
                            break;
                        }
                        if (this.level.material.meta != -1) {
                            this.level.material.meta += 1;
                        }
                    }
                }
                catch (Throwable t)
                {
                    BetterBarrels.debug("34 - MATERIAL LOOKUP ERROR");
                    BetterBarrels.log.error("Error loading resource material texture: " + t.getMessage());
                    t.printStackTrace();
                }
                finally
                {
                    if (!foundSourceMaterial)
                    {
                        BetterBarrels.log.error("Encountered an issue while locating the requested source material[" + (this.level.material.isOreDict() ? this.level.material.name : new StringBuilder().append(this.level.material.modDomain).append(":").append(this.level.material.name).append(":").append(this.level.material.meta).toString()) + "].  Ore Dictionary returned " + this.materialStack.func_77977_a() + " as the first itemStack for that request.");
                    }
                    else if (materialPixels == null)
                    {
                        materialPixels = new int[1];
                        BetterBarrels.debug("13 - No texture data read, creating empty array of for color black");
                    }
                }
            }
            else
            {
                materialPixels = new int[1];
                materialPixels[0] = this.colorOverride;
                foundSourceMaterial = true;
            }
            if (foundSourceMaterial)
            {
                PixelARGB color = averageColorFromArrayB(materialPixels);
                BetterBarrels.debug("Calculated Color for [" + this.name + "]: {R: " + color.R + ", G: " + color.G + ", B: " + color.B + "}");

                this.textColor = color.YIQContrastTextColor().combined;

                grainMergeArrayWithColor(labelBorderPixels, color);
                grainMergeArrayWithColor(topBorderPixels, color);
                grainMergeArrayWithColor(topLabelBorderPixels, color);
                grainMergeArrayWithColor(sideBorderPixels, color);
                grainMergeArrayWithColor(itemArrowPixels, color);

                this.textColor = averageColorFromArrayB(labelBorderPixels).YIQContrastTextColor().combined;

                int mipmapLevels = ((Integer) Utils.ReflectionHelper.getFieldValue(Integer.class, Integer.valueOf(Minecraft.func_71410_x().field_71474_y.field_151442_I), Minecraft.func_71410_x().func_147117_R(), TextureMap.class, new String[] { "j", "field_147636_j", "mipmapLevels" }, Level.WARN, "Unable to reflect Block TextureMap mipmapLevels. Defaulting to GameSettings mipmapLevels")).intValue();
                try
                {
                    mergeArraysBasedOnAlpha(labelBorderPixels, labelBackgroundPixels);
                    mergeArraysBasedOnAlpha(topBorderPixels, topBackgroundPixels);
                    mergeArraysBasedOnAlpha(topLabelBorderPixels, topLabelBackgroundPixels);
                    mergeArraysBasedOnAlpha(sideBorderPixels, sideBackgroundPixels);
                    mergeArraysBasedOnAlpha(itemBasePixels, itemArrowPixels);
                    mergeArraysBasedOnAlpha(itemBasePixels, itemRomanPixels);

                    GL11.glPushAttrib(1048575);
                    GL11.glBindTexture(3553, terrainTextureId);
                    this.iconBlockLabel.replaceTextureData(labelBorderPixels, mipmapLevels);
                    this.iconBlockTop.replaceTextureData(topBorderPixels, mipmapLevels);
                    this.iconBlockTopLabel.replaceTextureData(topLabelBorderPixels, mipmapLevels);
                    this.iconBlockSide.replaceTextureData(sideBorderPixels, mipmapLevels);

                    GL11.glBindTexture(3553, itemTextureId);
                    this.iconItem.replaceTextureData(itemBasePixels, 0);
                    GL11.glBindTexture(3553, previousTextureID);
                    GL11.glPopAttrib();
                    return true;
                }
                catch (Exception e)
                {
                    BetterBarrels.log.error("caught exception while generating icons: " + e.toString() + e.getMessage());
                }
            }
        }
        return false;
    }

}
