package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpCounter64;
import com.sun.jmx.snmp.SnmpGauge;
import com.sun.jmx.snmp.SnmpInt;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibGroup;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
import com.sun.jmx.snmp.agent.SnmpMibTable;
import com.sun.jmx.snmp.agent.SnmpStandardMetaServer;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.io.Serializable;
import javax.management.MBeanServer;

public class JvmMemoryMeta extends SnmpMibGroup implements Serializable, SnmpStandardMetaServer {
   private static final long serialVersionUID = 9047644262627149214L;
   protected JvmMemoryMBean node;
   protected SnmpStandardObjectServer objectserver = null;
   protected JvmMemMgrPoolRelTableMeta tableJvmMemMgrPoolRelTable = null;
   protected JvmMemPoolTableMeta tableJvmMemPoolTable = null;
   protected JvmMemGCTableMeta tableJvmMemGCTable = null;
   protected JvmMemManagerTableMeta tableJvmMemManagerTable = null;

   public JvmMemoryMeta(SnmpMib var1, SnmpStandardObjectServer var2) {
      this.objectserver = var2;

      try {
         this.registerObject(120L);
         this.registerObject(23L);
         this.registerObject(22L);
         this.registerObject(21L);
         this.registerObject(110L);
         this.registerObject(20L);
         this.registerObject(13L);
         this.registerObject(12L);
         this.registerObject(3L);
         this.registerObject(11L);
         this.registerObject(2L);
         this.registerObject(101L);
         this.registerObject(10L);
         this.registerObject(1L);
         this.registerObject(100L);
      } catch (IllegalAccessException var4) {
         throw new RuntimeException(var4.getMessage());
      }
   }

   public SnmpValue get(long var1, Object var3) throws SnmpStatusException {
      switch((int)var1) {
      case 1:
         return new SnmpGauge(this.node.getJvmMemoryPendingFinalCount());
      case 2:
         return new SnmpInt(this.node.getJvmMemoryGCVerboseLevel());
      case 3:
         return new SnmpInt(this.node.getJvmMemoryGCCall());
      case 10:
         return new SnmpCounter64(this.node.getJvmMemoryHeapInitSize());
      case 11:
         return new SnmpCounter64(this.node.getJvmMemoryHeapUsed());
      case 12:
         return new SnmpCounter64(this.node.getJvmMemoryHeapCommitted());
      case 13:
         return new SnmpCounter64(this.node.getJvmMemoryHeapMaxSize());
      case 20:
         return new SnmpCounter64(this.node.getJvmMemoryNonHeapInitSize());
      case 21:
         return new SnmpCounter64(this.node.getJvmMemoryNonHeapUsed());
      case 22:
         return new SnmpCounter64(this.node.getJvmMemoryNonHeapCommitted());
      case 23:
         return new SnmpCounter64(this.node.getJvmMemoryNonHeapMaxSize());
      case 100:
         throw new SnmpStatusException(224);
      case 101:
         throw new SnmpStatusException(224);
      case 110:
         throw new SnmpStatusException(224);
      case 120:
         throw new SnmpStatusException(224);
      default:
         throw new SnmpStatusException(225);
      }
   }

   public SnmpValue set(SnmpValue var1, long var2, Object var4) throws SnmpStatusException {
      switch((int)var2) {
      case 1:
         throw new SnmpStatusException(17);
      case 2:
         if (var1 instanceof SnmpInt) {
            try {
               this.node.setJvmMemoryGCVerboseLevel(new EnumJvmMemoryGCVerboseLevel(((SnmpInt)var1).toInteger()));
            } catch (IllegalArgumentException var7) {
               throw new SnmpStatusException(10);
            }

            return new SnmpInt(this.node.getJvmMemoryGCVerboseLevel());
         }

         throw new SnmpStatusException(7);
      case 3:
         if (var1 instanceof SnmpInt) {
            try {
               this.node.setJvmMemoryGCCall(new EnumJvmMemoryGCCall(((SnmpInt)var1).toInteger()));
            } catch (IllegalArgumentException var6) {
               throw new SnmpStatusException(10);
            }

            return new SnmpInt(this.node.getJvmMemoryGCCall());
         }

         throw new SnmpStatusException(7);
      case 10:
         throw new SnmpStatusException(17);
      case 11:
         throw new SnmpStatusException(17);
      case 12:
         throw new SnmpStatusException(17);
      case 13:
         throw new SnmpStatusException(17);
      case 20:
         throw new SnmpStatusException(17);
      case 21:
         throw new SnmpStatusException(17);
      case 22:
         throw new SnmpStatusException(17);
      case 23:
         throw new SnmpStatusException(17);
      case 100:
         throw new SnmpStatusException(17);
      case 101:
         throw new SnmpStatusException(17);
      case 110:
         throw new SnmpStatusException(17);
      case 120:
         throw new SnmpStatusException(17);
      default:
         throw new SnmpStatusException(17);
      }
   }

   public void check(SnmpValue var1, long var2, Object var4) throws SnmpStatusException {
      switch((int)var2) {
      case 1:
         throw new SnmpStatusException(17);
      case 2:
         if (!(var1 instanceof SnmpInt)) {
            throw new SnmpStatusException(7);
         }

         try {
            this.node.checkJvmMemoryGCVerboseLevel(new EnumJvmMemoryGCVerboseLevel(((SnmpInt)var1).toInteger()));
            break;
         } catch (IllegalArgumentException var6) {
            throw new SnmpStatusException(10);
         }
      case 3:
         if (!(var1 instanceof SnmpInt)) {
            throw new SnmpStatusException(7);
         }

         try {
            this.node.checkJvmMemoryGCCall(new EnumJvmMemoryGCCall(((SnmpInt)var1).toInteger()));
            break;
         } catch (IllegalArgumentException var7) {
            throw new SnmpStatusException(10);
         }
      case 10:
         throw new SnmpStatusException(17);
      case 11:
         throw new SnmpStatusException(17);
      case 12:
         throw new SnmpStatusException(17);
      case 13:
         throw new SnmpStatusException(17);
      case 20:
         throw new SnmpStatusException(17);
      case 21:
         throw new SnmpStatusException(17);
      case 22:
         throw new SnmpStatusException(17);
      case 23:
         throw new SnmpStatusException(17);
      case 100:
         throw new SnmpStatusException(17);
      case 101:
         throw new SnmpStatusException(17);
      case 110:
         throw new SnmpStatusException(17);
      case 120:
         throw new SnmpStatusException(17);
      default:
         throw new SnmpStatusException(17);
      }

   }

   protected void setInstance(JvmMemoryMBean var1) {
      this.node = var1;
   }

   public void get(SnmpMibSubRequest var1, int var2) throws SnmpStatusException {
      this.objectserver.get(this, var1, var2);
   }

   public void set(SnmpMibSubRequest var1, int var2) throws SnmpStatusException {
      this.objectserver.set(this, var1, var2);
   }

   public void check(SnmpMibSubRequest var1, int var2) throws SnmpStatusException {
      this.objectserver.check(this, var1, var2);
   }

   public boolean isVariable(long var1) {
      switch((int)var1) {
      case 1:
      case 2:
      case 3:
      case 10:
      case 11:
      case 12:
      case 13:
      case 20:
      case 21:
      case 22:
      case 23:
         return true;
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      default:
         return false;
      }
   }

   public boolean isReadable(long var1) {
      switch((int)var1) {
      case 1:
      case 2:
      case 3:
      case 10:
      case 11:
      case 12:
      case 13:
      case 20:
      case 21:
      case 22:
      case 23:
         return true;
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      default:
         return false;
      }
   }

   public boolean skipVariable(long var1, Object var3, int var4) {
      switch((int)var1) {
      case 10:
      case 11:
      case 12:
      case 13:
      case 20:
      case 21:
      case 22:
      case 23:
         if (var4 == 0) {
            return true;
         }
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      default:
         return super.skipVariable(var1, var3, var4);
      }
   }

   public String getAttributeName(long var1) throws SnmpStatusException {
      switch((int)var1) {
      case 1:
         return "JvmMemoryPendingFinalCount";
      case 2:
         return "JvmMemoryGCVerboseLevel";
      case 3:
         return "JvmMemoryGCCall";
      case 10:
         return "JvmMemoryHeapInitSize";
      case 11:
         return "JvmMemoryHeapUsed";
      case 12:
         return "JvmMemoryHeapCommitted";
      case 13:
         return "JvmMemoryHeapMaxSize";
      case 20:
         return "JvmMemoryNonHeapInitSize";
      case 21:
         return "JvmMemoryNonHeapUsed";
      case 22:
         return "JvmMemoryNonHeapCommitted";
      case 23:
         return "JvmMemoryNonHeapMaxSize";
      case 100:
         throw new SnmpStatusException(224);
      case 101:
         throw new SnmpStatusException(224);
      case 110:
         throw new SnmpStatusException(224);
      case 120:
         throw new SnmpStatusException(224);
      default:
         throw new SnmpStatusException(225);
      }
   }

   public boolean isTable(long var1) {
      switch((int)var1) {
      case 100:
         return true;
      case 101:
         return true;
      case 110:
         return true;
      case 120:
         return true;
      default:
         return false;
      }
   }

   public SnmpMibTable getTable(long var1) {
      switch((int)var1) {
      case 100:
         return this.tableJvmMemManagerTable;
      case 101:
         return this.tableJvmMemGCTable;
      case 110:
         return this.tableJvmMemPoolTable;
      case 120:
         return this.tableJvmMemMgrPoolRelTable;
      default:
         return null;
      }
   }

   public void registerTableNodes(SnmpMib var1, MBeanServer var2) {
      this.tableJvmMemMgrPoolRelTable = this.createJvmMemMgrPoolRelTableMetaNode("JvmMemMgrPoolRelTable", "JvmMemory", var1, var2);
      if (this.tableJvmMemMgrPoolRelTable != null) {
         this.tableJvmMemMgrPoolRelTable.registerEntryNode(var1, var2);
         var1.registerTableMeta("JvmMemMgrPoolRelTable", this.tableJvmMemMgrPoolRelTable);
      }

      this.tableJvmMemPoolTable = this.createJvmMemPoolTableMetaNode("JvmMemPoolTable", "JvmMemory", var1, var2);
      if (this.tableJvmMemPoolTable != null) {
         this.tableJvmMemPoolTable.registerEntryNode(var1, var2);
         var1.registerTableMeta("JvmMemPoolTable", this.tableJvmMemPoolTable);
      }

      this.tableJvmMemGCTable = this.createJvmMemGCTableMetaNode("JvmMemGCTable", "JvmMemory", var1, var2);
      if (this.tableJvmMemGCTable != null) {
         this.tableJvmMemGCTable.registerEntryNode(var1, var2);
         var1.registerTableMeta("JvmMemGCTable", this.tableJvmMemGCTable);
      }

      this.tableJvmMemManagerTable = this.createJvmMemManagerTableMetaNode("JvmMemManagerTable", "JvmMemory", var1, var2);
      if (this.tableJvmMemManagerTable != null) {
         this.tableJvmMemManagerTable.registerEntryNode(var1, var2);
         var1.registerTableMeta("JvmMemManagerTable", this.tableJvmMemManagerTable);
      }

   }

   protected JvmMemMgrPoolRelTableMeta createJvmMemMgrPoolRelTableMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmMemMgrPoolRelTableMeta(var3, this.objectserver);
   }

   protected JvmMemPoolTableMeta createJvmMemPoolTableMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmMemPoolTableMeta(var3, this.objectserver);
   }

   protected JvmMemGCTableMeta createJvmMemGCTableMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmMemGCTableMeta(var3, this.objectserver);
   }

   protected JvmMemManagerTableMeta createJvmMemManagerTableMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmMemManagerTableMeta(var3, this.objectserver);
   }
}
