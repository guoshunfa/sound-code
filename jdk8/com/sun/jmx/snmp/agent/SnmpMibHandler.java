package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;

public interface SnmpMibHandler {
   SnmpMibHandler addMib(SnmpMibAgent var1) throws IllegalArgumentException;

   SnmpMibHandler addMib(SnmpMibAgent var1, SnmpOid[] var2) throws IllegalArgumentException;

   SnmpMibHandler addMib(SnmpMibAgent var1, String var2) throws IllegalArgumentException;

   SnmpMibHandler addMib(SnmpMibAgent var1, String var2, SnmpOid[] var3) throws IllegalArgumentException;

   boolean removeMib(SnmpMibAgent var1);

   boolean removeMib(SnmpMibAgent var1, SnmpOid[] var2);

   boolean removeMib(SnmpMibAgent var1, String var2);

   boolean removeMib(SnmpMibAgent var1, String var2, SnmpOid[] var3);
}
