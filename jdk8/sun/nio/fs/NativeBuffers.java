package sun.nio.fs;

import sun.misc.Unsafe;

class NativeBuffers {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static final int TEMP_BUF_POOL_SIZE = 3;
   private static ThreadLocal<NativeBuffer[]> threadLocal = new ThreadLocal();

   private NativeBuffers() {
   }

   static NativeBuffer allocNativeBuffer(int var0) {
      if (var0 < 2048) {
         var0 = 2048;
      }

      return new NativeBuffer(var0);
   }

   static NativeBuffer getNativeBufferFromCache(int var0) {
      NativeBuffer[] var1 = (NativeBuffer[])threadLocal.get();
      if (var1 != null) {
         for(int var2 = 0; var2 < 3; ++var2) {
            NativeBuffer var3 = var1[var2];
            if (var3 != null && var3.size() >= var0) {
               var1[var2] = null;
               return var3;
            }
         }
      }

      return null;
   }

   static NativeBuffer getNativeBuffer(int var0) {
      NativeBuffer var1 = getNativeBufferFromCache(var0);
      if (var1 != null) {
         var1.setOwner((Object)null);
         return var1;
      } else {
         return allocNativeBuffer(var0);
      }
   }

   static void releaseNativeBuffer(NativeBuffer var0) {
      NativeBuffer[] var1 = (NativeBuffer[])threadLocal.get();
      if (var1 == null) {
         var1 = new NativeBuffer[]{var0, null, null};
         threadLocal.set(var1);
      } else {
         int var2;
         for(var2 = 0; var2 < 3; ++var2) {
            if (var1[var2] == null) {
               var1[var2] = var0;
               return;
            }
         }

         for(var2 = 0; var2 < 3; ++var2) {
            NativeBuffer var3 = var1[var2];
            if (var3.size() < var0.size()) {
               var3.cleaner().clean();
               var1[var2] = var0;
               return;
            }
         }

         var0.cleaner().clean();
      }
   }

   static void copyCStringToNativeBuffer(byte[] var0, NativeBuffer var1) {
      long var2 = (long)Unsafe.ARRAY_BYTE_BASE_OFFSET;
      long var4 = (long)var0.length;

      assert (long)var1.size() >= var4 + 1L;

      unsafe.copyMemory(var0, var2, (Object)null, var1.address(), var4);
      unsafe.putByte(var1.address() + var4, (byte)0);
   }

   static NativeBuffer asNativeBuffer(byte[] var0) {
      NativeBuffer var1 = getNativeBuffer(var0.length + 1);
      copyCStringToNativeBuffer(var0, var1);
      return var1;
   }
}
