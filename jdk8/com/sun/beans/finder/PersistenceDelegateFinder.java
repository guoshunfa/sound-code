package com.sun.beans.finder;

import java.beans.PersistenceDelegate;
import java.util.HashMap;
import java.util.Map;

public final class PersistenceDelegateFinder extends InstanceFinder<PersistenceDelegate> {
   private final Map<Class<?>, PersistenceDelegate> registry = new HashMap();

   public PersistenceDelegateFinder() {
      super(PersistenceDelegate.class, true, "PersistenceDelegate");
   }

   public void register(Class<?> var1, PersistenceDelegate var2) {
      synchronized(this.registry) {
         if (var2 != null) {
            this.registry.put(var1, var2);
         } else {
            this.registry.remove(var1);
         }

      }
   }

   public PersistenceDelegate find(Class<?> var1) {
      PersistenceDelegate var2;
      synchronized(this.registry) {
         var2 = (PersistenceDelegate)this.registry.get(var1);
      }

      return var2 != null ? var2 : (PersistenceDelegate)super.find(var1);
   }
}
