package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.Channel;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.InterruptibleChannel;
import sun.misc.SharedSecrets;
import sun.nio.ch.Interruptible;

public abstract class AbstractInterruptibleChannel implements Channel, InterruptibleChannel {
   private final Object closeLock = new Object();
   private volatile boolean open = true;
   private Interruptible interruptor;
   private volatile Thread interrupted;

   protected AbstractInterruptibleChannel() {
   }

   public final void close() throws IOException {
      synchronized(this.closeLock) {
         if (this.open) {
            this.open = false;
            this.implCloseChannel();
         }
      }
   }

   protected abstract void implCloseChannel() throws IOException;

   public final boolean isOpen() {
      return this.open;
   }

   protected final void begin() {
      if (this.interruptor == null) {
         this.interruptor = new Interruptible() {
            public void interrupt(Thread var1) {
               synchronized(AbstractInterruptibleChannel.this.closeLock) {
                  if (AbstractInterruptibleChannel.this.open) {
                     AbstractInterruptibleChannel.this.open = false;
                     AbstractInterruptibleChannel.this.interrupted = var1;

                     try {
                        AbstractInterruptibleChannel.this.implCloseChannel();
                     } catch (IOException var5) {
                     }

                  }
               }
            }
         };
      }

      blockedOn(this.interruptor);
      Thread var1 = Thread.currentThread();
      if (var1.isInterrupted()) {
         this.interruptor.interrupt(var1);
      }

   }

   protected final void end(boolean var1) throws AsynchronousCloseException {
      blockedOn((Interruptible)null);
      Thread var2 = this.interrupted;
      if (var2 != null && var2 == Thread.currentThread()) {
         var2 = null;
         throw new ClosedByInterruptException();
      } else if (!var1 && !this.open) {
         throw new AsynchronousCloseException();
      }
   }

   static void blockedOn(Interruptible var0) {
      SharedSecrets.getJavaLangAccess().blockedOn(Thread.currentThread(), var0);
   }
}
