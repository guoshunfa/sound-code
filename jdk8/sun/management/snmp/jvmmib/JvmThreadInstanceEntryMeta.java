package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpCounter64;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpString;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibEntry;
import com.sun.jmx.snmp.agent.SnmpMibNode;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
import com.sun.jmx.snmp.agent.SnmpStandardMetaServer;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.io.Serializable;

public class JvmThreadInstanceEntryMeta extends SnmpMibEntry implements Serializable, SnmpStandardMetaServer {
   static final long serialVersionUID = -2015330111801477399L;
   protected JvmThreadInstanceEntryMBean node;
   protected SnmpStandardObjectServer objectserver = null;

   public JvmThreadInstanceEntryMeta(SnmpMib var1, SnmpStandardObjectServer var2) {
      this.objectserver = var2;
      this.varList = new int[10];
      this.varList[0] = 9;
      this.varList[1] = 8;
      this.varList[2] = 7;
      this.varList[3] = 6;
      this.varList[4] = 5;
      this.varList[5] = 4;
      this.varList[6] = 3;
      this.varList[7] = 11;
      this.varList[8] = 2;
      this.varList[9] = 10;
      SnmpMibNode.sort(this.varList);
   }

   public SnmpValue get(long var1, Object var3) throws SnmpStatusException {
      switch((int)var1) {
      case 1:
         throw new SnmpStatusException(224);
      case 2:
         return new SnmpCounter64(this.node.getJvmThreadInstId());
      case 3:
         return new SnmpString(this.node.getJvmThreadInstState());
      case 4:
         return new SnmpCounter64(this.node.getJvmThreadInstBlockCount());
      case 5:
         return new SnmpCounter64(this.node.getJvmThreadInstBlockTimeMs());
      case 6:
         return new SnmpCounter64(this.node.getJvmThreadInstWaitCount());
      case 7:
         return new SnmpCounter64(this.node.getJvmThreadInstWaitTimeMs());
      case 8:
         return new SnmpCounter64(this.node.getJvmThreadInstCpuTimeNs());
      case 9:
         return new SnmpString(this.node.getJvmThreadInstName());
      case 10:
         return new SnmpString(this.node.getJvmThreadInstLockName());
      case 11:
         return new SnmpOid(this.node.getJvmThreadInstLockOwnerPtr());
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
      default:
         throw new SnmpStatusException(17);
      }
   }

   protected void setInstance(JvmThreadInstanceEntryMBean var1) {
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
         return true;
      default:
         return false;
      }
   }

   public boolean isReadable(long var1) {
      switch((int)var1) {
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
         return true;
      default:
         return false;
      }
   }

   public boolean skipVariable(long var1, Object var3, int var4) {
      switch((int)var1) {
      case 1:
         return true;
      case 2:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
         if (var4 == 0) {
            return true;
         }
      case 3:
      default:
         return super.skipVariable(var1, var3, var4);
      }
   }

   public String getAttributeName(long var1) throws SnmpStatusException {
      switch((int)var1) {
      case 1:
         return "JvmThreadInstIndex";
      case 2:
         return "JvmThreadInstId";
      case 3:
         return "JvmThreadInstState";
      case 4:
         return "JvmThreadInstBlockCount";
      case 5:
         return "JvmThreadInstBlockTimeMs";
      case 6:
         return "JvmThreadInstWaitCount";
      case 7:
         return "JvmThreadInstWaitTimeMs";
      case 8:
         return "JvmThreadInstCpuTimeNs";
      case 9:
         return "JvmThreadInstName";
      case 10:
         return "JvmThreadInstLockName";
      case 11:
         return "JvmThreadInstLockOwnerPtr";
      default:
         throw new SnmpStatusException(225);
      }
   }
}
