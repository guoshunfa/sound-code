package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import javax.management.MBeanServer;
import sun.management.snmp.jvmmib.JvmRTBootClassPathTableMeta;
import sun.management.snmp.jvmmib.JvmRTClassPathTableMeta;
import sun.management.snmp.jvmmib.JvmRTInputArgsTableMeta;
import sun.management.snmp.jvmmib.JvmRTLibraryPathTableMeta;
import sun.management.snmp.jvmmib.JvmRuntimeMeta;

public class JvmRuntimeMetaImpl extends JvmRuntimeMeta {
   static final long serialVersionUID = -6570428414857608618L;

   public JvmRuntimeMetaImpl(SnmpMib var1, SnmpStandardObjectServer var2) {
      super(var1, var2);
   }

   protected JvmRTInputArgsTableMeta createJvmRTInputArgsTableMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmRTInputArgsTableMetaImpl(var3, this.objectserver);
   }

   protected JvmRTLibraryPathTableMeta createJvmRTLibraryPathTableMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmRTLibraryPathTableMetaImpl(var3, this.objectserver);
   }

   protected JvmRTClassPathTableMeta createJvmRTClassPathTableMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmRTClassPathTableMetaImpl(var3, this.objectserver);
   }

   protected JvmRTBootClassPathTableMeta createJvmRTBootClassPathTableMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmRTBootClassPathTableMetaImpl(var3, this.objectserver);
   }
}
