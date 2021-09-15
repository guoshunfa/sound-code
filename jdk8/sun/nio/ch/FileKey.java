package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;

public class FileKey {
   private long st_dev;
   private long st_ino;

   private FileKey() {
   }

   public static FileKey create(FileDescriptor var0) {
      FileKey var1 = new FileKey();

      try {
         var1.init(var0);
         return var1;
      } catch (IOException var3) {
         throw new Error(var3);
      }
   }

   public int hashCode() {
      return (int)(this.st_dev ^ this.st_dev >>> 32) + (int)(this.st_ino ^ this.st_ino >>> 32);
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof FileKey)) {
         return false;
      } else {
         FileKey var2 = (FileKey)var1;
         return this.st_dev == var2.st_dev && this.st_ino == var2.st_ino;
      }
   }

   private native void init(FileDescriptor var1) throws IOException;

   private static native void initIDs();

   static {
      initIDs();
   }
}
