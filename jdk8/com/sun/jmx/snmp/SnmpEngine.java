package com.sun.jmx.snmp;

public interface SnmpEngine {
   int getEngineTime();

   SnmpEngineId getEngineId();

   int getEngineBoots();

   SnmpUsmKeyHandler getUsmKeyHandler();
}
