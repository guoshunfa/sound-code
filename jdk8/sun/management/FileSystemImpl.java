package sun.management;

import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class FileSystemImpl extends FileSystem {
   public boolean supportsFileSecurity(File var1) throws IOException {
      return true;
   }

   public boolean isAccessUserOnly(File var1) throws IOException {
      return isAccessUserOnly0(var1.getPath());
   }

   static native boolean isAccessUserOnly0(String var0) throws IOException;

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("management");
            return null;
         }
      });
   }
}
