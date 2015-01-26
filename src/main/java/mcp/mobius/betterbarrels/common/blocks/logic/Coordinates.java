package mcp.mobius.betterbarrels.common.blocks.logic;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.DimensionManager;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public final class Coordinates {

    public final int dim;
    public final double x;
    public final double y;
    public final double z;

    public Coordinates(int dim, double x, double y, double z)
    {
        this.dim = dim;this.x = x;this.y = y;this.z = z;
    }

    public Coordinates(NBTTagCompound tag)
    {
        this.dim = tag.func_74762_e("dim");
        this.x = tag.func_74769_h("x");
        this.y = tag.func_74769_h("y");
        this.z = tag.func_74769_h("z");
    }

    public TileEntity getEntityAt()
    {
        IBlockAccess world = DimensionManager.getWorld(this.dim);
        if (world == null) {
            return null;
        }
        return world.func_147438_o(MathHelper.func_76128_c(this.x), MathHelper.func_76128_c(this.y), MathHelper.func_76128_c(this.z));
    }

    public boolean equals(Object o)
    {
        if (o == null) {
            return false;
        }
        Coordinates c = (Coordinates)o;
        return (this.dim == c.dim) && (this.x == c.x) && (this.y == c.y) && (this.z == c.z);
    }

    public int hashCode()
    {
        return MathHelper.func_76128_c(this.dim + 31.0D * this.x + 877.0D * this.y + 3187.0D * this.z);
    }

    public NBTTagCompound writeToNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.func_74768_a("dim", this.dim);
        tag.func_74780_a("x", this.x);
        tag.func_74780_a("y", this.y);
        tag.func_74780_a("z", this.z);
        return tag;
    }

}
