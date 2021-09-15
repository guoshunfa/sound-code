package sun.management.snmp.util;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import java.util.TreeMap;

public final class SnmpLoadedClassData extends SnmpCachedData {
   public SnmpLoadedClassData(long var1, TreeMap<SnmpOid, Object> var3) {
      super(var1, var3, false);
   }

   public final Object getData(SnmpOid var1) {
      boolean var2 = false;

      int var5;
      try {
         var5 = (int)var1.getOidArc(0);
      } catch (SnmpStatusException var4) {
         return null;
      }

      return var5 >= this.datas.length ? null : this.datas[var5];
   }

   public final SnmpOid getNext(SnmpOid var1) {
      boolean var2 = false;
      if (var1 == null && this.datas != null && this.datas.length >= 1) {
         return new SnmpOid(0L);
      } else {
         int var5;
         try {
            var5 = (int)var1.getOidArc(0);
         } catch (SnmpStatusException var4) {
            return null;
         }

         return var5 < this.datas.length - 1 ? new SnmpOid((long)(var5 + 1)) : null;
      }
   }

   public final boolean contains(SnmpOid var1) {
      boolean var2 = false;

      int var5;
      try {
         var5 = (int)var1.getOidArc(0);
      } catch (SnmpStatusException var4) {
         return false;
      }

      return var5 < this.datas.length;
   }
}
