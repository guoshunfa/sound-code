package java.lang;

import java.io.DataInputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.zip.InflaterInputStream;

class CharacterName {
   private static SoftReference<byte[]> refStrPool;
   private static int[][] lookup;

   private static synchronized byte[] initNamePool() {
      Object var0 = null;
      byte[] var19;
      if (refStrPool != null && (var19 = (byte[])refStrPool.get()) != null) {
         return var19;
      } else {
         DataInputStream var1 = null;

         try {
            var1 = new DataInputStream(new InflaterInputStream((InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
               public InputStream run() {
                  return this.getClass().getResourceAsStream("uniName.dat");
               }
            })));
            lookup = new int[4352][];
            int var2 = var1.readInt();
            int var3 = var1.readInt();
            byte[] var4 = new byte[var3];
            var1.readFully(var4);
            int var5 = 0;
            int var6 = 0;
            int var7 = 0;

            do {
               int var8 = var4[var6++] & 255;
               if (var8 == 0) {
                  var8 = var4[var6++] & 255;
                  var7 = (var4[var6++] & 255) << 16 | (var4[var6++] & 255) << 8 | var4[var6++] & 255;
               } else {
                  ++var7;
               }

               int var9 = var7 >> 8;
               if (lookup[var9] == null) {
                  lookup[var9] = new int[256];
               }

               lookup[var9][var7 & 255] = var5 << 8 | var8;
               var5 += var8;
            } while(var6 < var3);

            var19 = new byte[var2 - var3];
            var1.readFully(var19);
            refStrPool = new SoftReference(var19);
            return var19;
         } catch (Exception var17) {
            throw new InternalError(var17.getMessage(), var17);
         } finally {
            try {
               if (var1 != null) {
                  var1.close();
               }
            } catch (Exception var16) {
            }

         }
      }
   }

   public static String get(int var0) {
      byte[] var1 = null;
      if (refStrPool == null || (var1 = (byte[])refStrPool.get()) == null) {
         var1 = initNamePool();
      }

      boolean var2 = false;
      int var4;
      if (lookup[var0 >> 8] != null && (var4 = lookup[var0 >> 8][var0 & 255]) != 0) {
         String var3 = new String(var1, 0, var4 >>> 8, var4 & 255);
         return var3;
      } else {
         return null;
      }
   }
}
