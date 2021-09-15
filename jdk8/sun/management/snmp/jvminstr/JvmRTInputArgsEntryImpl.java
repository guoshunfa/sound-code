package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import java.io.Serializable;
import sun.management.snmp.jvmmib.JvmRTInputArgsEntryMBean;

public class JvmRTInputArgsEntryImpl implements JvmRTInputArgsEntryMBean, Serializable {
   static final long serialVersionUID = 1000306518436503395L;
   private final String item;
   private final int index;

   public JvmRTInputArgsEntryImpl(String var1, int var2) {
      this.item = this.validArgValueTC(var1);
      this.index = var2;
   }

   private String validArgValueTC(String var1) {
      return JVM_MANAGEMENT_MIB_IMPL.validArgValueTC(var1);
   }

   public String getJvmRTInputArgsItem() throws SnmpStatusException {
      return this.item;
   }

   public Integer getJvmRTInputArgsIndex() throws SnmpStatusException {
      return new Integer(this.index);
   }
}
