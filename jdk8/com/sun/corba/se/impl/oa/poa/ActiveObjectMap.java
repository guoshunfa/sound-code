package com.sun.corba.se.impl.oa.poa;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public abstract class ActiveObjectMap {
   protected POAImpl poa;
   private Map keyToEntry = new HashMap();
   private Map entryToServant = new HashMap();
   private Map servantToEntry = new HashMap();

   protected ActiveObjectMap(POAImpl var1) {
      this.poa = var1;
   }

   public static ActiveObjectMap create(POAImpl var0, boolean var1) {
      return (ActiveObjectMap)(var1 ? new MultipleObjectMap(var0) : new SingleObjectMap(var0));
   }

   public final boolean contains(Servant var1) {
      return this.servantToEntry.containsKey(var1);
   }

   public final boolean containsKey(ActiveObjectMap.Key var1) {
      return this.keyToEntry.containsKey(var1);
   }

   public final AOMEntry get(ActiveObjectMap.Key var1) {
      AOMEntry var2 = (AOMEntry)this.keyToEntry.get(var1);
      if (var2 == null) {
         var2 = new AOMEntry(this.poa);
         this.putEntry(var1, var2);
      }

      return var2;
   }

   public final Servant getServant(AOMEntry var1) {
      return (Servant)this.entryToServant.get(var1);
   }

   public abstract ActiveObjectMap.Key getKey(AOMEntry var1) throws WrongPolicy;

   public ActiveObjectMap.Key getKey(Servant var1) throws WrongPolicy {
      AOMEntry var2 = (AOMEntry)this.servantToEntry.get(var1);
      return this.getKey(var2);
   }

   protected void putEntry(ActiveObjectMap.Key var1, AOMEntry var2) {
      this.keyToEntry.put(var1, var2);
   }

   public final void putServant(Servant var1, AOMEntry var2) {
      this.entryToServant.put(var2, var1);
      this.servantToEntry.put(var1, var2);
   }

   protected abstract void removeEntry(AOMEntry var1, ActiveObjectMap.Key var2);

   public final void remove(ActiveObjectMap.Key var1) {
      AOMEntry var2 = (AOMEntry)this.keyToEntry.remove(var1);
      Servant var3 = (Servant)this.entryToServant.remove(var2);
      if (var3 != null) {
         this.servantToEntry.remove(var3);
      }

      this.removeEntry(var2, var1);
   }

   public abstract boolean hasMultipleIDs(AOMEntry var1);

   protected void clear() {
      this.keyToEntry.clear();
   }

   public final Set keySet() {
      return this.keyToEntry.keySet();
   }

   public static class Key {
      public byte[] id;

      Key(byte[] var1) {
         this.id = var1;
      }

      public String toString() {
         StringBuffer var1 = new StringBuffer();

         for(int var2 = 0; var2 < this.id.length; ++var2) {
            var1.append(Integer.toString(this.id[var2], 16));
            if (var2 != this.id.length - 1) {
               var1.append(":");
            }
         }

         return var1.toString();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof ActiveObjectMap.Key)) {
            return false;
         } else {
            ActiveObjectMap.Key var2 = (ActiveObjectMap.Key)var1;
            if (var2.id.length != this.id.length) {
               return false;
            } else {
               for(int var3 = 0; var3 < this.id.length; ++var3) {
                  if (this.id[var3] != var2.id[var3]) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int var1 = 0;

         for(int var2 = 0; var2 < this.id.length; ++var2) {
            var1 = 31 * var1 + this.id[var2];
         }

         return var1;
      }
   }
}
