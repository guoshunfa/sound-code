package com.sun.jmx.snmp;

import java.util.Vector;

public interface SnmpOidDatabase extends SnmpOidTable {
   void add(SnmpOidTable var1);

   void remove(SnmpOidTable var1) throws SnmpStatusException;

   void removeAll();

   SnmpOidRecord resolveVarName(String var1) throws SnmpStatusException;

   SnmpOidRecord resolveVarOid(String var1) throws SnmpStatusException;

   Vector<?> getAllEntries();
}
