package mcp.mobius.betterbarrels.common.items.dolly.api;

import mcp.mobius.betterbarrels.BetterBarrels;

import java.util.HashMap;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public enum MovableRegistrar {

    INSTANCE;

    private HashMap<Class, IDollyHandler> handlers = new HashMap();

    private MovableRegistrar() {}

    public void registerHandler(String name, IDollyHandler handler)
    {
        try
        {
            Class clazz = Class.forName(name);
            if (this.handlers.containsKey(clazz)) {
                BetterBarrels.log.warn(String.format("Handler already found for target %s. Overwritting %s with %s", new Object[] { clazz, this.handlers.get(clazz), handler }));
            }
            this.handlers.put(clazz, handler);
        }
        catch (ClassNotFoundException e)
        {
            BetterBarrels.log.warn(String.format("Didn't find class %s to add to the dolly.", new Object[] { name }));
        }
    }

    public void registerHandler(Class clazz, IDollyHandler handler)
    {
        if (this.handlers.containsKey(clazz)) {
            BetterBarrels.log.warn(String.format("Handler already found for target %s. Overwritting %s with %s", new Object[] { clazz, this.handlers.get(clazz), handler }));
        }
        this.handlers.put(clazz, handler);
    }

    public IDollyHandler getHandler(Object obj)
    {
        IDollyHandler retVal = null;
        for (Class clazz : this.handlers.keySet()) {
            if (clazz.isInstance(obj))
            {
                if (retVal != null) {
                    throw new RuntimeException(String.format("Multiple handlers to move object %s", new Object[] { obj }));
                }
                retVal = (IDollyHandler)this.handlers.get(clazz);
            }
        }
        return retVal;
    }

}
