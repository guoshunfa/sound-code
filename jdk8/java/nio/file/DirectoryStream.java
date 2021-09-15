package java.nio.file;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

public interface DirectoryStream<T> extends Closeable, Iterable<T> {
   Iterator<T> iterator();

   @FunctionalInterface
   public interface Filter<T> {
      boolean accept(T var1) throws IOException;
   }
}
