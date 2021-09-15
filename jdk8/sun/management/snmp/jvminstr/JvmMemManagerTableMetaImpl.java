package sun.management.snmp.jvminstr;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryManagerMXBean;
import java.util.List;
import java.util.Map;
import sun.management.snmp.jvmmib.JvmMemManagerTableMeta;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.SnmpNamedListTableCache;
import sun.management.snmp.util.SnmpTableCache;
import sun.management.snmp.util.SnmpTableHandler;

public class JvmMemManagerTableMetaImpl extends JvmMemManagerTableMeta {
   static final long serialVersionUID = 36176771566817592L;
   protected SnmpTableCache cache;
   static final MibLogger log = new MibLogger(JvmMemManagerTableMetaImpl.class);

   public JvmMemManagerTableMetaImpl(SnmpMib var1, SnmpStandardObjectServer var2) {
      super(var1, var2);
      this.cache = new JvmMemManagerTableMetaImpl.JvmMemManagerTableCache(((JVM_MANAGEMENT_MIB_IMPL)var1).validity());
   }

   protected SnmpOid getNextOid(Object var1) throws SnmpStatusException {
      return this.getNextOid((SnmpOid)null, var1);
   }

   protected SnmpOid getNextOid(SnmpOid var1, Object var2) throws SnmpStatusException {
      boolean var3 = log.isDebugOn();
      if (var3) {
         log.debug("getNextOid", "previous=" + var1);
      }

      SnmpTableHandler var4 = this.getHandler(var2);
      if (var4 == null) {
         if (var3) {
            log.debug("getNextOid", "handler is null!");
         }

         throw new SnmpStatusException(224);
      } else {
         SnmpOid var5 = var4.getNext(var1);
         if (var3) {
            log.debug("getNextOid", "next=" + var5);
         }

         if (var5 == null) {
            throw new SnmpStatusException(224);
         } else {
            return var5;
         }
      }
   }

   protected boolean contains(SnmpOid var1, Object var2) {
      SnmpTableHandler var3 = this.getHandler(var2);
      return var3 == null ? false : var3.contains(var1);
   }

   public Object getEntry(SnmpOid var1) throws SnmpStatusException {
      if (var1 == null) {
         throw new SnmpStatusException(224);
      } else {
         Map var2 = JvmContextFactory.getUserData();
         long var3 = var1.getOidArc(0);
         String var5 = var2 == null ? null : "JvmMemManagerTable.entry." + var3;
         if (var2 != null) {
            Object var6 = var2.get(var5);
            if (var6 != null) {
               return var6;
            }
         }

         SnmpTableHandler var9 = this.getHandler(var2);
         if (var9 == null) {
            throw new SnmpStatusException(224);
         } else {
            Object var7 = var9.getData(var1);
            if (var7 == null) {
               throw new SnmpStatusException(224);
            } else {
               JvmMemManagerEntryImpl var8 = new JvmMemManagerEntryImpl((MemoryManagerMXBean)var7, (int)var3);
               if (var2 != null && var8 != null) {
                  var2.put(var5, var8);
               }

               return var8;
            }
         }
      }
   }

   protected SnmpTableHandler getHandler(Object var1) {
      Map var2;
      if (var1 instanceof Map) {
         var2 = (Map)Util.cast(var1);
      } else {
         var2 = null;
      }

      SnmpTableHandler var3;
      if (var2 != null) {
         var3 = (SnmpTableHandler)var2.get("JvmMemManagerTable.handler");
         if (var3 != null) {
            return var3;
         }
      }

      var3 = this.cache.getTableHandler();
      if (var2 != null && var3 != null) {
         var2.put("JvmMemManagerTable.handler", var3);
      }

      return var3;
   }

   private static class JvmMemManagerTableCache extends SnmpNamedListTableCache {
      static final long serialVersionUID = 6564294074653009240L;

      JvmMemManagerTableCache(long var1) {
         this.validity = var1;
      }

      protected String getKey(Object var1, List<?> var2, int var3, Object var4) {
         if (var4 == null) {
            return null;
         } else {
            String var5 = ((MemoryManagerMXBean)var4).getName();
            JvmMemManagerTableMetaImpl.log.debug("getKey", "key=" + var5);
            return var5;
         }
      }

      public SnmpTableHandler getTableHandler() {
         Map var1 = JvmContextFactory.getUserData();
         return this.getTableDatas(var1);
      }

      protected String getRawDatasKey() {
         return "JvmMemManagerTable.getMemoryManagers";
      }

      protected List<MemoryManagerMXBean> loadRawDatas(Map<Object, Object> var1) {
         return ManagementFactory.getMemoryManagerMXBeans();
      }
   }
}
