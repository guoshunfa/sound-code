package sun.nio.ch;

import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashMap;
import java.util.Iterator;

class KQueueSelectorImpl extends SelectorImpl {
   protected int fd0;
   protected int fd1;
   KQueueArrayWrapper kqueueWrapper;
   private int totalChannels;
   private HashMap<Integer, KQueueSelectorImpl.MapEntry> fdMap;
   private boolean closed = false;
   private Object interruptLock = new Object();
   private boolean interruptTriggered = false;
   private long updateCount;

   KQueueSelectorImpl(SelectorProvider var1) {
      super(var1);
      long var2 = IOUtil.makePipe(false);
      this.fd0 = (int)(var2 >>> 32);
      this.fd1 = (int)var2;

      try {
         this.kqueueWrapper = new KQueueArrayWrapper();
         this.kqueueWrapper.initInterrupt(this.fd0, this.fd1);
         this.fdMap = new HashMap();
         this.totalChannels = 1;
      } catch (Throwable var8) {
         try {
            FileDispatcherImpl.closeIntFD(this.fd0);
         } catch (IOException var7) {
            var8.addSuppressed(var7);
         }

         try {
            FileDispatcherImpl.closeIntFD(this.fd1);
         } catch (IOException var6) {
            var8.addSuppressed(var6);
         }

         throw var8;
      }
   }

   protected int doSelect(long var1) throws IOException {
      boolean var3 = false;
      if (this.closed) {
         throw new ClosedSelectorException();
      } else {
         this.processDeregisterQueue();

         int var7;
         try {
            this.begin();
            var7 = this.kqueueWrapper.poll(var1);
         } finally {
            this.end();
         }

         this.processDeregisterQueue();
         return this.updateSelectedKeys(var7);
      }
   }

   private int updateSelectedKeys(int var1) throws IOException {
      int var2 = 0;
      boolean var3 = false;
      ++this.updateCount;

      for(int var4 = 0; var4 < var1; ++var4) {
         int var5 = this.kqueueWrapper.getDescriptor(var4);
         if (var5 == this.fd0) {
            var3 = true;
         } else {
            KQueueSelectorImpl.MapEntry var6 = (KQueueSelectorImpl.MapEntry)this.fdMap.get(var5);
            if (var6 != null) {
               int var7 = this.kqueueWrapper.getReventOps(var4);
               SelectionKeyImpl var8 = var6.ski;
               if (this.selectedKeys.contains(var8)) {
                  if (var6.updateCount != this.updateCount) {
                     if (var8.channel.translateAndSetReadyOps(var7, var8)) {
                        ++var2;
                        var6.updateCount = this.updateCount;
                     }
                  } else {
                     var8.channel.translateAndUpdateReadyOps(var7, var8);
                  }
               } else {
                  var8.channel.translateAndSetReadyOps(var7, var8);
                  if ((var8.nioReadyOps() & var8.nioInterestOps()) != 0) {
                     this.selectedKeys.add(var8);
                     ++var2;
                     var6.updateCount = this.updateCount;
                  }
               }
            }
         }
      }

      if (var3) {
         synchronized(this.interruptLock) {
            IOUtil.drain(this.fd0);
            this.interruptTriggered = false;
         }
      }

      return var2;
   }

   protected void implClose() throws IOException {
      if (!this.closed) {
         this.closed = true;
         synchronized(this.interruptLock) {
            this.interruptTriggered = true;
         }

         FileDispatcherImpl.closeIntFD(this.fd0);
         FileDispatcherImpl.closeIntFD(this.fd1);
         if (this.kqueueWrapper != null) {
            this.kqueueWrapper.close();
            this.kqueueWrapper = null;
            this.selectedKeys = null;

            for(Iterator var1 = this.keys.iterator(); var1.hasNext(); var1.remove()) {
               SelectionKeyImpl var2 = (SelectionKeyImpl)var1.next();
               this.deregister(var2);
               SelectableChannel var3 = var2.channel();
               if (!var3.isOpen() && !var3.isRegistered()) {
                  ((SelChImpl)var3).kill();
               }
            }

            this.totalChannels = 0;
         }

         this.fd0 = -1;
         this.fd1 = -1;
      }

   }

   protected void implRegister(SelectionKeyImpl var1) {
      if (this.closed) {
         throw new ClosedSelectorException();
      } else {
         int var2 = IOUtil.fdVal(var1.channel.getFD());
         this.fdMap.put(var2, new KQueueSelectorImpl.MapEntry(var1));
         ++this.totalChannels;
         this.keys.add(var1);
      }
   }

   protected void implDereg(SelectionKeyImpl var1) throws IOException {
      int var2 = var1.channel.getFDVal();
      this.fdMap.remove(var2);
      this.kqueueWrapper.release(var1.channel);
      --this.totalChannels;
      this.keys.remove(var1);
      this.selectedKeys.remove(var1);
      this.deregister(var1);
      SelectableChannel var3 = var1.channel();
      if (!var3.isOpen() && !var3.isRegistered()) {
         ((SelChImpl)var3).kill();
      }

   }

   public void putEventOps(SelectionKeyImpl var1, int var2) {
      if (this.closed) {
         throw new ClosedSelectorException();
      } else {
         this.kqueueWrapper.setInterest(var1.channel, var2);
      }
   }

   public Selector wakeup() {
      synchronized(this.interruptLock) {
         if (!this.interruptTriggered) {
            this.kqueueWrapper.interrupt();
            this.interruptTriggered = true;
         }

         return this;
      }
   }

   private static class MapEntry {
      SelectionKeyImpl ski;
      long updateCount;

      MapEntry(SelectionKeyImpl var1) {
         this.ski = var1;
      }
   }
}
