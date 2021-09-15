package java.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

abstract class ChangeListenerMap<L extends EventListener> {
   private Map<String, L[]> map;

   protected abstract L[] newArray(int var1);

   protected abstract L newProxy(String var1, L var2);

   public final synchronized void add(String var1, L var2) {
      if (this.map == null) {
         this.map = new HashMap();
      }

      EventListener[] var3 = (EventListener[])this.map.get(var1);
      int var4 = var3 != null ? var3.length : 0;
      EventListener[] var5 = this.newArray(var4 + 1);
      var5[var4] = var2;
      if (var3 != null) {
         System.arraycopy(var3, 0, var5, 0, var4);
      }

      this.map.put(var1, var5);
   }

   public final synchronized void remove(String var1, L var2) {
      if (this.map != null) {
         EventListener[] var3 = (EventListener[])this.map.get(var1);
         if (var3 != null) {
            for(int var4 = 0; var4 < var3.length; ++var4) {
               if (var2.equals(var3[var4])) {
                  int var5 = var3.length - 1;
                  if (var5 > 0) {
                     EventListener[] var6 = this.newArray(var5);
                     System.arraycopy(var3, 0, var6, 0, var4);
                     System.arraycopy(var3, var4 + 1, var6, var4, var5 - var4);
                     this.map.put(var1, var6);
                  } else {
                     this.map.remove(var1);
                     if (this.map.isEmpty()) {
                        this.map = null;
                     }
                  }
                  break;
               }
            }
         }
      }

   }

   public final synchronized L[] get(String var1) {
      return this.map != null ? (EventListener[])this.map.get(var1) : null;
   }

   public final void set(String var1, L[] var2) {
      if (var2 != null) {
         if (this.map == null) {
            this.map = new HashMap();
         }

         this.map.put(var1, var2);
      } else if (this.map != null) {
         this.map.remove(var1);
         if (this.map.isEmpty()) {
            this.map = null;
         }
      }

   }

   public final synchronized L[] getListeners() {
      if (this.map == null) {
         return this.newArray(0);
      } else {
         ArrayList var1 = new ArrayList();
         EventListener[] var2 = (EventListener[])this.map.get((Object)null);
         if (var2 != null) {
            EventListener[] var3 = var2;
            int var4 = var2.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               EventListener var6 = var3[var5];
               var1.add(var6);
            }
         }

         Iterator var10 = this.map.entrySet().iterator();

         while(true) {
            Map.Entry var11;
            String var12;
            do {
               if (!var10.hasNext()) {
                  return (EventListener[])var1.toArray(this.newArray(var1.size()));
               }

               var11 = (Map.Entry)var10.next();
               var12 = (String)var11.getKey();
            } while(var12 == null);

            EventListener[] var13 = (EventListener[])var11.getValue();
            int var7 = var13.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               EventListener var9 = var13[var8];
               var1.add(this.newProxy(var12, var9));
            }
         }
      }
   }

   public final L[] getListeners(String var1) {
      if (var1 != null) {
         EventListener[] var2 = this.get(var1);
         if (var2 != null) {
            return (EventListener[])var2.clone();
         }
      }

      return this.newArray(0);
   }

   public final synchronized boolean hasListeners(String var1) {
      if (this.map == null) {
         return false;
      } else {
         EventListener[] var2 = (EventListener[])this.map.get((Object)null);
         return var2 != null || var1 != null && null != this.map.get(var1);
      }
   }

   public final Set<Map.Entry<String, L[]>> getEntries() {
      return this.map != null ? this.map.entrySet() : Collections.emptySet();
   }

   public abstract L extract(L var1);
}
