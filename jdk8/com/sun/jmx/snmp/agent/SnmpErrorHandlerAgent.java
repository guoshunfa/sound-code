package com.sun.jmx.snmp.agent;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.logging.Level;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class SnmpErrorHandlerAgent extends SnmpMibAgent implements Serializable {
   private static final long serialVersionUID = 7751082923508885650L;

   public void init() throws IllegalAccessException {
   }

   public ObjectName preRegister(MBeanServer var1, ObjectName var2) throws Exception {
      return var2;
   }

   public long[] getRootOid() {
      return null;
   }

   public void get(SnmpMibRequest var1) throws SnmpStatusException {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpErrorHandlerAgent.class.getName(), "get", "Get in Exception");
      if (var1.getVersion() == 0) {
         throw new SnmpStatusException(2);
      } else {
         Enumeration var2 = var1.getElements();

         while(var2.hasMoreElements()) {
            SnmpVarBind var3 = (SnmpVarBind)var2.nextElement();
            var3.setNoSuchObject();
         }

      }
   }

   public void check(SnmpMibRequest var1) throws SnmpStatusException {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpErrorHandlerAgent.class.getName(), "check", "Check in Exception");
      throw new SnmpStatusException(17);
   }

   public void set(SnmpMibRequest var1) throws SnmpStatusException {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpErrorHandlerAgent.class.getName(), "set", "Set in Exception, CANNOT be called");
      throw new SnmpStatusException(17);
   }

   public void getNext(SnmpMibRequest var1) throws SnmpStatusException {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpErrorHandlerAgent.class.getName(), "getNext", "GetNext in Exception");
      if (var1.getVersion() == 0) {
         throw new SnmpStatusException(2);
      } else {
         Enumeration var2 = var1.getElements();

         while(var2.hasMoreElements()) {
            SnmpVarBind var3 = (SnmpVarBind)var2.nextElement();
            var3.setEndOfMibView();
         }

      }
   }

   public void getBulk(SnmpMibRequest var1, int var2, int var3) throws SnmpStatusException {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpErrorHandlerAgent.class.getName(), "getBulk", "GetBulk in Exception");
      if (var1.getVersion() == 0) {
         throw new SnmpStatusException(5, 0);
      } else {
         Enumeration var4 = var1.getElements();

         while(var4.hasMoreElements()) {
            SnmpVarBind var5 = (SnmpVarBind)var4.nextElement();
            var5.setEndOfMibView();
         }

      }
   }
}
