package sun.nio.ch;

import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;

abstract class AbstractPollSelectorImpl extends SelectorImpl {
   PollArrayWrapper pollWrapper;
   protected final int INIT_CAP = 10;
   protected SelectionKeyImpl[] channelArray;
   protected int channelOffset = 0;
   protected int totalChannels;
   private boolean closed = false;
   private Object closeLock = new Object();

   AbstractPollSelectorImpl(SelectorProvider var1, int var2, int var3) {
      super(var1);
      this.totalChannels = var2;
      this.channelOffset = var3;
   }

   public void putEventOps(SelectionKeyImpl var1, int var2) {
      synchronized(this.closeLock) {
         if (this.closed) {
            throw new ClosedSelectorException();
         } else {
            this.pollWrapper.putEventOps(var1.getIndex(), var2);
         }
      }
   }

   public Selector wakeup() {
      this.pollWrapper.interrupt();
      return this;
   }

   protected abstract int doSelect(long var1) throws IOException;

   protected void implClose() throws IOException {
      synchronized(this.closeLock) {
         if (!this.closed) {
            this.closed = true;

            for(int var2 = this.channelOffset; var2 < this.totalChannels; ++var2) {
               SelectionKeyImpl var3 = this.channelArray[var2];

               assert var3.getIndex() != -1;

               var3.setIndex(-1);
               this.deregister(var3);
               SelectableChannel var4 = this.channelArray[var2].channel();
               if (!var4.isOpen() && !var4.isRegistered()) {
                  ((SelChImpl)var4).kill();
               }
            }

            this.implCloseInterrupt();
            this.pollWrapper.free();
            this.pollWrapper = null;
            this.selectedKeys = null;
            this.channelArray = null;
            this.totalChannels = 0;
         }
      }
   }

   protected abstract void implCloseInterrupt() throws IOException;

   protected int updateSelectedKeys() {
      int var1 = 0;

      for(int var2 = this.channelOffset; var2 < this.totalChannels; ++var2) {
         int var3 = this.pollWrapper.getReventOps(var2);
         if (var3 != 0) {
            SelectionKeyImpl var4 = this.channelArray[var2];
            this.pollWrapper.putReventOps(var2, 0);
            if (this.selectedKeys.contains(var4)) {
               if (var4.channel.translateAndSetReadyOps(var3, var4)) {
                  ++var1;
               }
            } else {
               var4.channel.translateAndSetReadyOps(var3, var4);
               if ((var4.nioReadyOps() & var4.nioInterestOps()) != 0) {
                  this.selectedKeys.add(var4);
                  ++var1;
               }
            }
         }
      }

      return var1;
   }

   protected void implRegister(SelectionKeyImpl var1) {
      synchronized(this.closeLock) {
         if (this.closed) {
            throw new ClosedSelectorException();
         } else {
            if (this.channelArray.length == this.totalChannels) {
               int var3 = this.pollWrapper.totalChannels * 2;
               SelectionKeyImpl[] var4 = new SelectionKeyImpl[var3];

               for(int var5 = this.channelOffset; var5 < this.totalChannels; ++var5) {
                  var4[var5] = this.channelArray[var5];
               }

               this.channelArray = var4;
               this.pollWrapper.grow(var3);
            }

            this.channelArray[this.totalChannels] = var1;
            var1.setIndex(this.totalChannels);
            this.pollWrapper.addEntry(var1.channel);
            ++this.totalChannels;
            this.keys.add(var1);
         }
      }
   }

   protected void implDereg(SelectionKeyImpl var1) throws IOException {
      int var2 = var1.getIndex();

      assert var2 >= 0;

      if (var2 != this.totalChannels - 1) {
         SelectionKeyImpl var3 = this.channelArray[this.totalChannels - 1];
         this.channelArray[var2] = var3;
         var3.setIndex(var2);
         this.pollWrapper.release(var2);
         PollArrayWrapper.replaceEntry(this.pollWrapper, this.totalChannels - 1, this.pollWrapper, var2);
      } else {
         this.pollWrapper.release(var2);
      }

      this.channelArray[this.totalChannels - 1] = null;
      --this.totalChannels;
      --this.pollWrapper.totalChannels;
      var1.setIndex(-1);
      this.keys.remove(var1);
      this.selectedKeys.remove(var1);
      this.deregister(var1);
      SelectableChannel var4 = var1.channel();
      if (!var4.isOpen() && !var4.isRegistered()) {
         ((SelChImpl)var4).kill();
      }

   }
}
