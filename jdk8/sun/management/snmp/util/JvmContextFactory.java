package sun.management.snmp.util;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.ThreadContext;
import com.sun.jmx.snmp.agent.SnmpUserDataFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JvmContextFactory implements SnmpUserDataFactory {
   public Object allocateUserData(SnmpPdu var1) throws SnmpStatusException {
      return Collections.synchronizedMap(new HashMap());
   }

   public void releaseUserData(Object var1, SnmpPdu var2) throws SnmpStatusException {
      ((Map)var1).clear();
   }

   public static Map<Object, Object> getUserData() {
      Object var0 = ThreadContext.get("SnmpUserData");
      return var0 instanceof Map ? (Map)Util.cast(var0) : null;
   }
}
