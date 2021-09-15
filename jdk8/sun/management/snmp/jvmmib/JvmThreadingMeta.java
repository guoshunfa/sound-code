package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpCounter;
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

public class JvmThreadingMeta extends SnmpMibGroup implements Serializable, SnmpStandardMetaServer {
   static final long serialVersionUID = 5223833578005322854L;
   protected JvmThreadingMBean node;
   protected SnmpStandardObjectServer objectserver = null;
   protected JvmThreadInstanceTableMeta tableJvmThreadInstanceTable = null;

   public JvmThreadingMeta(SnmpMib var1, SnmpStandardObjectServer var2) {
      this.objectserver = var2;

      try {
         this.registerObject(6L);
         this.registerObject(5L);
         this.registerObject(4L);
         this.registerObject(3L);
         this.registerObject(2L);
         this.registerObject(1L);
         this.registerObject(10L);
         this.registerObject(7L);
      } catch (IllegalAccessException var4) {
         throw new RuntimeException(var4.getMessage());
      }
   }

   public SnmpValue get(long var1, Object var3) throws SnmpStatusException {
      switch((int)var1) {
      case 1:
         return new SnmpGauge(this.node.getJvmThreadCount());
      case 2:
         return new SnmpGauge(this.node.getJvmThreadDaemonCount());
      case 3:
         return new SnmpCounter(this.node.getJvmThreadPeakCount());
      case 4:
         return new SnmpCounter64(this.node.getJvmThreadTotalStartedCount());
      case 5:
         return new SnmpInt(this.node.getJvmThreadContentionMonitoring());
      case 6:
         return new SnmpInt(this.node.getJvmThreadCpuTimeMonitoring());
      case 7:
         return new SnmpCounter64(this.node.getJvmThreadPeakCountReset());
      case 8:
      case 9:
      default:
         throw new SnmpStatusException(225);
      case 10:
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
         if (var1 instanceof SnmpInt) {
            try {
               this.node.setJvmThreadContentionMonitoring(new EnumJvmThreadContentionMonitoring(((SnmpInt)var1).toInteger()));
            } catch (IllegalArgumentException var7) {
               throw new SnmpStatusException(10);
            }

            return new SnmpInt(this.node.getJvmThreadContentionMonitoring());
         }

         throw new SnmpStatusException(7);
      case 6:
         if (var1 instanceof SnmpInt) {
            try {
               this.node.setJvmThreadCpuTimeMonitoring(new EnumJvmThreadCpuTimeMonitoring(((SnmpInt)var1).toInteger()));
            } catch (IllegalArgumentException var6) {
               throw new SnmpStatusException(10);
            }

            return new SnmpInt(this.node.getJvmThreadCpuTimeMonitoring());
         }

         throw new SnmpStatusException(7);
      case 7:
         if (var1 instanceof SnmpCounter64) {
            this.node.setJvmThreadPeakCountReset(((SnmpCounter64)var1).toLong());
            return new SnmpCounter64(this.node.getJvmThreadPeakCountReset());
         }

         throw new SnmpStatusException(7);
      case 8:
      case 9:
      default:
         throw new SnmpStatusException(17);
      case 10:
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
         if (!(var1 instanceof SnmpInt)) {
            throw new SnmpStatusException(7);
         }

         try {
            this.node.checkJvmThreadContentionMonitoring(new EnumJvmThreadContentionMonitoring(((SnmpInt)var1).toInteger()));
            break;
         } catch (IllegalArgumentException var6) {
            throw new SnmpStatusException(10);
         }
      case 6:
         if (!(var1 instanceof SnmpInt)) {
            throw new SnmpStatusException(7);
         }

         try {
            this.node.checkJvmThreadCpuTimeMonitoring(new EnumJvmThreadCpuTimeMonitoring(((SnmpInt)var1).toInteger()));
            break;
         } catch (IllegalArgumentException var7) {
            throw new SnmpStatusException(10);
         }
      case 7:
         if (!(var1 instanceof SnmpCounter64)) {
            throw new SnmpStatusException(7);
         }

         this.node.checkJvmThreadPeakCountReset(((SnmpCounter64)var1).toLong());
         break;
      case 8:
      case 9:
      default:
         throw new SnmpStatusException(17);
      case 10:
         throw new SnmpStatusException(17);
      }

   }

   protected void setInstance(JvmThreadingMBean var1) {
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
         return true;
      default:
         return false;
      }
   }

   public boolean skipVariable(long var1, Object var3, int var4) {
      switch((int)var1) {
      case 4:
      case 7:
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
         return "JvmThreadCount";
      case 2:
         return "JvmThreadDaemonCount";
      case 3:
         return "JvmThreadPeakCount";
      case 4:
         return "JvmThreadTotalStartedCount";
      case 5:
         return "JvmThreadContentionMonitoring";
      case 6:
         return "JvmThreadCpuTimeMonitoring";
      case 7:
         return "JvmThreadPeakCountReset";
      case 8:
      case 9:
      default:
         throw new SnmpStatusException(225);
      case 10:
         throw new SnmpStatusException(224);
      }
   }

   public boolean isTable(long var1) {
      switch((int)var1) {
      case 10:
         return true;
      default:
         return false;
      }
   }

   public SnmpMibTable getTable(long var1) {
      switch((int)var1) {
      case 10:
         return this.tableJvmThreadInstanceTable;
      default:
         return null;
      }
   }

   public void registerTableNodes(SnmpMib var1, MBeanServer var2) {
      this.tableJvmThreadInstanceTable = this.createJvmThreadInstanceTableMetaNode("JvmThreadInstanceTable", "JvmThreading", var1, var2);
      if (this.tableJvmThreadInstanceTable != null) {
         this.tableJvmThreadInstanceTable.registerEntryNode(var1, var2);
         var1.registerTableMeta("JvmThreadInstanceTable", this.tableJvmThreadInstanceTable);
      }

   }

   protected JvmThreadInstanceTableMeta createJvmThreadInstanceTableMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmThreadInstanceTableMeta(var3, this.objectserver);
   }
}
