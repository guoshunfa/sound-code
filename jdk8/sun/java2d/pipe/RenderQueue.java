package sun.java2d.pipe;

import java.util.HashSet;
import java.util.Set;
import sun.awt.SunToolkit;

public abstract class RenderQueue {
   private static final int BUFFER_SIZE = 32000;
   protected RenderBuffer buf = RenderBuffer.allocate(32000);
   protected Set refSet = new HashSet();

   protected RenderQueue() {
   }

   public final void lock() {
      SunToolkit.awtLock();
   }

   public final boolean tryLock() {
      return SunToolkit.awtTryLock();
   }

   public final void unlock() {
      SunToolkit.awtUnlock();
   }

   public final void addReference(Object var1) {
      this.refSet.add(var1);
   }

   public final RenderBuffer getBuffer() {
      return this.buf;
   }

   public final void ensureCapacity(int var1) {
      if (this.buf.remaining() < var1) {
         this.flushNow();
      }

   }

   public final void ensureCapacityAndAlignment(int var1, int var2) {
      this.ensureCapacity(var1 + 4);
      this.ensureAlignment(var2);
   }

   public final void ensureAlignment(int var1) {
      int var2 = this.buf.position() + var1;
      if ((var2 & 7) != 0) {
         this.buf.putInt(90);
      }

   }

   public abstract void flushNow();

   public abstract void flushAndInvokeNow(Runnable var1);

   public void flushNow(int var1) {
      this.buf.position((long)var1);
      this.flushNow();
   }
}
