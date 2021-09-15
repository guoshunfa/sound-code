package sun.nio.fs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileTypeDetector;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

public class MacOSXFileSystemProvider extends BsdFileSystemProvider {
   MacOSXFileSystem newFileSystem(String var1) {
      return new MacOSXFileSystem(this, var1);
   }

   FileTypeDetector getFileTypeDetector() {
      Path var1 = Paths.get((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.home"))), ".mime.types");
      return new MimeTypesFileTypeDetector(var1);
   }
}
