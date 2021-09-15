package sun.management.snmp.util;

import com.sun.jmx.snmp.SnmpOid;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public abstract class SnmpListTableCache extends SnmpTableCache {
   protected abstract SnmpOid getIndex(Object var1, List<?> var2, int var3, Object var4);

   protected Object getData(Object var1, List<?> var2, int var3, Object var4) {
      return var4;
   }

   protected SnmpCachedData updateCachedDatas(Object var1, List<?> var2) {
      int var3 = var2 == null ? 0 : var2.size();
      if (var3 == 0) {
         return null;
      } else {
         long var4 = System.currentTimeMillis();
         Iterator var6 = var2.iterator();
         TreeMap var7 = new TreeMap(SnmpCachedData.oidComparator);

         for(int var8 = 0; var6.hasNext(); ++var8) {
            Object var9 = var6.next();
            SnmpOid var10 = this.getIndex(var1, var2, var8, var9);
            Object var11 = this.getData(var1, var2, var8, var9);
            if (var10 != null) {
               var7.put(var10, var11);
            }
         }

         return new SnmpCachedData(var4, var7);
      }
   }
}
