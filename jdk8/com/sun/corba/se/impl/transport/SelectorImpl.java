package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.pept.transport.ListenerThread;
import com.sun.corba.se.pept.transport.ReaderThread;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchThreadPoolException;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class SelectorImpl extends Thread implements Selector {
   private ORB orb;
   private java.nio.channels.Selector selector;
   private long timeout;
   private List deferredRegistrations;
   private List interestOpsList;
   private HashMap listenerThreads;
   private Map readerThreads;
   private boolean selectorStarted;
   private volatile boolean closed;
   private ORBUtilSystemException wrapper;

   public SelectorImpl(ORB var1) {
      this.orb = var1;
      this.selector = null;
      this.selectorStarted = false;
      this.timeout = 60000L;
      this.deferredRegistrations = new ArrayList();
      this.interestOpsList = new ArrayList();
      this.listenerThreads = new HashMap();
      this.readerThreads = Collections.synchronizedMap(new HashMap());
      this.closed = false;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.transport");
   }

   public void setTimeout(long var1) {
      this.timeout = var1;
   }

   public long getTimeout() {
      return this.timeout;
   }

   public void registerInterestOps(EventHandler var1) {
      if (this.orb.transportDebugFlag) {
         this.dprint(".registerInterestOps:-> " + var1);
      }

      SelectionKey var2 = var1.getSelectionKey();
      if (var2.isValid()) {
         int var3 = var1.getInterestOps();
         SelectorImpl.SelectionKeyAndOp var4 = new SelectorImpl.SelectionKeyAndOp(var2, var3);
         synchronized(this.interestOpsList) {
            this.interestOpsList.add(var4);
         }

         try {
            if (this.selector != null) {
               this.selector.wakeup();
            }
         } catch (Throwable var8) {
            if (this.orb.transportDebugFlag) {
               this.dprint(".registerInterestOps: selector.wakeup: ", var8);
            }
         }
      } else {
         this.wrapper.selectionKeyInvalid(var1.toString());
         if (this.orb.transportDebugFlag) {
            this.dprint(".registerInterestOps: EventHandler SelectionKey not valid " + var1);
         }
      }

      if (this.orb.transportDebugFlag) {
         this.dprint(".registerInterestOps:<- ");
      }

   }

   public void registerForEvent(EventHandler var1) {
      if (this.orb.transportDebugFlag) {
         this.dprint(".registerForEvent: " + var1);
      }

      if (this.isClosed()) {
         if (this.orb.transportDebugFlag) {
            this.dprint(".registerForEvent: closed: " + var1);
         }

      } else if (var1.shouldUseSelectThreadToWait()) {
         synchronized(this.deferredRegistrations) {
            this.deferredRegistrations.add(var1);
         }

         if (!this.selectorStarted) {
            this.startSelector();
         }

         this.selector.wakeup();
      } else {
         switch(var1.getInterestOps()) {
         case 1:
            this.createReaderThread(var1);
            break;
         case 16:
            this.createListenerThread(var1);
            break;
         default:
            if (this.orb.transportDebugFlag) {
               this.dprint(".registerForEvent: default: " + var1);
            }

            throw new RuntimeException("SelectorImpl.registerForEvent: unknown interest ops");
         }

      }
   }

   public void unregisterForEvent(EventHandler var1) {
      if (this.orb.transportDebugFlag) {
         this.dprint(".unregisterForEvent: " + var1);
      }

      if (this.isClosed()) {
         if (this.orb.transportDebugFlag) {
            this.dprint(".unregisterForEvent: closed: " + var1);
         }

      } else if (var1.shouldUseSelectThreadToWait()) {
         SelectionKey var2;
         synchronized(this.deferredRegistrations) {
            var2 = var1.getSelectionKey();
         }

         if (var2 != null) {
            var2.cancel();
         }

         if (this.selector != null) {
            this.selector.wakeup();
         }

      } else {
         switch(var1.getInterestOps()) {
         case 1:
            this.destroyReaderThread(var1);
            break;
         case 16:
            this.destroyListenerThread(var1);
            break;
         default:
            if (this.orb.transportDebugFlag) {
               this.dprint(".unregisterForEvent: default: " + var1);
            }

            throw new RuntimeException("SelectorImpl.uregisterForEvent: unknown interest ops");
         }

      }
   }

   public void close() {
      if (this.orb.transportDebugFlag) {
         this.dprint(".close");
      }

      if (this.isClosed()) {
         if (this.orb.transportDebugFlag) {
            this.dprint(".close: already closed");
         }

      } else {
         this.setClosed(true);
         Iterator var1 = this.listenerThreads.values().iterator();

         while(var1.hasNext()) {
            ListenerThread var2 = (ListenerThread)var1.next();
            var2.close();
         }

         var1 = this.readerThreads.values().iterator();

         while(var1.hasNext()) {
            ReaderThread var4 = (ReaderThread)var1.next();
            var4.close();
         }

         this.clearDeferredRegistrations();

         try {
            if (this.selector != null) {
               this.selector.wakeup();
            }
         } catch (Throwable var3) {
            if (this.orb.transportDebugFlag) {
               this.dprint(".close: selector.wakeup: ", var3);
            }
         }

      }
   }

   public void run() {
      this.setName("SelectorThread");

      while(!this.closed) {
         try {
            int var1 = 0;
            if (this.timeout == 0L && this.orb.transportDebugFlag) {
               this.dprint(".run: Beginning of selection cycle");
            }

            this.handleDeferredRegistrations();
            this.enableInterestOps();

            try {
               var1 = this.selector.select(this.timeout);
            } catch (IOException var8) {
               if (this.orb.transportDebugFlag) {
                  this.dprint(".run: selector.select: ", var8);
               }
            } catch (ClosedSelectorException var9) {
               if (this.orb.transportDebugFlag) {
                  this.dprint(".run: selector.select: ", var9);
               }
               break;
            }

            if (this.closed) {
               break;
            }

            Iterator var2 = this.selector.selectedKeys().iterator();
            if (this.orb.transportDebugFlag && var2.hasNext()) {
               this.dprint(".run: n = " + var1);
            }

            while(var2.hasNext()) {
               SelectionKey var3 = (SelectionKey)var2.next();
               var2.remove();
               EventHandler var4 = (EventHandler)var3.attachment();

               try {
                  var4.handleEvent();
               } catch (Throwable var7) {
                  if (this.orb.transportDebugFlag) {
                     this.dprint(".run: eventHandler.handleEvent", var7);
                  }
               }
            }

            if (this.timeout == 0L && this.orb.transportDebugFlag) {
               this.dprint(".run: End of selection cycle");
            }
         } catch (Throwable var10) {
            if (this.orb.transportDebugFlag) {
               this.dprint(".run: ignoring", var10);
            }
         }
      }

      try {
         if (this.selector != null) {
            if (this.orb.transportDebugFlag) {
               this.dprint(".run: selector.close ");
            }

            this.selector.close();
         }
      } catch (Throwable var6) {
         if (this.orb.transportDebugFlag) {
            this.dprint(".run: selector.close: ", var6);
         }
      }

   }

   private void clearDeferredRegistrations() {
      synchronized(this.deferredRegistrations) {
         int var2 = this.deferredRegistrations.size();
         if (this.orb.transportDebugFlag) {
            this.dprint(".clearDeferredRegistrations:deferred list size == " + var2);
         }

         for(int var3 = 0; var3 < var2; ++var3) {
            EventHandler var4 = (EventHandler)this.deferredRegistrations.get(var3);
            if (this.orb.transportDebugFlag) {
               this.dprint(".clearDeferredRegistrations: " + var4);
            }

            SelectableChannel var5 = var4.getChannel();
            SelectionKey var6 = null;

            try {
               if (this.orb.transportDebugFlag) {
                  this.dprint(".clearDeferredRegistrations:close channel == " + var5);
                  this.dprint(".clearDeferredRegistrations:close channel class == " + var5.getClass().getName());
               }

               var5.close();
               var6 = var4.getSelectionKey();
               if (var6 != null) {
                  var6.cancel();
                  var6.attach((Object)null);
               }
            } catch (IOException var9) {
               if (this.orb.transportDebugFlag) {
                  this.dprint(".clearDeferredRegistrations: ", var9);
               }
            }
         }

         this.deferredRegistrations.clear();
      }
   }

   private synchronized boolean isClosed() {
      return this.closed;
   }

   private synchronized void setClosed(boolean var1) {
      this.closed = var1;
   }

   private void startSelector() {
      try {
         this.selector = java.nio.channels.Selector.open();
      } catch (IOException var3) {
         if (this.orb.transportDebugFlag) {
            this.dprint(".startSelector: Selector.open: IOException: ", var3);
         }

         RuntimeException var2 = new RuntimeException(".startSelector: Selector.open exception");
         var2.initCause(var3);
         throw var2;
      }

      this.setDaemon(true);
      this.start();
      this.selectorStarted = true;
      if (this.orb.transportDebugFlag) {
         this.dprint(".startSelector: selector.start completed.");
      }

   }

   private void handleDeferredRegistrations() {
      synchronized(this.deferredRegistrations) {
         int var2 = this.deferredRegistrations.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            EventHandler var4 = (EventHandler)this.deferredRegistrations.get(var3);
            if (this.orb.transportDebugFlag) {
               this.dprint(".handleDeferredRegistrations: " + var4);
            }

            SelectableChannel var5 = var4.getChannel();
            SelectionKey var6 = null;

            try {
               var6 = var5.register(this.selector, var4.getInterestOps(), var4);
            } catch (ClosedChannelException var9) {
               if (this.orb.transportDebugFlag) {
                  this.dprint(".handleDeferredRegistrations: ", var9);
               }
            }

            var4.setSelectionKey(var6);
         }

         this.deferredRegistrations.clear();
      }
   }

   private void enableInterestOps() {
      synchronized(this.interestOpsList) {
         int var2 = this.interestOpsList.size();
         if (var2 > 0) {
            if (this.orb.transportDebugFlag) {
               this.dprint(".enableInterestOps:->");
            }

            SelectionKey var3 = null;
            SelectorImpl.SelectionKeyAndOp var4 = null;
            boolean var6 = false;

            for(int var7 = 0; var7 < var2; ++var7) {
               var4 = (SelectorImpl.SelectionKeyAndOp)this.interestOpsList.get(var7);
               var3 = var4.selectionKey;
               if (var3.isValid()) {
                  if (this.orb.transportDebugFlag) {
                     this.dprint(".enableInterestOps: " + var4);
                  }

                  int var5 = var4.keyOp;
                  int var10 = var3.interestOps();
                  var3.interestOps(var10 | var5);
               }
            }

            this.interestOpsList.clear();
            if (this.orb.transportDebugFlag) {
               this.dprint(".enableInterestOps:<-");
            }
         }

      }
   }

   private void createListenerThread(EventHandler var1) {
      if (this.orb.transportDebugFlag) {
         this.dprint(".createListenerThread: " + var1);
      }

      Acceptor var2 = var1.getAcceptor();
      ListenerThreadImpl var3 = new ListenerThreadImpl(this.orb, var2, this);
      this.listenerThreads.put(var1, var3);
      Object var4 = null;

      try {
         this.orb.getThreadPoolManager().getThreadPool(0).getWorkQueue(0).addWork((Work)var3);
      } catch (NoSuchThreadPoolException var6) {
         var4 = var6;
      } catch (NoSuchWorkQueueException var7) {
         var4 = var7;
      }

      if (var4 != null) {
         RuntimeException var5 = new RuntimeException(((Throwable)var4).toString());
         var5.initCause((Throwable)var4);
         throw var5;
      }
   }

   private void destroyListenerThread(EventHandler var1) {
      if (this.orb.transportDebugFlag) {
         this.dprint(".destroyListenerThread: " + var1);
      }

      ListenerThread var2 = (ListenerThread)this.listenerThreads.get(var1);
      if (var2 == null) {
         if (this.orb.transportDebugFlag) {
            this.dprint(".destroyListenerThread: cannot find ListenerThread - ignoring.");
         }

      } else {
         this.listenerThreads.remove(var1);
         var2.close();
      }
   }

   private void createReaderThread(EventHandler var1) {
      if (this.orb.transportDebugFlag) {
         this.dprint(".createReaderThread: " + var1);
      }

      Connection var2 = var1.getConnection();
      ReaderThreadImpl var3 = new ReaderThreadImpl(this.orb, var2, this);
      this.readerThreads.put(var1, var3);
      Object var4 = null;

      try {
         this.orb.getThreadPoolManager().getThreadPool(0).getWorkQueue(0).addWork((Work)var3);
      } catch (NoSuchThreadPoolException var6) {
         var4 = var6;
      } catch (NoSuchWorkQueueException var7) {
         var4 = var7;
      }

      if (var4 != null) {
         RuntimeException var5 = new RuntimeException(((Throwable)var4).toString());
         var5.initCause((Throwable)var4);
         throw var5;
      }
   }

   private void destroyReaderThread(EventHandler var1) {
      if (this.orb.transportDebugFlag) {
         this.dprint(".destroyReaderThread: " + var1);
      }

      ReaderThread var2 = (ReaderThread)this.readerThreads.get(var1);
      if (var2 == null) {
         if (this.orb.transportDebugFlag) {
            this.dprint(".destroyReaderThread: cannot find ReaderThread - ignoring.");
         }

      } else {
         this.readerThreads.remove(var1);
         var2.close();
      }
   }

   private void dprint(String var1) {
      ORBUtility.dprint("SelectorImpl", var1);
   }

   protected void dprint(String var1, Throwable var2) {
      this.dprint(var1);
      var2.printStackTrace(System.out);
   }

   private class SelectionKeyAndOp {
      public int keyOp;
      public SelectionKey selectionKey;

      public SelectionKeyAndOp(SelectionKey var2, int var3) {
         this.selectionKey = var2;
         this.keyOp = var3;
      }
   }
}
