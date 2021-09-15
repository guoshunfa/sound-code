package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ServiceNotFoundException;

public interface SnmpMibAgentMBean {
   void get(SnmpMibRequest var1) throws SnmpStatusException;

   void getNext(SnmpMibRequest var1) throws SnmpStatusException;

   void getBulk(SnmpMibRequest var1, int var2, int var3) throws SnmpStatusException;

   void set(SnmpMibRequest var1) throws SnmpStatusException;

   void check(SnmpMibRequest var1) throws SnmpStatusException;

   MBeanServer getMBeanServer();

   SnmpMibHandler getSnmpAdaptor();

   void setSnmpAdaptor(SnmpMibHandler var1);

   void setSnmpAdaptor(SnmpMibHandler var1, SnmpOid[] var2);

   void setSnmpAdaptor(SnmpMibHandler var1, String var2);

   void setSnmpAdaptor(SnmpMibHandler var1, String var2, SnmpOid[] var3);

   ObjectName getSnmpAdaptorName();

   void setSnmpAdaptorName(ObjectName var1) throws InstanceNotFoundException, ServiceNotFoundException;

   void setSnmpAdaptorName(ObjectName var1, SnmpOid[] var2) throws InstanceNotFoundException, ServiceNotFoundException;

   void setSnmpAdaptorName(ObjectName var1, String var2) throws InstanceNotFoundException, ServiceNotFoundException;

   void setSnmpAdaptorName(ObjectName var1, String var2, SnmpOid[] var3) throws InstanceNotFoundException, ServiceNotFoundException;

   boolean getBindingState();

   String getMibName();
}
