package sun.nio.ch;

import java.io.IOException;
import sun.misc.Unsafe;

class KQueue {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static final int SIZEOF_KQUEUEEVENT = keventSize();
   private static final int OFFSET_IDENT = identOffset();
   private static final int OFFSET_FILTER = filterOffset();
   private static final int OFFSET_FLAGS = flagsOffset();
   static final int EVFILT_READ = -1;
   static final int EVFILT_WRITE = -2;
   static final int EV_ADD = 1;
   static final int EV_ONESHOT = 16;
   static final int EV_CLEAR = 32;

   private KQueue() {
   }

   static long allocatePollArray(int var0) {
      return unsafe.allocateMemory((long)(var0 * SIZEOF_KQUEUEEVENT));
   }

   static void freePollArray(long var0) {
      unsafe.freeMemory(var0);
   }

   static long getEvent(long var0, int var2) {
      return var0 + (long)(SIZEOF_KQUEUEEVENT * var2);
   }

   static int getDescriptor(long var0) {
      return unsafe.getInt(var0 + (long)OFFSET_IDENT);
   }

   static int getFilter(long var0) {
      return unsafe.getShort(var0 + (long)OFFSET_FILTER);
   }

   static int getFlags(long var0) {
      return unsafe.getShort(var0 + (long)OFFSET_FLAGS);
   }

   private static native int keventSize();

   private static native int identOffset();

   private static native int filterOffset();

   private static native int flagsOffset();

   static native int kqueue() throws IOException;

   static native int keventRegister(int var0, int var1, int var2, int var3);

   static native int keventPoll(int var0, long var1, int var3) throws IOException;

   static {
      IOUtil.load();
   }
}
