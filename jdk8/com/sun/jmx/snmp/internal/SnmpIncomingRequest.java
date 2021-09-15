package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpBadSecurityLevelException;
import com.sun.jmx.snmp.SnmpMsg;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpSecurityParameters;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpUnknownSecModelException;
import java.net.InetAddress;

public interface SnmpIncomingRequest {
   SnmpSecurityParameters getSecurityParameters();

   boolean isReport();

   boolean isResponse();

   void noResponse();

   String getPrincipal();

   int getSecurityLevel();

   int getSecurityModel();

   byte[] getContextName();

   byte[] getContextEngineId();

   byte[] getAccessContext();

   int encodeMessage(byte[] var1) throws SnmpTooBigException;

   void decodeMessage(byte[] var1, int var2, InetAddress var3, int var4) throws SnmpStatusException, SnmpUnknownSecModelException, SnmpBadSecurityLevelException;

   SnmpMsg encodeSnmpPdu(SnmpPdu var1, int var2) throws SnmpStatusException, SnmpTooBigException;

   SnmpPdu decodeSnmpPdu() throws SnmpStatusException;

   String printRequestMessage();

   String printResponseMessage();
}
