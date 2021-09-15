package sun.nio.fs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class BsdFileSystem extends UnixFileSystem {
   BsdFileSystem(UnixFileSystemProvider var1, String var2) {
      super(var1, var2);
   }

   public WatchService newWatchService() throws IOException {
      return new PollingWatchService();
   }

   public Set<String> supportedFileAttributeViews() {
      return BsdFileSystem.SupportedFileFileAttributeViewsHolder.supportedFileAttributeViews;
   }

   void copyNonPosixAttributes(int var1, int var2) {
   }

   Iterable<UnixMountEntry> getMountEntries() {
      ArrayList var1 = new ArrayList();

      try {
         long var2 = BsdNativeDispatcher.getfsstat();

         try {
            while(true) {
               UnixMountEntry var4 = new UnixMountEntry();
               int var5 = BsdNativeDispatcher.fsstatEntry(var2, var4);
               if (var5 < 0) {
                  break;
               }

               var1.add(var4);
            }
         } finally {
            BsdNativeDispatcher.endfsstat(var2);
         }
      } catch (UnixException var10) {
      }

      return var1;
   }

   FileStore getFileStore(UnixMountEntry var1) throws IOException {
      return new BsdFileStore(this, var1);
   }

   private static class SupportedFileFileAttributeViewsHolder {
      static final Set<String> supportedFileAttributeViews = supportedFileAttributeViews();

      private static Set<String> supportedFileAttributeViews() {
         HashSet var0 = new HashSet();
         var0.addAll(UnixFileSystem.standardFileAttributeViews());
         return Collections.unmodifiableSet(var0);
      }
   }
}
