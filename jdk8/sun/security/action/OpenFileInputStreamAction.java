package sun.security.action;

import java.io.File;
import java.io.FileInputStream;
import java.security.PrivilegedExceptionAction;

public class OpenFileInputStreamAction implements PrivilegedExceptionAction<FileInputStream> {
   private final File file;

   public OpenFileInputStreamAction(File var1) {
      this.file = var1;
   }

   public OpenFileInputStreamAction(String var1) {
      this.file = new File(var1);
   }

   public FileInputStream run() throws Exception {
      return new FileInputStream(this.file);
   }
}
