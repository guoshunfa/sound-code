package sun.misc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.PrivilegedAction;

public final class Perf {
   private static Perf instance;
   private static final int PERF_MODE_RO = 0;
   private static final int PERF_MODE_RW = 1;

   private Perf() {
   }

   public static Perf getPerf() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         RuntimePermission var1 = new RuntimePermission("sun.misc.Perf.getPerf");
         var0.checkPermission(var1);
      }

      return instance;
   }

   public ByteBuffer attach(int var1, String var2) throws IllegalArgumentException, IOException {
      if (var2.compareTo("r") == 0) {
         return this.attachImpl((String)null, var1, 0);
      } else if (var2.compareTo("rw") == 0) {
         return this.attachImpl((String)null, var1, 1);
      } else {
         throw new IllegalArgumentException("unknown mode");
      }
   }

   public ByteBuffer attach(String var1, int var2, String var3) throws IllegalArgumentException, IOException {
      if (var3.compareTo("r") == 0) {
         return this.attachImpl(var1, var2, 0);
      } else if (var3.compareTo("rw") == 0) {
         return this.attachImpl(var1, var2, 1);
      } else {
         throw new IllegalArgumentException("unknown mode");
      }
   }

   private ByteBuffer attachImpl(String var1, int var2, int var3) throws IllegalArgumentException, IOException {
      final ByteBuffer var4 = this.attach(var1, var2, var3);
      if (var2 == 0) {
         return var4;
      } else {
         ByteBuffer var5 = var4.duplicate();
         Cleaner.create(var5, new Runnable() {
            public void run() {
               try {
                  Perf.instance.detach(var4);
               } catch (Throwable var2) {
                  assert false : var2.toString();
               }

            }
         });
         return var5;
      }
   }

   private native ByteBuffer attach(String var1, int var2, int var3) throws IllegalArgumentException, IOException;

   private native void detach(ByteBuffer var1);

   public native ByteBuffer createLong(String var1, int var2, int var3, long var4);

   public ByteBuffer createString(String var1, int var2, int var3, String var4, int var5) {
      byte[] var6 = getBytes(var4);
      byte[] var7 = new byte[var6.length + 1];
      System.arraycopy(var6, 0, var7, 0, var6.length);
      var7[var6.length] = 0;
      return this.createByteArray(var1, var2, var3, var7, Math.max(var7.length, var5));
   }

   public ByteBuffer createString(String var1, int var2, int var3, String var4) {
      byte[] var5 = getBytes(var4);
      byte[] var6 = new byte[var5.length + 1];
      System.arraycopy(var5, 0, var6, 0, var5.length);
      var6[var5.length] = 0;
      return this.createByteArray(var1, var2, var3, var6, var6.length);
   }

   public native ByteBuffer createByteArray(String var1, int var2, int var3, byte[] var4, int var5);

   private static byte[] getBytes(String var0) {
      byte[] var1 = null;

      try {
         var1 = var0.getBytes("UTF-8");
      } catch (UnsupportedEncodingException var3) {
      }

      return var1;
   }

   public native long highResCounter();

   public native long highResFrequency();

   private static native void registerNatives();

   static {
      registerNatives();
      instance = new Perf();
   }

   public static class GetPerfAction implements PrivilegedAction<Perf> {
      public Perf run() {
         return Perf.getPerf();
      }
   }
}
