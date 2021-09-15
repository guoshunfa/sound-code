package java.nio.file;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

public class SimpleFileVisitor<T> implements FileVisitor<T> {
   protected SimpleFileVisitor() {
   }

   public FileVisitResult preVisitDirectory(T var1, BasicFileAttributes var2) throws IOException {
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      return FileVisitResult.CONTINUE;
   }

   public FileVisitResult visitFile(T var1, BasicFileAttributes var2) throws IOException {
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      return FileVisitResult.CONTINUE;
   }

   public FileVisitResult visitFileFailed(T var1, IOException var2) throws IOException {
      Objects.requireNonNull(var1);
      throw var2;
   }

   public FileVisitResult postVisitDirectory(T var1, IOException var2) throws IOException {
      Objects.requireNonNull(var1);
      if (var2 != null) {
         throw var2;
      } else {
         return FileVisitResult.CONTINUE;
      }
   }
}
