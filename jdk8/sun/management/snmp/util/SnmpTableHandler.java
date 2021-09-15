package sun.management.snmp.util;

import com.sun.jmx.snmp.SnmpOid;

public interface SnmpTableHandler {
   Object getData(SnmpOid var1);

   SnmpOid getNext(SnmpOid var1);

   boolean contains(SnmpOid var1);
}
