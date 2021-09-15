package java.nio.file.spi;

import java.io.IOException;
import java.nio.file.Path;

public abstract class FileTypeDetector {
   private static Void checkPermission() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(new RuntimePermission("fileTypeDetector"));
      }

      return null;
   }

   private FileTypeDetector(Void var1) {
   }

   protected FileTypeDetector() {
      this(checkPermission());
   }

   public abstract String probeContentType(Path var1) throws IOException;
}
