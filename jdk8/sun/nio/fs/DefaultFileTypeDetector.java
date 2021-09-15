package sun.nio.fs;

import java.nio.file.FileSystems;
import java.nio.file.spi.FileSystemProvider;
import java.nio.file.spi.FileTypeDetector;

public class DefaultFileTypeDetector {
   private DefaultFileTypeDetector() {
   }

   public static FileTypeDetector create() {
      FileSystemProvider var0 = FileSystems.getDefault().provider();
      return ((UnixFileSystemProvider)var0).getFileTypeDetector();
   }
}
