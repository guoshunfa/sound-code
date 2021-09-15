package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import sun.management.snmp.jvmmib.JvmMemMgrPoolRelEntryMBean;

public class JvmMemMgrPoolRelEntryImpl implements JvmMemMgrPoolRelEntryMBean {
   protected final int JvmMemManagerIndex;
   protected final int JvmMemPoolIndex;
   protected final String mmmName;
   protected final String mpmName;

   public JvmMemMgrPoolRelEntryImpl(String var1, String var2, int var3, int var4) {
      this.JvmMemManagerIndex = var3;
      this.JvmMemPoolIndex = var4;
      this.mmmName = var1;
      this.mpmName = var2;
   }

   public String getJvmMemMgrRelPoolName() throws SnmpStatusException {
      return JVM_MANAGEMENT_MIB_IMPL.validJavaObjectNameTC(this.mpmName);
   }

   public String getJvmMemMgrRelManagerName() throws SnmpStatusException {
      return JVM_MANAGEMENT_MIB_IMPL.validJavaObjectNameTC(this.mmmName);
   }

   public Integer getJvmMemManagerIndex() throws SnmpStatusException {
      return new Integer(this.JvmMemManagerIndex);
   }

   public Integer getJvmMemPoolIndex() throws SnmpStatusException {
      return new Integer(this.JvmMemPoolIndex);
   }
}
