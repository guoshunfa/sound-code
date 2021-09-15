package com.sun.jmx.snmp.agent;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpUnknownModelException;
import com.sun.jmx.snmp.internal.SnmpAccessControlModel;
import com.sun.jmx.snmp.internal.SnmpEngineImpl;
import java.util.logging.Level;

class AcmChecker {
   SnmpAccessControlModel model = null;
   String principal = null;
   int securityLevel = -1;
   int version = -1;
   int pduType = -1;
   int securityModel = -1;
   byte[] contextName = null;
   SnmpEngineImpl engine = null;
   LongList l = null;

   AcmChecker(SnmpMibRequest var1) {
      this.engine = (SnmpEngineImpl)var1.getEngine();
      if (this.engine != null && this.engine.isCheckOidActivated()) {
         try {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "AcmChecker(SnmpMibRequest)", "SNMP V3 Access Control to be done");
            }

            this.model = (SnmpAccessControlModel)this.engine.getAccessControlSubSystem().getModel(3);
            this.principal = var1.getPrincipal();
            this.securityLevel = var1.getSecurityLevel();
            this.pduType = var1.getPdu().type;
            this.version = var1.getRequestPduVersion();
            this.securityModel = var1.getSecurityModel();
            this.contextName = var1.getAccessContextName();
            this.l = new LongList();
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               StringBuilder var2 = (new StringBuilder()).append("Will check oid for : principal : ").append(this.principal).append("; securityLevel : ").append(this.securityLevel).append("; pduType : ").append(this.pduType).append("; version : ").append(this.version).append("; securityModel : ").append(this.securityModel).append("; contextName : ").append((Object)this.contextName);
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "AcmChecker(SnmpMibRequest)", var2.toString());
            }
         } catch (SnmpUnknownModelException var3) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "AcmChecker(SnmpMibRequest)", "Unknown Model, no ACM check.");
            }
         }
      }

   }

   void add(int var1, long var2) {
      if (this.model != null) {
         this.l.add(var1, var2);
      }

   }

   void remove(int var1) {
      if (this.model != null) {
         this.l.remove(var1);
      }

   }

   void add(int var1, long[] var2, int var3, int var4) {
      if (this.model != null) {
         this.l.add(var1, var2, var3, var4);
      }

   }

   void remove(int var1, int var2) {
      if (this.model != null) {
         this.l.remove(var1, var2);
      }

   }

   void checkCurrentOid() throws SnmpStatusException {
      if (this.model != null) {
         SnmpOid var1 = new SnmpOid(this.l.toArray());
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "checkCurrentOid", "Checking access for : " + var1);
         }

         this.model.checkAccess(this.version, this.principal, this.securityLevel, this.pduType, this.securityModel, this.contextName, var1);
      }

   }
}
