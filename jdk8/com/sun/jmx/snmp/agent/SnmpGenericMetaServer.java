package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;

public interface SnmpGenericMetaServer {
   Object buildAttributeValue(long var1, SnmpValue var3) throws SnmpStatusException;

   SnmpValue buildSnmpValue(long var1, Object var3) throws SnmpStatusException;

   String getAttributeName(long var1) throws SnmpStatusException;

   void checkSetAccess(SnmpValue var1, long var2, Object var4) throws SnmpStatusException;

   void checkGetAccess(long var1, Object var3) throws SnmpStatusException;
}
