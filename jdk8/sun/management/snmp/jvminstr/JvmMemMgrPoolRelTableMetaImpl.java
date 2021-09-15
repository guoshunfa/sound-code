package sun.management.snmp.jvminstr;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.io.Serializable;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import sun.management.snmp.jvmmib.JvmMemMgrPoolRelTableMeta;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.SnmpCachedData;
import sun.management.snmp.util.SnmpTableCache;
import sun.management.snmp.util.SnmpTableHandler;

public class JvmMemMgrPoolRelTableMetaImpl extends JvmMemMgrPoolRelTableMeta implements Serializable {
   static final long serialVersionUID = 1896509775012355443L;
   protected SnmpTableCache cache;
   private transient JvmMemManagerTableMetaImpl managers = null;
   private transient JvmMemPoolTableMetaImpl pools = null;
   static final MibLogger log = new MibLogger(JvmMemMgrPoolRelTableMetaImpl.class);

   public JvmMemMgrPoolRelTableMetaImpl(SnmpMib var1, SnmpStandardObjectServer var2) {
      super(var1, var2);
      this.cache = new JvmMemMgrPoolRelTableMetaImpl.JvmMemMgrPoolRelTableCache(this, ((JVM_MANAGEMENT_MIB_IMPL)var1).validity());
   }

   private final JvmMemManagerTableMetaImpl getManagers(SnmpMib var1) {
      if (this.managers == null) {
         this.managers = (JvmMemManagerTableMetaImpl)var1.getRegisteredTableMeta("JvmMemManagerTable");
      }

      return this.managers;
   }

   private final JvmMemPoolTableMetaImpl getPools(SnmpMib var1) {
      if (this.pools == null) {
         this.pools = (JvmMemPoolTableMetaImpl)var1.getRegisteredTableMeta("JvmMemPoolTable");
      }

      return this.pools;
   }

   protected SnmpTableHandler getManagerHandler(Object var1) {
      JvmMemManagerTableMetaImpl var2 = this.getManagers(this.theMib);
      return var2.getHandler(var1);
   }

   protected SnmpTableHandler getPoolHandler(Object var1) {
      JvmMemPoolTableMetaImpl var2 = this.getPools(this.theMib);
      return var2.getHandler(var1);
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
      if (var1 != null && var1.getLength() >= 2) {
         Map var2 = JvmContextFactory.getUserData();
         long var3 = var1.getOidArc(0);
         long var5 = var1.getOidArc(1);
         String var7 = var2 == null ? null : "JvmMemMgrPoolRelTable.entry." + var3 + "." + var5;
         if (var2 != null) {
            Object var8 = var2.get(var7);
            if (var8 != null) {
               return var8;
            }
         }

         SnmpTableHandler var11 = this.getHandler(var2);
         if (var11 == null) {
            throw new SnmpStatusException(224);
         } else {
            Object var9 = var11.getData(var1);
            if (!(var9 instanceof JvmMemMgrPoolRelEntryImpl)) {
               throw new SnmpStatusException(224);
            } else {
               JvmMemMgrPoolRelEntryImpl var10 = (JvmMemMgrPoolRelEntryImpl)var9;
               if (var2 != null && var10 != null) {
                  var2.put(var7, var10);
               }

               return var10;
            }
         }
      } else {
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
         var3 = (SnmpTableHandler)var2.get("JvmMemMgrPoolRelTable.handler");
         if (var3 != null) {
            return var3;
         }
      }

      var3 = this.cache.getTableHandler();
      if (var2 != null && var3 != null) {
         var2.put("JvmMemMgrPoolRelTable.handler", var3);
      }

      return var3;
   }

   private static class JvmMemMgrPoolRelTableCache extends SnmpTableCache {
      static final long serialVersionUID = 6059937161990659184L;
      private final JvmMemMgrPoolRelTableMetaImpl meta;

      JvmMemMgrPoolRelTableCache(JvmMemMgrPoolRelTableMetaImpl var1, long var2) {
         this.validity = var2;
         this.meta = var1;
      }

      public SnmpTableHandler getTableHandler() {
         Map var1 = JvmContextFactory.getUserData();
         return this.getTableDatas(var1);
      }

      private static Map<String, SnmpOid> buildPoolIndexMap(SnmpTableHandler var0) {
         if (var0 instanceof SnmpCachedData) {
            return buildPoolIndexMap((SnmpCachedData)var0);
         } else {
            HashMap var1 = new HashMap();
            SnmpOid var2 = null;

            while((var2 = var0.getNext(var2)) != null) {
               MemoryPoolMXBean var3 = (MemoryPoolMXBean)var0.getData(var2);
               if (var3 != null) {
                  String var4 = var3.getName();
                  if (var4 != null) {
                     var1.put(var4, var2);
                  }
               }
            }

            return var1;
         }
      }

      private static Map<String, SnmpOid> buildPoolIndexMap(SnmpCachedData var0) {
         if (var0 == null) {
            return Collections.emptyMap();
         } else {
            SnmpOid[] var1 = var0.indexes;
            Object[] var2 = var0.datas;
            int var3 = var1.length;
            HashMap var4 = new HashMap(var3);

            for(int var5 = 0; var5 < var3; ++var5) {
               SnmpOid var6 = var1[var5];
               if (var6 != null) {
                  MemoryPoolMXBean var7 = (MemoryPoolMXBean)var2[var5];
                  if (var7 != null) {
                     String var8 = var7.getName();
                     if (var8 != null) {
                        var4.put(var8, var6);
                     }
                  }
               }
            }

            return var4;
         }
      }

      protected SnmpCachedData updateCachedDatas(Object var1) {
         SnmpTableHandler var2 = this.meta.getManagerHandler(var1);
         SnmpTableHandler var3 = this.meta.getPoolHandler(var1);
         long var4 = System.currentTimeMillis();
         Map var6 = buildPoolIndexMap(var3);
         TreeMap var7 = new TreeMap(SnmpCachedData.oidComparator);
         this.updateTreeMap(var7, var1, var2, var3, var6);
         return new SnmpCachedData(var4, var7);
      }

      protected String[] getMemoryPools(Object var1, MemoryManagerMXBean var2, long var3) {
         String var5 = "JvmMemManager." + var3 + ".getMemoryPools";
         String[] var6 = null;
         if (var1 instanceof Map) {
            var6 = (String[])((String[])((Map)var1).get(var5));
            if (var6 != null) {
               return var6;
            }
         }

         if (var2 != null) {
            var6 = var2.getMemoryPoolNames();
         }

         if (var6 != null && var1 instanceof Map) {
            Map var7 = (Map)Util.cast(var1);
            var7.put(var5, var6);
         }

         return var6;
      }

      protected void updateTreeMap(TreeMap<SnmpOid, Object> var1, Object var2, MemoryManagerMXBean var3, SnmpOid var4, Map<String, SnmpOid> var5) {
         long var6;
         try {
            var6 = var4.getOidArc(0);
         } catch (SnmpStatusException var17) {
            JvmMemMgrPoolRelTableMetaImpl.log.debug("updateTreeMap", "Bad MemoryManager OID index: " + var4);
            JvmMemMgrPoolRelTableMetaImpl.log.debug("updateTreeMap", (Throwable)var17);
            return;
         }

         String[] var8 = this.getMemoryPools(var2, var3, var6);
         if (var8 != null && var8.length >= 1) {
            String var9 = var3.getName();

            for(int var10 = 0; var10 < var8.length; ++var10) {
               String var11 = var8[var10];
               if (var11 != null) {
                  SnmpOid var12 = (SnmpOid)var5.get(var11);
                  if (var12 != null) {
                     long var13;
                     try {
                        var13 = var12.getOidArc(0);
                     } catch (SnmpStatusException var18) {
                        JvmMemMgrPoolRelTableMetaImpl.log.debug("updateTreeMap", "Bad MemoryPool OID index: " + var12);
                        JvmMemMgrPoolRelTableMetaImpl.log.debug("updateTreeMap", (Throwable)var18);
                        continue;
                     }

                     long[] var15 = new long[]{var6, var13};
                     SnmpOid var16 = new SnmpOid(var15);
                     var1.put(var16, new JvmMemMgrPoolRelEntryImpl(var9, var11, (int)var6, (int)var13));
                  }
               }
            }

         }
      }

      protected void updateTreeMap(TreeMap<SnmpOid, Object> var1, Object var2, SnmpTableHandler var3, SnmpTableHandler var4, Map<String, SnmpOid> var5) {
         if (var3 instanceof SnmpCachedData) {
            this.updateTreeMap(var1, var2, (SnmpCachedData)var3, var4, var5);
         } else {
            SnmpOid var6 = null;

            while((var6 = var3.getNext(var6)) != null) {
               MemoryManagerMXBean var7 = (MemoryManagerMXBean)var3.getData(var6);
               if (var7 != null) {
                  this.updateTreeMap(var1, var2, var7, var6, var5);
               }
            }

         }
      }

      protected void updateTreeMap(TreeMap<SnmpOid, Object> var1, Object var2, SnmpCachedData var3, SnmpTableHandler var4, Map<String, SnmpOid> var5) {
         SnmpOid[] var6 = var3.indexes;
         Object[] var7 = var3.datas;
         int var8 = var6.length;

         for(int var9 = var8 - 1; var9 > -1; --var9) {
            MemoryManagerMXBean var10 = (MemoryManagerMXBean)var7[var9];
            if (var10 != null) {
               this.updateTreeMap(var1, var2, var10, var6[var9], var5);
            }
         }

      }
   }
}
