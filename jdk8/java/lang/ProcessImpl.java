package java.lang;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

final class ProcessImpl {
   private static final JavaIOFileDescriptorAccess fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();

   private ProcessImpl() {
   }

   private static byte[] toCString(String var0) {
      if (var0 == null) {
         return null;
      } else {
         byte[] var1 = var0.getBytes();
         byte[] var2 = new byte[var1.length + 1];
         System.arraycopy(var1, 0, var2, 0, var1.length);
         var2[var2.length - 1] = 0;
         return var2;
      }
   }

   static Process start(String[] var0, Map<String, String> var1, String var2, ProcessBuilder.Redirect[] var3, boolean var4) throws IOException {
      assert var0 != null && var0.length > 0;

      byte[][] var5 = new byte[var0.length - 1][];
      int var6 = var5.length;

      for(int var7 = 0; var7 < var5.length; ++var7) {
         var5[var7] = var0[var7 + 1].getBytes();
         var6 += var5[var7].length;
      }

      byte[] var70 = new byte[var6];
      int var8 = 0;
      byte[][] var9 = var5;
      int var10 = var5.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         byte[] var12 = var9[var11];
         System.arraycopy(var12, 0, var70, var8, var12.length);
         var8 += var12.length + 1;
      }

      int[] var71 = new int[1];
      byte[] var72 = ProcessEnvironment.toEnvironmentBlock(var1, var71);
      FileInputStream var74 = null;
      FileOutputStream var13 = null;
      FileOutputStream var14 = null;

      UNIXProcess var15;
      try {
         int[] var73;
         if (var3 == null) {
            var73 = new int[]{-1, -1, -1};
         } else {
            var73 = new int[3];
            if (var3[0] == ProcessBuilder.Redirect.PIPE) {
               var73[0] = -1;
            } else if (var3[0] == ProcessBuilder.Redirect.INHERIT) {
               var73[0] = 0;
            } else {
               var74 = new FileInputStream(var3[0].file());
               var73[0] = fdAccess.get(var74.getFD());
            }

            if (var3[1] == ProcessBuilder.Redirect.PIPE) {
               var73[1] = -1;
            } else if (var3[1] == ProcessBuilder.Redirect.INHERIT) {
               var73[1] = 1;
            } else {
               var13 = new FileOutputStream(var3[1].file(), var3[1].append());
               var73[1] = fdAccess.get(var13.getFD());
            }

            if (var3[2] == ProcessBuilder.Redirect.PIPE) {
               var73[2] = -1;
            } else if (var3[2] == ProcessBuilder.Redirect.INHERIT) {
               var73[2] = 2;
            } else {
               var14 = new FileOutputStream(var3[2].file(), var3[2].append());
               var73[2] = fdAccess.get(var14.getFD());
            }
         }

         var15 = new UNIXProcess(toCString(var0[0]), var70, var5.length, var72, var71[0], toCString(var2), var73, var4);
      } finally {
         try {
            if (var74 != null) {
               var74.close();
            }
         } finally {
            try {
               if (var13 != null) {
                  var13.close();
               }
            } finally {
               if (var14 != null) {
                  var14.close();
               }

            }

         }

      }

      return var15;
   }
}
