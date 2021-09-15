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
import java.util.Enumeration;
import java.util.logging.Level;

class SnmpSubBulkRequestHandler extends SnmpSubRequestHandler {
   private SnmpAdaptorServer server = null;
   protected int nonRepeat = 0;
   protected int maxRepeat = 0;
   protected int globalR = 0;
   protected int size = 0;

   protected SnmpSubBulkRequestHandler(SnmpEngine var1, SnmpAdaptorServer var2, SnmpIncomingRequest var3, SnmpMibAgent var4, SnmpPdu var5, int var6, int var7, int var8) {
      super(var1, var3, var4, var5);
      this.init(var2, var5, var6, var7, var8);
   }

   protected SnmpSubBulkRequestHandler(SnmpAdaptorServer var1, SnmpMibAgent var2, SnmpPdu var3, int var4, int var5, int var6) {
      super(var2, var3);
      this.init(var1, var3, var4, var5, var6);
   }

   public void run() {
      this.size = this.varBind.size();

      try {
         ThreadContext var1 = ThreadContext.push("SnmpUserData", this.data);

         try {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:getBulk operation on " + this.agent.getMibName());
            }

            this.agent.getBulk(this.createMibRequest(this.varBind, this.version, this.data), this.nonRepeat, this.maxRepeat);
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

   private void init(SnmpAdaptorServer var1, SnmpPdu var2, int var3, int var4, int var5) {
      this.server = var1;
      this.nonRepeat = var3;
      this.maxRepeat = var4;
      this.globalR = var5;
      int var6 = this.translation.length;
      SnmpVarBind[] var7 = var2.varBindList;
      SnmpSubRequestHandler.NonSyncVector var8 = (SnmpSubRequestHandler.NonSyncVector)this.varBind;

      for(int var9 = 0; var9 < var6; ++var9) {
         this.translation[var9] = var9;
         SnmpVarBind var10 = new SnmpVarBind(var7[var9].oid, var7[var9].value);
         var8.addNonSyncElement(var10);
      }

   }

   private SnmpVarBind findVarBind(SnmpVarBind var1, SnmpVarBind var2) {
      if (var1 == null) {
         return null;
      } else if (var2.oid == null) {
         return var1;
      } else if (var1.value == SnmpVarBind.endOfMibView) {
         return var2;
      } else if (var2.value == SnmpVarBind.endOfMibView) {
         return var1;
      } else {
         SnmpValue var3 = var2.value;
         int var4 = var1.oid.compareTo(var2.oid);
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "Comparing OID element : " + var1.oid + " with result : " + var2.oid);
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "Values element : " + var1.value + " result : " + var2.value);
         }

         if (var4 < 0) {
            return var1;
         } else if (var4 == 0) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", " oid overlapping. Oid : " + var1.oid + "value :" + var1.value);
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "Already present varBind : " + var2);
            }

            SnmpOid var5 = var2.oid;
            SnmpMibAgent var6 = this.server.getAgentMib(var5);
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "Deeper agent : " + var6);
            }

            if (var6 == this.agent) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "The current agent is the deeper one. Update the value with the current one");
               }

               return var1;
            } else {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "The current agent is not the deeper one. return the previous one.");
               }

               return var2;
            }
         } else {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "findVarBind", "The right varBind is the already present one");
            }

            return var2;
         }
      }
   }

   protected void updateResult(SnmpVarBind[] var1) {
      Enumeration var2 = this.varBind.elements();
      int var3 = var1.length;

      int var4;
      int var5;
      for(var4 = 0; var4 < this.size; ++var4) {
         if (!var2.hasMoreElements()) {
            return;
         }

         var5 = this.translation[var4];
         if (var5 >= var3) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "updateResult", "Position '" + var5 + "' is out of bound...");
            }
         } else {
            SnmpVarBind var6 = (SnmpVarBind)var2.nextElement();
            if (var6 != null) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "updateResult", "Non repeaters Current element : " + var6 + " from agent : " + this.agent);
               }

               SnmpVarBind var7 = this.findVarBind(var6, var1[var5]);
               if (var7 != null) {
                  var1[var5] = var7;
               }
            }
         }
      }

      var4 = this.size - this.nonRepeat;

      for(var5 = 2; var5 <= this.maxRepeat; ++var5) {
         for(int var10 = 0; var10 < var4; ++var10) {
            int var11 = (var5 - 1) * this.globalR + this.translation[this.nonRepeat + var10];
            if (var11 >= var3) {
               return;
            }

            if (!var2.hasMoreElements()) {
               return;
            }

            SnmpVarBind var8 = (SnmpVarBind)var2.nextElement();
            if (var8 != null) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "updateResult", "Repeaters Current element : " + var8 + " from agent : " + this.agent);
               }

               SnmpVarBind var9 = this.findVarBind(var8, var1[var11]);
               if (var9 != null) {
                  var1[var11] = var9;
               }
            }
         }
      }

   }
}
