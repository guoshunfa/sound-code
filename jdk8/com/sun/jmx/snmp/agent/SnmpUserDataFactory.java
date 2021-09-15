package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;

public interface SnmpUserDataFactory {
   Object allocateUserData(SnmpPdu var1) throws SnmpStatusException;

   void releaseUserData(Object var1, SnmpPdu var2) throws SnmpStatusException;
}
