package java.util.jar;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JarOutputStream extends ZipOutputStream {
   private static final int JAR_MAGIC = 51966;
   private boolean firstEntry = true;

   public JarOutputStream(OutputStream var1, Manifest var2) throws IOException {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("man");
      } else {
         ZipEntry var3 = new ZipEntry("META-INF/MANIFEST.MF");
         this.putNextEntry(var3);
         var2.write(new BufferedOutputStream(this));
         this.closeEntry();
      }
   }

   public JarOutputStream(OutputStream var1) throws IOException {
      super(var1);
   }

   public void putNextEntry(ZipEntry var1) throws IOException {
      if (this.firstEntry) {
         byte[] var2 = var1.getExtra();
         if (var2 == null || !hasMagic(var2)) {
            if (var2 == null) {
               var2 = new byte[4];
            } else {
               byte[] var3 = new byte[var2.length + 4];
               System.arraycopy(var2, 0, var3, 4, var2.length);
               var2 = var3;
            }

            set16(var2, 0, 51966);
            set16(var2, 2, 0);
            var1.setExtra(var2);
         }

         this.firstEntry = false;
      }

      super.putNextEntry(var1);
   }

   private static boolean hasMagic(byte[] var0) {
      try {
         for(int var1 = 0; var1 < var0.length; var1 += get16(var0, var1 + 2) + 4) {
            if (get16(var0, var1) == 51966) {
               return true;
            }
         }
      } catch (ArrayIndexOutOfBoundsException var2) {
      }

      return false;
   }

   private static int get16(byte[] var0, int var1) {
      return Byte.toUnsignedInt(var0[var1]) | Byte.toUnsignedInt(var0[var1 + 1]) << 8;
   }

   private static void set16(byte[] var0, int var1, int var2) {
      var0[var1 + 0] = (byte)var2;
      var0[var1 + 1] = (byte)(var2 >> 8);
   }
}
