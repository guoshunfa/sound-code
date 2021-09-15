package com.sun.corba.se.impl.oa.poa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.omg.PortableServer.POAPackage.WrongPolicy;

class MultipleObjectMap extends ActiveObjectMap {
   private Map entryToKeys = new HashMap();

   public MultipleObjectMap(POAImpl var1) {
      super(var1);
   }

   public ActiveObjectMap.Key getKey(AOMEntry var1) throws WrongPolicy {
      throw new WrongPolicy();
   }

   protected void putEntry(ActiveObjectMap.Key var1, AOMEntry var2) {
      super.putEntry(var1, var2);
      Object var3 = (Set)this.entryToKeys.get(var2);
      if (var3 == null) {
         var3 = new HashSet();
         this.entryToKeys.put(var2, var3);
      }

      ((Set)var3).add(var1);
   }

   public boolean hasMultipleIDs(AOMEntry var1) {
      Set var2 = (Set)this.entryToKeys.get(var1);
      if (var2 == null) {
         return false;
      } else {
         return var2.size() > 1;
      }
   }

   protected void removeEntry(AOMEntry var1, ActiveObjectMap.Key var2) {
      Set var3 = (Set)this.entryToKeys.get(var1);
      if (var3 != null) {
         var3.remove(var2);
         if (var3.isEmpty()) {
            this.entryToKeys.remove(var1);
         }
      }

   }

   public void clear() {
      super.clear();
      this.entryToKeys.clear();
   }
}
