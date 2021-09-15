package java.nio.file;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

class FileTreeIterator implements Iterator<FileTreeWalker.Event>, Closeable {
   private final FileTreeWalker walker;
   private FileTreeWalker.Event next;

   FileTreeIterator(Path var1, int var2, FileVisitOption... var3) throws IOException {
      this.walker = new FileTreeWalker(Arrays.asList(var3), var2);
      this.next = this.walker.walk(var1);

      assert this.next.type() == FileTreeWalker.EventType.ENTRY || this.next.type() == FileTreeWalker.EventType.START_DIRECTORY;

      IOException var4 = this.next.ioeException();
      if (var4 != null) {
         throw var4;
      }
   }

   private void fetchNextIfNeeded() {
      if (this.next == null) {
         for(FileTreeWalker.Event var1 = this.walker.next(); var1 != null; var1 = this.walker.next()) {
            IOException var2 = var1.ioeException();
            if (var2 != null) {
               throw new UncheckedIOException(var2);
            }

            if (var1.type() != FileTreeWalker.EventType.END_DIRECTORY) {
               this.next = var1;
               return;
            }
         }
      }

   }

   public boolean hasNext() {
      if (!this.walker.isOpen()) {
         throw new IllegalStateException();
      } else {
         this.fetchNextIfNeeded();
         return this.next != null;
      }
   }

   public FileTreeWalker.Event next() {
      if (!this.walker.isOpen()) {
         throw new IllegalStateException();
      } else {
         this.fetchNextIfNeeded();
         if (this.next == null) {
            throw new NoSuchElementException();
         } else {
            FileTreeWalker.Event var1 = this.next;
            this.next = null;
            return var1;
         }
      }
   }

   public void close() {
      this.walker.close();
   }
}
