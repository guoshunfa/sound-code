package com.sun.jmx.snmp;

public interface SnmpSecurityParameters {
   int encode(byte[] var1) throws SnmpTooBigException;

   void decode(byte[] var1) throws SnmpStatusException;

   String getPrincipal();
}
