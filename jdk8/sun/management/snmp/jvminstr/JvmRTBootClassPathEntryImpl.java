package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import java.io.Serializable;
import sun.management.snmp.jvmmib.JvmRTBootClassPathEntryMBean;

public class JvmRTBootClassPathEntryImpl implements JvmRTBootClassPathEntryMBean, Serializable {
   static final long serialVersionUID = -2282652055235913013L;
   private final String item;
   private final int index;

   public JvmRTBootClassPathEntryImpl(String var1, int var2) {
      this.item = this.validPathElementTC(var1);
      this.index = var2;
   }

   private String validPathElementTC(String var1) {
      return JVM_MANAGEMENT_MIB_IMPL.validPathElementTC(var1);
   }

   public String getJvmRTBootClassPathItem() throws SnmpStatusException {
      return this.item;
   }

   public Integer getJvmRTBootClassPathIndex() throws SnmpStatusException {
      return new Integer(this.index);
   }
}
