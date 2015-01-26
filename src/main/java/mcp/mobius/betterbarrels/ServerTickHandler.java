package mcp.mobius.betterbarrels;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import mcp.mobius.betterbarrels.bspace.BSpaceStorageHandler;
import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;

import java.util.Arrays;
import java.util.WeakHashMap;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public enum ServerTickHandler {

    INSTANCE;

    private ServerTickHandler() {}

    class Timer
    {
        private long interval;
        private long lastTick = System.nanoTime();

        public Timer(long interval)
        {
            this.interval = (interval * 1000L * 1000L);
        }

        public boolean isDone()
        {
            long time = System.nanoTime();
            long delta = time - this.lastTick - this.interval;
            boolean done = delta >= 0L;
            if (!done) {
                return false;
            }
            this.lastTick = (time - delta);
            return true;
        }
    }

    private WeakHashMap<TileEntityBarrel, Boolean> dirtyBarrels = new WeakHashMap();
    public Timer timer = new Timer(BetterBarrels.limiterDelay);

    @SubscribeEvent
    public void tickServer(TickEvent.ServerTickEvent event)
    {
        if ((this.timer.isDone()) &&
                (this.dirtyBarrels.size() > 0))
        {
            for (TileEntityBarrel barrel : (TileEntityBarrel[]) Arrays.copyOf(this.dirtyBarrels.keySet().toArray(), this.dirtyBarrels.size(),[Lmcp.mobius.betterbarrels.common.blocks.TileEntityBarrel.class)) {
            if (barrel != null) {
                barrel.markDirtyExec();
            }
        }
            this.dirtyBarrels.clear();
        }
    }

    public void markDirty(TileEntityBarrel barrel)
    {
        markDirty(barrel, true);
    }

    public void markDirty(TileEntityBarrel barrel, boolean bspace)
    {
        this.dirtyBarrels.put(barrel, Boolean.valueOf(true));
        if ((bspace) &&
                (barrel.coreUpgrades.hasEnder) && (!barrel.func_145831_w().field_72995_K)) {
            BSpaceStorageHandler.instance().markAllDirty(barrel.id);
        }
    }

}
