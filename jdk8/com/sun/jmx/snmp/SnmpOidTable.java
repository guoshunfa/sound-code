package com.sun.jmx.snmp;

import java.util.Vector;

public interface SnmpOidTable {
   SnmpOidRecord resolveVarName(String var1) throws SnmpStatusException;

   SnmpOidRecord resolveVarOid(String var1) throws SnmpStatusException;

   Vector<?> getAllEntries();
}
