package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchThreadPoolException;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import java.nio.channels.SelectionKey;
import org.omg.CORBA.INTERNAL;

public abstract class EventHandlerBase implements EventHandler {
   protected ORB orb;
   protected Work work;
   protected boolean useWorkerThreadForEvent;
   protected boolean useSelectThreadToWait;
   protected SelectionKey selectionKey;

   public void setUseSelectThreadToWait(boolean var1) {
      this.useSelectThreadToWait = var1;
   }

   public boolean shouldUseSelectThreadToWait() {
      return this.useSelectThreadToWait;
   }

   public void setSelectionKey(SelectionKey var1) {
      this.selectionKey = var1;
   }

   public SelectionKey getSelectionKey() {
      return this.selectionKey;
   }

   public void handleEvent() {
      if (this.orb.transportDebugFlag) {
         this.dprint(".handleEvent->: " + this);
      }

      this.getSelectionKey().interestOps(this.getSelectionKey().interestOps() & ~this.getInterestOps());
      if (this.shouldUseWorkerThreadForEvent()) {
         Object var1 = null;

         try {
            if (this.orb.transportDebugFlag) {
               this.dprint(".handleEvent: addWork to pool: 0");
            }

            this.orb.getThreadPoolManager().getThreadPool(0).getWorkQueue(0).addWork(this.getWork());
         } catch (NoSuchThreadPoolException var3) {
            var1 = var3;
         } catch (NoSuchWorkQueueException var4) {
            var1 = var4;
         }

         if (var1 != null) {
            if (this.orb.transportDebugFlag) {
               this.dprint(".handleEvent: " + var1);
            }

            INTERNAL var2 = new INTERNAL("NoSuchThreadPoolException");
            var2.initCause((Throwable)var1);
            throw var2;
         }
      } else {
         if (this.orb.transportDebugFlag) {
            this.dprint(".handleEvent: doWork");
         }

         this.getWork().doWork();
      }

      if (this.orb.transportDebugFlag) {
         this.dprint(".handleEvent<-: " + this);
      }

   }

   public boolean shouldUseWorkerThreadForEvent() {
      return this.useWorkerThreadForEvent;
   }

   public void setUseWorkerThreadForEvent(boolean var1) {
      this.useWorkerThreadForEvent = var1;
   }

   public void setWork(Work var1) {
      this.work = var1;
   }

   public Work getWork() {
      return this.work;
   }

   private void dprint(String var1) {
      ORBUtility.dprint("EventHandlerBase", var1);
   }
}
