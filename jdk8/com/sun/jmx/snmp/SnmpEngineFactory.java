package com.sun.jmx.snmp;

public interface SnmpEngineFactory {
   SnmpEngine createEngine(SnmpEngineParameters var1);

   SnmpEngine createEngine(SnmpEngineParameters var1, InetAddressAcl var2);
}
