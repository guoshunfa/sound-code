package sun.management.snmp.util;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpOid;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class SnmpNamedListTableCache extends SnmpListTableCache {
   protected TreeMap<String, SnmpOid> names = new TreeMap();
   protected long last = 0L;
   boolean wrapped = false;
   static final MibLogger log = new MibLogger(SnmpNamedListTableCache.class);

   protected abstract String getKey(Object var1, List<?> var2, int var3, Object var4);

   protected SnmpOid makeIndex(Object var1, List<?> var2, int var3, Object var4) {
      if (++this.last > 4294967295L) {
         log.debug("makeIndex", "Index wrapping...");
         this.last = 0L;
         this.wrapped = true;
      }

      if (!this.wrapped) {
         return new SnmpOid(this.last);
      } else {
         for(int var5 = 1; (long)var5 < 4294967295L; ++var5) {
            if (++this.last > 4294967295L) {
               this.last = 1L;
            }

            SnmpOid var6 = new SnmpOid(this.last);
            if (this.names == null) {
               return var6;
            }

            if (!this.names.containsValue(var6)) {
               if (var1 == null) {
                  return var6;
               }

               if (!((Map)var1).containsValue(var6)) {
                  return var6;
               }
            }
         }

         return null;
      }
   }

   protected SnmpOid getIndex(Object var1, List<?> var2, int var3, Object var4) {
      String var5 = this.getKey(var1, var2, var3, var4);
      Object var6 = this.names != null && var5 != null ? this.names.get(var5) : null;
      SnmpOid var7 = var6 != null ? (SnmpOid)var6 : this.makeIndex(var1, var2, var3, var4);
      if (var1 != null && var5 != null && var7 != null) {
         Map var8 = (Map)Util.cast(var1);
         var8.put(var5, var7);
      }

      log.debug("getIndex", "key=" + var5 + ", index=" + var7);
      return var7;
   }

   protected SnmpCachedData updateCachedDatas(Object var1, List<?> var2) {
      TreeMap var3 = new TreeMap();
      SnmpCachedData var4 = super.updateCachedDatas(var1, var2);
      this.names = var3;
      return var4;
   }

   protected abstract List<?> loadRawDatas(Map<Object, Object> var1);

   protected abstract String getRawDatasKey();

   protected List<?> getRawDatas(Map<Object, Object> var1, String var2) {
      List var3 = null;
      if (var1 != null) {
         var3 = (List)var1.get(var2);
      }

      if (var3 == null) {
         var3 = this.loadRawDatas(var1);
         if (var3 != null && var1 != null) {
            var1.put(var2, var3);
         }
      }

      return var3;
   }

   protected SnmpCachedData updateCachedDatas(Object var1) {
      Map var2 = var1 instanceof Map ? (Map)Util.cast(var1) : null;
      List var3 = this.getRawDatas(var2, this.getRawDatasKey());
      log.debug("updateCachedDatas", "rawDatas.size()=" + (var3 == null ? "<no data>" : "" + var3.size()));
      TreeMap var4 = new TreeMap();
      SnmpCachedData var5 = super.updateCachedDatas(var4, var3);
      this.names = var4;
      return var5;
   }
}
