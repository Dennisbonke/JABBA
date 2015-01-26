package mcp.mobius.betterbarrels.common.blocks.logic;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public final class OreDictPair {

    public final ItemImmut itemA;
    public final ItemImmut itemB;

    public OreDictPair(ItemImmut itemA, ItemImmut itemB)
    {
        this.itemA = itemA;
        this.itemB = itemB;
    }

    public boolean equals(Object o)
    {
        OreDictPair c = (OreDictPair)o;
        return (this.itemA.equals(c.itemA)) && (this.itemB.equals(c.itemB));
    }

    public int hashCode()
    {
        return this.itemA.hashCode() + this.itemB.hashCode() * 877;
    }

}
