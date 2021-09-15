package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.util.Enumeration;
import java.util.Vector;

public interface SnmpMibSubRequest extends SnmpMibRequest {
   Enumeration<SnmpVarBind> getElements();

   Vector<SnmpVarBind> getSubList();

   SnmpOid getEntryOid();

   boolean isNewEntry();

   SnmpVarBind getRowStatusVarBind();

   void registerGetException(SnmpVarBind var1, SnmpStatusException var2) throws SnmpStatusException;

   void registerSetException(SnmpVarBind var1, SnmpStatusException var2) throws SnmpStatusException;

   void registerCheckException(SnmpVarBind var1, SnmpStatusException var2) throws SnmpStatusException;
}
