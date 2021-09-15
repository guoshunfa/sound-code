package sun.management.snmp.jvminstr;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;
import java.util.Map;
import sun.management.snmp.jvmmib.JvmMemPoolTableMeta;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.SnmpNamedListTableCache;
import sun.management.snmp.util.SnmpTableCache;
import sun.management.snmp.util.SnmpTableHandler;

public class JvmMemPoolTableMetaImpl extends JvmMemPoolTableMeta {
   static final long serialVersionUID = -2525820976094284957L;
   protected SnmpTableCache cache;
   static final MibLogger log = new MibLogger(JvmMemPoolTableMetaImpl.class);

   public JvmMemPoolTableMetaImpl(SnmpMib var1, SnmpStandardObjectServer var2) {
      super(var1, var2);
      this.cache = new JvmMemPoolTableMetaImpl.JvmMemPoolTableCache(((JVM_MANAGEMENT_MIB_IMPL)var1).validity() * 30L);
   }

   protected SnmpOid getNextOid(Object var1) throws SnmpStatusException {
      return this.getNextOid((SnmpOid)null, var1);
   }

   protected SnmpOid getNextOid(SnmpOid var1, Object var2) throws SnmpStatusException {
      boolean var3 = log.isDebugOn();

      try {
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
      } catch (SnmpStatusException var6) {
         if (var3) {
            log.debug("getNextOid", "End of MIB View: " + var6);
         }

         throw var6;
      } catch (RuntimeException var7) {
         if (var3) {
            log.debug("getNextOid", "Unexpected exception: " + var7);
         }

         if (var3) {
            log.debug("getNextOid", (Throwable)var7);
         }

         throw var7;
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
         Map var2 = (Map)Util.cast(JvmContextFactory.getUserData());
         long var3 = var1.getOidArc(0);
         String var5 = var2 == null ? null : "JvmMemPoolTable.entry." + var3;
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
               if (log.isDebugOn()) {
                  log.debug("getEntry", "data is a: " + var7.getClass().getName());
               }

               JvmMemPoolEntryImpl var8 = new JvmMemPoolEntryImpl((MemoryPoolMXBean)var7, (int)var3);
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
         var3 = (SnmpTableHandler)var2.get("JvmMemPoolTable.handler");
         if (var3 != null) {
            return var3;
         }
      }

      var3 = this.cache.getTableHandler();
      if (var2 != null && var3 != null) {
         var2.put("JvmMemPoolTable.handler", var3);
      }

      return var3;
   }

   private static class JvmMemPoolTableCache extends SnmpNamedListTableCache {
      static final long serialVersionUID = -1755520683086760574L;

      JvmMemPoolTableCache(long var1) {
         this.validity = var1;
      }

      protected String getKey(Object var1, List<?> var2, int var3, Object var4) {
         if (var4 == null) {
            return null;
         } else {
            String var5 = ((MemoryPoolMXBean)var4).getName();
            JvmMemPoolTableMetaImpl.log.debug("getKey", "key=" + var5);
            return var5;
         }
      }

      public SnmpTableHandler getTableHandler() {
         Map var1 = JvmContextFactory.getUserData();
         return this.getTableDatas(var1);
      }

      protected String getRawDatasKey() {
         return "JvmMemManagerTable.getMemoryPools";
      }

      protected List<MemoryPoolMXBean> loadRawDatas(Map<Object, Object> var1) {
         return ManagementFactory.getMemoryPoolMXBeans();
      }
   }
}
