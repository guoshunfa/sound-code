package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;

public interface SnmpAccessControlModel extends SnmpModel {
   void checkAccess(int var1, String var2, int var3, int var4, int var5, byte[] var6, SnmpOid var7) throws SnmpStatusException;

   void checkPduAccess(int var1, String var2, int var3, int var4, int var5, byte[] var6, SnmpPdu var7) throws SnmpStatusException;

   boolean enableSnmpV1V2SetRequest();

   boolean disableSnmpV1V2SetRequest();

   boolean isSnmpV1V2SetRequestAuthorized();
}
