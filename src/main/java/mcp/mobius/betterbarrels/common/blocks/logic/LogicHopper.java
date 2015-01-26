package mcp.mobius.betterbarrels.common.blocks.logic;

import mcp.mobius.betterbarrels.common.blocks.IBarrelStorage;
import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;
import mcp.mobius.betterbarrels.common.items.upgrades.UpgradeSide;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public enum LogicHopper {

    INSTANCE;

    private LogicHopper() {}

    private boolean isStorage(TileEntity inventory)
    {
        if ((inventory instanceof IDeepStorageUnit)) {
            return true;
        }
        if ((inventory instanceof IInventory)) {
            return true;
        }
        return false;
    }

    public boolean run(TileEntityBarrel barrel)
    {
        boolean transaction = false;
        IBarrelStorage store = barrel.getStorage();
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (barrel.sideUpgrades[side.ordinal()] == UpgradeSide.HOPPER) {
                if (((barrel.sideMetadata[side.ordinal()] != UpgradeSide.RS_FULL) || ((store.hasItem()) && (store.getAmount() > 0))) && (
                        (barrel.sideMetadata[side.ordinal()] != UpgradeSide.RS_EMPT) || (store.getAmount() < store.getMaxStoredCount())))
                {
                    TileEntity targetEntity = barrel.func_145831_w().func_147438_o(barrel.field_145851_c + side.offsetX, barrel.field_145848_d + side.offsetY, barrel.field_145849_e + side.offsetZ);
                    if (isStorage(targetEntity)) {
                        if (barrel.sideMetadata[side.ordinal()] == UpgradeSide.RS_FULL)
                        {
                            ItemStack stack = store.func_70301_a(1);
                            if (!isFull(targetEntity, side.getOpposite()))
                            {
                                stack = barrel.getStorage().func_70301_a(1);
                                if ((stack != null) && (stack.field_77994_a > 0) && (pushItemToInventory(targetEntity, side.getOpposite(), stack)))
                                {
                                    barrel.getStorage().func_70296_d();
                                    transaction = true;
                                    targetEntity.func_70296_d();
                                }
                            }
                        }
                        else if (store.getAmount() != store.getMaxStoredCount())
                        {
                            ItemStack pulledStack = pullMatchingItemFromInventory(store, targetEntity, side.getOpposite());
                            if (pulledStack != null)
                            {
                                if (store.hasItem()) {
                                    store.setStoredItemCount(store.getAmount() + 1);
                                } else {
                                    store.setStoredItemType(pulledStack, 1);
                                }
                                transaction = true;
                                targetEntity.func_70296_d();
                            }
                        }
                    }
                }
            }
        }
        return transaction;
    }

    private boolean isFull(TileEntity inventory, ForgeDirection side)
    {
        if ((inventory instanceof IDeepStorageUnit))
        {
            IDeepStorageUnit dsu = (IDeepStorageUnit)inventory;
            ItemStack is = dsu.getStoredItemType();
            if ((is == null) || (is.field_77994_a != dsu.getMaxStoredCount())) {
                return false;
            }
            return true;
        }
        if (((inventory instanceof ISidedInventory)) && (side.ordinal() > -1))
        {
            ISidedInventory sinv = (ISidedInventory)inventory;
            int[] islots = sinv.func_94128_d(side.ordinal());
            for (int index : islots)
            {
                ItemStack is = sinv.func_70301_a(index);
                if ((is == null) || (is.field_77994_a != is.func_77976_d())) {
                    return false;
                }
            }
            return true;
        }
        if ((inventory instanceof IInventory))
        {
            IInventory inv = (IInventory)inventory;
            for (int index = 0; index < inv.func_70302_i_(); index++)
            {
                ItemStack is = inv.func_70301_a(index);
                if ((is == null) || (is.field_77994_a != is.func_77976_d())) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    private boolean pushItemToInventory(TileEntity inventory, ForgeDirection side, ItemStack stack)
    {
        if ((inventory instanceof IDeepStorageUnit))
        {
            IDeepStorageUnit dsu = (IDeepStorageUnit)inventory;
            ItemStack is = dsu.getStoredItemType();
            if (is == null)
            {
                is = stack.func_77946_l();
                dsu.setStoredItemType(is, 1);
                stack.field_77994_a -= 1;
                return true;
            }
            if ((is.func_77969_a(stack)) && (is.field_77994_a < dsu.getMaxStoredCount()))
            {
                dsu.setStoredItemCount(is.field_77994_a + 1);
                stack.field_77994_a -= 1;
                return true;
            }
        }
        else if (((inventory instanceof ISidedInventory)) && (side.ordinal() > -1))
        {
            ISidedInventory sinv = (ISidedInventory)inventory;
            int[] islots = sinv.func_94128_d(side.ordinal());
            for (int slot : islots) {
                if (sinv.func_102007_a(slot, stack, side.ordinal()))
                {
                    ItemStack targetStack = sinv.func_70301_a(slot);
                    if (targetStack == null)
                    {
                        targetStack = stack.func_77946_l();
                        targetStack.field_77994_a = 1;
                        sinv.func_70299_a(slot, targetStack);
                        stack.field_77994_a -= 1;
                        return true;
                    }
                    if ((targetStack.func_77969_a(stack)) && (targetStack.field_77994_a < targetStack.func_77976_d()))
                    {
                        targetStack.field_77994_a += 1;
                        stack.field_77994_a -= 1;
                        return true;
                    }
                }
            }
        }
        else if ((inventory instanceof IInventory))
        {
            IInventory inv = (IInventory)inventory;
            int nslots = inv.func_70302_i_();
            for (int slot = 0; slot < nslots; slot++)
            {
                ItemStack targetStack = inv.func_70301_a(slot);
                if (targetStack == null)
                {
                    targetStack = stack.func_77946_l();
                    targetStack.field_77994_a = 1;
                    inv.func_70299_a(slot, targetStack);
                    stack.field_77994_a -= 1;
                    return true;
                }
                if ((targetStack.func_77969_a(stack)) && (targetStack.field_77994_a < targetStack.func_77976_d()))
                {
                    targetStack.field_77994_a += 1;
                    stack.field_77994_a -= 1;
                    return true;
                }
            }
        }
        return false;
    }

    private ItemStack pullMatchingItemFromInventory(IBarrelStorage barrel, TileEntity source, ForgeDirection side)
    {
        if ((source instanceof IDeepStorageUnit))
        {
            IDeepStorageUnit dsu = (IDeepStorageUnit)source;
            ItemStack stack = dsu.getStoredItemType();
            if ((stack != null) && (barrel.sameItem(stack)) && (stack.field_77994_a > 0))
            {
                dsu.setStoredItemCount(stack.field_77994_a - 1);
                stack = stack.func_77946_l();
                stack.field_77994_a = 1;
                return stack;
            }
        }
        else if (((source instanceof ISidedInventory)) && (side.ordinal() > -1))
        {
            ISidedInventory sinv = (ISidedInventory)source;
            int[] islots = sinv.func_94128_d(side.ordinal());
            for (int slot : islots) {
                if (sinv.func_102008_b(slot, barrel.getItem(), side.ordinal()))
                {
                    ItemStack stack = sinv.func_70301_a(slot);
                    if ((stack != null) && (barrel.sameItem(stack)) && (stack.field_77994_a > 0)) {
                        return sinv.func_70298_a(slot, 1);
                    }
                }
            }
        }
        else if ((source instanceof IInventory))
        {
            IInventory inv = (IInventory)source;
            int nslots = inv.func_70302_i_();
            for (int slot = 0; slot < nslots; slot++)
            {
                ItemStack stack = inv.func_70301_a(slot);
                if ((stack != null) && (barrel.sameItem(stack)) && (stack.field_77994_a > 0)) {
                    return inv.func_70298_a(slot, 1);
                }
            }
        }
        return null;
    }

}
