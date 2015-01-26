package mcp.mobius.betterbarrels.common.blocks.logic;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public final class ItemImmut {

    public final int id;
    public final int meta;

    public ItemImmut(int id, int meta)
    {
        this.id = id;
        this.meta = meta;
    }

    public boolean equals(Object o)
    {
        ItemImmut c = (ItemImmut)o;
        return (this.id == c.id) && (this.meta == c.meta);
    }

    public int hashCode()
    {
        return this.meta + this.id * 32768;
    }

}
