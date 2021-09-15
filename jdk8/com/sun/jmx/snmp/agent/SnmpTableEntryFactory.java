package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;

public interface SnmpTableEntryFactory extends SnmpTableCallbackHandler {
   void createNewEntry(SnmpMibSubRequest var1, SnmpOid var2, int var3, SnmpMibTable var4) throws SnmpStatusException;
}
