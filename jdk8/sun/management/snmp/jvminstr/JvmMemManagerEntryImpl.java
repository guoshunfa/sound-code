package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import java.lang.management.MemoryManagerMXBean;
import sun.management.snmp.jvmmib.EnumJvmMemManagerState;
import sun.management.snmp.jvmmib.JvmMemManagerEntryMBean;

public class JvmMemManagerEntryImpl implements JvmMemManagerEntryMBean {
   protected final int JvmMemManagerIndex;
   protected MemoryManagerMXBean manager;
   private static final EnumJvmMemManagerState JvmMemManagerStateValid = new EnumJvmMemManagerState("valid");
   private static final EnumJvmMemManagerState JvmMemManagerStateInvalid = new EnumJvmMemManagerState("invalid");

   public JvmMemManagerEntryImpl(MemoryManagerMXBean var1, int var2) {
      this.manager = var1;
      this.JvmMemManagerIndex = var2;
   }

   public String getJvmMemManagerName() throws SnmpStatusException {
      return JVM_MANAGEMENT_MIB_IMPL.validJavaObjectNameTC(this.manager.getName());
   }

   public Integer getJvmMemManagerIndex() throws SnmpStatusException {
      return new Integer(this.JvmMemManagerIndex);
   }

   public EnumJvmMemManagerState getJvmMemManagerState() throws SnmpStatusException {
      return this.manager.isValid() ? JvmMemManagerStateValid : JvmMemManagerStateInvalid;
   }
}
