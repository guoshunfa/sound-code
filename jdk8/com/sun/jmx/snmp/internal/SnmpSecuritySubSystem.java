package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpSecurityException;
import com.sun.jmx.snmp.SnmpSecurityParameters;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpUnknownSecModelException;

public interface SnmpSecuritySubSystem extends SnmpSubSystem {
   SnmpSecurityCache createSecurityCache(int var1) throws SnmpUnknownSecModelException;

   void releaseSecurityCache(int var1, SnmpSecurityCache var2) throws SnmpUnknownSecModelException;

   int generateRequestMsg(SnmpSecurityCache var1, int var2, int var3, int var4, byte var5, int var6, SnmpSecurityParameters var7, byte[] var8, byte[] var9, byte[] var10, int var11, byte[] var12) throws SnmpTooBigException, SnmpStatusException, SnmpSecurityException, SnmpUnknownSecModelException;

   int generateResponseMsg(SnmpSecurityCache var1, int var2, int var3, int var4, byte var5, int var6, SnmpSecurityParameters var7, byte[] var8, byte[] var9, byte[] var10, int var11, byte[] var12) throws SnmpTooBigException, SnmpStatusException, SnmpSecurityException, SnmpUnknownSecModelException;

   SnmpSecurityParameters processIncomingRequest(SnmpSecurityCache var1, int var2, int var3, int var4, byte var5, int var6, byte[] var7, byte[] var8, byte[] var9, byte[] var10, byte[] var11, SnmpDecryptedPdu var12) throws SnmpStatusException, SnmpSecurityException, SnmpUnknownSecModelException;

   SnmpSecurityParameters processIncomingResponse(SnmpSecurityCache var1, int var2, int var3, int var4, byte var5, int var6, byte[] var7, byte[] var8, byte[] var9, byte[] var10, byte[] var11, SnmpDecryptedPdu var12) throws SnmpStatusException, SnmpSecurityException, SnmpUnknownSecModelException;
}
