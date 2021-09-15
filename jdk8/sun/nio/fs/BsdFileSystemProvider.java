package sun.nio.fs;

import java.io.IOException;

public class BsdFileSystemProvider extends UnixFileSystemProvider {
   BsdFileSystem newFileSystem(String var1) {
      return new BsdFileSystem(this, var1);
   }

   BsdFileStore getFileStore(UnixPath var1) throws IOException {
      return new BsdFileStore(var1);
   }
}
