package mcp.mobius.betterbarrels.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.betterbarrels.BetterBarrels;
import mcp.mobius.betterbarrels.Utils;
import mcp.mobius.betterbarrels.bspace.BSpaceStorageHandler;
import mcp.mobius.betterbarrels.common.JabbaCreativeTab;
import mcp.mobius.betterbarrels.common.StructuralLevel;
import mcp.mobius.betterbarrels.common.items.upgrades.UpgradeCore;
import mcp.mobius.betterbarrels.common.items.upgrades.UpgradeSide;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class BlockBarrel extends BlockContainer {

    public static IIcon text_sidehopper = null;
    public static IIcon text_siders = null;
    public static IIcon text_lock = null;
    public static IIcon text_linked = null;
    public static IIcon text_locklinked = null;

    public BlockBarrel()
    {
        super(new Material(MapColor.woodColor) {});
        setHardness(2.0F);
        setResistance(5.0F);
        setHarvestLevel("axe", 1);
        setBlockName("blockbarrel");
        setCreativeTab(JabbaCreativeTab.tab);
    }

    public TileEntity createNewTileEntity(World world, int i)
    {
        return new TileEntityBarrel();
    }

    public void registerBlockIcons(IIconRegister iconRegister)
    {
        text_sidehopper = iconRegister.registerIcon("JABBA:facade_hopper");
        text_siders = iconRegister.registerIcon("JABBA:facade_redstone");
        text_lock = iconRegister.registerIcon("JABBA:overlay_locked");
        text_linked = iconRegister.registerIcon("JABBA:overlay_linked");
        text_locklinked = iconRegister.registerIcon("JABBA:overlay_lockedlinked");
        for (int i = 0; i < StructuralLevel.LEVELS.length; i++) {
            StructuralLevel.LEVELS[i].clientData.registerBlockIcons(iconRegister, i);
        }
    }

    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack par6ItemStack)
    {
        TileEntityBarrel barrelEntity = (TileEntityBarrel)world.getTileEntity(x, y, z);
        if (barrelEntity != null)
        {
            barrelEntity.orientation = Utils.getDirectionFacingEntity(entity, BetterBarrels.allowVerticalPlacement);
            barrelEntity.rotation = Utils.getDirectionFacingEntity(entity, false);

            barrelEntity.sideUpgrades[barrelEntity.orientation.ordinal()] = UpgradeSide.FRONT;
        }
    }

    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z)
    {
        return removedByPlayer(world, player, x, y, z, false);
    }

    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest)
    {
        if ((player.field_71075_bZ.field_75098_d) && (!player.func_70093_af()))
        {
            func_149699_a(world, x, y, z, player);
            return false;
        }
        return world.func_147468_f(x, y, z);
    }

    public void func_149699_a(World world, int x, int y, int z, EntityPlayer player)
    {
        if (!world.field_72995_K)
        {
            TileEntity tileEntity = world.func_147438_o(x, y, z);
            ((TileEntityBarrel)tileEntity).leftClick(player);
        }
    }

    public boolean func_149727_a(World world, int x, int y, int z, EntityPlayer player, int side, float var7, float var8, float var9)
    {
        if (!world.field_72995_K)
        {
            TileEntity tileEntity = world.func_147438_o(x, y, z);
            ((TileEntityBarrel)tileEntity).rightClick(player, side);
        }
        return true;
    }

    private void dropStack(World world, ItemStack stack, int x, int y, int z)
    {
        Random random = new Random();
        float var10 = random.nextFloat() * 0.8F + 0.1F;
        float var11 = random.nextFloat() * 0.8F + 0.1F;
        EntityItem items;
        for (float var12 = random.nextFloat() * 0.8F + 0.1F; stack.field_77994_a > 0; world.func_72838_d(items))
        {
            int var13 = random.nextInt(21) + 10;
            if (var13 > stack.field_77994_a) {
                var13 = stack.field_77994_a;
            }
            stack.field_77994_a -= var13;
            items = new EntityItem(world, x + var10, y + var11, z + var12, new ItemStack(stack.func_77973_b(), var13, stack.func_77960_j()));
            float var15 = 0.05F;
            items.field_70159_w = ((float)random.nextGaussian() * var15);
            items.field_70181_x = ((float)random.nextGaussian() * var15 + 0.2F);
            items.field_70179_y = ((float)random.nextGaussian() * var15);
            if (stack.func_77942_o()) {
                items.func_92059_d().func_77982_d((NBTTagCompound)stack.func_77978_p().func_74737_b());
            }
        }
    }

    public void func_149749_a(World world, int x, int y, int z, Block block, int meta)
    {
        if (world.field_72995_K) {
            return;
        }
        TileEntityBarrel barrelEntity = (TileEntityBarrel)Utils.getTileEntityWithoutCreating(world, x, y, z);
        if (barrelEntity == null) {
            return;
        }
        int currentUpgrade;
        if (barrelEntity.coreUpgrades.levelStructural > 0)
        {
            currentUpgrade = barrelEntity.coreUpgrades.levelStructural;
            while (currentUpgrade > 0)
            {
                ItemStack droppedStack = new ItemStack(BetterBarrels.itemUpgradeStructural, 1, currentUpgrade - 1);
                dropStack(world, droppedStack, x, y, z);
                currentUpgrade--;
            }
        }
        for (UpgradeCore core : barrelEntity.coreUpgrades.upgradeList)
        {
            ItemStack droppedStack = new ItemStack(BetterBarrels.itemUpgradeCore, 1, core.ordinal());
            dropStack(world, droppedStack, x, y, z);
        }
        for (int i = 0; i < 6; i++)
        {
            Item upgrade = UpgradeSide.mapItem[barrelEntity.sideUpgrades[i]];
            if (upgrade != null)
            {
                ItemStack droppedStack = new ItemStack(upgrade, 1, UpgradeSide.mapMeta[barrelEntity.sideUpgrades[i]]);
                dropStack(world, droppedStack, x, y, z);
            }
        }
        if ((barrelEntity.getStorage().hasItem()) && (!barrelEntity.getLinked()))
        {
            barrelEntity.func_145845_h();
            int ndroppedstacks = 0;
            ItemStack droppedstack = barrelEntity.getStorage().getStack();
            while ((droppedstack != null) && (ndroppedstacks <= 64))
            {
                ndroppedstacks++;
                if (droppedstack != null) {
                    dropStack(world, droppedstack, x, y, z);
                }
                droppedstack = barrelEntity.getStorage().getStack();
            }
        }
        try
        {
            BSpaceStorageHandler.instance().unregisterEnderBarrel(barrelEntity.id);
        }
        catch (Exception e)
        {
            BetterBarrels.log.info("Tried to remove the barrel from the index without a valid entity");
        }
        world.func_147475_p(x, y, z);
    }

    public int func_149748_c(IBlockAccess world, int x, int y, int z, int side)
    {
        return func_149709_b(world, x, y, z, side);
    }

    public boolean func_149744_f()
    {
        return true;
    }

    public int func_149709_b(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntityBarrel barrel = (TileEntityBarrel)Utils.getTileEntityPreferNotCreating(world, x, y, z);
        if (barrel == null) {
            return 0;
        }
        return barrel.getRedstonePower(side);
    }

    private int redstoneToMC(int redSide)
    {
        switch (redSide)
        {
            case -1:
            default:
                return 1;
            case 0:
                return 2;
            case 1:
                return 5;
            case 2:
                return 3;
        }
        return 4;
    }

    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntityBarrel barrel = (TileEntityBarrel)Utils.getTileEntityPreferNotCreating(world, x, y, z);
        if ((barrel != null) && (barrel.sideUpgrades[redstoneToMC(side)] == UpgradeSide.REDSTONE)) {
            return true;
        }
        return false;
    }

    public boolean func_149740_M()
    {
        return true;
    }

    public int func_149736_g(World world, int x, int y, int z, int dir)
    {
        TileEntityBarrel barrel = (TileEntityBarrel)Utils.getTileEntityWithoutCreating(world, x, y, z);
        if (barrel == null) {
            return 0;
        }
        IBarrelStorage store = barrel.getStorage();
        int currentAmount = store.getAmount();
        int maxStorable = store.getMaxStoredCount();
        if (currentAmount == 0) {
            return 0;
        }
        if (currentAmount == maxStorable) {
            return 15;
        }
        return MathHelper.func_76141_d(currentAmount / maxStorable * 14.0F) + 1;
    }

    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        if (side == ForgeDirection.DOWN) {
            return false;
        }
        TileEntityBarrel barrel = (TileEntityBarrel)Utils.getTileEntityPreferNotCreating(world, x, y, z);
        if ((barrel != null) && ((barrel.sideUpgrades[side.ordinal()] == UpgradeSide.FRONT) || (barrel.sideUpgrades[side.ordinal()] == UpgradeSide.STICKER))) {
            return false;
        }
        return true;
    }

    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z)
    {
        return isSideSolid(world, x, y, z, ForgeDirection.UP);
    }

    public int func_149645_b()
    {
        BetterBarrels.proxy.checkRenderers();

        return BetterBarrels.blockBarrelRendererID;
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149673_e(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntityBarrel barrel = (TileEntityBarrel)Utils.getTileEntityPreferNotCreating(world, x, y, z);
        if (barrel == null) {
            return Blocks.field_150344_f.func_149673_e(world, x, y, z, side);
        }
        int levelStructural = barrel.coreUpgrades.levelStructural;

        boolean ghosting = barrel.getStorage().isGhosting();
        boolean linked = barrel.getLinked();
        boolean sideIsLabel = (barrel.sideUpgrades[side] == UpgradeSide.FRONT) || (barrel.sideUpgrades[side] == UpgradeSide.STICKER);


        IIcon ret = StructuralLevel.LEVELS[levelStructural].clientData.getIconLabel();
        if (barrel.overlaying)
        {
            if (barrel.sideUpgrades[side] == UpgradeSide.HOPPER) {
                ret = text_sidehopper;
            } else if (barrel.sideUpgrades[side] == UpgradeSide.REDSTONE) {
                ret = text_siders;
            } else if (sideIsLabel) {
                if ((ghosting) && (linked)) {
                    ret = text_locklinked;
                } else if (ghosting) {
                    ret = text_lock;
                } else if (linked) {
                    ret = text_linked;
                }
            }
        }
        else if (((side == 0) || (side == 1)) && (sideIsLabel)) {
            ret = StructuralLevel.LEVELS[levelStructural].clientData.getIconLabelTop();
        } else if (((side == 0) || (side == 1)) && (!sideIsLabel)) {
            ret = StructuralLevel.LEVELS[levelStructural].clientData.getIconTop();
        } else if (sideIsLabel) {
            ret = StructuralLevel.LEVELS[levelStructural].clientData.getIconLabel();
        } else {
            ret = StructuralLevel.LEVELS[levelStructural].clientData.getIconSide();
        }
        return side == 0 ? new IconFlipped(ret, true, false) : ret;
    }

    @SideOnly(Side.CLIENT)
    public boolean func_149646_a(IBlockAccess world, int x, int y, int z, int side)
    {
        ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];

        TileEntityBarrel barrel = (TileEntityBarrel) Utils.getTileEntityPreferNotCreating(world, x - dir.offsetX, y - dir.offsetY, z - dir.offsetZ);
        if ((barrel == null) || (!barrel.overlaying)) {
            return super.func_149646_a(world, x, y, z, side);
        }
        boolean ghosting = barrel.getStorage().isGhosting();
        boolean linked = barrel.getLinked();
        boolean sideIsLabel = (barrel.sideUpgrades[side] == UpgradeSide.FRONT) || (barrel.sideUpgrades[side] == UpgradeSide.STICKER);
        if (barrel.sideUpgrades[side] == UpgradeSide.HOPPER) {
            return true;
        }
        if (barrel.sideUpgrades[side] == UpgradeSide.REDSTONE) {
            return true;
        }
        if (sideIsLabel) {
            return (ghosting) || (linked);
        }
        return false;
    }

}
