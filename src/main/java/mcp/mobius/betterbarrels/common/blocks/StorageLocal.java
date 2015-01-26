package mcp.mobius.betterbarrels.common.blocks;

import mcp.mobius.betterbarrels.BetterBarrels;
import mcp.mobius.betterbarrels.common.blocks.logic.Coordinates;
import mcp.mobius.betterbarrels.common.blocks.logic.ItemImmut;
import mcp.mobius.betterbarrels.common.blocks.logic.OreDictPair;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class StorageLocal implements IBarrelStorage {

    private ItemStack inputStack = null;
    private ItemStack prevInputStack = null;
    private ItemStack outputStack = null;
    private ItemStack prevOutputStack = null;
    private ItemStack itemTemplate = null;
    private ItemStack renderingTemplate = null;
    private int totalAmount = 0;
    private int stackAmount = 64;
    private int basestacks = BetterBarrels.stacksSize;
    private int maxstacks = BetterBarrels.stacksSize;
    private int totalCapacity = 64 * this.maxstacks;
    private int upgCapacity = 0;
    private boolean keepLastItem = false;
    private boolean deleteExcess = false;
    private boolean alwaysProvide = false;
    private Set<Coordinates> linkedStorages = new HashSet();
    private ItemImmut cachedBarrelOreItem = null;
    private static HashMap<OreDictPair, Boolean> oreDictCache = new HashMap();

    public StorageLocal()
    {
        func_70296_d();
    }

    public StorageLocal(NBTTagCompound tag)
    {
        readTagCompound(tag);func_70296_d();
    }

    public StorageLocal(int nupgrades)
    {
        for (int i = 0; i < nupgrades; i++) {
            addStorageUpgrade();
        }
        func_70296_d();
    }

    private ItemStack getStackFromSlot(int slot)
    {
        return slot == 0 ? this.inputStack : this.outputStack;
    }

    private int getFreeSpace()
    {
        return this.totalCapacity - (this.deleteExcess ? 0 : this.totalAmount);
    }

    public boolean hasItem()
    {
        return this.itemTemplate != null;
    }

    public ItemStack getItem()
    {
        return this.itemTemplate;
    }

    public ItemStack getItemForRender()
    {
        if (this.renderingTemplate == null)
        {
            this.renderingTemplate = this.itemTemplate.func_77946_l();
            if ((this.renderingTemplate.func_77942_o()) && (this.renderingTemplate.func_77978_p().func_74764_b("ench"))) {
                this.renderingTemplate.func_77978_p().func_82580_o("ench");
            }
            if ((this.renderingTemplate.func_77942_o()) && (this.renderingTemplate.func_77978_p().func_74764_b("CustomPotionEffects"))) {
                this.renderingTemplate.func_77978_p().func_82580_o("CustomPotionEffects");
            }
            if (this.renderingTemplate.func_77973_b() == Items.field_151068_bn) {
                this.renderingTemplate.func_77964_b(0);
            }
            if (this.renderingTemplate.func_77973_b() == Items.field_151062_by) {
                this.renderingTemplate = new ItemStack(Items.field_151068_bn, 0, 0);
            }
        }
        return this.renderingTemplate;
    }

    public void setItem(ItemStack stack)
    {
        if (stack != null)
        {
            this.itemTemplate = stack.func_77946_l();
            this.itemTemplate.field_77994_a = 0;
            this.stackAmount = stack.func_77976_d();
            this.cachedBarrelOreItem = new ItemImmut(Item.func_150891_b(this.itemTemplate.func_77973_b()), this.itemTemplate.func_77960_j());
        }
        else
        {
            this.itemTemplate = null;
            this.renderingTemplate = null;
            this.stackAmount = 64;
            this.cachedBarrelOreItem = null;
        }
        this.totalCapacity = (this.maxstacks * this.stackAmount);
    }

    public boolean sameItem(ItemStack stack)
    {
        if (this.itemTemplate == null)
        {
            if (this.keepLastItem) {
                return false;
            }
            return true;
        }
        if (stack == null) {
            return false;
        }
        if (!this.itemTemplate.func_77969_a(stack))
        {
            OreDictPair orePair = new OreDictPair(this.cachedBarrelOreItem, new ItemImmut(Item.func_150891_b(stack.func_77973_b()), stack.func_77960_j()));
            if (!oreDictCache.containsKey(orePair))
            {
                int[] oreIDsBarrel = OreDictionary.getOreIDs(this.itemTemplate);
                int[] oreIDsStack = OreDictionary.getOreIDs(stack);

                boolean equivalent = false;
                if ((oreIDsBarrel.length > 0) && (oreIDsStack.length > 0)) {
                    for (int barrelOreID : oreIDsBarrel)
                    {
                        String oreNameBarrel = OreDictionary.getOreName(barrelOreID);



                        boolean stackIsMetal = (oreNameBarrel.startsWith("ingot")) || (oreNameBarrel.startsWith("ore")) || (oreNameBarrel.startsWith("dust")) || (oreNameBarrel.startsWith("nugget"));
                        if (stackIsMetal)
                        {
                            for (int stackOreID : oreIDsStack)
                            {
                                equivalent = barrelOreID == stackOreID;
                                if (equivalent) {
                                    break;
                                }
                            }
                            if (equivalent) {
                                break;
                            }
                        }
                    }
                }
                oreDictCache.put(orePair, Boolean.valueOf(equivalent));
            }
            return ((Boolean)oreDictCache.get(orePair)).booleanValue();
        }
        return ItemStack.func_77970_a(this.itemTemplate, stack);
    }

    public NBTTagCompound writeTagCompound()
    {
        NBTTagCompound retTag = new NBTTagCompound();

        retTag.func_74768_a("amount", this.totalAmount);
        retTag.func_74768_a("maxstacks", this.maxstacks);
        retTag.func_74768_a("upgCapacity", this.upgCapacity);
        if (this.itemTemplate != null)
        {
            NBTTagCompound var3 = new NBTTagCompound();
            this.itemTemplate.func_77955_b(var3);
            retTag.func_74782_a("current_item", var3);
        }
        if (this.keepLastItem) {
            retTag.func_74757_a("keepLastItem", this.keepLastItem);
        }
        if (this.deleteExcess) {
            retTag.func_74757_a("deleteExcess", this.deleteExcess);
        }
        if (this.alwaysProvide) {
            retTag.func_74757_a("alwaysProvide", this.alwaysProvide);
        }
        return retTag;
    }

    public void readTagCompound(NBTTagCompound tag)
    {
        this.totalAmount = tag.func_74762_e("amount");
        this.maxstacks = tag.func_74762_e("maxstacks");
        this.upgCapacity = tag.func_74762_e("upgCapacity");
        this.itemTemplate = (tag.func_74764_b("current_item") ? ItemStack.func_77949_a(tag.func_74775_l("current_item")) : null);
        this.keepLastItem = (tag.func_74764_b("keepLastItem") ? tag.func_74767_n("keepLastItem") : false);
        this.deleteExcess = (tag.func_74764_b("deleteExcess") ? tag.func_74767_n("deleteExcess") : false);
        this.alwaysProvide = (tag.func_74764_b("alwaysProvide") ? tag.func_74767_n("alwaysProvide") : false);
        setItem(this.itemTemplate);
        if ((this.itemTemplate != null) && (this.totalAmount < 0))
        {
            this.totalAmount = 0;
            if (!this.keepLastItem) {
                this.keepLastItem = true;
            }
        }
    }

    public int addStack(ItemStack stack)
    {
        boolean skip = (stack == null) || (!sameItem(stack));
        if ((this.itemTemplate == null) && (this.keepLastItem) && (stack != null)) {
            skip = false;
        }
        if (skip) {
            return 0;
        }
        int deposit;
        if (this.inputStack == null)
        {
            this.inputStack = stack;
            deposit = stack.field_77994_a;
        }
        else
        {
            deposit = Math.min(stack.field_77994_a, this.stackAmount - this.inputStack.field_77994_a);
            this.inputStack.field_77994_a += deposit;
        }
        func_70296_d();

        stack.field_77994_a -= deposit;

        deposit = this.deleteExcess ? this.stackAmount : deposit;

        return deposit;
    }

    public ItemStack getStack()
    {
        if (this.itemTemplate != null) {
            return getStack(this.stackAmount);
        }
        return null;
    }

    public ItemStack getStack(int amount)
    {
        func_70296_d();

        ItemStack retStack = null;
        if (this.itemTemplate != null)
        {
            amount = Math.min(amount, this.stackAmount);
            if (!this.alwaysProvide) {
                amount = Math.min(amount, this.totalAmount);
            }
            retStack = this.itemTemplate.func_77946_l();
            if (!this.alwaysProvide) {
                this.outputStack.field_77994_a -= amount;
            }
            retStack.field_77994_a = amount;
        }
        func_70296_d();
        return retStack;
    }

    public boolean switchGhosting()
    {
        this.keepLastItem = (!this.keepLastItem);func_70296_d();return this.keepLastItem;
    }

    public boolean isGhosting()
    {
        return this.keepLastItem;
    }

    public void setGhosting(boolean locked)
    {
        this.keepLastItem = locked;
        if (this.totalAmount <= 0) {
            setItem(null);
        }
    }

    public boolean isVoid()
    {
        return this.deleteExcess;
    }

    public void setVoid(boolean delete)
    {
        this.deleteExcess = delete;
    }

    public boolean isCreative()
    {
        return this.alwaysProvide;
    }

    public void setCreative(boolean infinite)
    {
        this.alwaysProvide = infinite;
    }

    public int getAmount()
    {
        return this.totalAmount;
    }

    public void setAmount(int amount)
    {
        this.totalAmount = amount;
    }

    protected void recalcCapacities()
    {
        this.maxstacks = (this.basestacks * (this.upgCapacity + 1));
        this.totalCapacity = (this.maxstacks * this.stackAmount);
    }

    public void setBaseStacks(int basestacks)
    {
        this.basestacks = basestacks;
        recalcCapacities();
    }

    public int getMaxStacks()
    {
        return this.maxstacks;
    }

    public void addStorageUpgrade()
    {
        this.upgCapacity += 1;
        recalcCapacities();
    }

    public void rmStorageUpgrade()
    {
        this.upgCapacity -= 1;
        recalcCapacities();
    }

    private static final int[] accessibleSides = { 0, 1 };

    public int[] func_94128_d(int var1)
    {
        return accessibleSides;
    }

    public boolean func_102007_a(int slot, ItemStack itemstack, int side)
    {
        if (slot == 1) {
            return false;
        }
        if (getFreeSpace() <= 0) {
            return false;
        }
        return sameItem(itemstack);
    }

    public boolean func_102008_b(int slot, ItemStack itemstack, int side)
    {
        if (slot == 0) {
            return false;
        }
        if (!hasItem()) {
            return false;
        }
        if (itemstack == null) {
            return true;
        }
        return sameItem(itemstack);
    }

    public int func_70302_i_()
    {
        return 2;
    }

    public ItemStack func_70301_a(int slot)
    {
        func_70296_d();
        return getStackFromSlot(slot);
    }

    public ItemStack func_70298_a(int slot, int quantity)
    {
        if (slot == 0) {
            throw new RuntimeException("[JABBA] Tried to decr the stack size of the input slot");
        }
        ItemStack stack = this.outputStack.func_77946_l();
        int stackSize = Math.min(quantity, stack.field_77994_a);
        stack.field_77994_a = stackSize;
        this.outputStack.field_77994_a -= stackSize;

        func_70296_d();
        return stack;
    }

    public ItemStack decrStackSize_Hopper(int slot, int quantity)
    {
        if (slot == 0) {
            throw new RuntimeException("[JABBA] Tried to decr the stack size of the input slot");
        }
        ItemStack stack = this.outputStack.func_77946_l();
        int stackSize = Math.min(quantity, stack.field_77994_a);
        stack.field_77994_a = stackSize;
        this.outputStack.field_77994_a -= stackSize;


        return stack;
    }

    public ItemStack func_70304_b(int slot)
    {
        return getStackFromSlot(slot);
    }

    public void func_70299_a(int slot, ItemStack itemstack)
    {
        if (slot == 0) {
            this.inputStack = itemstack;
        } else {
            this.outputStack = itemstack;
        }
        func_70296_d();
    }

    public String func_145825_b()
    {
        return "jabba.localstorage";
    }

    public boolean func_145818_k_()
    {
        return false;
    }

    public int func_70297_j_()
    {
        if (BetterBarrels.exposeFullStorageSize) {
            return this.totalCapacity;
        }
        return 64;
    }

    public void func_70296_d()
    {
        if (this.inputStack != null)
        {
            if (this.itemTemplate == null) {
                setItem(this.inputStack);
            }
            if (this.totalCapacity - this.totalAmount > 0)
            {
                if (this.prevInputStack == null) {
                    this.totalAmount += this.inputStack.field_77994_a;
                } else {
                    this.totalAmount += this.inputStack.field_77994_a - this.prevInputStack.field_77994_a;
                }
                if (this.totalAmount > this.totalCapacity) {
                    this.totalAmount = this.totalCapacity;
                }
            }
            if ((this.deleteExcess) || (this.totalCapacity - this.totalAmount >= this.stackAmount))
            {
                this.inputStack = null;
                this.prevInputStack = null;
            }
            else
            {
                this.inputStack.field_77994_a = (this.stackAmount - (this.totalCapacity - this.totalAmount));
                this.prevInputStack = this.inputStack.func_77946_l();
            }
        }
        if ((!this.alwaysProvide) && (this.prevOutputStack != null))
        {
            if (this.outputStack != null) {
                this.totalAmount -= this.prevOutputStack.field_77994_a - this.outputStack.field_77994_a;
            } else {
                this.totalAmount -= this.prevOutputStack.field_77994_a;
            }
            if (this.totalAmount < 0) {
                this.totalAmount = 0;
            }
        }
        if ((this.totalAmount == 0) && (!this.keepLastItem))
        {
            setItem(null);
            this.outputStack = null;
            this.prevOutputStack = null;
            this.inputStack = null;
            this.prevInputStack = null;
        }
        else if (this.itemTemplate != null)
        {
            if (this.outputStack == null) {
                this.outputStack = this.itemTemplate.func_77946_l();
            }
            this.outputStack.field_77994_a = (this.alwaysProvide ? this.totalCapacity : this.totalAmount);
            if (!BetterBarrels.exposeFullStorageSize) {
                this.outputStack.field_77994_a = Math.min(this.outputStack.field_77994_a, this.stackAmount);
            }
            this.prevOutputStack = this.outputStack.func_77946_l();
        }
    }

    public boolean func_70300_a(EntityPlayer entityplayer)
    {
        return true;
    }

    public void func_70295_k_() {}

    public void func_70305_f() {}

    public boolean func_94041_b(int slot, ItemStack itemstack)
    {
        return sameItem(itemstack);
    }

    public ItemStack getStoredItemType()
    {
        if (this.itemTemplate != null)
        {
            ItemStack stack = this.itemTemplate.func_77946_l();
            stack.field_77994_a = (this.alwaysProvide ? this.totalCapacity : this.totalAmount);
            return stack;
        }
        if (this.keepLastItem) {
            return new ItemStack(Blocks.field_150384_bq, 0);
        }
        return null;
    }

    public void setStoredItemCount(int amount)
    {
        if (amount > this.totalCapacity) {
            amount = this.totalCapacity;
        }
        this.totalAmount = amount;
        func_70296_d();
    }

    public void setStoredItemType(ItemStack type, int amount)
    {
        setItem(type);
        if (amount > this.totalCapacity) {
            amount = this.totalCapacity;
        }
        this.totalAmount = amount;
        func_70296_d();
    }

    public int getMaxStoredCount()
    {
        return this.deleteExcess ? this.totalCapacity + this.stackAmount : this.totalCapacity;
    }

}
