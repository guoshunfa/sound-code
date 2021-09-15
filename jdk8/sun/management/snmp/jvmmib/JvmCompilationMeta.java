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

public class JvmCompilationMeta extends SnmpMibGroup implements Serializable, SnmpStandardMetaServer {
   static final long serialVersionUID = -95492874115033638L;
   protected JvmCompilationMBean node;
   protected SnmpStandardObjectServer objectserver = null;

   public JvmCompilationMeta(SnmpMib var1, SnmpStandardObjectServer var2) {
      this.objectserver = var2;

      try {
         this.registerObject(3L);
         this.registerObject(2L);
         this.registerObject(1L);
      } catch (IllegalAccessException var4) {
         throw new RuntimeException(var4.getMessage());
      }
   }

   public SnmpValue get(long var1, Object var3) throws SnmpStatusException {
      switch((int)var1) {
      case 1:
         return new SnmpString(this.node.getJvmJITCompilerName());
      case 2:
         return new SnmpCounter64(this.node.getJvmJITCompilerTimeMs());
      case 3:
         return new SnmpInt(this.node.getJvmJITCompilerTimeMonitoring());
      default:
         throw new SnmpStatusException(225);
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
      default:
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
      default:
         throw new SnmpStatusException(17);
      }
   }

   protected void setInstance(JvmCompilationMBean var1) {
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
         return true;
      default:
         return false;
      }
   }

   public boolean skipVariable(long var1, Object var3, int var4) {
      switch((int)var1) {
      case 2:
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
         return "JvmJITCompilerName";
      case 2:
         return "JvmJITCompilerTimeMs";
      case 3:
         return "JvmJITCompilerTimeMonitoring";
      default:
         throw new SnmpStatusException(225);
      }
   }

   public boolean isTable(long var1) {
      switch((int)var1) {
      default:
         return false;
      }
   }

   public SnmpMibTable getTable(long var1) {
      return null;
   }

   public void registerTableNodes(SnmpMib var1, MBeanServer var2) {
   }
}
