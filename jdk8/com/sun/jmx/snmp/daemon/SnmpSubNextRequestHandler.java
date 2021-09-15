package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.ThreadContext;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.internal.SnmpIncomingRequest;
import java.util.logging.Level;

class SnmpSubNextRequestHandler extends SnmpSubRequestHandler {
   private SnmpAdaptorServer server = null;

   protected SnmpSubNextRequestHandler(SnmpAdaptorServer var1, SnmpMibAgent var2, SnmpPdu var3) {
      super(var2, var3);
      this.init(var3, var1);
   }

   protected SnmpSubNextRequestHandler(SnmpEngine var1, SnmpAdaptorServer var2, SnmpIncomingRequest var3, SnmpMibAgent var4, SnmpPdu var5) {
      super(var1, var3, var4, var5);
      this.init(var5, var2);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubNextRequestHandler.class.getName(), "SnmpSubNextRequestHandler", "Constructor : " + this);
      }

   }

   private void init(SnmpPdu var1, SnmpAdaptorServer var2) {
      this.server = var2;
      int var3 = this.translation.length;
      SnmpVarBind[] var4 = var1.varBindList;
      SnmpSubRequestHandler.NonSyncVector var5 = (SnmpSubRequestHandler.NonSyncVector)this.varBind;

      for(int var6 = 0; var6 < var3; ++var6) {
         this.translation[var6] = var6;
         SnmpVarBind var7 = new SnmpVarBind(var4[var6].oid, var4[var6].value);
         var5.addNonSyncElement(var7);
      }

   }

   public void run() {
      try {
         ThreadContext var1 = ThreadContext.push("SnmpUserData", this.data);

         try {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:getNext operation on " + this.agent.getMibName());
            }

            this.agent.getNext(this.createMibRequest(this.varBind, 1, this.data));
         } finally {
            ThreadContext.restore(var1);
         }
      } catch (SnmpStatusException var7) {
         this.errorStatus = var7.getStatus();
         this.errorIndex = var7.getErrorIndex();
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "run", (String)("[" + Thread.currentThread() + "]:an Snmp error occurred during the operation"), (Throwable)var7);
         }
      } catch (Exception var8) {
         this.errorStatus = 5;
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "run", (String)("[" + Thread.currentThread() + "]:a generic error occurred during the operation"), (Throwable)var8);
         }
      }

      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:operation completed");
      }

   }

   protected void updateRequest(SnmpVarBind var1, int var2) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "updateRequest", "Copy :" + var1);
      }

      int var3 = this.varBind.size();
      this.translation[var3] = var2;
      SnmpVarBind var4 = new SnmpVarBind(var1.oid, var1.value);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "updateRequest", "Copied :" + var4);
      }

      this.varBind.addElement(var4);
   }

   protected void updateResult(SnmpVarBind[] var1) {
      int var2 = this.varBind.size();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = this.translation[var3];
         SnmpVarBind var5 = (SnmpVarBind)((SnmpSubRequestHandler.NonSyncVector)this.varBind).elementAtNonSync(var3);
         SnmpVarBind var6 = var1[var4];
         if (var6 == null) {
            var1[var4] = var5;
         } else {
            SnmpValue var7 = var6.value;
            if (var7 != null && var7 != SnmpVarBind.endOfMibView) {
               if (var5 != null && var5.value != SnmpVarBind.endOfMibView) {
                  int var8 = var5.oid.compareTo(var6.oid);
                  if (var8 < 0) {
                     var1[var4] = var5;
                  } else if (var8 == 0) {
                     if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "updateResult", " oid overlapping. Oid : " + var5.oid + "value :" + var5.value);
                        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "updateResult", "Already present varBind : " + var6);
                     }

                     SnmpOid var9 = var6.oid;
                     SnmpMibAgent var10 = this.server.getAgentMib(var9);
                     if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "updateResult", "Deeper agent : " + var10);
                     }

                     if (var10 == this.agent) {
                        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                           JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "updateResult", "The current agent is the deeper one. Update the value with the current one");
                        }

                        var1[var4].value = var5.value;
                     }
                  }
               }
            } else if (var5 != null && var5.value != SnmpVarBind.endOfMibView) {
               var1[var4] = var5;
            }
         }
      }

   }
}
