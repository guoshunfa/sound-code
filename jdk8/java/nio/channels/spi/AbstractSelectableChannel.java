package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public abstract class AbstractSelectableChannel extends SelectableChannel {
   private final SelectorProvider provider;
   private SelectionKey[] keys = null;
   private int keyCount = 0;
   private final Object keyLock = new Object();
   private final Object regLock = new Object();
   boolean blocking = true;

   protected AbstractSelectableChannel(SelectorProvider var1) {
      this.provider = var1;
   }

   public final SelectorProvider provider() {
      return this.provider;
   }

   private void addKey(SelectionKey var1) {
      assert Thread.holdsLock(this.keyLock);

      int var2 = 0;
      if (this.keys != null && this.keyCount < this.keys.length) {
         for(var2 = 0; var2 < this.keys.length && this.keys[var2] != null; ++var2) {
         }
      } else if (this.keys == null) {
         this.keys = new SelectionKey[3];
      } else {
         int var3 = this.keys.length * 2;
         SelectionKey[] var4 = new SelectionKey[var3];

         for(var2 = 0; var2 < this.keys.length; ++var2) {
            var4[var2] = this.keys[var2];
         }

         this.keys = var4;
         var2 = this.keyCount;
      }

      this.keys[var2] = var1;
      ++this.keyCount;
   }

   private SelectionKey findKey(Selector var1) {
      synchronized(this.keyLock) {
         if (this.keys == null) {
            return null;
         } else {
            for(int var3 = 0; var3 < this.keys.length; ++var3) {
               if (this.keys[var3] != null && this.keys[var3].selector() == var1) {
                  return this.keys[var3];
               }
            }

            return null;
         }
      }
   }

   void removeKey(SelectionKey var1) {
      synchronized(this.keyLock) {
         for(int var3 = 0; var3 < this.keys.length; ++var3) {
            if (this.keys[var3] == var1) {
               this.keys[var3] = null;
               --this.keyCount;
            }
         }

         ((AbstractSelectionKey)var1).invalidate();
      }
   }

   private boolean haveValidKeys() {
      synchronized(this.keyLock) {
         if (this.keyCount == 0) {
            return false;
         } else {
            for(int var2 = 0; var2 < this.keys.length; ++var2) {
               if (this.keys[var2] != null && this.keys[var2].isValid()) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public final boolean isRegistered() {
      synchronized(this.keyLock) {
         return this.keyCount != 0;
      }
   }

   public final SelectionKey keyFor(Selector var1) {
      return this.findKey(var1);
   }

   public final SelectionKey register(Selector var1, int var2, Object var3) throws ClosedChannelException {
      synchronized(this.regLock) {
         if (!this.isOpen()) {
            throw new ClosedChannelException();
         } else if ((var2 & ~this.validOps()) != 0) {
            throw new IllegalArgumentException();
         } else if (this.blocking) {
            throw new IllegalBlockingModeException();
         } else {
            SelectionKey var5 = this.findKey(var1);
            if (var5 != null) {
               var5.interestOps(var2);
               var5.attach(var3);
            }

            if (var5 == null) {
               synchronized(this.keyLock) {
                  if (!this.isOpen()) {
                     throw new ClosedChannelException();
                  }

                  var5 = ((AbstractSelector)var1).register(this, var2, var3);
                  this.addKey(var5);
               }
            }

            return var5;
         }
      }
   }

   protected final void implCloseChannel() throws IOException {
      this.implCloseSelectableChannel();
      synchronized(this.keyLock) {
         int var2 = this.keys == null ? 0 : this.keys.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            SelectionKey var4 = this.keys[var3];
            if (var4 != null) {
               var4.cancel();
            }
         }

      }
   }

   protected abstract void implCloseSelectableChannel() throws IOException;

   public final boolean isBlocking() {
      synchronized(this.regLock) {
         return this.blocking;
      }
   }

   public final Object blockingLock() {
      return this.regLock;
   }

   public final SelectableChannel configureBlocking(boolean var1) throws IOException {
      synchronized(this.regLock) {
         if (!this.isOpen()) {
            throw new ClosedChannelException();
         } else if (this.blocking == var1) {
            return this;
         } else if (var1 && this.haveValidKeys()) {
            throw new IllegalBlockingModeException();
         } else {
            this.implConfigureBlocking(var1);
            this.blocking = var1;
            return this;
         }
      }
   }

   protected abstract void implConfigureBlocking(boolean var1) throws IOException;
}
