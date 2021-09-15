package com.sun.jmx.snmp.mpm;

import com.sun.jmx.snmp.SnmpMsg;
import com.sun.jmx.snmp.SnmpSecurityParameters;

public interface SnmpMsgTranslator {
   int getMsgId(SnmpMsg var1);

   int getMsgMaxSize(SnmpMsg var1);

   byte getMsgFlags(SnmpMsg var1);

   int getMsgSecurityModel(SnmpMsg var1);

   int getSecurityLevel(SnmpMsg var1);

   byte[] getFlatSecurityParameters(SnmpMsg var1);

   SnmpSecurityParameters getSecurityParameters(SnmpMsg var1);

   byte[] getContextEngineId(SnmpMsg var1);

   byte[] getContextName(SnmpMsg var1);

   byte[] getRawContextName(SnmpMsg var1);

   byte[] getAccessContext(SnmpMsg var1);

   byte[] getEncryptedPdu(SnmpMsg var1);

   void setContextName(SnmpMsg var1, byte[] var2);

   void setContextEngineId(SnmpMsg var1, byte[] var2);
}
