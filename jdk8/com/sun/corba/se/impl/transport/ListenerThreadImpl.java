package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.ListenerThread;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.threadpool.Work;

public class ListenerThreadImpl implements ListenerThread, Work {
   private ORB orb;
   private Acceptor acceptor;
   private Selector selector;
   private boolean keepRunning;
   private long enqueueTime;

   public ListenerThreadImpl(ORB var1, Acceptor var2, Selector var3) {
      this.orb = var1;
      this.acceptor = var2;
      this.selector = var3;
      this.keepRunning = true;
   }

   public Acceptor getAcceptor() {
      return this.acceptor;
   }

   public void close() {
      if (this.orb.transportDebugFlag) {
         this.dprint(".close: " + this.acceptor);
      }

      this.keepRunning = false;
   }

   public void doWork() {
      try {
         if (this.orb.transportDebugFlag) {
            this.dprint(".doWork: Start ListenerThread: " + this.acceptor);
         }

         while(this.keepRunning) {
            try {
               if (this.orb.transportDebugFlag) {
                  this.dprint(".doWork: BEFORE ACCEPT CYCLE: " + this.acceptor);
               }

               this.acceptor.accept();
               if (this.orb.transportDebugFlag) {
                  this.dprint(".doWork: AFTER ACCEPT CYCLE: " + this.acceptor);
               }
            } catch (Throwable var5) {
               if (this.orb.transportDebugFlag) {
                  this.dprint(".doWork: Exception in accept: " + this.acceptor, var5);
               }

               this.orb.getTransportManager().getSelector(0).unregisterForEvent(this.getAcceptor().getEventHandler());
               this.getAcceptor().close();
            }
         }
      } finally {
         if (this.orb.transportDebugFlag) {
            this.dprint(".doWork: Terminated ListenerThread: " + this.acceptor);
         }

      }

   }

   public void setEnqueueTime(long var1) {
      this.enqueueTime = var1;
   }

   public long getEnqueueTime() {
      return this.enqueueTime;
   }

   public String getName() {
      return "ListenerThread";
   }

   private void dprint(String var1) {
      ORBUtility.dprint("ListenerThreadImpl", var1);
   }

   private void dprint(String var1, Throwable var2) {
      this.dprint(var1);
      var2.printStackTrace(System.out);
   }
}
