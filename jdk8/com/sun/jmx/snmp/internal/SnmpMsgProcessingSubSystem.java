package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpParams;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpSecurityParameters;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpUnknownMsgProcModelException;

public interface SnmpMsgProcessingSubSystem extends SnmpSubSystem {
   void setSecuritySubSystem(SnmpSecuritySubSystem var1);

   SnmpSecuritySubSystem getSecuritySubSystem();

   SnmpIncomingRequest getIncomingRequest(int var1, SnmpPduFactory var2) throws SnmpUnknownMsgProcModelException;

   SnmpOutgoingRequest getOutgoingRequest(int var1, SnmpPduFactory var2) throws SnmpUnknownMsgProcModelException;

   SnmpPdu getRequestPdu(int var1, SnmpParams var2, int var3) throws SnmpUnknownMsgProcModelException, SnmpStatusException;

   SnmpIncomingResponse getIncomingResponse(int var1, SnmpPduFactory var2) throws SnmpUnknownMsgProcModelException;

   int encode(int var1, int var2, int var3, byte var4, int var5, SnmpSecurityParameters var6, byte[] var7, byte[] var8, byte[] var9, int var10, byte[] var11) throws SnmpTooBigException, SnmpUnknownMsgProcModelException;

   int encodePriv(int var1, int var2, int var3, byte var4, int var5, SnmpSecurityParameters var6, byte[] var7, byte[] var8) throws SnmpTooBigException, SnmpUnknownMsgProcModelException;

   SnmpDecryptedPdu decode(int var1, byte[] var2) throws SnmpStatusException, SnmpUnknownMsgProcModelException;

   int encode(int var1, SnmpDecryptedPdu var2, byte[] var3) throws SnmpTooBigException, SnmpUnknownMsgProcModelException;
}
