package sun.nio.ch;

import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;

class PollSelectorImpl extends AbstractPollSelectorImpl {
   private int fd0;
   private int fd1;
   private Object interruptLock = new Object();
   private boolean interruptTriggered = false;

   PollSelectorImpl(SelectorProvider var1) {
      super(var1, 1, 1);
      long var2 = IOUtil.makePipe(false);
      this.fd0 = (int)(var2 >>> 32);
      this.fd1 = (int)var2;

      try {
         this.pollWrapper = new PollArrayWrapper(10);
         this.pollWrapper.initInterrupt(this.fd0, this.fd1);
         this.channelArray = new SelectionKeyImpl[10];
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
      if (this.channelArray == null) {
         throw new ClosedSelectorException();
      } else {
         this.processDeregisterQueue();

         try {
            this.begin();
            this.pollWrapper.poll(this.totalChannels, 0, var1);
         } finally {
            this.end();
         }

         this.processDeregisterQueue();
         int var3 = this.updateSelectedKeys();
         if (this.pollWrapper.getReventOps(0) != 0) {
            this.pollWrapper.putReventOps(0, 0);
            synchronized(this.interruptLock) {
               IOUtil.drain(this.fd0);
               this.interruptTriggered = false;
            }
         }

         return var3;
      }
   }

   protected void implCloseInterrupt() throws IOException {
      synchronized(this.interruptLock) {
         this.interruptTriggered = true;
      }

      FileDispatcherImpl.closeIntFD(this.fd0);
      FileDispatcherImpl.closeIntFD(this.fd1);
      this.fd0 = -1;
      this.fd1 = -1;
      this.pollWrapper.release(0);
   }

   public Selector wakeup() {
      synchronized(this.interruptLock) {
         if (!this.interruptTriggered) {
            this.pollWrapper.interrupt();
            this.interruptTriggered = true;
         }

         return this;
      }
   }
}
