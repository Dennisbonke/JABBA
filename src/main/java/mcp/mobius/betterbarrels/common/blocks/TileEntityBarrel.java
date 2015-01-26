package mcp.mobius.betterbarrels.common.blocks;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import mcp.mobius.betterbarrels.BetterBarrels;
import mcp.mobius.betterbarrels.ServerTickHandler;
import mcp.mobius.betterbarrels.Utils;
import mcp.mobius.betterbarrels.bspace.BSpaceStorageHandler;
import mcp.mobius.betterbarrels.common.LocalizedChat;
import mcp.mobius.betterbarrels.common.blocks.logic.LogicHopper;
import mcp.mobius.betterbarrels.common.items.ItemBarrelHammer;
import mcp.mobius.betterbarrels.common.items.upgrades.UpgradeCore;
import mcp.mobius.betterbarrels.common.items.upgrades.UpgradeSide;
import mcp.mobius.betterbarrels.network.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

import java.util.ArrayList;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class TileEntityBarrel extends TileEntity implements ISidedInventory, IDeepStorageUnit {

    private static int version = 5;
    private long clickTime = -20L;
    IBarrelStorage storage = new StorageLocal();
    public ForgeDirection orientation = ForgeDirection.UNKNOWN;
    public ForgeDirection rotation = ForgeDirection.UNKNOWN;
    public int[] sideUpgrades = { UpgradeSide.NONE, UpgradeSide.NONE, UpgradeSide.NONE, UpgradeSide.NONE, UpgradeSide.NONE, UpgradeSide.NONE };
    public int[] sideMetadata = { 0, 0, 0, 0, 0, 0 };
    public boolean isTicking = false;
    public boolean isLinked = false;
    public byte nTicks = 0;
    public int id = -1;
    public long timeSinceLastUpd = System.currentTimeMillis();
    public boolean overlaying = false;
    private Message0x01ContentUpdate lastContentMessage;
    private Message0x02GhostUpdate lastGhostMessage;
    public BarrelCoreUpgrades coreUpgrades;

    public TileEntityBarrel()
    {
        this.coreUpgrades = new BarrelCoreUpgrades(this);
    }

    public void setLinked(boolean linked)
    {
        this.isLinked = linked;
        BarrelPacketHandler.INSTANCE.sendToDimension(new Message0x08LinkUpdate(this), this.field_145850_b.field_73011_w.field_76574_g);
    }

    public boolean getLinked()
    {
        return this.isLinked;
    }

    public IBarrelStorage getStorage()
    {
        IBarrelStorage ret;
        if ((this.coreUpgrades.hasEnder) && (!this.field_145850_b.field_72995_K)) {
            ret = BSpaceStorageHandler.instance().getStorage(this.id);
        } else {
            ret = this.storage;
        }
        if (ret == null)
        {
            BetterBarrels.log.error(String.format("This is the most unusual case. Storage appears to be null for [%d %d %d %d] with id [%d]", new Object[] { Integer.valueOf(this.field_145850_b.field_73011_w.field_76574_g), Integer.valueOf(this.field_145851_c), Integer.valueOf(this.field_145848_d), Integer.valueOf(this.field_145849_e), Integer.valueOf(this.id) }));
            if (this.storage == null)
            {
                this.storage = new StorageLocal();
                BetterBarrels.log.error("Local storage was null. Created a new one.");
            }
            if ((this.coreUpgrades.hasEnder) && (!this.field_145850_b.field_72995_K))
            {
                this.id = BSpaceStorageHandler.instance().getNextBarrelID();

                BetterBarrels.log.error(String.format("Barrel is BSpaced. Generating new ID for it and registering the storage with the main handler.", new Object[0]));

                BSpaceStorageHandler.instance().registerEnderBarrel(this.id, this.storage);
            }
            if ((this.coreUpgrades.hasEnder) && (!this.field_145850_b.field_72995_K)) {
                ret = BSpaceStorageHandler.instance().getStorage(this.id);
            } else {
                ret = this.storage;
            }
        }
        if (ret == null) {
            throw new RuntimeException(String.format("Attempts to salvage [%d %d %d %d] with id [%d] have failed ! Please contact your closest modder to bitch at him.", new Object[] { Integer.valueOf(this.field_145850_b.field_73011_w.field_76574_g), Integer.valueOf(this.field_145851_c), Integer.valueOf(this.field_145848_d), Integer.valueOf(this.field_145849_e), Integer.valueOf(this.id) }));
        }
        return ret;
    }

    public void setStorage(IBarrelStorage storage)
    {
        this.storage = storage;
    }

    public void setVoid(boolean delete)
    {
        this.coreUpgrades.hasVoid = delete;
        this.storage.setVoid(delete);
    }

    public void setCreative(boolean infinite)
    {
        this.coreUpgrades.hasCreative = infinite;
        this.storage.setCreative(infinite);
    }

    public boolean canUpdate()
    {
        if ((this.field_145850_b != null) && (this.field_145850_b.field_72995_K)) {
            return false;
        }
        return this.isTicking;
    }

    public void func_145845_h()
    {
        if (this.field_145850_b.field_72995_K) {
            return;
        }
        if ((this.nTicks = (byte)(this.nTicks + 1)) % 8 == 0)
        {
            if (LogicHopper.INSTANCE.run(this)) {
                func_70296_d();
            }
            this.nTicks = 0;
        }
    }

    void startTicking()
    {
        this.isTicking = true;
        if (!this.field_145850_b.field_147482_g.contains(this)) {
            this.field_145850_b.addTileEntity(this);
        }
    }

    void stopTicking()
    {
        this.isTicking = false;
        if (this.field_145850_b.field_147482_g.contains(this)) {
            this.field_145850_b.field_147482_g.remove(this);
        }
    }

    static final int[] sideSwitch = { 1, 0, 3, 2, 5, 4 };

    public int getRedstonePower(int side)
    {
        if (!this.coreUpgrades.hasRedstone) {
            return 0;
        }
        side = sideSwitch[side];

        IBarrelStorage store = getStorage();
        int currentAmount = store.getAmount();
        int maxStorable = store.getMaxStoredCount();
        if ((this.coreUpgrades.hasVoid) && (store.hasItem())) {
            maxStorable -= store.getItem().func_77976_d();
        }
        if ((this.sideUpgrades[side] == UpgradeSide.REDSTONE) && (this.sideMetadata[side] == UpgradeSide.RS_FULL) && (currentAmount == maxStorable)) {
            return 15;
        }
        if ((this.sideUpgrades[side] == UpgradeSide.REDSTONE) && (this.sideMetadata[side] == UpgradeSide.RS_EMPT) && (currentAmount == 0)) {
            return 15;
        }
        if ((this.sideUpgrades[side] == UpgradeSide.REDSTONE) && (this.sideMetadata[side] == UpgradeSide.RS_PROP))
        {
            if (currentAmount == 0) {
                return 0;
            }
            if (currentAmount == maxStorable) {
                return 15;
            }
            return MathHelper.func_76141_d(currentAmount / maxStorable * 14.0F) + 1;
        }
        return 0;
    }

    public void leftClick(EntityPlayer player)
    {
        if (this.field_145850_b.field_72995_K) {
            return;
        }
        ItemStack droppedStack = null;
        if (player.func_70093_af()) {
            droppedStack = getStorage().getStack(1);
        } else {
            droppedStack = getStorage().getStack();
        }
        if ((droppedStack != null) && (droppedStack.field_77994_a > 0)) {
            Utils.dropItemInWorld(this, player, droppedStack, 0.02D);
        }
        func_70296_d();
    }

    public void rightClick(EntityPlayer player, int side)
    {
        if (this.field_145850_b.field_72995_K) {
            return;
        }
        ItemStack stack = player.func_70694_bm();
        if (!player.func_70093_af())
        {
            if ((stack != null) && ((stack.func_77973_b() instanceof ItemBarrelHammer))) {
                configSide(stack, player, ForgeDirection.getOrientation(side));
            } else {
                manualStackAdd(player);
            }
        }
        else if (stack == null) {
            switchLocked();
        } else if ((stack.func_77973_b() instanceof ItemUpgradeSide)) {
            applySideUpgrade(stack, player, ForgeDirection.getOrientation(side));
        } else if ((stack.func_77973_b() instanceof ItemUpgradeCore)) {
            this.coreUpgrades.applyUpgrade(stack, player);
        } else if ((stack.func_77973_b() instanceof ItemUpgradeStructural)) {
            this.coreUpgrades.applyStructural(stack, player);
        } else if ((stack.func_77973_b() instanceof ItemBarrelHammer)) {
            removeUpgrade(stack, player, ForgeDirection.getOrientation(side));
        } else if ((stack.func_77973_b() instanceof ItemTuningFork))
        {
            if (stack.func_77960_j() == 0) {
                tuneFork(stack, player, ForgeDirection.getOrientation(side));
            } else {
                tuneBarrel(stack, player, ForgeDirection.getOrientation(side));
            }
        }
        else {
            manualStackAdd(player);
        }
    }

    private void tuneFork(ItemStack stack, EntityPlayer player, ForgeDirection side)
    {
        if (!this.coreUpgrades.hasEnder)
        {
            BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BSPACE_NOREACT, new Object[0]);
            return;
        }
        BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BSPACE_FORK_RESONATING, new Object[0]);
        stack.func_77964_b(1);
        stack.func_77982_d(new NBTTagCompound());
        stack.func_77978_p().func_74768_a("tuneID", this.id);
        stack.func_77978_p().func_74768_a("structural", this.coreUpgrades.levelStructural);
        stack.func_77978_p().func_74768_a("storage", this.coreUpgrades.nStorageUpg);
        stack.func_77978_p().func_74757_a("void", this.coreUpgrades.hasVoid);
        stack.func_77978_p().func_74757_a("creative", this.coreUpgrades.hasCreative);
    }

    private void tuneBarrel(ItemStack stack, EntityPlayer player, ForgeDirection side)
    {
        if (!this.coreUpgrades.hasEnder)
        {
            BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BSPACE_NOREACT, new Object[0]);
            return;
        }
        if (getStorage().hasItem())
        {
            BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BSPACE_CONTENT, new Object[0]);
            return;
        }
        int structural = stack.func_77978_p().func_74762_e("structural");
        int storage = stack.func_77978_p().func_74762_e("storage");
        int barrelID = stack.func_77978_p().func_74762_e("tuneID");
        boolean hasVoid = stack.func_77978_p().func_74767_n("void");
        boolean hasCreative = stack.func_77978_p().func_74767_n("creative");
        if ((this.coreUpgrades.levelStructural != structural) || (this.coreUpgrades.nStorageUpg != storage) || (this.coreUpgrades.hasVoid != hasVoid) || (this.coreUpgrades.hasCreative != hasCreative))
        {
            BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BSAPCE_STRUCTURE, new Object[0]);
            return;
        }
        if (this.id == barrelID)
        {
            stack.func_77964_b(1);
            return;
        }
        if ((BSpaceStorageHandler.instance().getBarrel(barrelID) == null) || (!BSpaceStorageHandler.instance().getBarrel(barrelID).coreUpgrades.hasEnder))
        {
            BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BSPACE_FORK_LOST, new Object[0]);
            stack.func_77964_b(0);
            stack.func_77982_d(new NBTTagCompound());
            return;
        }
        BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.BSPACE_RESONATING, new Object[0]);
        stack.func_77964_b(0);
        stack.func_77982_d(new NBTTagCompound());

        BSpaceStorageHandler.instance().linkStorages(barrelID, this.id);
        BarrelPacketHandler.INSTANCE.sendToDimension(new Message0x02GhostUpdate(this), this.field_145850_b.field_73011_w.field_76574_g);
        BarrelPacketHandler.INSTANCE.sendToDimension(new Message0x06FullStorage(this), this.field_145850_b.field_73011_w.field_76574_g);
    }

    private void configSide(ItemStack stack, EntityPlayer player, ForgeDirection side)
    {
        int type = this.sideUpgrades[side.ordinal()];

        boolean sendChange = false;
        if (type == UpgradeSide.REDSTONE)
        {
            this.sideMetadata[side.ordinal()] += 1;
            if (this.sideMetadata[side.ordinal()] > UpgradeSide.RS_PROP) {
                this.sideMetadata[side.ordinal()] = UpgradeSide.RS_FULL;
            }
            sendChange = true;
        }
        if (type == UpgradeSide.HOPPER)
        {
            if (this.sideMetadata[side.ordinal()] == UpgradeSide.RS_FULL) {
                this.sideMetadata[side.ordinal()] = UpgradeSide.RS_EMPT;
            } else {
                this.sideMetadata[side.ordinal()] = UpgradeSide.RS_FULL;
            }
            sendChange = true;
        }
        if (sendChange)
        {
            func_70296_d();
            BarrelPacketHandler.INSTANCE.sendToDimension(new Message0x03SideupgradeUpdate(this), this.field_145850_b.field_73011_w.field_76574_g);
        }
    }

    void removeUpgradeFacades(EntityPlayer player)
    {
        for (ForgeDirection s : ForgeDirection.VALID_DIRECTIONS)
        {
            int sideType = this.sideUpgrades[s.ordinal()];
            if ((UpgradeSide.mapReq[sideType] != -1) && (!this.coreUpgrades.hasUpgrade(UpgradeCore.values()[UpgradeSide.mapReq[sideType]]))) {
                dropSideUpgrade(player, s);
            }
        }
    }

    private void removeUpgrade(ItemStack stack, EntityPlayer player, ForgeDirection side)
    {
        int type = this.sideUpgrades[side.ordinal()];
        if ((type != UpgradeSide.NONE) && (type != UpgradeSide.FRONT)) {
            dropSideUpgrade(player, side);
        } else {
            this.coreUpgrades.removeUpgrade(stack, player, side);
        }
        this.field_145850_b.func_147444_c(this.field_145851_c, this.field_145848_d, this.field_145849_e, this.field_145850_b.func_147439_a(this.field_145851_c, this.field_145848_d, this.field_145849_e));
        BarrelPacketHandler.INSTANCE.sendToDimension(new Message0x03SideupgradeUpdate(this), this.field_145850_b.field_73011_w.field_76574_g);
        BarrelPacketHandler.INSTANCE.sendToDimension(new Message0x04Structuralupdate(this), this.field_145850_b.field_73011_w.field_76574_g);
        BarrelPacketHandler.INSTANCE.sendToDimension(new Message0x05CoreUpdate(this), this.field_145850_b.field_73011_w.field_76574_g);
        BarrelPacketHandler.INSTANCE.sendToDimension(new Message0x06FullStorage(this), this.field_145850_b.field_73011_w.field_76574_g);
    }

    private void dropSideUpgrade(EntityPlayer player, ForgeDirection side)
    {
        int type = this.sideUpgrades[side.ordinal()];
        ItemStack droppedStack = new ItemStack(UpgradeSide.mapItem[type], 1, UpgradeSide.mapMeta[type]);
        Utils.dropItemInWorld(this, player, droppedStack, 0.02D);
        this.sideUpgrades[side.ordinal()] = UpgradeSide.NONE;
        this.sideMetadata[side.ordinal()] = UpgradeSide.NONE;
    }

    private void applySideUpgrade(ItemStack stack, EntityPlayer player, ForgeDirection side)
    {
        int type = UpgradeSide.mapRevMeta[stack.func_77960_j()];
        if (this.sideUpgrades[side.ordinal()] != UpgradeSide.NONE) {
            return;
        }
        if (type == UpgradeSide.STICKER)
        {
            this.sideUpgrades[side.ordinal()] = UpgradeSide.STICKER;
            this.sideMetadata[side.ordinal()] = UpgradeSide.NONE;
        }
        else if (type == UpgradeSide.REDSTONE)
        {
            if (this.coreUpgrades.hasRedstone)
            {
                this.sideUpgrades[side.ordinal()] = UpgradeSide.REDSTONE;
                this.sideMetadata[side.ordinal()] = UpgradeSide.RS_FULL;
            }
            else
            {
                BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.FACADE_REDSTONE, new Object[0]);
            }
        }
        else if (type == UpgradeSide.HOPPER)
        {
            if (this.coreUpgrades.hasHopper)
            {
                this.sideUpgrades[side.ordinal()] = UpgradeSide.HOPPER;
                this.sideMetadata[side.ordinal()] = UpgradeSide.NONE;
            }
            else
            {
                BarrelPacketHandler.sendLocalizedChat(player, LocalizedChat.FACADE_HOPPER, new Object[0]);
                return;
            }
        }
        if (!player.field_71075_bZ.field_75098_d) {
            stack.field_77994_a -= 1;
        }
        func_70296_d();
        BarrelPacketHandler.INSTANCE.sendToDimension(new Message0x03SideupgradeUpdate(this), this.field_145850_b.field_73011_w.field_76574_g);
    }

    private void switchLocked()
    {
        getStorage().switchGhosting();
        func_70296_d();
    }

    public void setLocked(boolean locked)
    {
        getStorage().setGhosting(locked);
        func_70296_d();
    }

    private void manualStackAdd(EntityPlayer player)
    {
        ItemStack heldStack = player.field_71071_by.func_70448_g();
        getStorage().addStack(heldStack);
        if (this.field_145850_b.func_82737_E() - this.clickTime < 10L)
        {
            InventoryPlayer playerInv = player.field_71071_by;
            for (int invSlot = 0; invSlot < playerInv.func_70302_i_(); invSlot++)
            {
                ItemStack slotStack = playerInv.func_70301_a(invSlot);
                if ((getStorage().addStack(slotStack) > 0) &&
                        (slotStack.field_77994_a == 0)) {
                    playerInv.func_70299_a(invSlot, (ItemStack)null);
                }
            }
        }
        BetterBarrels.proxy.updatePlayerInventory(player);
        this.clickTime = this.field_145850_b.func_82737_E();

        func_70296_d();
    }

    public void func_145841_b(NBTTagCompound NBTTag)
    {
        if (this.id == -1) {
            this.id = BSpaceStorageHandler.instance().getNextBarrelID();
        }
        BSpaceStorageHandler.instance().updateBarrel(this.id, this.field_145850_b.field_73011_w.field_76574_g, this.field_145851_c, this.field_145848_d, this.field_145849_e);

        super.func_145841_b(NBTTag);
        NBTTag.func_74768_a("version", version);
        NBTTag.func_74768_a("orientation", this.orientation.ordinal());
        NBTTag.func_74768_a("rotation", this.rotation.ordinal());
        NBTTag.func_74783_a("sideUpgrades", this.sideUpgrades);
        this.coreUpgrades.writeToNBT(NBTTag);
        NBTTag.func_74783_a("sideMeta", this.sideMetadata);
        NBTTag.func_74757_a("ticking", this.isTicking);
        NBTTag.func_74757_a("linked", this.isLinked);
        NBTTag.func_74774_a("nticks", this.nTicks);
        NBTTag.func_74782_a("storage", getStorage().writeTagCompound());
        NBTTag.func_74768_a("bspaceid", this.id);
    }

    public void func_145839_a(NBTTagCompound NBTTag)
    {
        super.func_145839_a(NBTTag);


        int saveVersion = NBTTag.func_74762_e("version");
        if (saveVersion == 2)
        {
            readFromNBT_v2(NBTTag);
            return;
        }
        this.orientation = ForgeDirection.getOrientation(NBTTag.func_74762_e("orientation"));
        this.rotation = (NBTTag.func_74764_b("rotation") ? ForgeDirection.getOrientation(NBTTag.func_74762_e("rotation")) : this.orientation);
        this.sideUpgrades = NBTTag.func_74759_k("sideUpgrades");
        this.sideMetadata = NBTTag.func_74759_k("sideMeta");
        this.coreUpgrades = new BarrelCoreUpgrades(this);
        this.coreUpgrades.readFromNBT(NBTTag, saveVersion);
        this.isTicking = NBTTag.func_74767_n("ticking");
        this.isLinked = (NBTTag.func_74764_b("linked") ? NBTTag.func_74767_n("linked") : false);
        this.nTicks = NBTTag.func_74771_c("nticks");
        this.id = NBTTag.func_74762_e("bspaceid");
        if ((this.coreUpgrades.hasEnder) && (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)) {
            this.storage = BSpaceStorageHandler.instance().getStorage(this.id);
        } else {
            getStorage().readTagCompound(NBTTag.func_74775_l("storage"));
        }
        if (this.field_145850_b != null)
        {
            this.field_145850_b.func_147471_g(this.field_145851_c, this.field_145848_d, this.field_145849_e);
            if (this.isTicking) {
                startTicking();
            }
        }
    }

    private void readFromNBT_v2(NBTTagCompound NBTTag)
    {
        int blockOrientation = NBTTag.func_74762_e("barrelOrient");
        int upgradeCapacity = NBTTag.func_74762_e("upgradeCapacity");
        int blockOriginalOrient = NBTTag.func_74764_b("barrelOrigOrient") ? NBTTag.func_74762_e("barrelOrigOrient") : blockOrientation;
        StorageLocal storage = new StorageLocal();
        storage.readTagCompound(NBTTag.func_74775_l("storage"));


        this.orientation = ((ForgeDirection)convertOrientationFlagToForge(blockOriginalOrient).get(0));
        this.rotation = this.orientation;

        ArrayList<ForgeDirection> stickers = convertOrientationFlagToForge(blockOrientation);
        for (ForgeDirection s : stickers) {
            this.sideUpgrades[s.ordinal()] = UpgradeSide.STICKER;
        }
        this.sideUpgrades[this.orientation.ordinal()] = UpgradeSide.FRONT;


        this.coreUpgrades.levelStructural = upgradeCapacity;
        int freeSlots = this.coreUpgrades.getFreeSlots();
        for (int i = 0; i < freeSlots; i++)
        {
            this.coreUpgrades.upgradeList.add(UpgradeCore.STORAGE);
            getStorage().addStorageUpgrade();
            this.coreUpgrades.nStorageUpg += 1;
        }
        getStorage().setStoredItemType(storage.getItem(), storage.getAmount());
        getStorage().setGhosting(storage.isGhosting());


        this.id = BSpaceStorageHandler.instance().getNextBarrelID();
        if (this.field_145850_b != null) {
            this.field_145850_b.func_147471_g(this.field_145851_c, this.field_145848_d, this.field_145849_e);
        }
    }

    private ArrayList<ForgeDirection> convertOrientationFlagToForge(int flags)
    {
        ArrayList<ForgeDirection> directions = new ArrayList();
        for (int i = 0; i < 4; i++) {
            if ((1 << i & flags) != 0) {
                directions.add(ForgeDirection.getOrientation(i + 2));
            }
        }
        return directions;
    }

    public Packet func_145844_m()
    {
        return ((FMLEmbeddedChannel)BarrelPacketHandler.INSTANCE.channels.get(Side.SERVER)).generatePacketFrom(new Message0x00FulleTileEntityNBT(this));
    }

    public void func_70296_d()
    {
        super.func_70296_d();
        ServerTickHandler.INSTANCE.markDirty(this);
    }

    public void markDirtyExec()
    {
        super.func_70296_d();
        if ((this.coreUpgrades.hasRedstone) || (this.coreUpgrades.hasHopper)) {
            this.field_145850_b.func_147444_c(this.field_145851_c, this.field_145848_d, this.field_145849_e, this.field_145850_b.func_147439_a(this.field_145851_c, this.field_145848_d, this.field_145849_e));
        }
        if (!this.field_145850_b.field_72995_K)
        {
            sendContentSyncPacket(false);
            sendGhostSyncPacket(false);
        }
    }

    public boolean sendContentSyncPacket(boolean force)
    {
        IBarrelStorage tempStore = getStorage();
        if ((force) || (this.lastContentMessage == null) || (this.lastContentMessage.amount != tempStore.getAmount()) || (!tempStore.sameItem(this.lastContentMessage.stack)))
        {
            this.lastContentMessage = new Message0x01ContentUpdate(this);

            BarrelPacketHandler.INSTANCE.sendToAllAround(this.lastContentMessage, new NetworkRegistry.TargetPoint(this.field_145850_b.field_73011_w.field_76574_g, this.field_145851_c, this.field_145848_d, this.field_145849_e, 500.0D));
            return true;
        }
        return false;
    }

    public boolean sendGhostSyncPacket(boolean force)
    {
        if ((force) || (this.lastGhostMessage == null) || (this.lastGhostMessage.locked != getStorage().isGhosting()))
        {
            this.lastGhostMessage = new Message0x02GhostUpdate(this);

            BarrelPacketHandler.INSTANCE.sendToAllAround(this.lastGhostMessage, new NetworkRegistry.TargetPoint(this.field_145850_b.field_73011_w.field_76574_g, this.field_145851_c, this.field_145848_d, this.field_145849_e, 500.0D));
            return true;
        }
        return false;
    }

    public int func_70302_i_()
    {
        return getStorage().func_70302_i_();
    }

    public ItemStack func_70301_a(int islot)
    {
        ItemStack stack = getStorage().func_70301_a(islot);
        func_70296_d();
        return stack;
    }

    public ItemStack func_70298_a(int islot, int quantity)
    {
        TileEntity ent = this.field_145850_b.func_147438_o(this.field_145851_c, this.field_145848_d - 1, this.field_145849_e);
        ItemStack stack;
        if ((ent instanceof TileEntityHopper)) {
            stack = getStorage().decrStackSize_Hopper(islot, quantity);
        } else {
            stack = getStorage().func_70298_a(islot, quantity);
        }
        func_70296_d();
        return stack;
    }

    public void func_70299_a(int islot, ItemStack stack)
    {
        getStorage().func_70299_a(islot, stack);
        func_70296_d();
    }

    public ItemStack func_70304_b(int var1)
    {
        return null;
    }

    public String func_145825_b()
    {
        return "mcp.mobius.betterbarrel";
    }

    public int func_70297_j_()
    {
        return getStorage().func_70297_j_();
    }

    public boolean func_70300_a(EntityPlayer var1)
    {
        return this.field_145850_b.func_147438_o(this.field_145851_c, this.field_145848_d, this.field_145849_e) == this;
    }

    public void func_70295_k_() {}

    public void func_70305_f() {}

    public boolean func_145818_k_()
    {
        return false;
    }

    public boolean func_94041_b(int i, ItemStack itemstack)
    {
        return getStorage().func_94041_b(i, itemstack);
    }

    public int[] func_94128_d(int side)
    {
        if (this.sideUpgrades[side] == UpgradeSide.HOPPER) {
            return new int[] { 1 };
        }
        return getStorage().func_94128_d(side);
    }

    public boolean func_102007_a(int slot, ItemStack itemstack, int side)
    {
        if (this.sideUpgrades[side] == UpgradeSide.HOPPER) {
            return false;
        }
        return getStorage().func_102007_a(slot, itemstack, side);
    }

    public boolean func_102008_b(int slot, ItemStack itemstack, int side)
    {
        return getStorage().func_102008_b(slot, itemstack, side);
    }

    public ItemStack getStoredItemType()
    {
        return getStorage().getStoredItemType();
    }

    public void setStoredItemCount(int amount)
    {
        getStorage().setStoredItemCount(amount);
        func_70296_d();
    }

    public void setStoredItemType(ItemStack type, int amount)
    {
        getStorage().setStoredItemType(type, amount);
        func_70296_d();
    }

    public int getMaxStoredCount()
    {
        return getStorage().getMaxStoredCount();
    }

}
