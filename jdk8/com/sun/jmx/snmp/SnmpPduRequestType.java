package com.sun.jmx.snmp;

public interface SnmpPduRequestType extends SnmpAckPdu {
   void setErrorIndex(int var1);

   void setErrorStatus(int var1);

   int getErrorIndex();

   int getErrorStatus();
}
