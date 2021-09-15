package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpParams;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpSecurityParameters;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.mpm.SnmpMsgTranslator;

public interface SnmpMsgProcessingModel extends SnmpModel {
   SnmpOutgoingRequest getOutgoingRequest(SnmpPduFactory var1);

   SnmpIncomingRequest getIncomingRequest(SnmpPduFactory var1);

   SnmpIncomingResponse getIncomingResponse(SnmpPduFactory var1);

   SnmpPdu getRequestPdu(SnmpParams var1, int var2) throws SnmpStatusException;

   int encode(int var1, int var2, int var3, byte var4, int var5, SnmpSecurityParameters var6, byte[] var7, byte[] var8, byte[] var9, int var10, byte[] var11) throws SnmpTooBigException;

   int encodePriv(int var1, int var2, int var3, byte var4, int var5, SnmpSecurityParameters var6, byte[] var7, byte[] var8) throws SnmpTooBigException;

   SnmpDecryptedPdu decode(byte[] var1) throws SnmpStatusException;

   int encode(SnmpDecryptedPdu var1, byte[] var2) throws SnmpTooBigException;

   void setMsgTranslator(SnmpMsgTranslator var1);

   SnmpMsgTranslator getMsgTranslator();
}
