package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpVarBind;
import java.util.Enumeration;
import java.util.Vector;

public interface SnmpMibRequest {
   Enumeration<SnmpVarBind> getElements();

   Vector<SnmpVarBind> getSubList();

   int getVersion();

   int getRequestPduVersion();

   SnmpEngine getEngine();

   String getPrincipal();

   int getSecurityLevel();

   int getSecurityModel();

   byte[] getContextName();

   byte[] getAccessContextName();

   Object getUserData();

   int getVarIndex(SnmpVarBind var1);

   void addVarBind(SnmpVarBind var1);

   int getSize();

   SnmpPdu getPdu();
}
