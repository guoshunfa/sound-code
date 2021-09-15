package sun.nio.fs;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

class BsdFileStore extends UnixFileStore {
   BsdFileStore(UnixPath var1) throws IOException {
      super(var1);
   }

   BsdFileStore(UnixFileSystem var1, UnixMountEntry var2) throws IOException {
      super(var1, var2);
   }

   UnixMountEntry findMountEntry() throws IOException {
      UnixFileSystem var1 = this.file().getFileSystem();
      UnixPath var2 = null;

      try {
         byte[] var3 = UnixNativeDispatcher.realpath(this.file());
         var2 = new UnixPath(var1, var3);
      } catch (UnixException var8) {
         var8.rethrowAsIOException(this.file());
      }

      for(UnixPath var9 = var2.getParent(); var9 != null; var9 = var9.getParent()) {
         UnixFileAttributes var4 = null;

         try {
            var4 = UnixFileAttributes.get(var9, true);
         } catch (UnixException var7) {
            var7.rethrowAsIOException(var9);
         }

         if (var4.dev() != this.dev()) {
            break;
         }

         var2 = var9;
      }

      byte[] var10 = var2.asByteArray();
      Iterator var5 = var1.getMountEntries().iterator();

      UnixMountEntry var6;
      do {
         if (!var5.hasNext()) {
            throw new IOException("Mount point not found in fstab");
         }

         var6 = (UnixMountEntry)var5.next();
      } while(!Arrays.equals(var10, var6.dir()));

      return var6;
   }
}
