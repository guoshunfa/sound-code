package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpCounter64;
import com.sun.jmx.snmp.SnmpInt;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpString;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibGroup;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
import com.sun.jmx.snmp.agent.SnmpMibTable;
import com.sun.jmx.snmp.agent.SnmpStandardMetaServer;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.io.Serializable;
import javax.management.MBeanServer;

public class JvmRuntimeMeta extends SnmpMibGroup implements Serializable, SnmpStandardMetaServer {
   static final long serialVersionUID = 1994595220765880109L;
   protected JvmRuntimeMBean node;
   protected SnmpStandardObjectServer objectserver = null;
   protected JvmRTLibraryPathTableMeta tableJvmRTLibraryPathTable = null;
   protected JvmRTClassPathTableMeta tableJvmRTClassPathTable = null;
   protected JvmRTBootClassPathTableMeta tableJvmRTBootClassPathTable = null;
   protected JvmRTInputArgsTableMeta tableJvmRTInputArgsTable = null;

   public JvmRuntimeMeta(SnmpMib var1, SnmpStandardObjectServer var2) {
      this.objectserver = var2;

      try {
         this.registerObject(23L);
         this.registerObject(22L);
         this.registerObject(21L);
         this.registerObject(9L);
         this.registerObject(20L);
         this.registerObject(8L);
         this.registerObject(7L);
         this.registerObject(6L);
         this.registerObject(5L);
         this.registerObject(4L);
         this.registerObject(3L);
         this.registerObject(12L);
         this.registerObject(11L);
         this.registerObject(2L);
         this.registerObject(1L);
         this.registerObject(10L);
      } catch (IllegalAccessException var4) {
         throw new RuntimeException(var4.getMessage());
      }
   }

   public SnmpValue get(long var1, Object var3) throws SnmpStatusException {
      switch((int)var1) {
      case 1:
         return new SnmpString(this.node.getJvmRTName());
      case 2:
         return new SnmpString(this.node.getJvmRTVMName());
      case 3:
         return new SnmpString(this.node.getJvmRTVMVendor());
      case 4:
         return new SnmpString(this.node.getJvmRTVMVersion());
      case 5:
         return new SnmpString(this.node.getJvmRTSpecName());
      case 6:
         return new SnmpString(this.node.getJvmRTSpecVendor());
      case 7:
         return new SnmpString(this.node.getJvmRTSpecVersion());
      case 8:
         return new SnmpString(this.node.getJvmRTManagementSpecVersion());
      case 9:
         return new SnmpInt(this.node.getJvmRTBootClassPathSupport());
      case 10:
         return new SnmpInt(this.node.getJvmRTInputArgsCount());
      case 11:
         return new SnmpCounter64(this.node.getJvmRTUptimeMs());
      case 12:
         return new SnmpCounter64(this.node.getJvmRTStartTimeMs());
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      default:
         throw new SnmpStatusException(225);
      case 20:
         throw new SnmpStatusException(224);
      case 21:
         throw new SnmpStatusException(224);
      case 22:
         throw new SnmpStatusException(224);
      case 23:
         throw new SnmpStatusException(224);
      }
   }

   public SnmpValue set(SnmpValue var1, long var2, Object var4) throws SnmpStatusException {
      switch((int)var2) {
      case 1:
         throw new SnmpStatusException(17);
      case 2:
         throw new SnmpStatusException(17);
      case 3:
         throw new SnmpStatusException(17);
      case 4:
         throw new SnmpStatusException(17);
      case 5:
         throw new SnmpStatusException(17);
      case 6:
         throw new SnmpStatusException(17);
      case 7:
         throw new SnmpStatusException(17);
      case 8:
         throw new SnmpStatusException(17);
      case 9:
         throw new SnmpStatusException(17);
      case 10:
         throw new SnmpStatusException(17);
      case 11:
         throw new SnmpStatusException(17);
      case 12:
         throw new SnmpStatusException(17);
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      default:
         throw new SnmpStatusException(17);
      case 20:
         throw new SnmpStatusException(17);
      case 21:
         throw new SnmpStatusException(17);
      case 22:
         throw new SnmpStatusException(17);
      case 23:
         throw new SnmpStatusException(17);
      }
   }

   public void check(SnmpValue var1, long var2, Object var4) throws SnmpStatusException {
      switch((int)var2) {
      case 1:
         throw new SnmpStatusException(17);
      case 2:
         throw new SnmpStatusException(17);
      case 3:
         throw new SnmpStatusException(17);
      case 4:
         throw new SnmpStatusException(17);
      case 5:
         throw new SnmpStatusException(17);
      case 6:
         throw new SnmpStatusException(17);
      case 7:
         throw new SnmpStatusException(17);
      case 8:
         throw new SnmpStatusException(17);
      case 9:
         throw new SnmpStatusException(17);
      case 10:
         throw new SnmpStatusException(17);
      case 11:
         throw new SnmpStatusException(17);
      case 12:
         throw new SnmpStatusException(17);
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      default:
         throw new SnmpStatusException(17);
      case 20:
         throw new SnmpStatusException(17);
      case 21:
         throw new SnmpStatusException(17);
      case 22:
         throw new SnmpStatusException(17);
      case 23:
         throw new SnmpStatusException(17);
      }
   }

   protected void setInstance(JvmRuntimeMBean var1) {
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
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
         return true;
      default:
         return false;
      }
   }

   public boolean isReadable(long var1) {
      switch((int)var1) {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
         return true;
      default:
         return false;
      }
   }

   public boolean skipVariable(long var1, Object var3, int var4) {
      switch((int)var1) {
      case 11:
      case 12:
         if (var4 == 0) {
            return true;
         }
      default:
         return super.skipVariable(var1, var3, var4);
      }
   }

   public String getAttributeName(long var1) throws SnmpStatusException {
      switch((int)var1) {
      case 1:
         return "JvmRTName";
      case 2:
         return "JvmRTVMName";
      case 3:
         return "JvmRTVMVendor";
      case 4:
         return "JvmRTVMVersion";
      case 5:
         return "JvmRTSpecName";
      case 6:
         return "JvmRTSpecVendor";
      case 7:
         return "JvmRTSpecVersion";
      case 8:
         return "JvmRTManagementSpecVersion";
      case 9:
         return "JvmRTBootClassPathSupport";
      case 10:
         return "JvmRTInputArgsCount";
      case 11:
         return "JvmRTUptimeMs";
      case 12:
         return "JvmRTStartTimeMs";
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      default:
         throw new SnmpStatusException(225);
      case 20:
         throw new SnmpStatusException(224);
      case 21:
         throw new SnmpStatusException(224);
      case 22:
         throw new SnmpStatusException(224);
      case 23:
         throw new SnmpStatusException(224);
      }
   }

   public boolean isTable(long var1) {
      switch((int)var1) {
      case 20:
         return true;
      case 21:
         return true;
      case 22:
         return true;
      case 23:
         return true;
      default:
         return false;
      }
   }

   public SnmpMibTable getTable(long var1) {
      switch((int)var1) {
      case 20:
         return this.tableJvmRTInputArgsTable;
      case 21:
         return this.tableJvmRTBootClassPathTable;
      case 22:
         return this.tableJvmRTClassPathTable;
      case 23:
         return this.tableJvmRTLibraryPathTable;
      default:
         return null;
      }
   }

   public void registerTableNodes(SnmpMib var1, MBeanServer var2) {
      this.tableJvmRTLibraryPathTable = this.createJvmRTLibraryPathTableMetaNode("JvmRTLibraryPathTable", "JvmRuntime", var1, var2);
      if (this.tableJvmRTLibraryPathTable != null) {
         this.tableJvmRTLibraryPathTable.registerEntryNode(var1, var2);
         var1.registerTableMeta("JvmRTLibraryPathTable", this.tableJvmRTLibraryPathTable);
      }

      this.tableJvmRTClassPathTable = this.createJvmRTClassPathTableMetaNode("JvmRTClassPathTable", "JvmRuntime", var1, var2);
      if (this.tableJvmRTClassPathTable != null) {
         this.tableJvmRTClassPathTable.registerEntryNode(var1, var2);
         var1.registerTableMeta("JvmRTClassPathTable", this.tableJvmRTClassPathTable);
      }

      this.tableJvmRTBootClassPathTable = this.createJvmRTBootClassPathTableMetaNode("JvmRTBootClassPathTable", "JvmRuntime", var1, var2);
      if (this.tableJvmRTBootClassPathTable != null) {
         this.tableJvmRTBootClassPathTable.registerEntryNode(var1, var2);
         var1.registerTableMeta("JvmRTBootClassPathTable", this.tableJvmRTBootClassPathTable);
      }

      this.tableJvmRTInputArgsTable = this.createJvmRTInputArgsTableMetaNode("JvmRTInputArgsTable", "JvmRuntime", var1, var2);
      if (this.tableJvmRTInputArgsTable != null) {
         this.tableJvmRTInputArgsTable.registerEntryNode(var1, var2);
         var1.registerTableMeta("JvmRTInputArgsTable", this.tableJvmRTInputArgsTable);
      }

   }

   protected JvmRTLibraryPathTableMeta createJvmRTLibraryPathTableMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmRTLibraryPathTableMeta(var3, this.objectserver);
   }

   protected JvmRTClassPathTableMeta createJvmRTClassPathTableMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmRTClassPathTableMeta(var3, this.objectserver);
   }

   protected JvmRTBootClassPathTableMeta createJvmRTBootClassPathTableMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmRTBootClassPathTableMeta(var3, this.objectserver);
   }

   protected JvmRTInputArgsTableMeta createJvmRTInputArgsTableMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmRTInputArgsTableMeta(var3, this.objectserver);
   }
}
