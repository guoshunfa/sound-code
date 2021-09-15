package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpSecurityException;
import com.sun.jmx.snmp.SnmpSecurityParameters;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;

public interface SnmpSecurityModel extends SnmpModel {
   int generateRequestMsg(SnmpSecurityCache var1, int var2, int var3, int var4, byte var5, int var6, SnmpSecurityParameters var7, byte[] var8, byte[] var9, byte[] var10, int var11, byte[] var12) throws SnmpTooBigException, SnmpStatusException, SnmpSecurityException;

   int generateResponseMsg(SnmpSecurityCache var1, int var2, int var3, int var4, byte var5, int var6, SnmpSecurityParameters var7, byte[] var8, byte[] var9, byte[] var10, int var11, byte[] var12) throws SnmpTooBigException, SnmpStatusException, SnmpSecurityException;

   SnmpSecurityParameters processIncomingRequest(SnmpSecurityCache var1, int var2, int var3, int var4, byte var5, int var6, byte[] var7, byte[] var8, byte[] var9, byte[] var10, byte[] var11, SnmpDecryptedPdu var12) throws SnmpStatusException, SnmpSecurityException;

   SnmpSecurityParameters processIncomingResponse(SnmpSecurityCache var1, int var2, int var3, int var4, byte var5, int var6, byte[] var7, byte[] var8, byte[] var9, byte[] var10, byte[] var11, SnmpDecryptedPdu var12) throws SnmpStatusException, SnmpSecurityException;

   SnmpSecurityCache createSecurityCache();

   void releaseSecurityCache(SnmpSecurityCache var1);
}
