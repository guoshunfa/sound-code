package com.sun.jmx.snmp;

public interface SnmpPduBulkType extends SnmpAckPdu {
   void setMaxRepetitions(int var1);

   void setNonRepeaters(int var1);

   int getMaxRepetitions();

   int getNonRepeaters();
}
