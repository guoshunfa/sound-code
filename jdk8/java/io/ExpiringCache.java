package java.io;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class ExpiringCache {
   private long millisUntilExpiration;
   private Map<String, ExpiringCache.Entry> map;
   private int queryCount;
   private int queryOverflow;
   private int MAX_ENTRIES;

   ExpiringCache() {
      this(30000L);
   }

   ExpiringCache(long var1) {
      this.queryOverflow = 300;
      this.MAX_ENTRIES = 200;
      this.millisUntilExpiration = var1;
      this.map = new LinkedHashMap<String, ExpiringCache.Entry>() {
         protected boolean removeEldestEntry(Map.Entry<String, ExpiringCache.Entry> var1) {
            return this.size() > ExpiringCache.this.MAX_ENTRIES;
         }
      };
   }

   synchronized String get(String var1) {
      if (++this.queryCount >= this.queryOverflow) {
         this.cleanup();
      }

      ExpiringCache.Entry var2 = this.entryFor(var1);
      return var2 != null ? var2.val() : null;
   }

   synchronized void put(String var1, String var2) {
      if (++this.queryCount >= this.queryOverflow) {
         this.cleanup();
      }

      ExpiringCache.Entry var3 = this.entryFor(var1);
      if (var3 != null) {
         var3.setTimestamp(System.currentTimeMillis());
         var3.setVal(var2);
      } else {
         this.map.put(var1, new ExpiringCache.Entry(System.currentTimeMillis(), var2));
      }

   }

   synchronized void clear() {
      this.map.clear();
   }

   private ExpiringCache.Entry entryFor(String var1) {
      ExpiringCache.Entry var2 = (ExpiringCache.Entry)this.map.get(var1);
      if (var2 != null) {
         long var3 = System.currentTimeMillis() - var2.timestamp();
         if (var3 < 0L || var3 >= this.millisUntilExpiration) {
            this.map.remove(var1);
            var2 = null;
         }
      }

      return var2;
   }

   private void cleanup() {
      Set var1 = this.map.keySet();
      String[] var2 = new String[var1.size()];
      int var3 = 0;

      String var5;
      for(Iterator var4 = var1.iterator(); var4.hasNext(); var2[var3++] = var5) {
         var5 = (String)var4.next();
      }

      for(int var6 = 0; var6 < var2.length; ++var6) {
         this.entryFor(var2[var6]);
      }

      this.queryCount = 0;
   }

   static class Entry {
      private long timestamp;
      private String val;

      Entry(long var1, String var3) {
         this.timestamp = var1;
         this.val = var3;
      }

      long timestamp() {
         return this.timestamp;
      }

      void setTimestamp(long var1) {
         this.timestamp = var1;
      }

      String val() {
         return this.val;
      }

      void setVal(String var1) {
         this.val = var1;
      }
   }
}
