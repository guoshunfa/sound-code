package sun.management.snmp.jvminstr;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.util.Map;
import sun.management.snmp.jvmmib.JvmRTInputArgsTableMeta;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.SnmpCachedData;
import sun.management.snmp.util.SnmpTableCache;
import sun.management.snmp.util.SnmpTableHandler;

public class JvmRTInputArgsTableMetaImpl extends JvmRTInputArgsTableMeta {
   static final long serialVersionUID = -2083438094888099238L;
   private SnmpTableCache cache = new JvmRTInputArgsTableMetaImpl.JvmRTInputArgsTableCache(this, -1L);
   static final MibLogger log = new MibLogger(JvmRTInputArgsTableMetaImpl.class);

   public JvmRTInputArgsTableMetaImpl(SnmpMib var1, SnmpStandardObjectServer var2) {
      super(var1, var2);
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
            log.debug("*** **** **** **** getNextOid", "next=" + var5);
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
      boolean var2 = log.isDebugOn();
      if (var2) {
         log.debug("getEntry", "oid [" + var1 + "]");
      }

      if (var1 != null && var1.getLength() == 1) {
         Map var3 = JvmContextFactory.getUserData();
         String var4 = var3 == null ? null : "JvmRTInputArgsTable.entry." + var1.toString();
         if (var3 != null) {
            Object var5 = var3.get(var4);
            if (var5 != null) {
               if (var2) {
                  log.debug("getEntry", "Entry is already in the cache");
               }

               return var5;
            }

            if (var2) {
               log.debug("getEntry", "Entry is not in the cache");
            }
         }

         SnmpTableHandler var8 = this.getHandler(var3);
         if (var8 == null) {
            throw new SnmpStatusException(224);
         } else {
            Object var6 = var8.getData(var1);
            if (var6 == null) {
               throw new SnmpStatusException(224);
            } else {
               if (var2) {
                  log.debug("getEntry", "data is a: " + var6.getClass().getName());
               }

               JvmRTInputArgsEntryImpl var7 = new JvmRTInputArgsEntryImpl((String)var6, (int)var1.getOidArc(0));
               if (var3 != null && var7 != null) {
                  var3.put(var4, var7);
               }

               return var7;
            }
         }
      } else {
         if (var2) {
            log.debug("getEntry", "Invalid oid [" + var1 + "]");
         }

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
         var3 = (SnmpTableHandler)var2.get("JvmRTInputArgsTable.handler");
         if (var3 != null) {
            return var3;
         }
      }

      var3 = this.cache.getTableHandler();
      if (var2 != null && var3 != null) {
         var2.put("JvmRTInputArgsTable.handler", var3);
      }

      return var3;
   }

   private static class JvmRTInputArgsTableCache extends SnmpTableCache {
      static final long serialVersionUID = 1693751105464785192L;
      private JvmRTInputArgsTableMetaImpl meta;

      JvmRTInputArgsTableCache(JvmRTInputArgsTableMetaImpl var1, long var2) {
         this.meta = var1;
         this.validity = var2;
      }

      public SnmpTableHandler getTableHandler() {
         Map var1 = JvmContextFactory.getUserData();
         return this.getTableDatas(var1);
      }

      protected SnmpCachedData updateCachedDatas(Object var1) {
         String[] var2 = JvmRuntimeImpl.getInputArguments(var1);
         long var3 = System.currentTimeMillis();
         SnmpOid[] var5 = new SnmpOid[var2.length];

         for(int var6 = 0; var6 < var2.length; ++var6) {
            var5[var6] = new SnmpOid((long)(var6 + 1));
         }

         return new SnmpCachedData(var3, var5, var2);
      }
   }
}
