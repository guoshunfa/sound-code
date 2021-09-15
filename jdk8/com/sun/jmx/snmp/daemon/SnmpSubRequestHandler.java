package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.ThreadContext;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.agent.SnmpMibRequest;
import com.sun.jmx.snmp.internal.SnmpIncomingRequest;
import java.util.Vector;
import java.util.logging.Level;

class SnmpSubRequestHandler implements SnmpDefinitions, Runnable {
   protected SnmpIncomingRequest incRequest;
   protected SnmpEngine engine;
   protected int version;
   protected int type;
   protected SnmpMibAgent agent;
   protected int errorStatus;
   protected int errorIndex;
   protected Vector<SnmpVarBind> varBind;
   protected int[] translation;
   protected Object data;
   private SnmpMibRequest mibRequest;
   private SnmpPdu reqPdu;

   protected SnmpSubRequestHandler(SnmpEngine var1, SnmpIncomingRequest var2, SnmpMibAgent var3, SnmpPdu var4) {
      this(var3, var4);
      this.init(var1, var2);
   }

   protected SnmpSubRequestHandler(SnmpEngine var1, SnmpIncomingRequest var2, SnmpMibAgent var3, SnmpPdu var4, boolean var5) {
      this(var3, var4, var5);
      this.init(var1, var2);
   }

   protected SnmpSubRequestHandler(SnmpMibAgent var1, SnmpPdu var2) {
      this.incRequest = null;
      this.engine = null;
      this.version = 0;
      this.type = 0;
      this.errorStatus = 0;
      this.errorIndex = -1;
      this.mibRequest = null;
      this.reqPdu = null;
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "constructor", "creating instance for request " + String.valueOf(var2.requestId));
      }

      this.version = var2.version;
      this.type = var2.type;
      this.agent = var1;
      this.reqPdu = var2;
      int var3 = var2.varBindList.length;
      this.translation = new int[var3];
      this.varBind = new SnmpSubRequestHandler.NonSyncVector(var3);
   }

   protected SnmpSubRequestHandler(SnmpMibAgent var1, SnmpPdu var2, boolean var3) {
      this(var1, var2);
      int var4 = this.translation.length;
      SnmpVarBind[] var5 = var2.varBindList;

      for(int var6 = 0; var6 < var4; ++var6) {
         this.translation[var6] = var6;
         ((SnmpSubRequestHandler.NonSyncVector)this.varBind).addNonSyncElement(var5[var6]);
      }

   }

   SnmpMibRequest createMibRequest(Vector<SnmpVarBind> var1, int var2, Object var3) {
      if (this.type == 163 && this.mibRequest != null) {
         return this.mibRequest;
      } else {
         SnmpMibRequest var4 = null;
         if (this.incRequest != null) {
            var4 = SnmpMibAgent.newMibRequest(this.engine, this.reqPdu, var1, var2, var3, this.incRequest.getPrincipal(), this.incRequest.getSecurityLevel(), this.incRequest.getSecurityModel(), this.incRequest.getContextName(), this.incRequest.getAccessContext());
         } else {
            var4 = SnmpMibAgent.newMibRequest(this.reqPdu, var1, var2, var3);
         }

         if (this.type == 253) {
            this.mibRequest = var4;
         }

         return var4;
      }
   }

   void setUserData(Object var1) {
      this.data = var1;
   }

   public void run() {
      try {
         ThreadContext var1 = ThreadContext.push("SnmpUserData", this.data);

         try {
            switch(this.type) {
            case 160:
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:get operation on " + this.agent.getMibName());
               }

               this.agent.get(this.createMibRequest(this.varBind, this.version, this.data));
               break;
            case 161:
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:getNext operation on " + this.agent.getMibName());
               }

               this.agent.getNext(this.createMibRequest(this.varBind, this.version, this.data));
               break;
            case 163:
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:set operation on " + this.agent.getMibName());
               }

               this.agent.set(this.createMibRequest(this.varBind, this.version, this.data));
               break;
            case 253:
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:check operation on " + this.agent.getMibName());
               }

               this.agent.check(this.createMibRequest(this.varBind, this.version, this.data));
               break;
            default:
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "run", "[" + Thread.currentThread() + "]:unknown operation (" + this.type + ") on " + this.agent.getMibName());
               }

               this.errorStatus = 5;
               this.errorIndex = 1;
            }
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

   static final int mapErrorStatusToV1(int var0, int var1) {
      if (var0 == 0) {
         return 0;
      } else if (var0 == 5) {
         return 5;
      } else if (var0 == 2) {
         return 2;
      } else if (var0 != 224 && var0 != 225 && var0 != 6 && var0 != 18 && var0 != 16) {
         if (var0 != 16 && var0 != 17) {
            if (var0 == 11) {
               return 2;
            } else if (var0 != 7 && var0 != 8 && var0 != 9 && var0 != 10 && var0 != 8 && var0 != 12) {
               if (var0 != 13 && var0 != 14 && var0 != 15) {
                  if (var0 == 1) {
                     return 1;
                  } else if (var0 != 3 && var0 != 4) {
                     return 5;
                  } else {
                     return var1 != 163 && var1 != 253 ? 2 : var0;
                  }
               } else {
                  return 5;
               }
            } else {
               return var1 != 163 && var1 != 253 ? 2 : 3;
            }
         } else {
            return var1 == 253 ? 4 : 2;
         }
      } else {
         return 2;
      }
   }

   static final int mapErrorStatusToV2(int var0, int var1) {
      if (var0 == 0) {
         return 0;
      } else if (var0 == 5) {
         return 5;
      } else if (var0 == 1) {
         return 1;
      } else if (var1 != 163 && var1 != 253) {
         return var0 == 16 ? var0 : 5;
      } else if (var0 == 2) {
         return 6;
      } else if (var0 == 4) {
         return 17;
      } else if (var0 == 3) {
         return 10;
      } else {
         return var0 != 6 && var0 != 18 && var0 != 16 && var0 != 17 && var0 != 11 && var0 != 7 && var0 != 8 && var0 != 9 && var0 != 10 && var0 != 8 && var0 != 12 && var0 != 13 && var0 != 14 && var0 != 15 ? 5 : var0;
      }
   }

   static final int mapErrorStatus(int var0, int var1, int var2) {
      if (var0 == 0) {
         return 0;
      } else if (var1 == 0) {
         return mapErrorStatusToV1(var0, var2);
      } else {
         return var1 != 1 && var1 != 3 ? 5 : mapErrorStatusToV2(var0, var2);
      }
   }

   protected int getErrorStatus() {
      return this.errorStatus == 0 ? 0 : mapErrorStatus(this.errorStatus, this.version, this.type);
   }

   protected int getErrorIndex() {
      if (this.errorStatus == 0) {
         return -1;
      } else {
         if (this.errorIndex == 0 || this.errorIndex == -1) {
            this.errorIndex = 1;
         }

         return this.translation[this.errorIndex - 1];
      }
   }

   protected void updateRequest(SnmpVarBind var1, int var2) {
      int var3 = this.varBind.size();
      this.translation[var3] = var2;
      this.varBind.addElement(var1);
   }

   protected void updateResult(SnmpVarBind[] var1) {
      if (var1 != null) {
         int var2 = this.varBind.size();
         int var3 = var1.length;

         for(int var4 = 0; var4 < var2; ++var4) {
            int var5 = this.translation[var4];
            if (var5 < var3) {
               var1[var5] = (SnmpVarBind)((SnmpSubRequestHandler.NonSyncVector)this.varBind).elementAtNonSync(var4);
            } else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSubRequestHandler.class.getName(), "updateResult", "Position `" + var5 + "' is out of bound...");
            }
         }

      }
   }

   private void init(SnmpEngine var1, SnmpIncomingRequest var2) {
      this.incRequest = var2;
      this.engine = var1;
   }

   class NonSyncVector<E> extends Vector<E> {
      public NonSyncVector(int var2) {
         super(var2);
      }

      final void addNonSyncElement(E var1) {
         this.ensureCapacity(this.elementCount + 1);
         this.elementData[this.elementCount++] = var1;
      }

      final E elementAtNonSync(int var1) {
         return this.elementData[var1];
      }
   }
}
