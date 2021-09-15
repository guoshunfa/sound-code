package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import sun.nio.ch.Interruptible;

public abstract class AbstractSelector extends Selector {
   private AtomicBoolean selectorOpen = new AtomicBoolean(true);
   private final SelectorProvider provider;
   private final Set<SelectionKey> cancelledKeys = new HashSet();
   private Interruptible interruptor = null;

   protected AbstractSelector(SelectorProvider var1) {
      this.provider = var1;
   }

   void cancel(SelectionKey var1) {
      synchronized(this.cancelledKeys) {
         this.cancelledKeys.add(var1);
      }
   }

   public final void close() throws IOException {
      boolean var1 = this.selectorOpen.getAndSet(false);
      if (var1) {
         this.implCloseSelector();
      }
   }

   protected abstract void implCloseSelector() throws IOException;

   public final boolean isOpen() {
      return this.selectorOpen.get();
   }

   public final SelectorProvider provider() {
      return this.provider;
   }

   protected final Set<SelectionKey> cancelledKeys() {
      return this.cancelledKeys;
   }

   protected abstract SelectionKey register(AbstractSelectableChannel var1, int var2, Object var3);

   protected final void deregister(AbstractSelectionKey var1) {
      ((AbstractSelectableChannel)var1.channel()).removeKey(var1);
   }

   protected final void begin() {
      if (this.interruptor == null) {
         this.interruptor = new Interruptible() {
            public void interrupt(Thread var1) {
               AbstractSelector.this.wakeup();
            }
         };
      }

      AbstractInterruptibleChannel.blockedOn(this.interruptor);
      Thread var1 = Thread.currentThread();
      if (var1.isInterrupted()) {
         this.interruptor.interrupt(var1);
      }

   }

   protected final void end() {
      AbstractInterruptibleChannel.blockedOn((Interruptible)null);
   }
}
