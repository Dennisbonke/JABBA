package mcp.mobius.betterbarrels;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class Utils {

    public static void dropItemInWorld(TileEntity source, EntityPlayer player, ItemStack stack, double speedfactor)
    {
        int hitOrientation = MathHelper.func_76128_c(player.field_70177_z * 4.0F / 360.0F + 0.5D) & 0x3;
        double stackCoordX = 0.0D;double stackCoordY = 0.0D;double stackCoordZ = 0.0D;
        switch (hitOrientation)
        {
            case 0:
                stackCoordX = source.field_145851_c + 0.5D;
                stackCoordY = source.field_145848_d + 0.5D;
                stackCoordZ = source.field_145849_e - 0.25D;
                break;
            case 1:
                stackCoordX = source.field_145851_c + 1.25D;
                stackCoordY = source.field_145848_d + 0.5D;
                stackCoordZ = source.field_145849_e + 0.5D;
                break;
            case 2:
                stackCoordX = source.field_145851_c + 0.5D;
                stackCoordY = source.field_145848_d + 0.5D;
                stackCoordZ = source.field_145849_e + 1.25D;
                break;
            case 3:
                stackCoordX = source.field_145851_c - 0.25D;
                stackCoordY = source.field_145848_d + 0.5D;
                stackCoordZ = source.field_145849_e + 0.5D;
        }
        EntityItem droppedEntity = new EntityItem(source.func_145831_w(), stackCoordX, stackCoordY, stackCoordZ, stack);
        if (player != null)
        {
            Vec3 motion = Vec3.func_72443_a(player.field_70165_t - stackCoordX, player.field_70163_u - stackCoordY, player.field_70161_v - stackCoordZ);
            motion.func_72432_b();
            droppedEntity.field_70159_w = motion.field_72450_a;
            droppedEntity.field_70181_x = motion.field_72448_b;
            droppedEntity.field_70179_y = motion.field_72449_c;
            double offset = 0.25D;
            droppedEntity.func_70091_d(motion.field_72450_a * offset, motion.field_72448_b * offset, motion.field_72449_c * offset);
        }
        droppedEntity.field_70159_w *= speedfactor;
        droppedEntity.field_70181_x *= speedfactor;
        droppedEntity.field_70179_y *= speedfactor;

        source.func_145831_w().func_72838_d(droppedEntity);
    }

    public static ForgeDirection getDirectionFacingEntity(EntityLivingBase player, boolean allowVertical)
    {
        Vec3 playerLook = player.func_70040_Z();
        if (allowVertical)
        {
            if (playerLook.field_72448_b <= -BetterBarrels.verticalPlacementRange) {
                return ForgeDirection.UP;
            }
            if (playerLook.field_72448_b >= BetterBarrels.verticalPlacementRange) {
                return ForgeDirection.DOWN;
            }
        }
        if (Math.abs(playerLook.field_72450_a) >= Math.abs(playerLook.field_72449_c))
        {
            if (playerLook.field_72450_a > 0.0D) {
                return ForgeDirection.WEST;
            }
            return ForgeDirection.EAST;
        }
        if (playerLook.field_72449_c > 0.0D) {
            return ForgeDirection.NORTH;
        }
        return ForgeDirection.SOUTH;
    }

    public static class Material
    {
        public String name;
        public String modDomain;
        public int meta = 0;
        ArrayList<ItemStack> ores = null;
        static final ItemStack portalStack = new ItemStack(Blocks.field_150427_aO);

        public Material(String in)
        {
            if (in.contains("Ore."))
            {
                this.name = in.split("\\.")[1];
            }
            else if (in.contains(":"))
            {
                int splitCh = in.indexOf(':');

                this.modDomain = in.substring(0, splitCh);
                String itemStr = in.substring(splitCh + 1, in.length());

                int metaCh = itemStr.indexOf(':');
                int wildCh = itemStr.indexOf('*');
                if (metaCh >= 0)
                {
                    if (wildCh == metaCh + 1) {
                        this.meta = 32767;
                    } else {
                        this.meta = Integer.parseInt(itemStr.substring(metaCh + 1, itemStr.length()));
                    }
                    this.name = itemStr.substring(0, metaCh);
                }
                else
                {
                    this.name = itemStr;
                    this.meta = 0;
                }
            }
            else
            {
                BetterBarrels.log.error("Unable to parse input string into oreDict or item:" + in);
            }
        }

        public boolean isOreDict()
        {
            return (this.name != null) && (this.modDomain == null);
        }

        public ItemStack getStack()
        {
            ItemStack ret = portalStack;
            if (isOreDict())
            {
                if (this.ores == null) {
                    this.ores = OreDictionary.getOres(this.name);
                }
                if (this.ores.size() > 0)
                {
                    if (this.meta >= this.ores.size()) {
                        this.meta = -1;
                    }
                    ret = (ItemStack)this.ores.get(this.meta >= 0 ? this.meta : 0);
                }
                BetterBarrels.debug("05 - Looking up [" + this.name + "] and found: " + ret.func_82833_r());
            }
            else
            {
                try
                {
                    ret = new ItemStack((Item)Item.field_150901_e.func_82594_a(this.modDomain + ":" + this.name), 1, this.meta);
                    BetterBarrels.debug("05 - Looking up [" + this.modDomain + ":" + this.name + ":" + this.meta + "] and found: " + ret.func_82833_r());
                }
                catch (Throwable t)
                {
                    BetterBarrels.log.error("Error while trying to initialize material with name " + this.modDomain + ":" + this.name + ":" + this.meta);
                }
            }
            return ret;
        }
    }

    public static class ReflectionHelper
    {
        public static Method getMethod(Class targetClass, String[] targetNames, Class[] targetParams)
        {
            return getMethod(targetClass, targetNames, targetParams, Level.ERROR, "Unable to reflect requested method[" + targetNames.toString() + "] with a paramter signature of [" + targetParams.toString() + "] in class[" + targetClass.getCanonicalName() + "]");
        }

        public static Method getMethod(Class targetClass, String[] targetNames, Class[] targetParams, Level errorLevel, String errorMessage)
        {
            Method foundMethod = null;
            for (String methodName : targetNames) {
                try
                {
                    foundMethod = targetClass.getDeclaredMethod(methodName, targetParams);
                    if (foundMethod != null)
                    {
                        foundMethod.setAccessible(true);
                        break;
                    }
                }
                catch (Throwable t) {}
            }
            if ((foundMethod == null) && (errorMessage != null)) {
                BetterBarrels.log.log(errorLevel, errorMessage);
            }
            return foundMethod;
        }

        public static Field getField(Class targetClass, String[] targetNames)
        {
            return getField(targetClass, targetNames, Level.ERROR, "Unable to reflect requested field[" + targetNames.toString() + "] in class[" + targetClass.getCanonicalName() + "]");
        }

        public static Field getField(Class targetClass, String[] targetNames, Level errorLevel, String errorMessage)
        {
            Field foundField = null;
            for (String fieldName : targetNames) {
                try
                {
                    foundField = targetClass.getDeclaredField(fieldName);
                    if (foundField != null)
                    {
                        foundField.setAccessible(true);
                        break;
                    }
                }
                catch (Throwable t) {}
            }
            if ((foundField == null) && (errorMessage != null)) {
                BetterBarrels.log.log(errorLevel, errorMessage);
            }
            return foundField;
        }

        public static <T> T getFieldValue(Class<T> returnType, Object targetObject, Class targetClass, String[] targetNames)
        {
            if (!returnType.isPrimitive()) {
                return getFieldValue(returnType, null, targetObject, targetClass, targetNames, Level.ERROR, "Unable to reflect and return value for requested field[" + targetNames.toString() + "] in class[" + targetClass.getCanonicalName() + "], defaulting to null or 0");
            }
            return getFieldValue(returnType, returnType.cast(Integer.valueOf(0)), targetObject, targetClass, targetNames, Level.ERROR, "Unable to reflect and return value for requested field[" + targetNames.toString() + "] in class[" + targetClass.getCanonicalName() + "], defaulting to null or 0");
        }

        public static <T> T getFieldValue(Class<T> returnType, T errorValue, Object targetObject, Class targetClass, String[] targetNames, Level errorLevel, String errorMessage)
        {
            T returnValue = errorValue;
            Field foundField = getField(targetClass, targetNames, errorLevel, errorMessage);
            if (foundField != null) {
                try
                {
                    returnValue = returnType.cast(foundField.get(targetObject));
                    BetterBarrels.debug("Reflected field [" + foundField.getName() + "] and found value [" + returnValue + "], had a backup value of " + errorValue);
                }
                catch (Throwable t)
                {
                    BetterBarrels.log.error("Unable to cast found field [" + foundField.getName() + "] to return type [" + returnType.getName() + "]. Defaulting to provided error value.");
                }
            }
            return returnValue;
        }
    }

    public static String romanNumeral(int num)
    {
        LinkedHashMap<String, Integer> numeralConversion = new LinkedHashMap();
        numeralConversion.put("M", Integer.valueOf(1000));
        numeralConversion.put("CM", Integer.valueOf(900));
        numeralConversion.put("D", Integer.valueOf(500));
        numeralConversion.put("CD", Integer.valueOf(400));
        numeralConversion.put("C", Integer.valueOf(100));
        numeralConversion.put("XC", Integer.valueOf(90));
        numeralConversion.put("L", Integer.valueOf(50));
        numeralConversion.put("XL", Integer.valueOf(40));
        numeralConversion.put("X", Integer.valueOf(10));
        numeralConversion.put("IX", Integer.valueOf(9));
        numeralConversion.put("V", Integer.valueOf(5));
        numeralConversion.put("IV", Integer.valueOf(4));
        numeralConversion.put("I", Integer.valueOf(1));

        String result = new String();
        while (numeralConversion.size() > 0)
        {
            String romanKey = (String)numeralConversion.keySet().toArray()[0];
            Integer arabicValue = (Integer)numeralConversion.values().toArray()[0];
            if (num < arabicValue.intValue())
            {
                numeralConversion.remove(romanKey);
            }
            else
            {
                num -= arabicValue.intValue();
                result = result + romanKey;
            }
        }
        return result;
    }

    private static Field chunkCacheWorld = ReflectionHelper.getField(ChunkCache.class, new String[] { "e", "field_72815_e", "worldObj" });

    public static TileEntity getTileEntityPreferNotCreating(IBlockAccess blockAccess, int x, int y, int z)
    {
        if ((blockAccess instanceof World)) {
            return getTileEntityWithoutCreating((World)blockAccess, x, y, z);
        }
        if ((blockAccess instanceof ChunkCache)) {
            return getTileEntityWithoutCreating((ChunkCache)blockAccess, x, y, z);
        }
        return blockAccess.func_147438_o(x, y, z);
    }

    public static TileEntity getTileEntityWithoutCreating(ChunkCache chunkCache, int x, int y, int z)
    {
        try
        {
            return getTileEntityWithoutCreating((World)chunkCacheWorld.get(chunkCache), x, y, z);
        }
        catch (Throwable t) {}
        return null;
    }

    public static TileEntity getTileEntityWithoutCreating(World world, int x, int y, int z)
    {
        return world.func_72938_d(x, z).getTileEntityUnsafe(x & 0xF, y, z & 0xF);
    }

}
