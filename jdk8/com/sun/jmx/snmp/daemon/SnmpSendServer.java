package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;

final class SnmpSendServer extends Thread {
   private int intervalRange = 5000;
   private Vector<SnmpInformRequest> readyPool;
   SnmpQManager snmpq = null;
   boolean isBeingDestroyed = false;

   public SnmpSendServer(ThreadGroup var1, SnmpQManager var2) {
      super(var1, "SnmpSendServer");
      this.snmpq = var2;
      this.start();
   }

   public synchronized void stopSendServer() {
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
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSendServer.class.getName(), "run", "Thread Started");
      }

      while(true) {
         try {
            this.prepareAndSendRequest();
            if (this.isBeingDestroyed) {
               return;
            }
         } catch (Exception var2) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSendServer.class.getName(), "run", (String)"Exception in send server", (Throwable)var2);
            }
         } catch (ThreadDeath var3) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSendServer.class.getName(), "run", "Exiting... Fatal error");
            }

            throw var3;
         } catch (OutOfMemoryError var4) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSendServer.class.getName(), "run", "Out of memory");
            }
         } catch (Error var5) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSendServer.class.getName(), "run", (String)"Got unexpected error", (Throwable)var5);
            }

            throw var5;
         }
      }
   }

   private void prepareAndSendRequest() {
      if (this.readyPool != null && !this.readyPool.isEmpty()) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSendServer.class.getName(), "prepareAndSendRequest", "Inform requests from a previous block left unprocessed. Will try again");
         }
      } else {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSendServer.class.getName(), "prepareAndSendRequest", "Blocking for inform requests");
         }

         this.readyPool = this.snmpq.getAllOutstandingRequest((long)this.intervalRange);
         if (this.isBeingDestroyed) {
            return;
         }
      }

      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSendServer.class.getName(), "prepareAndSendRequest", "List of inform requests to send : " + this.reqListToString(this.readyPool));
      }

      synchronized(this) {
         if (this.readyPool.size() < 2) {
            this.fireRequestList(this.readyPool);
         } else {
            for(; !this.readyPool.isEmpty(); this.readyPool.removeElementAt(this.readyPool.size() - 1)) {
               SnmpInformRequest var2 = (SnmpInformRequest)this.readyPool.lastElement();
               if (var2 != null && var2.inProgress()) {
                  this.fireRequest(var2);
               }
            }

            this.readyPool.removeAllElements();
         }
      }
   }

   private void fireRequest(SnmpInformRequest var1) {
      if (var1 != null && var1.inProgress()) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSendServer.class.getName(), "fireRequest", "Firing inform request directly. -> " + var1.getRequestId());
         }

         var1.action();
      }

   }

   private void fireRequestList(Vector<SnmpInformRequest> var1) {
      for(; !var1.isEmpty(); var1.removeElementAt(var1.size() - 1)) {
         SnmpInformRequest var2 = (SnmpInformRequest)var1.lastElement();
         if (var2 != null && var2.inProgress()) {
            this.fireRequest(var2);
         }
      }

   }

   private final String reqListToString(Vector<SnmpInformRequest> var1) {
      StringBuilder var2 = new StringBuilder(var1.size() * 100);
      Enumeration var3 = var1.elements();

      while(var3.hasMoreElements()) {
         SnmpInformRequest var4 = (SnmpInformRequest)var3.nextElement();
         var2.append("InformRequestId -> ");
         var2.append(var4.getRequestId());
         var2.append(" / Destination -> ");
         var2.append((Object)var4.getAddress());
         var2.append(". ");
      }

      String var5 = var2.toString();
      return var5;
   }
}
