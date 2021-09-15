package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import java.io.Serializable;
import sun.management.snmp.jvmmib.JvmRTClassPathEntryMBean;

public class JvmRTClassPathEntryImpl implements JvmRTClassPathEntryMBean, Serializable {
   static final long serialVersionUID = 8524792845083365742L;
   private final String item;
   private final int index;

   public JvmRTClassPathEntryImpl(String var1, int var2) {
      this.item = this.validPathElementTC(var1);
      this.index = var2;
   }

   private String validPathElementTC(String var1) {
      return JVM_MANAGEMENT_MIB_IMPL.validPathElementTC(var1);
   }

   public String getJvmRTClassPathItem() throws SnmpStatusException {
      return this.item;
   }

   public Integer getJvmRTClassPathIndex() throws SnmpStatusException {
      return new Integer(this.index);
   }
}
