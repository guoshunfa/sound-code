package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.util.Map;
import sun.management.snmp.jvmmib.JvmMemGCTableMeta;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.SnmpCachedData;
import sun.management.snmp.util.SnmpTableHandler;

public class JvmMemGCTableMetaImpl extends JvmMemGCTableMeta {
   static final long serialVersionUID = 8250461197108867607L;
   private transient JvmMemManagerTableMetaImpl managers = null;
   private static JvmMemGCTableMetaImpl.GCTableFilter filter = new JvmMemGCTableMetaImpl.GCTableFilter();
   static final MibLogger log = new MibLogger(JvmMemGCTableMetaImpl.class);

   public JvmMemGCTableMetaImpl(SnmpMib var1, SnmpStandardObjectServer var2) {
      super(var1, var2);
   }

   private final JvmMemManagerTableMetaImpl getManagers(SnmpMib var1) {
      if (this.managers == null) {
         this.managers = (JvmMemManagerTableMetaImpl)var1.getRegisteredTableMeta("JvmMemManagerTable");
      }

      return this.managers;
   }

   protected SnmpTableHandler getHandler(Object var1) {
      JvmMemManagerTableMetaImpl var2 = this.getManagers(this.theMib);
      return var2.getHandler(var1);
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
            SnmpOid var5 = filter.getNext(var4, var1);
            if (var3) {
               log.debug("getNextOid", "next=" + var5);
            }

            if (var5 == null) {
               throw new SnmpStatusException(224);
            } else {
               return var5;
            }
         }
      } catch (RuntimeException var6) {
         if (var3) {
            log.debug("getNextOid", (Throwable)var6);
         }

         throw var6;
      }
   }

   protected boolean contains(SnmpOid var1, Object var2) {
      SnmpTableHandler var3 = this.getHandler(var2);
      return var3 == null ? false : filter.contains(var3, var1);
   }

   public Object getEntry(SnmpOid var1) throws SnmpStatusException {
      if (var1 == null) {
         throw new SnmpStatusException(224);
      } else {
         Map var2 = JvmContextFactory.getUserData();
         long var3 = var1.getOidArc(0);
         String var5 = var2 == null ? null : "JvmMemGCTable.entry." + var3;
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
            Object var7 = filter.getData(var9, var1);
            if (var7 == null) {
               throw new SnmpStatusException(224);
            } else {
               JvmMemGCEntryImpl var8 = new JvmMemGCEntryImpl((GarbageCollectorMXBean)var7, (int)var3);
               if (var2 != null && var8 != null) {
                  var2.put(var5, var8);
               }

               return var8;
            }
         }
      }
   }

   protected static class GCTableFilter {
      public SnmpOid getNext(SnmpCachedData var1, SnmpOid var2) {
         boolean var3 = JvmMemGCTableMetaImpl.log.isDebugOn();
         int var4 = var2 == null ? -1 : var1.find(var2);
         if (var3) {
            JvmMemGCTableMetaImpl.log.debug("GCTableFilter", "oid=" + var2 + " at insertion=" + var4);
         }

         int var5;
         if (var4 > -1) {
            var5 = var4 + 1;
         } else {
            var5 = -var4 - 1;
         }

         for(; var5 < var1.indexes.length; ++var5) {
            if (var3) {
               JvmMemGCTableMetaImpl.log.debug("GCTableFilter", "next=" + var5);
            }

            Object var6 = var1.datas[var5];
            if (var3) {
               JvmMemGCTableMetaImpl.log.debug("GCTableFilter", "value[" + var5 + "]=" + ((MemoryManagerMXBean)var6).getName());
            }

            if (var6 instanceof GarbageCollectorMXBean) {
               if (var3) {
                  JvmMemGCTableMetaImpl.log.debug("GCTableFilter", ((MemoryManagerMXBean)var6).getName() + " is a  GarbageCollectorMXBean.");
               }

               return var1.indexes[var5];
            }

            if (var3) {
               JvmMemGCTableMetaImpl.log.debug("GCTableFilter", ((MemoryManagerMXBean)var6).getName() + " is not a  GarbageCollectorMXBean: " + var6.getClass().getName());
            }
         }

         return null;
      }

      public SnmpOid getNext(SnmpTableHandler var1, SnmpOid var2) {
         if (var1 instanceof SnmpCachedData) {
            return this.getNext((SnmpCachedData)var1, var2);
         } else {
            SnmpOid var3 = var2;

            do {
               var3 = var1.getNext(var3);
               Object var4 = var1.getData(var3);
               if (var4 instanceof GarbageCollectorMXBean) {
                  return var3;
               }
            } while(var3 != null);

            return null;
         }
      }

      public Object getData(SnmpTableHandler var1, SnmpOid var2) {
         Object var3 = var1.getData(var2);
         return var3 instanceof GarbageCollectorMXBean ? var3 : null;
      }

      public boolean contains(SnmpTableHandler var1, SnmpOid var2) {
         return var1.getData(var2) instanceof GarbageCollectorMXBean;
      }
   }
}
