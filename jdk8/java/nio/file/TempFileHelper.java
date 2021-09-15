package java.nio.file;

import java.io.IOException;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.util.EnumSet;
import java.util.Set;
import sun.security.action.GetPropertyAction;

class TempFileHelper {
   private static final Path tmpdir = Paths.get((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.io.tmpdir"))));
   private static final boolean isPosix = FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
   private static final SecureRandom random = new SecureRandom();

   private TempFileHelper() {
   }

   private static Path generatePath(String var0, String var1, Path var2) {
      long var3 = random.nextLong();
      var3 = var3 == Long.MIN_VALUE ? 0L : Math.abs(var3);
      Path var5 = var2.getFileSystem().getPath(var0 + Long.toString(var3) + var1);
      if (var5.getParent() != null) {
         throw new IllegalArgumentException("Invalid prefix or suffix");
      } else {
         return var2.resolve(var5);
      }
   }

   private static Path create(Path var0, String var1, String var2, boolean var3, FileAttribute<?>[] var4) throws IOException {
      if (var1 == null) {
         var1 = "";
      }

      if (var2 == null) {
         var2 = var3 ? "" : ".tmp";
      }

      if (var0 == null) {
         var0 = tmpdir;
      }

      if (isPosix && var0.getFileSystem() == FileSystems.getDefault()) {
         if (var4.length == 0) {
            var4 = new FileAttribute[]{var3 ? TempFileHelper.PosixPermissions.dirPermissions : TempFileHelper.PosixPermissions.filePermissions};
         } else {
            boolean var5 = false;

            for(int var6 = 0; var6 < var4.length; ++var6) {
               if (var4[var6].name().equals("posix:permissions")) {
                  var5 = true;
                  break;
               }
            }

            if (!var5) {
               FileAttribute[] var12 = new FileAttribute[var4.length + 1];
               System.arraycopy(var4, 0, var12, 0, var4.length);
               var4 = var12;
               var12[var12.length - 1] = var3 ? TempFileHelper.PosixPermissions.dirPermissions : TempFileHelper.PosixPermissions.filePermissions;
            }
         }
      }

      SecurityManager var11 = System.getSecurityManager();

      while(true) {
         Path var13;
         try {
            var13 = generatePath(var1, var2, var0);
         } catch (InvalidPathException var8) {
            if (var11 != null) {
               throw new IllegalArgumentException("Invalid prefix or suffix");
            }

            throw var8;
         }

         try {
            if (var3) {
               return Files.createDirectory(var13, var4);
            }

            return Files.createFile(var13, var4);
         } catch (SecurityException var9) {
            if (var0 == tmpdir && var11 != null) {
               throw new SecurityException("Unable to create temporary file or directory");
            }

            throw var9;
         } catch (FileAlreadyExistsException var10) {
         }
      }
   }

   static Path createTempFile(Path var0, String var1, String var2, FileAttribute<?>[] var3) throws IOException {
      return create(var0, var1, var2, false, var3);
   }

   static Path createTempDirectory(Path var0, String var1, FileAttribute<?>[] var2) throws IOException {
      return create(var0, var1, (String)null, true, var2);
   }

   private static class PosixPermissions {
      static final FileAttribute<Set<PosixFilePermission>> filePermissions;
      static final FileAttribute<Set<PosixFilePermission>> dirPermissions;

      static {
         filePermissions = PosixFilePermissions.asFileAttribute(EnumSet.of(PosixFilePermission.OWNER_READ, (Enum)PosixFilePermission.OWNER_WRITE));
         dirPermissions = PosixFilePermissions.asFileAttribute(EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE));
      }
   }
}
