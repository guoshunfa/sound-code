package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import java.util.logging.Level;

final class SnmpTimerServer extends Thread {
   private SnmpInformRequest req = null;
   SnmpQManager snmpq = null;
   boolean isBeingDestroyed = false;

   public SnmpTimerServer(ThreadGroup var1, SnmpQManager var2) {
      super(var1, "SnmpTimerServer");
      this.setName("SnmpTimerServer");
      this.snmpq = var2;
      this.start();
   }

   public synchronized void stopTimerServer() {
      if (this.isAlive()) {
         this.interrupt();

         try {
            this.join();
         } catch (InterruptedException var2) {
         }
      }

   }

   public void run() {
      Thread.currentThread().setPriority(5);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpTimerServer.class.getName(), "run", "Timer Thread started");
      }

      while(true) {
         try {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpTimerServer.class.getName(), "run", "Blocking for inform requests");
            }

            if (this.req == null) {
               this.req = this.snmpq.getTimeoutRequests();
            }

            if (this.req != null && this.req.inProgress()) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpTimerServer.class.getName(), "run", "Handle timeout inform request " + this.req.getRequestId());
               }

               this.req.action();
               this.req = null;
            }

            if (this.isBeingDestroyed) {
               return;
            }
         } catch (Exception var2) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpTimerServer.class.getName(), "run", (String)"Got unexpected exception", (Throwable)var2);
            }
         } catch (ThreadDeath var3) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpTimerServer.class.getName(), "run", (String)"ThreadDeath, timer server unexpectedly shutting down", (Throwable)var3);
            }

            throw var3;
         } catch (OutOfMemoryError var4) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpTimerServer.class.getName(), "run", (String)"OutOfMemoryError", (Throwable)var4);
            }

            yield();
         } catch (Error var5) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpTimerServer.class.getName(), "run", (String)"Received Internal error", (Throwable)var5);
            }
         }
      }
   }
}
