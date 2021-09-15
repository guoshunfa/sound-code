package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpMsg;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpSecurityException;
import com.sun.jmx.snmp.SnmpSecurityParameters;
import com.sun.jmx.snmp.SnmpStatusException;
import java.net.InetAddress;

public interface SnmpIncomingResponse {
   InetAddress getAddress();

   int getPort();

   SnmpSecurityParameters getSecurityParameters();

   void setSecurityCache(SnmpSecurityCache var1);

   int getSecurityLevel();

   int getSecurityModel();

   byte[] getContextName();

   SnmpMsg decodeMessage(byte[] var1, int var2, InetAddress var3, int var4) throws SnmpStatusException, SnmpSecurityException;

   SnmpPdu decodeSnmpPdu() throws SnmpStatusException;

   int getRequestId(byte[] var1) throws SnmpStatusException;

   String printMessage();
}
