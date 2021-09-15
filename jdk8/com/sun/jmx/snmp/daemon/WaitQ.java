package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Vector;
import java.util.logging.Level;

class WaitQ extends Vector<SnmpInformRequest> {
   boolean isBeingDestroyed = false;

   WaitQ(int var1, int var2) {
      super(var1, var2);
   }

   public synchronized void addWaiting(SnmpInformRequest var1) {
      long var2 = var1.getAbsMaxTimeToWait();

      int var4;
      for(var4 = this.size(); var4 > 0 && var2 >= this.getRequestAt(var4 - 1).getAbsMaxTimeToWait(); --var4) {
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
            var1 = var5.getAbsMaxTimeToWait() - var3;
            if (var1 <= 0L) {
               return true;
            }
         }
      }

      return false;
   }

   public synchronized SnmpInformRequest getTimeoutRequests() {
      if (this.waitUntilReady()) {
         SnmpInformRequest var1 = (SnmpInformRequest)this.lastElement();
         --this.elementCount;
         return var1;
      } else {
         return null;
      }
   }

   private synchronized void notifyClients() {
      this.notifyAll();
   }

   public synchronized void waitOnThisQueue(long var1) {
      if (var1 == 0L && !this.isEmpty() && JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpQManager.class.getName(), "waitOnThisQueue", "[" + Thread.currentThread().toString() + "]:Fatal BUG :: Blocking on waitq permenantly. But size = " + this.size());
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
