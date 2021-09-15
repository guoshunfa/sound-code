package sun.management.snmp.jvminstr;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.lang.management.ThreadInfo;
import java.util.Map;
import java.util.TreeMap;
import sun.management.snmp.jvmmib.JvmThreadInstanceTableMeta;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.SnmpCachedData;
import sun.management.snmp.util.SnmpTableCache;
import sun.management.snmp.util.SnmpTableHandler;

public class JvmThreadInstanceTableMetaImpl extends JvmThreadInstanceTableMeta {
   static final long serialVersionUID = -8432271929226397492L;
   public static final int MAX_STACK_TRACE_DEPTH = 0;
   protected SnmpTableCache cache;
   static final MibLogger log = new MibLogger(JvmThreadInstanceTableMetaImpl.class);

   static SnmpOid makeOid(long var0) {
      long[] var2 = new long[]{var0 >> 56 & 255L, var0 >> 48 & 255L, var0 >> 40 & 255L, var0 >> 32 & 255L, var0 >> 24 & 255L, var0 >> 16 & 255L, var0 >> 8 & 255L, var0 & 255L};
      return new SnmpOid(var2);
   }

   static long makeId(SnmpOid var0) {
      long var1 = 0L;
      long[] var3 = var0.longValue(false);
      var1 |= var3[0] << 56;
      var1 |= var3[1] << 48;
      var1 |= var3[2] << 40;
      var1 |= var3[3] << 32;
      var1 |= var3[4] << 24;
      var1 |= var3[5] << 16;
      var1 |= var3[6] << 8;
      var1 |= var3[7];
      return var1;
   }

   public JvmThreadInstanceTableMetaImpl(SnmpMib var1, SnmpStandardObjectServer var2) {
      super(var1, var2);
      this.cache = new JvmThreadInstanceTableMetaImpl.JvmThreadInstanceTableCache(this, ((JVM_MANAGEMENT_MIB_IMPL)var1).validity());
      log.debug("JvmThreadInstanceTableMetaImpl", "Create Thread meta");
   }

   protected SnmpOid getNextOid(Object var1) throws SnmpStatusException {
      log.debug("JvmThreadInstanceTableMetaImpl", "getNextOid");
      return this.getNextOid((SnmpOid)null, var1);
   }

   protected SnmpOid getNextOid(SnmpOid var1, Object var2) throws SnmpStatusException {
      log.debug("getNextOid", "previous=" + var1);
      SnmpTableHandler var3 = this.getHandler(var2);
      if (var3 == null) {
         log.debug("getNextOid", "handler is null!");
         throw new SnmpStatusException(224);
      } else {
         SnmpOid var4 = var1;

         do {
            var4 = var3.getNext(var4);
         } while(var4 != null && this.getJvmThreadInstance(var2, var4) == null);

         log.debug("*** **** **** **** getNextOid", "next=" + var4);
         if (var4 == null) {
            throw new SnmpStatusException(224);
         } else {
            return var4;
         }
      }
   }

   protected boolean contains(SnmpOid var1, Object var2) {
      SnmpTableHandler var3 = this.getHandler(var2);
      if (var3 == null) {
         return false;
      } else if (!var3.contains(var1)) {
         return false;
      } else {
         JvmThreadInstanceEntryImpl var4 = this.getJvmThreadInstance(var2, var1);
         return var4 != null;
      }
   }

   public Object getEntry(SnmpOid var1) throws SnmpStatusException {
      log.debug("*** **** **** **** getEntry", "oid [" + var1 + "]");
      if (var1 != null && var1.getLength() == 8) {
         Map var2 = JvmContextFactory.getUserData();
         SnmpTableHandler var3 = this.getHandler(var2);
         if (var3 != null && var3.contains(var1)) {
            JvmThreadInstanceEntryImpl var4 = this.getJvmThreadInstance(var2, var1);
            if (var4 == null) {
               throw new SnmpStatusException(224);
            } else {
               return var4;
            }
         } else {
            throw new SnmpStatusException(224);
         }
      } else {
         log.debug("getEntry", "Invalid oid [" + var1 + "]");
         throw new SnmpStatusException(224);
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
         var3 = (SnmpTableHandler)var2.get("JvmThreadInstanceTable.handler");
         if (var3 != null) {
            return var3;
         }
      }

      var3 = this.cache.getTableHandler();
      if (var2 != null && var3 != null) {
         var2.put("JvmThreadInstanceTable.handler", var3);
      }

      return var3;
   }

   private ThreadInfo getThreadInfo(long var1) {
      return JvmThreadingImpl.getThreadMXBean().getThreadInfo(var1, 0);
   }

   private ThreadInfo getThreadInfo(SnmpOid var1) {
      return this.getThreadInfo(makeId(var1));
   }

   private JvmThreadInstanceEntryImpl getJvmThreadInstance(Object var1, SnmpOid var2) {
      JvmThreadInstanceEntryImpl var3 = null;
      String var4 = null;
      Map var5 = null;
      boolean var6 = log.isDebugOn();
      if (var1 instanceof Map) {
         var5 = (Map)Util.cast(var1);
         var4 = "JvmThreadInstanceTable.entry." + var2.toString();
         var3 = (JvmThreadInstanceEntryImpl)var5.get(var4);
      }

      if (var3 != null) {
         if (var6) {
            log.debug("*** getJvmThreadInstance", "Entry found in cache: " + var4);
         }

         return var3;
      } else {
         if (var6) {
            log.debug("*** getJvmThreadInstance", "Entry [" + var2 + "] is not in cache");
         }

         ThreadInfo var7 = null;

         try {
            var7 = this.getThreadInfo(var2);
         } catch (RuntimeException var9) {
            log.trace("*** getJvmThreadInstance", "Failed to get thread info for rowOid: " + var2);
            log.debug("*** getJvmThreadInstance", (Throwable)var9);
         }

         if (var7 == null) {
            if (var6) {
               log.debug("*** getJvmThreadInstance", "No entry by that oid [" + var2 + "]");
            }

            return null;
         } else {
            var3 = new JvmThreadInstanceEntryImpl(var7, var2.toByte());
            if (var5 != null) {
               var5.put(var4, var3);
            }

            if (var6) {
               log.debug("*** getJvmThreadInstance", "Entry created for Thread OID [" + var2 + "]");
            }

            return var3;
         }
      }
   }

   private static class JvmThreadInstanceTableCache extends SnmpTableCache {
      static final long serialVersionUID = 4947330124563406878L;
      private final JvmThreadInstanceTableMetaImpl meta;

      JvmThreadInstanceTableCache(JvmThreadInstanceTableMetaImpl var1, long var2) {
         this.validity = var2;
         this.meta = var1;
      }

      public SnmpTableHandler getTableHandler() {
         Map var1 = JvmContextFactory.getUserData();
         return this.getTableDatas(var1);
      }

      protected SnmpCachedData updateCachedDatas(Object var1) {
         long[] var2 = JvmThreadingImpl.getThreadMXBean().getAllThreadIds();
         long var3 = System.currentTimeMillis();
         SnmpOid[] var5 = new SnmpOid[var2.length];
         TreeMap var6 = new TreeMap(SnmpCachedData.oidComparator);

         for(int var7 = 0; var7 < var2.length; ++var7) {
            JvmThreadInstanceTableMetaImpl.log.debug("", "Making index for thread id [" + var2[var7] + "]");
            SnmpOid var8 = JvmThreadInstanceTableMetaImpl.makeOid(var2[var7]);
            var6.put(var8, var8);
         }

         return new SnmpCachedData(var3, var6);
      }
   }
}
