package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import javax.management.ObjectName;

public interface SnmpTableCallbackHandler {
   void addEntryCb(int var1, SnmpOid var2, ObjectName var3, Object var4, SnmpMibTable var5) throws SnmpStatusException;

   void removeEntryCb(int var1, SnmpOid var2, ObjectName var3, Object var4, SnmpMibTable var5) throws SnmpStatusException;
}
