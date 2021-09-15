package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpUnknownModelException;

public interface SnmpSubSystem {
   SnmpEngine getEngine();

   void addModel(int var1, SnmpModel var2);

   SnmpModel removeModel(int var1) throws SnmpUnknownModelException;

   SnmpModel getModel(int var1) throws SnmpUnknownModelException;

   int[] getModelIds();

   String[] getModelNames();
}
