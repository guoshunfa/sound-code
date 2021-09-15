package sun.nio.fs;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class UnixDirectoryStream implements DirectoryStream<Path> {
   private final UnixPath dir;
   private final long dp;
   private final DirectoryStream.Filter<? super Path> filter;
   private final ReentrantReadWriteLock streamLock = new ReentrantReadWriteLock(true);
   private volatile boolean isClosed;
   private Iterator<Path> iterator;

   UnixDirectoryStream(UnixPath var1, long var2, DirectoryStream.Filter<? super Path> var4) {
      this.dir = var1;
      this.dp = var2;
      this.filter = var4;
   }

   protected final UnixPath directory() {
      return this.dir;
   }

   protected final Lock readLock() {
      return this.streamLock.readLock();
   }

   protected final Lock writeLock() {
      return this.streamLock.writeLock();
   }

   protected final boolean isOpen() {
      return !this.isClosed;
   }

   protected final boolean closeImpl() throws IOException {
      if (!this.isClosed) {
         this.isClosed = true;

         try {
            UnixNativeDispatcher.closedir(this.dp);
            return true;
         } catch (UnixException var2) {
            throw new IOException(var2.errorString());
         }
      } else {
         return false;
      }
   }

   public void close() throws IOException {
      this.writeLock().lock();

      try {
         this.closeImpl();
      } finally {
         this.writeLock().unlock();
      }

   }

   protected final Iterator<Path> iterator(DirectoryStream<Path> var1) {
      if (this.isClosed) {
         throw new IllegalStateException("Directory stream is closed");
      } else {
         synchronized(this) {
            if (this.iterator != null) {
               throw new IllegalStateException("Iterator already obtained");
            } else {
               this.iterator = new UnixDirectoryStream.UnixDirectoryIterator(var1);
               return this.iterator;
            }
         }
      }
   }

   public Iterator<Path> iterator() {
      return this.iterator(this);
   }

   private class UnixDirectoryIterator implements Iterator<Path> {
      private final DirectoryStream<Path> stream;
      private boolean atEof = false;
      private Path nextEntry;

      UnixDirectoryIterator(DirectoryStream<Path> var2) {
         this.stream = var2;
      }

      private boolean isSelfOrParent(byte[] var1) {
         return var1[0] == 46 && (var1.length == 1 || var1.length == 2 && var1[1] == 46);
      }

      private Path readNextEntry() {
         assert Thread.holdsLock(this);

         while(true) {
            byte[] var1;
            do {
               var1 = null;
               UnixDirectoryStream.this.readLock().lock();

               try {
                  if (UnixDirectoryStream.this.isOpen()) {
                     var1 = UnixNativeDispatcher.readdir(UnixDirectoryStream.this.dp);
                  }
               } catch (UnixException var8) {
                  IOException var3 = var8.asIOException(UnixDirectoryStream.this.dir);
                  throw new DirectoryIteratorException(var3);
               } finally {
                  UnixDirectoryStream.this.readLock().unlock();
               }

               if (var1 == null) {
                  this.atEof = true;
                  return null;
               }
            } while(this.isSelfOrParent(var1));

            UnixPath var2 = UnixDirectoryStream.this.dir.resolve(var1);

            try {
               if (UnixDirectoryStream.this.filter == null || UnixDirectoryStream.this.filter.accept(var2)) {
                  return var2;
               }
            } catch (IOException var10) {
               throw new DirectoryIteratorException(var10);
            }
         }
      }

      public synchronized boolean hasNext() {
         if (this.nextEntry == null && !this.atEof) {
            this.nextEntry = this.readNextEntry();
         }

         return this.nextEntry != null;
      }

      public synchronized Path next() {
         Path var1;
         if (this.nextEntry == null && !this.atEof) {
            var1 = this.readNextEntry();
         } else {
            var1 = this.nextEntry;
            this.nextEntry = null;
         }

         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
