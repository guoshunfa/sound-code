package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBindList;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Level;

class SnmpSession implements SnmpDefinitions, Runnable {
   protected transient SnmpAdaptorServer adaptor;
   protected transient SnmpSocket informSocket = null;
   private transient Hashtable<SnmpInformRequest, SnmpInformRequest> informRequestList = new Hashtable();
   private transient Stack<SnmpInformRequest> informRespq = new Stack();
   private transient Thread myThread = null;
   private transient SnmpInformRequest syncInformReq;
   SnmpQManager snmpQman = null;
   private boolean isBeingCancelled = false;

   public SnmpSession(SnmpAdaptorServer var1) throws SocketException {
      this.adaptor = var1;
      this.snmpQman = new SnmpQManager();
      SnmpResponseHandler var2 = new SnmpResponseHandler(var1, this.snmpQman);
      this.initialize(var1, var2);
   }

   public SnmpSession() throws SocketException {
   }

   protected synchronized void initialize(SnmpAdaptorServer var1, SnmpResponseHandler var2) throws SocketException {
      this.informSocket = new SnmpSocket(var2, var1.getAddress(), var1.getBufferSize());
      this.myThread = new Thread(this, "SnmpSession");
      this.myThread.start();
   }

   synchronized boolean isSessionActive() {
      return this.adaptor.isActive() && this.myThread != null && this.myThread.isAlive();
   }

   SnmpSocket getSocket() {
      return this.informSocket;
   }

   SnmpQManager getSnmpQManager() {
      return this.snmpQman;
   }

   private synchronized boolean syncInProgress() {
      return this.syncInformReq != null;
   }

   private synchronized void setSyncMode(SnmpInformRequest var1) {
      this.syncInformReq = var1;
   }

   private synchronized void resetSyncMode() {
      if (this.syncInformReq != null) {
         this.syncInformReq = null;
         if (!this.thisSessionContext()) {
            this.notifyAll();
         }
      }
   }

   boolean thisSessionContext() {
      return Thread.currentThread() == this.myThread;
   }

   SnmpInformRequest makeAsyncRequest(InetAddress var1, String var2, SnmpInformHandler var3, SnmpVarBindList var4, int var5) throws SnmpStatusException {
      if (!this.isSessionActive()) {
         throw new SnmpStatusException("SNMP adaptor server not ONLINE");
      } else {
         SnmpInformRequest var6 = new SnmpInformRequest(this, this.adaptor, var1, var2, var5, var3);
         var6.start(var4);
         return var6;
      }
   }

   void waitForResponse(SnmpInformRequest var1, long var2) {
      if (var1.inProgress()) {
         this.setSyncMode(var1);
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(), "waitForResponse", "Session switching to sync mode for inform request " + var1.getRequestId());
         }

         long var4;
         if (var2 <= 0L) {
            var4 = System.currentTimeMillis() + 6000000L;
         } else {
            var4 = System.currentTimeMillis() + var2;
         }

         while(var1.inProgress() || this.syncInProgress()) {
            var2 = var4 - System.currentTimeMillis();
            if (var2 <= 0L) {
               break;
            }

            synchronized(this) {
               if (!this.informRespq.removeElement(var1)) {
                  try {
                     this.wait(var2);
                  } catch (InterruptedException var9) {
                  }
                  continue;
               }
            }

            try {
               this.processResponse(var1);
            } catch (Exception var10) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(), "waitForResponse", (String)"Got unexpected exception", (Throwable)var10);
               }
            }
         }

         this.resetSyncMode();
      }
   }

   public void run() {
      this.myThread = Thread.currentThread();
      this.myThread.setPriority(5);
      SnmpInformRequest var1 = null;

      while(this.myThread != null) {
         try {
            var1 = this.nextResponse();
            if (var1 != null) {
               this.processResponse(var1);
            }
         } catch (ThreadDeath var3) {
            this.myThread = null;
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(), "run", "ThreadDeath, session thread unexpectedly shutting down");
            }

            throw var3;
         }
      }

      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(), "run", "Session thread shutting down");
      }

      this.myThread = null;
   }

   private void processResponse(SnmpInformRequest var1) {
      while(var1 != null && this.myThread != null) {
         try {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(), "processResponse", "Processing response to req = " + var1.getRequestId());
            }

            var1.processResponse();
            var1 = null;
         } catch (Exception var3) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(), "processResponse", (String)"Got unexpected exception", (Throwable)var3);
            }

            var1 = null;
         } catch (OutOfMemoryError var4) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(), "processResponse", (String)"Out of memory error in session thread", (Throwable)var4);
            }

            Thread.yield();
         }
      }

   }

   synchronized void addInformRequest(SnmpInformRequest var1) throws SnmpStatusException {
      if (!this.isSessionActive()) {
         throw new SnmpStatusException("SNMP adaptor is not ONLINE or session is dead...");
      } else {
         this.informRequestList.put(var1, var1);
      }
   }

   synchronized void removeInformRequest(SnmpInformRequest var1) {
      if (!this.isBeingCancelled) {
         this.informRequestList.remove(var1);
      }

      if (this.syncInformReq != null && this.syncInformReq == var1) {
         this.resetSyncMode();
      }

   }

   private void cancelAllRequests() {
      SnmpInformRequest[] var1;
      synchronized(this) {
         if (this.informRequestList.isEmpty()) {
            return;
         }

         this.isBeingCancelled = true;
         var1 = new SnmpInformRequest[this.informRequestList.size()];
         Iterator var3 = this.informRequestList.values().iterator();
         int var4 = 0;

         while(true) {
            if (!var3.hasNext()) {
               this.informRequestList.clear();
               break;
            }

            SnmpInformRequest var5 = (SnmpInformRequest)var3.next();
            var1[var4++] = var5;
            var3.remove();
         }
      }

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2].cancelRequest();
      }

   }

   void addResponse(SnmpInformRequest var1) {
      if (this.isSessionActive()) {
         synchronized(this) {
            this.informRespq.push(var1);
            this.notifyAll();
         }
      } else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(), "addResponse", "Adaptor not ONLINE or session thread dead, so inform response is dropped..." + var1.getRequestId());
      }

   }

   private synchronized SnmpInformRequest nextResponse() {
      if (this.informRespq.isEmpty()) {
         try {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(), "nextResponse", "Blocking for response");
            }

            this.wait();
         } catch (InterruptedException var2) {
         }
      }

      if (this.informRespq.isEmpty()) {
         return null;
      } else {
         SnmpInformRequest var1 = (SnmpInformRequest)this.informRespq.firstElement();
         this.informRespq.removeElementAt(0);
         return var1;
      }
   }

   private synchronized void cancelAllResponses() {
      if (this.informRespq != null) {
         this.syncInformReq = null;
         this.informRespq.removeAllElements();
         this.notifyAll();
      }

   }

   final void destroySession() {
      this.cancelAllRequests();
      this.cancelAllResponses();
      synchronized(this) {
         this.informSocket.close();
         this.informSocket = null;
      }

      this.snmpQman.stopQThreads();
      this.snmpQman = null;
      this.killSessionThread();
   }

   private synchronized void killSessionThread() {
      if (this.myThread != null && this.myThread.isAlive()) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(), "killSessionThread", "Destroying session");
         }

         if (!this.thisSessionContext()) {
            this.myThread = null;
            this.notifyAll();
         } else {
            this.myThread = null;
         }
      }

   }

   protected void finalize() {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(), "finalize", "Shutting all servers");
      }

      if (this.informRespq != null) {
         this.informRespq.removeAllElements();
      }

      this.informRespq = null;
      if (this.informSocket != null) {
         this.informSocket.close();
      }

      this.informSocket = null;
      this.snmpQman = null;
   }
}
