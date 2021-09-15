package sun.nio.fs;

import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;

abstract class AbstractFileSystemProvider extends FileSystemProvider {
   protected AbstractFileSystemProvider() {
   }

   private static String[] split(String var0) {
      String[] var1 = new String[2];
      int var2 = var0.indexOf(58);
      if (var2 == -1) {
         var1[0] = "basic";
         var1[1] = var0;
      } else {
         var1[0] = var0.substring(0, var2++);
         var1[1] = var2 == var0.length() ? "" : var0.substring(var2);
      }

      return var1;
   }

   abstract DynamicFileAttributeView getFileAttributeView(Path var1, String var2, LinkOption... var3);

   public final void setAttribute(Path var1, String var2, Object var3, LinkOption... var4) throws IOException {
      String[] var5 = split(var2);
      if (var5[0].length() == 0) {
         throw new IllegalArgumentException(var2);
      } else {
         DynamicFileAttributeView var6 = this.getFileAttributeView(var1, var5[0], var4);
         if (var6 == null) {
            throw new UnsupportedOperationException("View '" + var5[0] + "' not available");
         } else {
            var6.setAttribute(var5[1], var3);
         }
      }
   }

   public final Map<String, Object> readAttributes(Path var1, String var2, LinkOption... var3) throws IOException {
      String[] var4 = split(var2);
      if (var4[0].length() == 0) {
         throw new IllegalArgumentException(var2);
      } else {
         DynamicFileAttributeView var5 = this.getFileAttributeView(var1, var4[0], var3);
         if (var5 == null) {
            throw new UnsupportedOperationException("View '" + var4[0] + "' not available");
         } else {
            return var5.readAttributes(var4[1].split(","));
         }
      }
   }

   abstract boolean implDelete(Path var1, boolean var2) throws IOException;

   public final void delete(Path var1) throws IOException {
      this.implDelete(var1, true);
   }

   public final boolean deleteIfExists(Path var1) throws IOException {
      return this.implDelete(var1, false);
   }
}
