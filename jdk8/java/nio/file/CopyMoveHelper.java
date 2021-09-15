package java.nio.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;

class CopyMoveHelper {
   private CopyMoveHelper() {
   }

   private static CopyOption[] convertMoveToCopyOptions(CopyOption... var0) throws AtomicMoveNotSupportedException {
      int var1 = var0.length;
      CopyOption[] var2 = new CopyOption[var1 + 2];

      for(int var3 = 0; var3 < var1; ++var3) {
         CopyOption var4 = var0[var3];
         if (var4 == StandardCopyOption.ATOMIC_MOVE) {
            throw new AtomicMoveNotSupportedException((String)null, (String)null, "Atomic move between providers is not supported");
         }

         var2[var3] = var4;
      }

      var2[var1] = LinkOption.NOFOLLOW_LINKS;
      var2[var1 + 1] = StandardCopyOption.COPY_ATTRIBUTES;
      return var2;
   }

   static void copyToForeignTarget(Path var0, Path var1, CopyOption... var2) throws IOException {
      CopyMoveHelper.CopyOptions var3 = CopyMoveHelper.CopyOptions.parse(var2);
      LinkOption[] var4 = var3.followLinks ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
      BasicFileAttributes var5 = Files.readAttributes(var0, BasicFileAttributes.class, var4);
      if (var5.isSymbolicLink()) {
         throw new IOException("Copying of symbolic links not supported");
      } else {
         if (var3.replaceExisting) {
            Files.deleteIfExists(var1);
         } else if (Files.exists(var1)) {
            throw new FileAlreadyExistsException(var1.toString());
         }

         if (var5.isDirectory()) {
            Files.createDirectory(var1);
         } else {
            InputStream var6 = Files.newInputStream(var0);
            Throwable var7 = null;

            try {
               Files.copy(var6, var1);
            } catch (Throwable var20) {
               var7 = var20;
               throw var20;
            } finally {
               if (var6 != null) {
                  if (var7 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var17) {
                        var7.addSuppressed(var17);
                     }
                  } else {
                     var6.close();
                  }
               }

            }
         }

         if (var3.copyAttributes) {
            BasicFileAttributeView var22 = (BasicFileAttributeView)Files.getFileAttributeView(var1, BasicFileAttributeView.class);

            try {
               var22.setTimes(var5.lastModifiedTime(), var5.lastAccessTime(), var5.creationTime());
            } catch (Throwable var19) {
               try {
                  Files.delete(var1);
               } catch (Throwable var18) {
                  var19.addSuppressed(var18);
               }

               throw var19;
            }
         }

      }
   }

   static void moveToForeignTarget(Path var0, Path var1, CopyOption... var2) throws IOException {
      copyToForeignTarget(var0, var1, convertMoveToCopyOptions(var2));
      Files.delete(var0);
   }

   private static class CopyOptions {
      boolean replaceExisting = false;
      boolean copyAttributes = false;
      boolean followLinks = true;

      static CopyMoveHelper.CopyOptions parse(CopyOption... var0) {
         CopyMoveHelper.CopyOptions var1 = new CopyMoveHelper.CopyOptions();
         CopyOption[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CopyOption var5 = var2[var4];
            if (var5 == StandardCopyOption.REPLACE_EXISTING) {
               var1.replaceExisting = true;
            } else if (var5 == LinkOption.NOFOLLOW_LINKS) {
               var1.followLinks = false;
            } else {
               if (var5 != StandardCopyOption.COPY_ATTRIBUTES) {
                  if (var5 == null) {
                     throw new NullPointerException();
                  }

                  throw new UnsupportedOperationException("'" + var5 + "' is not a recognized copy option");
               }

               var1.copyAttributes = true;
            }
         }

         return var1;
      }
   }
}
