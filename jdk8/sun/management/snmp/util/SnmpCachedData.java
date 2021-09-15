package sun.management.snmp.util;

import com.sun.jmx.snmp.SnmpOid;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;

public class SnmpCachedData implements SnmpTableHandler {
   public static final Comparator<SnmpOid> oidComparator = new Comparator<SnmpOid>() {
      public int compare(SnmpOid var1, SnmpOid var2) {
         return var1.compareTo(var2);
      }

      public boolean equals(Object var1, Object var2) {
         return var1 == var2 ? true : var1.equals(var2);
      }
   };
   public final long lastUpdated;
   public final SnmpOid[] indexes;
   public final Object[] datas;

   public SnmpCachedData(long var1, SnmpOid[] var3, Object[] var4) {
      this.lastUpdated = var1;
      this.indexes = var3;
      this.datas = var4;
   }

   public SnmpCachedData(long var1, TreeMap<SnmpOid, Object> var3) {
      this(var1, var3, true);
   }

   public SnmpCachedData(long var1, TreeMap<SnmpOid, Object> var3, boolean var4) {
      int var5 = var3.size();
      this.lastUpdated = var1;
      this.indexes = new SnmpOid[var5];
      this.datas = new Object[var5];
      if (var4) {
         var3.keySet().toArray(this.indexes);
         var3.values().toArray(this.datas);
      } else {
         var3.values().toArray(this.datas);
      }

   }

   public final int find(SnmpOid var1) {
      return Arrays.binarySearch(this.indexes, var1, oidComparator);
   }

   public Object getData(SnmpOid var1) {
      int var2 = this.find(var1);
      return var2 >= 0 && var2 < this.datas.length ? this.datas[var2] : null;
   }

   public SnmpOid getNext(SnmpOid var1) {
      if (var1 == null) {
         return this.indexes.length > 0 ? this.indexes[0] : null;
      } else {
         int var2 = this.find(var1);
         if (var2 > -1) {
            return var2 < this.indexes.length - 1 ? this.indexes[var2 + 1] : null;
         } else {
            int var3 = -var2 - 1;
            return var3 > -1 && var3 < this.indexes.length ? this.indexes[var3] : null;
         }
      }
   }

   public boolean contains(SnmpOid var1) {
      int var2 = this.find(var1);
      return var2 > -1 && var2 < this.indexes.length;
   }
}
