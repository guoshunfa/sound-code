package sun.nio.fs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;
import java.util.NoSuchElementException;

abstract class AbstractPath implements Path {
   protected AbstractPath() {
   }

   public final boolean startsWith(String var1) {
      return this.startsWith(this.getFileSystem().getPath(var1));
   }

   public final boolean endsWith(String var1) {
      return this.endsWith(this.getFileSystem().getPath(var1));
   }

   public final Path resolve(String var1) {
      return this.resolve(this.getFileSystem().getPath(var1));
   }

   public final Path resolveSibling(Path var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Path var2 = this.getParent();
         return var2 == null ? var1 : var2.resolve(var1);
      }
   }

   public final Path resolveSibling(String var1) {
      return this.resolveSibling(this.getFileSystem().getPath(var1));
   }

   public final Iterator<Path> iterator() {
      return new Iterator<Path>() {
         private int i = 0;

         public boolean hasNext() {
            return this.i < AbstractPath.this.getNameCount();
         }

         public Path next() {
            if (this.i < AbstractPath.this.getNameCount()) {
               Path var1 = AbstractPath.this.getName(this.i);
               ++this.i;
               return var1;
            } else {
               throw new NoSuchElementException();
            }
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   public final File toFile() {
      return new File(this.toString());
   }

   public final WatchKey register(WatchService var1, WatchEvent.Kind<?>... var2) throws IOException {
      return this.register(var1, var2, new WatchEvent.Modifier[0]);
   }
}
