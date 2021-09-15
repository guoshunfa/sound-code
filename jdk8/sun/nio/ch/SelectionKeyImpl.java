package sun.nio.ch;

import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectionKey;

public class SelectionKeyImpl extends AbstractSelectionKey {
   final SelChImpl channel;
   public final SelectorImpl selector;
   private int index;
   private volatile int interestOps;
   private int readyOps;

   SelectionKeyImpl(SelChImpl var1, SelectorImpl var2) {
      this.channel = var1;
      this.selector = var2;
   }

   public SelectableChannel channel() {
      return (SelectableChannel)this.channel;
   }

   public Selector selector() {
      return this.selector;
   }

   int getIndex() {
      return this.index;
   }

   void setIndex(int var1) {
      this.index = var1;
   }

   private void ensureValid() {
      if (!this.isValid()) {
         throw new CancelledKeyException();
      }
   }

   public int interestOps() {
      this.ensureValid();
      return this.interestOps;
   }

   public SelectionKey interestOps(int var1) {
      this.ensureValid();
      return this.nioInterestOps(var1);
   }

   public int readyOps() {
      this.ensureValid();
      return this.readyOps;
   }

   public void nioReadyOps(int var1) {
      this.readyOps = var1;
   }

   public int nioReadyOps() {
      return this.readyOps;
   }

   public SelectionKey nioInterestOps(int var1) {
      if ((var1 & ~this.channel().validOps()) != 0) {
         throw new IllegalArgumentException();
      } else {
         this.channel.translateAndSetInterestOps(var1, this);
         this.interestOps = var1;
         return this;
      }
   }

   public int nioInterestOps() {
      return this.interestOps;
   }
}
