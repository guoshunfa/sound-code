package com.sun.jmx.snmp.daemon;

import java.io.Serializable;
import java.util.Vector;

final class SnmpQManager implements Serializable {
   private static final long serialVersionUID = 2163709017015248264L;
   private SendQ newq = new SendQ(20, 5);
   private WaitQ waitq = new WaitQ(20, 5);
   private ThreadGroup queueThreadGroup = null;
   private Thread requestQThread = null;
   private Thread timerQThread = null;

   SnmpQManager() {
      this.queueThreadGroup = new ThreadGroup("Qmanager Thread Group");
      this.startQThreads();
   }

   public void startQThreads() {
      if (this.timerQThread == null || !this.timerQThread.isAlive()) {
         this.timerQThread = new SnmpTimerServer(this.queueThreadGroup, this);
      }

      if (this.requestQThread == null || !this.requestQThread.isAlive()) {
         this.requestQThread = new SnmpSendServer(this.queueThreadGroup, this);
      }

   }

   public void stopQThreads() {
      ((SnmpTimerServer)this.timerQThread).isBeingDestroyed = true;
      this.waitq.isBeingDestroyed = true;
      ((SnmpSendServer)this.requestQThread).isBeingDestroyed = true;
      this.newq.isBeingDestroyed = true;
      if (this.timerQThread != null && this.timerQThread.isAlive()) {
         ((SnmpTimerServer)this.timerQThread).stopTimerServer();
      }

      this.waitq = null;
      this.timerQThread = null;
      if (this.requestQThread != null && this.requestQThread.isAlive()) {
         ((SnmpSendServer)this.requestQThread).stopSendServer();
      }

      this.newq = null;
      this.requestQThread = null;
   }

   public void addRequest(SnmpInformRequest var1) {
      this.newq.addRequest(var1);
   }

   public void addWaiting(SnmpInformRequest var1) {
      this.waitq.addWaiting(var1);
   }

   public Vector<SnmpInformRequest> getAllOutstandingRequest(long var1) {
      return this.newq.getAllOutstandingRequest(var1);
   }

   public SnmpInformRequest getTimeoutRequests() {
      return this.waitq.getTimeoutRequests();
   }

   public void removeRequest(SnmpInformRequest var1) {
      this.newq.removeElement(var1);
      this.waitq.removeElement(var1);
   }

   public SnmpInformRequest removeRequest(long var1) {
      SnmpInformRequest var3;
      if ((var3 = this.newq.removeRequest(var1)) == null) {
         var3 = this.waitq.removeRequest(var1);
      }

      return var3;
   }
}
