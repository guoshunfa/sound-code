package sun.nio.fs;

class UnixFileStoreAttributes {
   private long f_frsize;
   private long f_blocks;
   private long f_bfree;
   private long f_bavail;

   private UnixFileStoreAttributes() {
   }

   static UnixFileStoreAttributes get(UnixPath var0) throws UnixException {
      UnixFileStoreAttributes var1 = new UnixFileStoreAttributes();
      UnixNativeDispatcher.statvfs(var0, var1);
      return var1;
   }

   long blockSize() {
      return this.f_frsize;
   }

   long totalBlocks() {
      return this.f_blocks;
   }

   long freeBlocks() {
      return this.f_bfree;
   }

   long availableBlocks() {
      return this.f_bavail;
   }
}
