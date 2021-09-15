package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.channels.Channel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class SharedFileLockTable extends FileLockTable {
   private static ConcurrentHashMap<FileKey, List<SharedFileLockTable.FileLockReference>> lockMap = new ConcurrentHashMap();
   private static ReferenceQueue<FileLock> queue = new ReferenceQueue();
   private final Channel channel;
   private final FileKey fileKey;

   SharedFileLockTable(Channel var1, FileDescriptor var2) throws IOException {
      this.channel = var1;
      this.fileKey = FileKey.create(var2);
   }

   public void add(FileLock var1) throws OverlappingFileLockException {
      List var2 = (List)lockMap.get(this.fileKey);

      while(true) {
         if (var2 == null) {
            ArrayList var9 = new ArrayList(2);
            List var3;
            synchronized(var9) {
               var3 = (List)lockMap.putIfAbsent(this.fileKey, var9);
               if (var3 == null) {
                  var9.add(new SharedFileLockTable.FileLockReference(var1, queue, this.fileKey));
                  break;
               }
            }

            var2 = var3;
         }

         synchronized(var2) {
            List var4 = (List)lockMap.get(this.fileKey);
            if (var2 == var4) {
               this.checkList(var2, var1.position(), var1.size());
               var2.add(new SharedFileLockTable.FileLockReference(var1, queue, this.fileKey));
               break;
            }

            var2 = var4;
         }
      }

      this.removeStaleEntries();
   }

   private void removeKeyIfEmpty(FileKey var1, List<SharedFileLockTable.FileLockReference> var2) {
      assert Thread.holdsLock(var2);

      assert lockMap.get(var1) == var2;

      if (var2.isEmpty()) {
         lockMap.remove(var1);
      }

   }

   public void remove(FileLock var1) {
      assert var1 != null;

      List var2 = (List)lockMap.get(this.fileKey);
      if (var2 != null) {
         synchronized(var2) {
            for(int var4 = 0; var4 < var2.size(); ++var4) {
               SharedFileLockTable.FileLockReference var5 = (SharedFileLockTable.FileLockReference)var2.get(var4);
               FileLock var6 = (FileLock)var5.get();
               if (var6 == var1) {
                  if ($assertionsDisabled || var6 != null && var6.acquiredBy() == this.channel) {
                     var5.clear();
                     var2.remove(var4);
                     break;
                  }

                  throw new AssertionError();
               }
            }

         }
      }
   }

   public List<FileLock> removeAll() {
      ArrayList var1 = new ArrayList();
      List var2 = (List)lockMap.get(this.fileKey);
      if (var2 != null) {
         synchronized(var2) {
            int var4 = 0;

            while(true) {
               while(var4 < var2.size()) {
                  SharedFileLockTable.FileLockReference var5 = (SharedFileLockTable.FileLockReference)var2.get(var4);
                  FileLock var6 = (FileLock)var5.get();
                  if (var6 != null && var6.acquiredBy() == this.channel) {
                     var5.clear();
                     var2.remove(var4);
                     var1.add(var6);
                  } else {
                     ++var4;
                  }
               }

               this.removeKeyIfEmpty(this.fileKey, var2);
               break;
            }
         }
      }

      return var1;
   }

   public void replace(FileLock var1, FileLock var2) {
      List var3 = (List)lockMap.get(this.fileKey);

      assert var3 != null;

      synchronized(var3) {
         for(int var5 = 0; var5 < var3.size(); ++var5) {
            SharedFileLockTable.FileLockReference var6 = (SharedFileLockTable.FileLockReference)var3.get(var5);
            FileLock var7 = (FileLock)var6.get();
            if (var7 == var1) {
               var6.clear();
               var3.set(var5, new SharedFileLockTable.FileLockReference(var2, queue, this.fileKey));
               break;
            }
         }

      }
   }

   private void checkList(List<SharedFileLockTable.FileLockReference> var1, long var2, long var4) throws OverlappingFileLockException {
      assert Thread.holdsLock(var1);

      Iterator var6 = var1.iterator();

      FileLock var8;
      do {
         if (!var6.hasNext()) {
            return;
         }

         SharedFileLockTable.FileLockReference var7 = (SharedFileLockTable.FileLockReference)var6.next();
         var8 = (FileLock)var7.get();
      } while(var8 == null || !var8.overlaps(var2, var4));

      throw new OverlappingFileLockException();
   }

   private void removeStaleEntries() {
      SharedFileLockTable.FileLockReference var1;
      while((var1 = (SharedFileLockTable.FileLockReference)queue.poll()) != null) {
         FileKey var2 = var1.fileKey();
         List var3 = (List)lockMap.get(var2);
         if (var3 != null) {
            synchronized(var3) {
               var3.remove(var1);
               this.removeKeyIfEmpty(var2, var3);
            }
         }
      }

   }

   private static class FileLockReference extends WeakReference<FileLock> {
      private FileKey fileKey;

      FileLockReference(FileLock var1, ReferenceQueue<FileLock> var2, FileKey var3) {
         super(var1, var2);
         this.fileKey = var3;
      }

      FileKey fileKey() {
         return this.fileKey;
      }
   }
}
