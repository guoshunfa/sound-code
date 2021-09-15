package com.sun.jmx.snmp.daemon;

import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpVarBindList;

public interface SnmpInformHandler extends SnmpDefinitions {
   void processSnmpPollData(SnmpInformRequest var1, int var2, int var3, SnmpVarBindList var4);

   void processSnmpPollTimeout(SnmpInformRequest var1);

   void processSnmpInternalError(SnmpInformRequest var1, String var2);
}
