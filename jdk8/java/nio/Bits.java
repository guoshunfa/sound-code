package java.nio;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicLong;
import sun.misc.JavaLangRefAccess;
import sun.misc.JavaNioAccess;
import sun.misc.SharedSecrets;
import sun.misc.Unsafe;
import sun.misc.VM;
import sun.security.action.GetPropertyAction;

class Bits {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static final ByteOrder byteOrder;
   private static int pageSize;
   private static boolean unaligned;
   private static boolean unalignedKnown;
   private static volatile long maxMemory;
   private static final AtomicLong reservedMemory;
   private static final AtomicLong totalCapacity;
   private static final AtomicLong count;
   private static volatile boolean memoryLimitSet;
   private static final int MAX_SLEEPS = 9;
   static final int JNI_COPY_TO_ARRAY_THRESHOLD = 6;
   static final int JNI_COPY_FROM_ARRAY_THRESHOLD = 6;
   static final long UNSAFE_COPY_THRESHOLD = 1048576L;

   private Bits() {
   }

   static short swap(short var0) {
      return Short.reverseBytes(var0);
   }

   static char swap(char var0) {
      return Character.reverseBytes(var0);
   }

   static int swap(int var0) {
      return Integer.reverseBytes(var0);
   }

   static long swap(long var0) {
      return Long.reverseBytes(var0);
   }

   private static char makeChar(byte var0, byte var1) {
      return (char)(var0 << 8 | var1 & 255);
   }

   static char getCharL(ByteBuffer var0, int var1) {
      return makeChar(var0._get(var1 + 1), var0._get(var1));
   }

   static char getCharL(long var0) {
      return makeChar(_get(var0 + 1L), _get(var0));
   }

   static char getCharB(ByteBuffer var0, int var1) {
      return makeChar(var0._get(var1), var0._get(var1 + 1));
   }

   static char getCharB(long var0) {
      return makeChar(_get(var0), _get(var0 + 1L));
   }

   static char getChar(ByteBuffer var0, int var1, boolean var2) {
      return var2 ? getCharB(var0, var1) : getCharL(var0, var1);
   }

   static char getChar(long var0, boolean var2) {
      return var2 ? getCharB(var0) : getCharL(var0);
   }

   private static byte char1(char var0) {
      return (byte)(var0 >> 8);
   }

   private static byte char0(char var0) {
      return (byte)var0;
   }

   static void putCharL(ByteBuffer var0, int var1, char var2) {
      var0._put(var1, char0(var2));
      var0._put(var1 + 1, char1(var2));
   }

   static void putCharL(long var0, char var2) {
      _put(var0, char0(var2));
      _put(var0 + 1L, char1(var2));
   }

   static void putCharB(ByteBuffer var0, int var1, char var2) {
      var0._put(var1, char1(var2));
      var0._put(var1 + 1, char0(var2));
   }

   static void putCharB(long var0, char var2) {
      _put(var0, char1(var2));
      _put(var0 + 1L, char0(var2));
   }

   static void putChar(ByteBuffer var0, int var1, char var2, boolean var3) {
      if (var3) {
         putCharB(var0, var1, var2);
      } else {
         putCharL(var0, var1, var2);
      }

   }

   static void putChar(long var0, char var2, boolean var3) {
      if (var3) {
         putCharB(var0, var2);
      } else {
         putCharL(var0, var2);
      }

   }

   private static short makeShort(byte var0, byte var1) {
      return (short)(var0 << 8 | var1 & 255);
   }

   static short getShortL(ByteBuffer var0, int var1) {
      return makeShort(var0._get(var1 + 1), var0._get(var1));
   }

   static short getShortL(long var0) {
      return makeShort(_get(var0 + 1L), _get(var0));
   }

   static short getShortB(ByteBuffer var0, int var1) {
      return makeShort(var0._get(var1), var0._get(var1 + 1));
   }

   static short getShortB(long var0) {
      return makeShort(_get(var0), _get(var0 + 1L));
   }

   static short getShort(ByteBuffer var0, int var1, boolean var2) {
      return var2 ? getShortB(var0, var1) : getShortL(var0, var1);
   }

   static short getShort(long var0, boolean var2) {
      return var2 ? getShortB(var0) : getShortL(var0);
   }

   private static byte short1(short var0) {
      return (byte)(var0 >> 8);
   }

   private static byte short0(short var0) {
      return (byte)var0;
   }

   static void putShortL(ByteBuffer var0, int var1, short var2) {
      var0._put(var1, short0(var2));
      var0._put(var1 + 1, short1(var2));
   }

   static void putShortL(long var0, short var2) {
      _put(var0, short0(var2));
      _put(var0 + 1L, short1(var2));
   }

   static void putShortB(ByteBuffer var0, int var1, short var2) {
      var0._put(var1, short1(var2));
      var0._put(var1 + 1, short0(var2));
   }

   static void putShortB(long var0, short var2) {
      _put(var0, short1(var2));
      _put(var0 + 1L, short0(var2));
   }

   static void putShort(ByteBuffer var0, int var1, short var2, boolean var3) {
      if (var3) {
         putShortB(var0, var1, var2);
      } else {
         putShortL(var0, var1, var2);
      }

   }

   static void putShort(long var0, short var2, boolean var3) {
      if (var3) {
         putShortB(var0, var2);
      } else {
         putShortL(var0, var2);
      }

   }

   private static int makeInt(byte var0, byte var1, byte var2, byte var3) {
      return var0 << 24 | (var1 & 255) << 16 | (var2 & 255) << 8 | var3 & 255;
   }

   static int getIntL(ByteBuffer var0, int var1) {
      return makeInt(var0._get(var1 + 3), var0._get(var1 + 2), var0._get(var1 + 1), var0._get(var1));
   }

   static int getIntL(long var0) {
      return makeInt(_get(var0 + 3L), _get(var0 + 2L), _get(var0 + 1L), _get(var0));
   }

   static int getIntB(ByteBuffer var0, int var1) {
      return makeInt(var0._get(var1), var0._get(var1 + 1), var0._get(var1 + 2), var0._get(var1 + 3));
   }

   static int getIntB(long var0) {
      return makeInt(_get(var0), _get(var0 + 1L), _get(var0 + 2L), _get(var0 + 3L));
   }

   static int getInt(ByteBuffer var0, int var1, boolean var2) {
      return var2 ? getIntB(var0, var1) : getIntL(var0, var1);
   }

   static int getInt(long var0, boolean var2) {
      return var2 ? getIntB(var0) : getIntL(var0);
   }

   private static byte int3(int var0) {
      return (byte)(var0 >> 24);
   }

   private static byte int2(int var0) {
      return (byte)(var0 >> 16);
   }

   private static byte int1(int var0) {
      return (byte)(var0 >> 8);
   }

   private static byte int0(int var0) {
      return (byte)var0;
   }

   static void putIntL(ByteBuffer var0, int var1, int var2) {
      var0._put(var1 + 3, int3(var2));
      var0._put(var1 + 2, int2(var2));
      var0._put(var1 + 1, int1(var2));
      var0._put(var1, int0(var2));
   }

   static void putIntL(long var0, int var2) {
      _put(var0 + 3L, int3(var2));
      _put(var0 + 2L, int2(var2));
      _put(var0 + 1L, int1(var2));
      _put(var0, int0(var2));
   }

   static void putIntB(ByteBuffer var0, int var1, int var2) {
      var0._put(var1, int3(var2));
      var0._put(var1 + 1, int2(var2));
      var0._put(var1 + 2, int1(var2));
      var0._put(var1 + 3, int0(var2));
   }

   static void putIntB(long var0, int var2) {
      _put(var0, int3(var2));
      _put(var0 + 1L, int2(var2));
      _put(var0 + 2L, int1(var2));
      _put(var0 + 3L, int0(var2));
   }

   static void putInt(ByteBuffer var0, int var1, int var2, boolean var3) {
      if (var3) {
         putIntB(var0, var1, var2);
      } else {
         putIntL(var0, var1, var2);
      }

   }

   static void putInt(long var0, int var2, boolean var3) {
      if (var3) {
         putIntB(var0, var2);
      } else {
         putIntL(var0, var2);
      }

   }

   private static long makeLong(byte var0, byte var1, byte var2, byte var3, byte var4, byte var5, byte var6, byte var7) {
      return (long)var0 << 56 | ((long)var1 & 255L) << 48 | ((long)var2 & 255L) << 40 | ((long)var3 & 255L) << 32 | ((long)var4 & 255L) << 24 | ((long)var5 & 255L) << 16 | ((long)var6 & 255L) << 8 | (long)var7 & 255L;
   }

   static long getLongL(ByteBuffer var0, int var1) {
      return makeLong(var0._get(var1 + 7), var0._get(var1 + 6), var0._get(var1 + 5), var0._get(var1 + 4), var0._get(var1 + 3), var0._get(var1 + 2), var0._get(var1 + 1), var0._get(var1));
   }

   static long getLongL(long var0) {
      return makeLong(_get(var0 + 7L), _get(var0 + 6L), _get(var0 + 5L), _get(var0 + 4L), _get(var0 + 3L), _get(var0 + 2L), _get(var0 + 1L), _get(var0));
   }

   static long getLongB(ByteBuffer var0, int var1) {
      return makeLong(var0._get(var1), var0._get(var1 + 1), var0._get(var1 + 2), var0._get(var1 + 3), var0._get(var1 + 4), var0._get(var1 + 5), var0._get(var1 + 6), var0._get(var1 + 7));
   }

   static long getLongB(long var0) {
      return makeLong(_get(var0), _get(var0 + 1L), _get(var0 + 2L), _get(var0 + 3L), _get(var0 + 4L), _get(var0 + 5L), _get(var0 + 6L), _get(var0 + 7L));
   }

   static long getLong(ByteBuffer var0, int var1, boolean var2) {
      return var2 ? getLongB(var0, var1) : getLongL(var0, var1);
   }

   static long getLong(long var0, boolean var2) {
      return var2 ? getLongB(var0) : getLongL(var0);
   }

   private static byte long7(long var0) {
      return (byte)((int)(var0 >> 56));
   }

   private static byte long6(long var0) {
      return (byte)((int)(var0 >> 48));
   }

   private static byte long5(long var0) {
      return (byte)((int)(var0 >> 40));
   }

   private static byte long4(long var0) {
      return (byte)((int)(var0 >> 32));
   }

   private static byte long3(long var0) {
      return (byte)((int)(var0 >> 24));
   }

   private static byte long2(long var0) {
      return (byte)((int)(var0 >> 16));
   }

   private static byte long1(long var0) {
      return (byte)((int)(var0 >> 8));
   }

   private static byte long0(long var0) {
      return (byte)((int)var0);
   }

   static void putLongL(ByteBuffer var0, int var1, long var2) {
      var0._put(var1 + 7, long7(var2));
      var0._put(var1 + 6, long6(var2));
      var0._put(var1 + 5, long5(var2));
      var0._put(var1 + 4, long4(var2));
      var0._put(var1 + 3, long3(var2));
      var0._put(var1 + 2, long2(var2));
      var0._put(var1 + 1, long1(var2));
      var0._put(var1, long0(var2));
   }

   static void putLongL(long var0, long var2) {
      _put(var0 + 7L, long7(var2));
      _put(var0 + 6L, long6(var2));
      _put(var0 + 5L, long5(var2));
      _put(var0 + 4L, long4(var2));
      _put(var0 + 3L, long3(var2));
      _put(var0 + 2L, long2(var2));
      _put(var0 + 1L, long1(var2));
      _put(var0, long0(var2));
   }

   static void putLongB(ByteBuffer var0, int var1, long var2) {
      var0._put(var1, long7(var2));
      var0._put(var1 + 1, long6(var2));
      var0._put(var1 + 2, long5(var2));
      var0._put(var1 + 3, long4(var2));
      var0._put(var1 + 4, long3(var2));
      var0._put(var1 + 5, long2(var2));
      var0._put(var1 + 6, long1(var2));
      var0._put(var1 + 7, long0(var2));
   }

   static void putLongB(long var0, long var2) {
      _put(var0, long7(var2));
      _put(var0 + 1L, long6(var2));
      _put(var0 + 2L, long5(var2));
      _put(var0 + 3L, long4(var2));
      _put(var0 + 4L, long3(var2));
      _put(var0 + 5L, long2(var2));
      _put(var0 + 6L, long1(var2));
      _put(var0 + 7L, long0(var2));
   }

   static void putLong(ByteBuffer var0, int var1, long var2, boolean var4) {
      if (var4) {
         putLongB(var0, var1, var2);
      } else {
         putLongL(var0, var1, var2);
      }

   }

   static void putLong(long var0, long var2, boolean var4) {
      if (var4) {
         putLongB(var0, var2);
      } else {
         putLongL(var0, var2);
      }

   }

   static float getFloatL(ByteBuffer var0, int var1) {
      return Float.intBitsToFloat(getIntL(var0, var1));
   }

   static float getFloatL(long var0) {
      return Float.intBitsToFloat(getIntL(var0));
   }

   static float getFloatB(ByteBuffer var0, int var1) {
      return Float.intBitsToFloat(getIntB(var0, var1));
   }

   static float getFloatB(long var0) {
      return Float.intBitsToFloat(getIntB(var0));
   }

   static float getFloat(ByteBuffer var0, int var1, boolean var2) {
      return var2 ? getFloatB(var0, var1) : getFloatL(var0, var1);
   }

   static float getFloat(long var0, boolean var2) {
      return var2 ? getFloatB(var0) : getFloatL(var0);
   }

   static void putFloatL(ByteBuffer var0, int var1, float var2) {
      putIntL(var0, var1, Float.floatToRawIntBits(var2));
   }

   static void putFloatL(long var0, float var2) {
      putIntL(var0, Float.floatToRawIntBits(var2));
   }

   static void putFloatB(ByteBuffer var0, int var1, float var2) {
      putIntB(var0, var1, Float.floatToRawIntBits(var2));
   }

   static void putFloatB(long var0, float var2) {
      putIntB(var0, Float.floatToRawIntBits(var2));
   }

   static void putFloat(ByteBuffer var0, int var1, float var2, boolean var3) {
      if (var3) {
         putFloatB(var0, var1, var2);
      } else {
         putFloatL(var0, var1, var2);
      }

   }

   static void putFloat(long var0, float var2, boolean var3) {
      if (var3) {
         putFloatB(var0, var2);
      } else {
         putFloatL(var0, var2);
      }

   }

   static double getDoubleL(ByteBuffer var0, int var1) {
      return Double.longBitsToDouble(getLongL(var0, var1));
   }

   static double getDoubleL(long var0) {
      return Double.longBitsToDouble(getLongL(var0));
   }

   static double getDoubleB(ByteBuffer var0, int var1) {
      return Double.longBitsToDouble(getLongB(var0, var1));
   }

   static double getDoubleB(long var0) {
      return Double.longBitsToDouble(getLongB(var0));
   }

   static double getDouble(ByteBuffer var0, int var1, boolean var2) {
      return var2 ? getDoubleB(var0, var1) : getDoubleL(var0, var1);
   }

   static double getDouble(long var0, boolean var2) {
      return var2 ? getDoubleB(var0) : getDoubleL(var0);
   }

   static void putDoubleL(ByteBuffer var0, int var1, double var2) {
      putLongL(var0, var1, Double.doubleToRawLongBits(var2));
   }

   static void putDoubleL(long var0, double var2) {
      putLongL(var0, Double.doubleToRawLongBits(var2));
   }

   static void putDoubleB(ByteBuffer var0, int var1, double var2) {
      putLongB(var0, var1, Double.doubleToRawLongBits(var2));
   }

   static void putDoubleB(long var0, double var2) {
      putLongB(var0, Double.doubleToRawLongBits(var2));
   }

   static void putDouble(ByteBuffer var0, int var1, double var2, boolean var4) {
      if (var4) {
         putDoubleB(var0, var1, var2);
      } else {
         putDoubleL(var0, var1, var2);
      }

   }

   static void putDouble(long var0, double var2, boolean var4) {
      if (var4) {
         putDoubleB(var0, var2);
      } else {
         putDoubleL(var0, var2);
      }

   }

   private static byte _get(long var0) {
      return unsafe.getByte(var0);
   }

   private static void _put(long var0, byte var2) {
      unsafe.putByte(var0, var2);
   }

   static Unsafe unsafe() {
      return unsafe;
   }

   static ByteOrder byteOrder() {
      if (byteOrder == null) {
         throw new Error("Unknown byte order");
      } else {
         return byteOrder;
      }
   }

   static int pageSize() {
      if (pageSize == -1) {
         pageSize = unsafe().pageSize();
      }

      return pageSize;
   }

   static int pageCount(long var0) {
      return (int)(var0 + (long)pageSize() - 1L) / pageSize();
   }

   static boolean unaligned() {
      if (unalignedKnown) {
         return unaligned;
      } else {
         String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("os.arch")));
         unaligned = var0.equals("i386") || var0.equals("x86") || var0.equals("amd64") || var0.equals("x86_64") || var0.equals("ppc64") || var0.equals("ppc64le");
         unalignedKnown = true;
         return unaligned;
      }
   }

   static void reserveMemory(long var0, int var2) {
      if (!memoryLimitSet && VM.isBooted()) {
         maxMemory = VM.maxDirectMemory();
         memoryLimitSet = true;
      }

      if (!tryReserveMemory(var0, var2)) {
         JavaLangRefAccess var3 = SharedSecrets.getJavaLangRefAccess();

         while(var3.tryHandlePendingReference()) {
            if (tryReserveMemory(var0, var2)) {
               return;
            }
         }

         System.gc();
         boolean var4 = false;

         try {
            long var5 = 1L;
            int var7 = 0;

            while(!tryReserveMemory(var0, var2)) {
               if (var7 >= 9) {
                  throw new OutOfMemoryError("Direct buffer memory");
               }

               if (!var3.tryHandlePendingReference()) {
                  try {
                     Thread.sleep(var5);
                     var5 <<= 1;
                     ++var7;
                  } catch (InterruptedException var12) {
                     var4 = true;
                  }
               }
            }
         } finally {
            if (var4) {
               Thread.currentThread().interrupt();
            }

         }

      }
   }

   private static boolean tryReserveMemory(long var0, int var2) {
      while(true) {
         long var3;
         if ((long)var2 <= maxMemory - (var3 = totalCapacity.get())) {
            if (!totalCapacity.compareAndSet(var3, var3 + (long)var2)) {
               continue;
            }

            reservedMemory.addAndGet(var0);
            count.incrementAndGet();
            return true;
         }

         return false;
      }
   }

   static void unreserveMemory(long var0, int var2) {
      long var3 = count.decrementAndGet();
      long var5 = reservedMemory.addAndGet(-var0);
      long var7 = totalCapacity.addAndGet((long)(-var2));

      assert var3 >= 0L && var5 >= 0L && var7 >= 0L;
   }

   static void copyFromArray(Object var0, long var1, long var3, long var5, long var7) {
      long var11;
      for(long var9 = var1 + var3; var7 > 0L; var5 += var11) {
         var11 = var7 > 1048576L ? 1048576L : var7;
         unsafe.copyMemory(var0, var9, (Object)null, var5, var11);
         var7 -= var11;
         var9 += var11;
      }

   }

   static void copyToArray(long var0, Object var2, long var3, long var5, long var7) {
      long var11;
      for(long var9 = var3 + var5; var7 > 0L; var9 += var11) {
         var11 = var7 > 1048576L ? 1048576L : var7;
         unsafe.copyMemory((Object)null, var0, var2, var9, var11);
         var7 -= var11;
         var0 += var11;
      }

   }

   static void copyFromCharArray(Object var0, long var1, long var3, long var5) {
      copyFromShortArray(var0, var1, var3, var5);
   }

   static void copyToCharArray(long var0, Object var2, long var3, long var5) {
      copyToShortArray(var0, var2, var3, var5);
   }

   static native void copyFromShortArray(Object var0, long var1, long var3, long var5);

   static native void copyToShortArray(long var0, Object var2, long var3, long var5);

   static native void copyFromIntArray(Object var0, long var1, long var3, long var5);

   static native void copyToIntArray(long var0, Object var2, long var3, long var5);

   static native void copyFromLongArray(Object var0, long var1, long var3, long var5);

   static native void copyToLongArray(long var0, Object var2, long var3, long var5);

   static {
      long var0 = unsafe.allocateMemory(8L);

      try {
         unsafe.putLong(var0, 72623859790382856L);
         byte var2 = unsafe.getByte(var0);
         switch(var2) {
         case 1:
            byteOrder = ByteOrder.BIG_ENDIAN;
            break;
         case 8:
            byteOrder = ByteOrder.LITTLE_ENDIAN;
            break;
         default:
            assert false;

            byteOrder = null;
         }
      } finally {
         unsafe.freeMemory(var0);
      }

      pageSize = -1;
      unalignedKnown = false;
      maxMemory = VM.maxDirectMemory();
      reservedMemory = new AtomicLong();
      totalCapacity = new AtomicLong();
      count = new AtomicLong();
      memoryLimitSet = false;
      SharedSecrets.setJavaNioAccess(new JavaNioAccess() {
         public JavaNioAccess.BufferPool getDirectBufferPool() {
            return new JavaNioAccess.BufferPool() {
               public String getName() {
                  return "direct";
               }

               public long getCount() {
                  return Bits.count.get();
               }

               public long getTotalCapacity() {
                  return Bits.totalCapacity.get();
               }

               public long getMemoryUsed() {
                  return Bits.reservedMemory.get();
               }
            };
         }

         public ByteBuffer newDirectByteBuffer(long var1, int var3, Object var4) {
            return new DirectByteBuffer(var1, var3, var4);
         }

         public void truncate(Buffer var1) {
            var1.truncate();
         }
      });
   }
}
