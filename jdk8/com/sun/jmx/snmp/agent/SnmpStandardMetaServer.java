package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;

public interface SnmpStandardMetaServer {
   SnmpValue get(long var1, Object var3) throws SnmpStatusException;

   SnmpValue set(SnmpValue var1, long var2, Object var4) throws SnmpStatusException;

   void check(SnmpValue var1, long var2, Object var4) throws SnmpStatusException;
}
