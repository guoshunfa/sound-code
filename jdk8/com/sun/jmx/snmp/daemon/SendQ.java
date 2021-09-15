package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Vector;
import java.util.logging.Level;

class SendQ extends Vector<SnmpInformRequest> {
   boolean isBeingDestroyed = false;

   SendQ(int var1, int var2) {
      super(var1, var2);
   }

   private synchronized void notifyClients() {
      this.notifyAll();
   }

   public synchronized void addRequest(SnmpInformRequest var1) {
      long var2 = var1.getAbsNextPollTime();

      int var4;
      for(var4 = this.size(); var4 > 0 && var2 >= this.getRequestAt(var4 - 1).getAbsNextPollTime(); --var4) {
      }

      if (var4 == this.size()) {
         this.addElement(var1);
         this.notifyClients();
      } else {
         this.insertElementAt(var1, var4);
      }

   }

   public synchronized boolean waitUntilReady() {
      long var1;
      for(; !this.isBeingDestroyed; this.waitOnThisQueue(var1)) {
         var1 = 0L;
         if (!this.isEmpty()) {
            long var3 = System.currentTimeMillis();
            SnmpInformRequest var5 = (SnmpInformRequest)this.lastElement();
            var1 = var5.getAbsNextPollTime() - var3;
            if (var1 <= 0L) {
               return true;
            }
         }
      }

      return false;
   }

   public synchronized Vector<SnmpInformRequest> getAllOutstandingRequest(long var1) {
      Vector var4 = new Vector();

      do {
         if (!this.waitUntilReady()) {
            return null;
         }

         long var5 = System.currentTimeMillis() + var1;

         for(int var3 = this.size(); var3 > 0; --var3) {
            SnmpInformRequest var7 = this.getRequestAt(var3 - 1);
            if (var7.getAbsNextPollTime() > var5) {
               break;
            }

            var4.addElement(var7);
         }
      } while(var4.isEmpty());

      this.elementCount -= var4.size();
      return var4;
   }

   public synchronized void waitOnThisQueue(long var1) {
      if (var1 == 0L && !this.isEmpty() && JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpQManager.class.getName(), "waitOnThisQueue", "[" + Thread.currentThread().toString() + "]:Fatal BUG :: Blocking on newq permenantly. But size = " + this.size());
      }

      try {
         this.wait(var1);
      } catch (InterruptedException var4) {
      }

   }

   public SnmpInformRequest getRequestAt(int var1) {
      return (SnmpInformRequest)this.elementAt(var1);
   }

   public synchronized SnmpInformRequest removeRequest(long var1) {
      int var3 = this.size();

      for(int var4 = 0; var4 < var3; ++var4) {
         SnmpInformRequest var5 = this.getRequestAt(var4);
         if (var1 == (long)var5.getRequestId()) {
            this.removeElementAt(var4);
            return var5;
         }
      }

      return null;
   }
}
