package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import java.io.Serializable;
import sun.management.snmp.jvmmib.JvmRTLibraryPathEntryMBean;

public class JvmRTLibraryPathEntryImpl implements JvmRTLibraryPathEntryMBean, Serializable {
   static final long serialVersionUID = -3322438153507369765L;
   private final String item;
   private final int index;

   public JvmRTLibraryPathEntryImpl(String var1, int var2) {
      this.item = this.validPathElementTC(var1);
      this.index = var2;
   }

   private String validPathElementTC(String var1) {
      return JVM_MANAGEMENT_MIB_IMPL.validPathElementTC(var1);
   }

   public String getJvmRTLibraryPathItem() throws SnmpStatusException {
      return this.item;
   }

   public Integer getJvmRTLibraryPathIndex() throws SnmpStatusException {
      return new Integer(this.index);
   }
}
