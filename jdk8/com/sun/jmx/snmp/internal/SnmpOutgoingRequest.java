package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpBadSecurityLevelException;
import com.sun.jmx.snmp.SnmpMsg;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpSecurityException;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpUnknownSecModelException;

public interface SnmpOutgoingRequest {
   SnmpSecurityCache getSecurityCache();

   int encodeMessage(byte[] var1) throws SnmpStatusException, SnmpTooBigException, SnmpSecurityException, SnmpUnknownSecModelException, SnmpBadSecurityLevelException;

   SnmpMsg encodeSnmpPdu(SnmpPdu var1, int var2) throws SnmpStatusException, SnmpTooBigException;

   String printMessage();
}
