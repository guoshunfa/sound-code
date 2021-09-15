package com.sun.corba.se.impl.oa.poa;

import java.util.HashMap;
import java.util.Map;
import org.omg.PortableServer.POAPackage.WrongPolicy;

class SingleObjectMap extends ActiveObjectMap {
   private Map entryToKey = new HashMap();

   public SingleObjectMap(POAImpl var1) {
      super(var1);
   }

   public ActiveObjectMap.Key getKey(AOMEntry var1) throws WrongPolicy {
      return (ActiveObjectMap.Key)this.entryToKey.get(var1);
   }

   protected void putEntry(ActiveObjectMap.Key var1, AOMEntry var2) {
      super.putEntry(var1, var2);
      this.entryToKey.put(var2, var1);
   }

   public boolean hasMultipleIDs(AOMEntry var1) {
      return false;
   }

   protected void removeEntry(AOMEntry var1, ActiveObjectMap.Key var2) {
      this.entryToKey.remove(var1);
   }

   public void clear() {
      super.clear();
      this.entryToKey.clear();
   }
}
