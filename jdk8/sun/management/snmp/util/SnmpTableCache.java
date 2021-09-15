package sun.management.snmp.util;

import java.io.Serializable;
import java.lang.ref.WeakReference;

public abstract class SnmpTableCache implements Serializable {
   protected long validity;
   protected transient WeakReference<SnmpCachedData> datas;

   protected boolean isObsolete(SnmpCachedData var1) {
      if (var1 == null) {
         return true;
      } else if (this.validity < 0L) {
         return false;
      } else {
         return System.currentTimeMillis() - var1.lastUpdated > this.validity;
      }
   }

   protected SnmpCachedData getCachedDatas() {
      if (this.datas == null) {
         return null;
      } else {
         SnmpCachedData var1 = (SnmpCachedData)this.datas.get();
         return var1 != null && !this.isObsolete(var1) ? var1 : null;
      }
   }

   protected synchronized SnmpCachedData getTableDatas(Object var1) {
      SnmpCachedData var2 = this.getCachedDatas();
      if (var2 != null) {
         return var2;
      } else {
         SnmpCachedData var3 = this.updateCachedDatas(var1);
         if (this.validity != 0L) {
            this.datas = new WeakReference(var3);
         }

         return var3;
      }
   }

   protected abstract SnmpCachedData updateCachedDatas(Object var1);

   public abstract SnmpTableHandler getTableHandler();
}
