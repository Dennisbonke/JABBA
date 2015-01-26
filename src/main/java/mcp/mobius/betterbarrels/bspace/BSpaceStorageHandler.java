package mcp.mobius.betterbarrels.bspace;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import mcp.mobius.betterbarrels.BetterBarrels;
import mcp.mobius.betterbarrels.ServerTickHandler;
import mcp.mobius.betterbarrels.common.blocks.IBarrelStorage;
import mcp.mobius.betterbarrels.common.blocks.StorageLocal;
import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;
import mcp.mobius.betterbarrels.common.blocks.logic.Coordinates;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.DimensionManager;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.util.*;
import java.util.zip.ZipException;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class BSpaceStorageHandler {

    private int version = 1;
    public static BSpaceStorageHandler _instance = new BSpaceStorageHandler();

    public static BSpaceStorageHandler instance()
    {
        return _instance;
    }

    private HashMap<Integer, Coordinates> barrels = new HashMap();
    private HashMap<Integer, IBarrelStorage> storageMap = new HashMap();
    private HashMap<Integer, IBarrelStorage> storageMapOriginal = new HashMap();
    private HashMap<Integer, HashSet<Integer>> links = new HashMap();
    private int maxBarrelID = 0;
    private File saveDir;
    private File[] saveFiles;
    private int saveTo;
    private NBTTagCompound saveTag;

    public int getNextBarrelID()
    {
        this.maxBarrelID += 1;
        return this.maxBarrelID;
    }

    public void updateBarrel(int id, int dim, int x, int y, int z)
    {
        Coordinates coord = new Coordinates(dim, x, y, z);
        if (!coord.equals(this.barrels.get(Integer.valueOf(id))))
        {
            this.barrels.put(Integer.valueOf(id), coord);
            writeToFile();
        }
    }

    public void registerEnderBarrel(int id, IBarrelStorage storage)
    {
        this.storageMap.put(Integer.valueOf(id), storage);
        this.storageMapOriginal.put(Integer.valueOf(id), storage);
        writeToFile();
    }

    public IBarrelStorage unregisterEnderBarrel(int id)
    {
        IBarrelStorage storage = (IBarrelStorage)this.storageMapOriginal.get(Integer.valueOf(id));
        this.storageMap.remove(Integer.valueOf(id));
        this.storageMapOriginal.remove(Integer.valueOf(id));
        unlinkStorage(id);

        writeToFile();
        return storage;
    }

    public IBarrelStorage getStorage(int id)
    {
        IBarrelStorage storage = (IBarrelStorage)this.storageMap.get(Integer.valueOf(id));


        return storage;
    }

    public IBarrelStorage getStorageOriginal(int id)
    {
        return (IBarrelStorage)this.storageMapOriginal.get(Integer.valueOf(id));
    }

    public TileEntityBarrel getBarrel(int id)
    {
        if (this.barrels.containsKey(Integer.valueOf(id)))
        {
            Coordinates coord = (Coordinates)this.barrels.get(Integer.valueOf(id));
            IBlockAccess world = DimensionManager.getWorld(coord.dim);
            if (world == null) {
                return null;
            }
            TileEntity te = world.func_147438_o(MathHelper.func_76128_c(coord.x), MathHelper.func_76128_c(coord.y), MathHelper.func_76128_c(coord.z));
            if (!(te instanceof TileEntityBarrel)) {
                return null;
            }
            TileEntityBarrel barrel = (TileEntityBarrel)te;
            if (barrel.id != id) {
                return null;
            }
            return barrel;
        }
        return null;
    }

    public void linkStorages(int sourceID, int targetID)
    {
        unlinkStorage(targetID);

        this.storageMap.put(Integer.valueOf(targetID), this.storageMap.get(Integer.valueOf(sourceID)));
        if (!this.links.containsKey(Integer.valueOf(sourceID))) {
            this.links.put(Integer.valueOf(sourceID), new HashSet());
        }
        this.links.put(Integer.valueOf(targetID), new HashSet());
        for (Iterator localIterator = this.links.values().iterator(); localIterator.hasNext();)
        {
            set = (HashSet)localIterator.next();
            set.remove(Integer.valueOf(targetID));
        }
        HashSet<Integer> set;
        ((HashSet)this.links.get(Integer.valueOf(sourceID))).add(Integer.valueOf(targetID));


        Object transferSet = new HashSet();
        ((HashSet)transferSet).add(Integer.valueOf(sourceID));
        ((HashSet)transferSet).add(Integer.valueOf(targetID));
        ((HashSet)transferSet).addAll((Collection)this.links.get(Integer.valueOf(sourceID)));
        for (Integer i : (HashSet)this.links.get(Integer.valueOf(sourceID)))
        {
            ((HashSet)this.links.get(i)).clear();
            ((HashSet)this.links.get(i)).addAll((Collection)transferSet);
            ((HashSet)this.links.get(i)).remove(i);

            TileEntityBarrel barrel = getBarrel(i.intValue());
            if (barrel != null) {
                barrel.setLinked(true);
            }
        }
        TileEntityBarrel source = getBarrel(sourceID);
        if (source != null) {
            source.setLinked(true);
        }
        cleanUpLinks();
        writeToFile();
    }

    private void cleanUpLinks()
    {
        HashSet<Integer> keys = new HashSet(this.links.keySet());
        for (Integer i : keys) {
            if (((HashSet)this.links.get(i)).size() == 0)
            {
                this.links.remove(i);

                TileEntityBarrel barrel = getBarrel(i.intValue());
                if (barrel != null) {
                    barrel.setLinked(false);
                }
            }
        }
    }

    public IBarrelStorage unlinkStorage(int sourceID)
    {
        if (!this.links.containsKey(Integer.valueOf(sourceID))) {
            return (IBarrelStorage)this.storageMapOriginal.get(Integer.valueOf(sourceID));
        }
        HashSet<Integer> copy = new HashSet((Collection)this.links.get(Integer.valueOf(sourceID)));
        for (Integer targetID : copy) {
            ((HashSet)this.links.get(targetID)).remove(Integer.valueOf(sourceID));
        }
        this.links.remove(Integer.valueOf(sourceID));

        TileEntityBarrel barrel = getBarrel(sourceID);
        if (barrel != null) {
            barrel.setLinked(false);
        }
        cleanUpLinks();
        writeToFile();

        return (IBarrelStorage)this.storageMapOriginal.get(Integer.valueOf(sourceID));
    }

    private void relinkStorages()
    {
        for (Iterator localIterator1 = this.links.keySet().iterator(); localIterator1.hasNext();)
        {
            source = (Integer)localIterator1.next();
            for (Integer target : (HashSet)this.links.get(source)) {
                this.storageMap.put(target, this.storageMap.get(source));
            }
        }
        Integer source;
    }

    public boolean hasLinks(int sourceID)
    {
        return this.links.containsKey(Integer.valueOf(sourceID));
    }

    public void updateAllBarrels(int sourceID)
    {
        if (!this.links.containsKey(Integer.valueOf(sourceID))) {
            return;
        }
        TileEntityBarrel source = getBarrel(sourceID);
        if (source == null) {
            return;
        }
        boolean updateRequiredContent = source.sendContentSyncPacket(false);
        boolean updateRequiredGhost = source.sendGhostSyncPacket(false);
        for (Integer targetID : (HashSet)this.links.get(Integer.valueOf(sourceID)))
        {
            TileEntityBarrel target = getBarrel(targetID.intValue());
            if (target != null)
            {
                target.getStorage().setGhosting(source.getStorage().isGhosting());
                target.sendContentSyncPacket(updateRequiredContent);
                target.sendGhostSyncPacket(updateRequiredGhost);
            }
        }
    }

    public void markAllDirty(int sourceID)
    {
        if (!this.links.containsKey(Integer.valueOf(sourceID))) {
            return;
        }
        TileEntityBarrel source = getBarrel(sourceID);
        if (source == null) {
            return;
        }
        for (Integer targetID : (HashSet)this.links.get(Integer.valueOf(sourceID)))
        {
            TileEntityBarrel target = getBarrel(targetID.intValue());
            if (target != null) {
                ServerTickHandler.INSTANCE.markDirty(target, false);
            }
        }
    }

    private void writeToNBT(NBTTagCompound nbt)
    {
        nbt.func_74768_a("version", this.version);
        nbt.func_74768_a("maxBarrelID", this.maxBarrelID);

        NBTTagCompound coords = new NBTTagCompound();
        for (Iterator localIterator = this.barrels.keySet().iterator(); localIterator.hasNext();)
        {
            Integer key;
            key = (Integer)localIterator.next();
            coords.func_74782_a(String.valueOf(key), ((Coordinates)this.barrels.get(key)).writeToNBT());
        }
        nbt.func_74782_a("barrelCoords", coords);

        NBTTagCompound stores = new NBTTagCompound();
        for (Integer key = this.storageMap.keySet().iterator(); key.hasNext();)
        {
            key = (Integer)key.next();
            stores.func_74782_a(String.valueOf(key), ((IBarrelStorage)this.storageMap.get(key)).writeTagCompound());
        }
        nbt.func_74782_a("storages", stores);

        NBTTagCompound storesOriginal = new NBTTagCompound();
        for (Integer key = this.storageMapOriginal.keySet().iterator(); key.hasNext();) {
            key = (Integer) key.next();
            storesOriginal.func_74782_a(String.valueOf(key), ((IBarrelStorage) this.storageMapOriginal.get(key)).writeTagCompound());
        }
        nbt.func_74782_a("storagesOriginal", storesOriginal);

        NBTTagCompound list = new NBTTagCompound();
        for (Integer key : this.links.keySet()) {
            list.func_74783_a(String.valueOf(key), convertInts((Set)this.links.get(key)));
        }
        nbt.func_74782_a("links", list);
    }

    private void readFromNBT(NBTTagCompound nbt)
    {
        this.maxBarrelID = (nbt.func_74764_b("maxBarrelID") ? nbt.func_74762_e("maxBarrelID") : 0);
        this.links = new HashMap();
        NBTTagCompound tag;
        if (nbt.func_74764_b("barrelCoords"))
        {
            tag = nbt.func_74775_l("barrelCoords");
            for (Object key : tag.func_150296_c()) {
                this.barrels.put(Integer.valueOf((String)key), new Coordinates(tag.func_74775_l((String)key)));
            }
        }
        NBTTagCompound tag;
        if (nbt.func_74764_b("storages"))
        {
            tag = nbt.func_74775_l("storages");
            for (Object key : tag.func_150296_c()) {
                this.storageMap.put(Integer.valueOf((String)key), new StorageLocal(tag.func_74775_l((String)key)));
            }
        }
        NBTTagCompound tag;
        if (nbt.func_74764_b("storagesOriginal"))
        {
            tag = nbt.func_74775_l("storagesOriginal");
            for (Object key : tag.func_150296_c()) {
                this.storageMapOriginal.put(Integer.valueOf((String)key), new StorageLocal(tag.func_74775_l((String)key)));
            }
        }
        if (nbt.func_74764_b("links"))
        {
            NBTTagCompound tag = nbt.func_74775_l("links");
            for (Object key : tag.func_150296_c()) {
                this.links.put(Integer.valueOf((String)key), convertHashSet(tag.func_74759_k((String)key)));
            }
            relinkStorages();
        }
    }

    public void writeToFile()
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            return;
        }
        try
        {
            writeToNBT(this.saveTag);

            File saveFile = this.saveFiles[this.saveTo];
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }
            DataOutputStream dout = new DataOutputStream(new FileOutputStream(saveFile));
            CompressedStreamTools.func_74799_a(this.saveTag, dout);
            dout.close();
            FileOutputStream fout = new FileOutputStream(this.saveFiles[2]);
            fout.write(this.saveTo);
            fout.close();
            this.saveTo ^= 0x1;
        }
        catch (Exception e)
        {
            BetterBarrels.log.info("JABBA state directory missing. Skipping saving state. If you are in hardcore mode, this is a perfectly normal situation, otherwise, please report to my bugtracker.\n");
        }
    }

    public void loadFromFile()
    {
        System.out.printf("Attemping to load JABBA data.\n", new Object[0]);

        this.saveDir = new File(DimensionManager.getCurrentSaveRootDirectory(), "JABBA");
        try
        {
            if (!this.saveDir.exists()) {
                this.saveDir.mkdirs();
            }
            this.saveFiles = new File[] { new File(this.saveDir, "data1.dat"), new File(this.saveDir, "data2.dat"), new File(this.saveDir, "lock.dat") };

            boolean dataLoaded = false;
            if ((this.saveFiles[2].exists()) && (this.saveFiles[2].length() > 0L))
            {
                FileInputStream fin = new FileInputStream(this.saveFiles[2]);
                this.saveTo = (fin.read() ^ 0x1);
                fin.close();
                try
                {
                    if (this.saveFiles[(this.saveTo ^ 0x1)].exists())
                    {
                        DataInputStream din = new DataInputStream(new FileInputStream(this.saveFiles[(this.saveTo ^ 0x1)]));
                        this.saveTag = CompressedStreamTools.func_74796_a(din);
                        din.close();
                        dataLoaded = true;
                    }
                }
                catch (ZipException e)
                {
                    if (this.saveFiles[this.saveTo].exists())
                    {
                        DataInputStream din = new DataInputStream(new FileInputStream(this.saveFiles[this.saveTo]));
                        this.saveTag = CompressedStreamTools.func_74796_a(din);
                        din.close();
                        dataLoaded = true;
                    }
                }
            }
            if (!dataLoaded) {
                this.saveTag = new NBTTagCompound();
            }
        }
        catch (Exception e)
        {
            if ((e instanceof ZipException)) {
                BetterBarrels.log.log(Level.ERROR, "Primary and Backup JABBA data files have been corrupted.");
            }
            throw new RuntimeException(e);
        }
        readFromNBT(this.saveTag);
    }

    private int[] convertInts(Set<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++) {
            ret[i] = ((Integer)iterator.next()).intValue();
        }
        return ret;
    }

    private HashSet<Integer> convertHashSet(int[] list)
    {
        HashSet<Integer> ret = new HashSet();
        for (int i = 0; i < list.length; i++) {
            ret.add(Integer.valueOf(list[i]));
        }
        return ret;
    }

}
