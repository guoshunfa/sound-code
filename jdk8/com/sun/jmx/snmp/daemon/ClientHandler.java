package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import javax.management.MBeanServer;
import javax.management.ObjectName;

abstract class ClientHandler implements Runnable {
   protected CommunicatorServer adaptorServer = null;
   protected int requestId = -1;
   protected MBeanServer mbs = null;
   protected ObjectName objectName = null;
   protected Thread thread = null;
   protected boolean interruptCalled = false;
   protected String dbgTag = null;

   public ClientHandler(CommunicatorServer var1, int var2, MBeanServer var3, ObjectName var4) {
      this.adaptorServer = var1;
      this.requestId = var2;
      this.mbs = var3;
      this.objectName = var4;
      this.interruptCalled = false;
      this.dbgTag = this.makeDebugTag();
      this.thread = this.createThread(this);
   }

   Thread createThread(Runnable var1) {
      return new Thread(this);
   }

   public void interrupt() {
      JmxProperties.SNMP_ADAPTOR_LOGGER.entering(this.dbgTag, "interrupt");
      this.interruptCalled = true;
      if (this.thread != null) {
         this.thread.interrupt();
      }

      JmxProperties.SNMP_ADAPTOR_LOGGER.exiting(this.dbgTag, "interrupt");
   }

   public void join() {
      if (this.thread != null) {
         try {
            this.thread.join();
         } catch (InterruptedException var2) {
         }
      }

   }

   public void run() {
      try {
         this.adaptorServer.notifyClientHandlerCreated(this);
         this.doRun();
      } finally {
         this.adaptorServer.notifyClientHandlerDeleted(this);
      }

   }

   public abstract void doRun();

   protected String makeDebugTag() {
      return "ClientHandler[" + this.adaptorServer.getProtocol() + ":" + this.adaptorServer.getPort() + "][" + this.requestId + "]";
   }
}
