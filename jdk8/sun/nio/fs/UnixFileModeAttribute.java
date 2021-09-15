package sun.nio.fs;

import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Iterator;
import java.util.Set;

class UnixFileModeAttribute {
   static final int ALL_PERMISSIONS = 511;
   static final int ALL_READWRITE = 438;
   static final int TEMPFILE_PERMISSIONS = 448;

   private UnixFileModeAttribute() {
   }

   static int toUnixMode(Set<PosixFilePermission> var0) {
      int var1 = 0;
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         PosixFilePermission var3 = (PosixFilePermission)var2.next();
         if (var3 == null) {
            throw new NullPointerException();
         }

         switch(var3) {
         case OWNER_READ:
            var1 |= 256;
            break;
         case OWNER_WRITE:
            var1 |= 128;
            break;
         case OWNER_EXECUTE:
            var1 |= 64;
            break;
         case GROUP_READ:
            var1 |= 32;
            break;
         case GROUP_WRITE:
            var1 |= 16;
            break;
         case GROUP_EXECUTE:
            var1 |= 8;
            break;
         case OTHERS_READ:
            var1 |= 4;
            break;
         case OTHERS_WRITE:
            var1 |= 2;
            break;
         case OTHERS_EXECUTE:
            var1 |= 1;
         }
      }

      return var1;
   }

   static int toUnixMode(int var0, FileAttribute<?>... var1) {
      int var2 = var0;
      FileAttribute[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         FileAttribute var6 = var3[var5];
         String var7 = var6.name();
         if (!var7.equals("posix:permissions") && !var7.equals("unix:permissions")) {
            throw new UnsupportedOperationException("'" + var6.name() + "' not supported as initial attribute");
         }

         var2 = toUnixMode((Set)var6.value());
      }

      return var2;
   }
}
