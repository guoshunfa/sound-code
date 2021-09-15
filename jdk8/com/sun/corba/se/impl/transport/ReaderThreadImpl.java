package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ReaderThread;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.threadpool.Work;

public class ReaderThreadImpl implements ReaderThread, Work {
   private ORB orb;
   private Connection connection;
   private Selector selector;
   private boolean keepRunning;
   private long enqueueTime;

   public ReaderThreadImpl(ORB var1, Connection var2, Selector var3) {
      this.orb = var1;
      this.connection = var2;
      this.selector = var3;
      this.keepRunning = true;
   }

   public Connection getConnection() {
      return this.connection;
   }

   public void close() {
      if (this.orb.transportDebugFlag) {
         this.dprint(".close: " + this.connection);
      }

      this.keepRunning = false;
   }

   public void doWork() {
      try {
         if (this.orb.transportDebugFlag) {
            this.dprint(".doWork: Start ReaderThread: " + this.connection);
         }

         while(this.keepRunning) {
            try {
               if (this.orb.transportDebugFlag) {
                  this.dprint(".doWork: Start ReaderThread cycle: " + this.connection);
               }

               if (this.connection.read()) {
                  return;
               }

               if (this.orb.transportDebugFlag) {
                  this.dprint(".doWork: End ReaderThread cycle: " + this.connection);
               }
            } catch (Throwable var5) {
               if (this.orb.transportDebugFlag) {
                  this.dprint(".doWork: exception in read: " + this.connection, var5);
               }

               this.orb.getTransportManager().getSelector(0).unregisterForEvent(this.getConnection().getEventHandler());
               this.getConnection().close();
            }
         }
      } finally {
         if (this.orb.transportDebugFlag) {
            this.dprint(".doWork: Terminated ReaderThread: " + this.connection);
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
      return "ReaderThread";
   }

   private void dprint(String var1) {
      ORBUtility.dprint("ReaderThreadImpl", var1);
   }

   protected void dprint(String var1, Throwable var2) {
      this.dprint(var1);
      var2.printStackTrace(System.out);
   }
}
