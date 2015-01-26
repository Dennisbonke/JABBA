package mcp.mobius.betterbarrels.common.blocks;

import mcp.mobius.betterbarrels.BetterBarrels;
import mcp.mobius.betterbarrels.Utils;
import mcp.mobius.betterbarrels.bspace.BSpaceStorageHandler;
import mcp.mobius.betterbarrels.common.LocalizedChat;
import mcp.mobius.betterbarrels.common.StructuralLevel;
import mcp.mobius.betterbarrels.common.items.upgrades.UpgradeCore;
import mcp.mobius.betterbarrels.network.BarrelPacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class BarrelCoreUpgrades {

    private TileEntityBarrel barrel;
    public ArrayList<UpgradeCore> upgradeList = new ArrayList();
    public int levelStructural = 0;
    public int nStorageUpg = 0;
    public boolean hasRedstone = false;
    public boolean hasHopper = false;
    public boolean hasEnder = false;
    public boolean hasVoid = false;
    public boolean hasCreative = false;

    public BarrelCoreUpgrades(TileEntityBarrel barrel)
    {
        this.barrel = barrel;
    }

    public int getMaxUpgradeSlots()
    {
        return StructuralLevel.LEVELS[this.levelStructural].getMaxCoreSlots();
    }

    public int getUsedSlots()
    {
        int nslots = 0;
        for (UpgradeCore core : this.upgradeList) {
            nslots += core.slotsUsed;
        }
        return nslots;
    }

    public int getFreeSlots()
    {
        return getMaxUpgradeSlots() - getUsedSlots();
    }

    public boolean hasUpgrade(UpgradeCore upgrade)
    {
        for (UpgradeCore core : this.upgradeList) {
            if (core == upgrade) {
                return true;
            }
        }
        return false;
    }

    public boolean hasUpgradeType(UpgradeCore.Type upgradeType)
    {
        for (UpgradeCore core : this.upgradeList) {
            if (core.type == upgradeType) {
                return true;
            }
        }
        return false;
    }

    private int findUpgradeIndex(UpgradeCore.Type type, Boolean first, Boolean exclude)
    {
        if (first.booleanValue()) {
            for (int i = 0; i < this.upgradeList.size(); i++) {
                if (exclude.booleanValue())
                {
                    if (((UpgradeCore)this.upgradeList.get(i)).type != type) {
                        return i;
                    }
                }
                else if (((UpgradeCore)this.upgradeList.get(i)).type == type) {
                    return i;
                }
            }
        } else {
            for (int i = this.upgradeList.size() - 1; i >= 0; i--) {
                if (exclude.booleanValue())
                {
                    if (((UpgradeCore)this.upgradeList.get(i)).type != type) {
                        return i;
                    }
                }
                else if (((UpgradeCore)this.upgradeList.get(i)).type == type) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void createAndDropItem(Item item, int meta, EntityPlayer player)
    {
        ItemStack droppedStack = new ItemStack(item, 1, meta);
        Utils.dropItemInWorld(this.barrel, player, droppedStack, 0.02D);
    }

    private void removeAndDropUpgrade(UpgradeCore upgrade, EntityPlayer player)
    {
        int coreIndex = findUpgradeIndex(upgrade.type, Boolean.valueOf(true), Boolean.valueOf(false));
        if (coreIndex >= 0)
        {
            this.upgradeList.remove(coreIndex);
            createAndDropItem(BetterBarrels.itemUpgradeCore, upgrade.ordinal(), player);
        }
    }

    private void removeEnder(EntityPlayer player)
    {
        if (BSpaceStorageHandler.instance().hasLinks(this.barrel.id))
        {
            BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BSPACE_REMOVE, new Object[0]);
            this.barrel.storage = new StorageLocal(this.nStorageUpg);
        }
        BSpaceStorageHandler.instance().unregisterEnderBarrel(this.barrel.id);
    }

    private void removeStorage(EntityPlayer player)
    {
        int indexLastUpdate = findUpgradeIndex(UpgradeCore.Type.STORAGE, Boolean.valueOf(false), Boolean.valueOf(false));
        if (indexLastUpdate == -1) {
            return;
        }
        UpgradeCore core = (UpgradeCore)this.upgradeList.get(indexLastUpdate);
        if (this.barrel.getStorage().getItem() != null)
        {
            int newMaxStoredItems = (this.barrel.getStorage().getMaxStacks() - 64 * core.slotsUsed) * this.barrel.getStorage().getItem().func_77976_d();
            if (this.barrel.getStorage().getAmount() > newMaxStoredItems)
            {
                BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.STACK_REMOVE, new Object[0]);
                return;
            }
        }
        this.upgradeList.remove(indexLastUpdate);
        createAndDropItem(BetterBarrels.itemUpgradeCore, core.ordinal(), player);
        for (int i = 0; i < core.slotsUsed; i++) {
            this.barrel.getStorage().rmStorageUpgrade();
        }
        this.nStorageUpg -= core.slotsUsed;
    }

    public void removeUpgrade(ItemStack stack, EntityPlayer player, ForgeDirection side)
    {
        switch (mcp.mobius.betterbarrels.common.items.ItemBarrelHammer.HammerMode.getMode(stack).ordinal())
        {
            case 1:
            default:
                int indexLastUpdate = findUpgradeIndex(UpgradeCore.Type.STORAGE, Boolean.valueOf(false), Boolean.valueOf(true));
                if (indexLastUpdate != -1)
                {
                    UpgradeCore core = (UpgradeCore)this.upgradeList.get(indexLastUpdate);
                    if ((core.type == UpgradeCore.Type.VOID) && (BSpaceStorageHandler.instance().hasLinks(this.barrel.id)))
                    {
                        BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BSPACE_PREVENT, new Object[0]);
                        return;
                    }
                    this.upgradeList.remove(indexLastUpdate);
                    createAndDropItem(BetterBarrels.itemUpgradeCore, core.ordinal(), player);

                    this.hasRedstone = hasUpgrade(UpgradeCore.REDSTONE);
                    this.hasHopper = hasUpgrade(UpgradeCore.HOPPER);
                    this.hasEnder = hasUpgrade(UpgradeCore.ENDER);
                    this.barrel.setVoid(hasUpgrade(UpgradeCore.VOID));
                    this.barrel.setCreative(hasUpgrade(UpgradeCore.CREATIVE));
                    if (core.type == UpgradeCore.Type.ENDER) {
                        removeEnder(player);
                    }
                    if (this.hasHopper) {
                        this.barrel.startTicking();
                    } else {
                        this.barrel.stopTicking();
                    }
                    this.barrel.removeUpgradeFacades(player);
                }
                else if (this.upgradeList.size() > 0)
                {
                    removeStorage(player);
                }
                else if (this.levelStructural > 0)
                {
                    createAndDropItem(BetterBarrels.itemUpgradeStructural, this.levelStructural - 1, player);
                    this.levelStructural -= 1;
                }
                else
                {
                    BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BONK, new Object[0]);
                }
                break;
            case 2:
                if (hasUpgrade(UpgradeCore.REDSTONE))
                {
                    removeAndDropUpgrade(UpgradeCore.REDSTONE, player);
                    this.hasRedstone = false;
                    this.barrel.removeUpgradeFacades(player);
                }
                else
                {
                    BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BONK, new Object[0]);
                }
                break;
            case 3:
                if (hasUpgrade(UpgradeCore.ENDER))
                {
                    removeAndDropUpgrade(UpgradeCore.ENDER, player);
                    this.hasEnder = false;
                    removeEnder(player);
                }
                else
                {
                    BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BONK, new Object[0]);
                }
                break;
            case 4:
                if (hasUpgrade(UpgradeCore.HOPPER))
                {
                    this.barrel.stopTicking();
                    removeAndDropUpgrade(UpgradeCore.HOPPER, player);
                    this.hasHopper = false;
                    this.barrel.removeUpgradeFacades(player);
                }
                else
                {
                    BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BONK, new Object[0]);
                }
                break;
            case 5:
                if (hasUpgradeType(UpgradeCore.Type.STORAGE))
                {
                    if (BSpaceStorageHandler.instance().hasLinks(this.barrel.id)) {
                        BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BSPACE_PREVENT, new Object[0]);
                    } else {
                        removeStorage(player);
                    }
                }
                else {
                    BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BONK, new Object[0]);
                }
                break;
            case 6:
                if (this.levelStructural > 0)
                {
                    if (BSpaceStorageHandler.instance().hasLinks(this.barrel.id))
                    {
                        BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BSPACE_PREVENT, new Object[0]);
                    }
                    else
                    {
                        int newLevel = this.levelStructural - 1;
                        int newTotalSlots = 0;
                        for (int i = 0; i < newLevel; i++) {
                            newTotalSlots += MathHelper.func_76128_c(Math.pow(2.0D, i));
                        }
                        if (newTotalSlots < getUsedSlots())
                        {
                            BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.UPGRADE_REMOVE, new Object[0]);
                        }
                        else
                        {
                            createAndDropItem(BetterBarrels.itemUpgradeStructural, this.levelStructural - 1, player);
                            this.levelStructural = newLevel;
                        }
                    }
                }
                else {
                    BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BONK, new Object[0]);
                }
                break;
            case 7:
                if (hasUpgrade(UpgradeCore.VOID))
                {
                    if (BSpaceStorageHandler.instance().hasLinks(this.barrel.id))
                    {
                        BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BSPACE_PREVENT, new Object[0]);
                    }
                    else
                    {
                        removeAndDropUpgrade(UpgradeCore.VOID, player);
                        this.barrel.setVoid(false);
                    }
                }
                else {
                    BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BONK, new Object[0]);
                }
                break;
            case 8:
                if (hasUpgrade(UpgradeCore.CREATIVE))
                {
                    if (BSpaceStorageHandler.instance().hasLinks(this.barrel.id))
                    {
                        BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BSPACE_PREVENT, new Object[0]);
                    }
                    else
                    {
                        removeAndDropUpgrade(UpgradeCore.CREATIVE, player);
                        this.barrel.setCreative(false);
                    }
                }
                else {
                    BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BONK, new Object[0]);
                }
                break;
        }
    }

    void applyUpgrade(ItemStack stack, EntityPlayer player)
    {
        UpgradeCore core = UpgradeCore.values()[stack.func_77960_j()];
        if ((core.type != UpgradeCore.Type.STORAGE) && (hasUpgrade(core)))
        {
            BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.COREUPGRADE_EXISTS, new Object[0]);
            return;
        }
        if (core.slotsUsed > getFreeSlots())
        {
            BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.UPGRADE_INSUFFICIENT, new Object[] { Integer.valueOf(core.slotsUsed) });
            return;
        }
        if (core.type == UpgradeCore.Type.STORAGE)
        {
            if (BSpaceStorageHandler.instance().hasLinks(this.barrel.id))
            {
                BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BSPACE_PREVENT, new Object[0]);
                return;
            }
            this.upgradeList.add(core);
            for (int i = 0; i < core.slotsUsed; i++) {
                this.barrel.getStorage().addStorageUpgrade();
            }
            this.nStorageUpg += core.slotsUsed;

            BarrelPacketHandler.INSTANCE.sendToDimension(new Message0x06FullStorage(this.barrel), this.barrel.func_145831_w().field_73011_w.field_76574_g);
        }
        if (core == UpgradeCore.REDSTONE)
        {
            this.upgradeList.add(UpgradeCore.REDSTONE);
            this.hasRedstone = true;
        }
        else if (core == UpgradeCore.HOPPER)
        {
            this.upgradeList.add(UpgradeCore.HOPPER);
            this.hasHopper = true;
            this.barrel.startTicking();
        }
        else if (core == UpgradeCore.ENDER)
        {
            this.upgradeList.add(UpgradeCore.ENDER);
            this.hasEnder = true;
            BSpaceStorageHandler.instance().registerEnderBarrel(this.barrel.id, this.barrel.storage);
        }
        else if (core == UpgradeCore.VOID)
        {
            if (BSpaceStorageHandler.instance().hasLinks(this.barrel.id))
            {
                BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BSPACE_PREVENT, new Object[0]);
                return;
            }
            this.upgradeList.add(UpgradeCore.VOID);
            this.barrel.setVoid(true);
        }
        else if (core == UpgradeCore.CREATIVE)
        {
            if (BSpaceStorageHandler.instance().hasLinks(this.barrel.id))
            {
                BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BSPACE_PREVENT, new Object[0]);
                return;
            }
            this.upgradeList.add(UpgradeCore.CREATIVE);
            this.barrel.setCreative(true);
        }
        if (!player.field_71075_bZ.field_75098_d) {
            stack.field_77994_a -= 1;
        }
        this.barrel.func_70296_d();
        BarrelPacketHandler.INSTANCE.sendToDimension(new Message0x05CoreUpdate(this.barrel), this.barrel.func_145831_w().field_73011_w.field_76574_g);
    }

    void applyStructural(ItemStack stack, EntityPlayer player)
    {
        if (BSpaceStorageHandler.instance().hasLinks(this.barrel.id))
        {
            BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BSPACE_PREVENT, new Object[0]);
            return;
        }
        if (stack.func_77960_j() == this.levelStructural)
        {
            if (!player.field_71075_bZ.field_75098_d) {
                stack.field_77994_a -= 1;
            }
            this.levelStructural += 1;
        }
        else if (((player instanceof EntityPlayerMP)) && (stack.func_77960_j() == this.levelStructural - 1))
        {
            BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.UPGRADE_EXISTS, new Object[0]);
        }
        else if (((player instanceof EntityPlayerMP)) && (stack.func_77960_j() < this.levelStructural))
        {
            BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.DOWNGRADE, new Object[0]);
        }
        else if (((player instanceof EntityPlayerMP)) && (stack.func_77960_j() > this.levelStructural))
        {
            BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.UPGRADE_REQUIRED, new Object[] { Integer.valueOf(stack.func_77960_j()) });
        }
        this.barrel.func_70296_d();
        BarrelPacketHandler.INSTANCE.sendToDimension(new Message0x04Structuralupdate(this.barrel), this.barrel.func_145831_w().field_73011_w.field_76574_g);
    }

    public void writeToNBT(NBTTagCompound NBTTag)
    {
        int[] savedUpgrades = new int[this.upgradeList.size()];
        Iterator<UpgradeCore> iterator = this.upgradeList.iterator();
        for (int i = 0; i < savedUpgrades.length; i++) {
            savedUpgrades[i] = ((UpgradeCore)iterator.next()).ordinal();
        }
        NBTTag.func_74783_a("coreUpgrades", savedUpgrades);
        NBTTag.func_74768_a("structural", this.levelStructural);
        NBTTag.func_74757_a("redstone", this.hasRedstone);
        NBTTag.func_74757_a("hopper", this.hasHopper);
        NBTTag.func_74757_a("ender", this.hasEnder);
        NBTTag.func_74757_a("void", this.hasVoid);
        NBTTag.func_74757_a("creative", this.hasCreative);
        NBTTag.func_74768_a("nStorageUpg", this.nStorageUpg);
    }

    public void readFromNBT(NBTTagCompound NBTTag, int saveVersion)
    {
        int[] savedUpgrades = NBTTag.func_74759_k("coreUpgrades");
        this.upgradeList = new ArrayList();
        for (int i = 0; i < savedUpgrades.length; i++) {
            this.upgradeList.add(UpgradeCore.values()[(savedUpgrades[i] + 0)]);
        }
        this.levelStructural = NBTTag.func_74762_e("structural");
        this.hasRedstone = NBTTag.func_74767_n("redstone");
        this.hasHopper = NBTTag.func_74767_n("hopper");
        this.hasEnder = NBTTag.func_74767_n("ender");
        if (saveVersion < 5) {
            this.nStorageUpg = NBTTag.func_74771_c("nStorageUpg");
        } else {
            this.nStorageUpg = NBTTag.func_74762_e("nStorageUpg");
        }
        this.barrel.setVoid(NBTTag.func_74767_n("void"));
        this.barrel.setCreative(NBTTag.func_74767_n("creative"));
    }

}
