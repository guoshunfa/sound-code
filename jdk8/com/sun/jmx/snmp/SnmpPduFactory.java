package com.sun.jmx.snmp;

public interface SnmpPduFactory {
   SnmpPdu decodeSnmpPdu(SnmpMsg var1) throws SnmpStatusException;

   SnmpMsg encodeSnmpPdu(SnmpPdu var1, int var2) throws SnmpStatusException, SnmpTooBigException;
}
