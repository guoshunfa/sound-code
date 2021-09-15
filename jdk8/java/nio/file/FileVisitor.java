package java.nio.file;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;

public interface FileVisitor<T> {
   FileVisitResult preVisitDirectory(T var1, BasicFileAttributes var2) throws IOException;

   FileVisitResult visitFile(T var1, BasicFileAttributes var2) throws IOException;

   FileVisitResult visitFileFailed(T var1, IOException var2) throws IOException;

   FileVisitResult postVisitDirectory(T var1, IOException var2) throws IOException;
}
