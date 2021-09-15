package sun.nio.fs;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Properties;

abstract class UnixFileStore extends FileStore {
   private final UnixPath file;
   private final long dev;
   private final UnixMountEntry entry;
   private static final Object loadLock = new Object();
   private static volatile Properties props;

   private static long devFor(UnixPath var0) throws IOException {
      try {
         return UnixFileAttributes.get(var0, true).dev();
      } catch (UnixException var2) {
         var2.rethrowAsIOException(var0);
         return 0L;
      }
   }

   UnixFileStore(UnixPath var1) throws IOException {
      this.file = var1;
      this.dev = devFor(var1);
      this.entry = this.findMountEntry();
   }

   UnixFileStore(UnixFileSystem var1, UnixMountEntry var2) throws IOException {
      this.file = new UnixPath(var1, var2.dir());
      this.dev = var2.dev() == 0L ? devFor(this.file) : var2.dev();
      this.entry = var2;
   }

   abstract UnixMountEntry findMountEntry() throws IOException;

   UnixPath file() {
      return this.file;
   }

   long dev() {
      return this.dev;
   }

   UnixMountEntry entry() {
      return this.entry;
   }

   public String name() {
      return this.entry.name();
   }

   public String type() {
      return this.entry.fstype();
   }

   public boolean isReadOnly() {
      return this.entry.isReadOnly();
   }

   private UnixFileStoreAttributes readAttributes() throws IOException {
      try {
         return UnixFileStoreAttributes.get(this.file);
      } catch (UnixException var2) {
         var2.rethrowAsIOException(this.file);
         return null;
      }
   }

   public long getTotalSpace() throws IOException {
      UnixFileStoreAttributes var1 = this.readAttributes();
      return var1.blockSize() * var1.totalBlocks();
   }

   public long getUsableSpace() throws IOException {
      UnixFileStoreAttributes var1 = this.readAttributes();
      return var1.blockSize() * var1.availableBlocks();
   }

   public long getUnallocatedSpace() throws IOException {
      UnixFileStoreAttributes var1 = this.readAttributes();
      return var1.blockSize() * var1.freeBlocks();
   }

   public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return (FileStoreAttributeView)null;
      }
   }

   public Object getAttribute(String var1) throws IOException {
      if (var1.equals("totalSpace")) {
         return this.getTotalSpace();
      } else if (var1.equals("usableSpace")) {
         return this.getUsableSpace();
      } else if (var1.equals("unallocatedSpace")) {
         return this.getUnallocatedSpace();
      } else {
         throw new UnsupportedOperationException("'" + var1 + "' not recognized");
      }
   }

   public boolean supportsFileAttributeView(Class<? extends FileAttributeView> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var1 == BasicFileAttributeView.class) {
         return true;
      } else if (var1 != PosixFileAttributeView.class && var1 != FileOwnerAttributeView.class) {
         return false;
      } else {
         UnixFileStore.FeatureStatus var2 = this.checkIfFeaturePresent("posix");
         return var2 != UnixFileStore.FeatureStatus.NOT_PRESENT;
      }
   }

   public boolean supportsFileAttributeView(String var1) {
      if (!var1.equals("basic") && !var1.equals("unix")) {
         if (var1.equals("posix")) {
            return this.supportsFileAttributeView(PosixFileAttributeView.class);
         } else {
            return var1.equals("owner") ? this.supportsFileAttributeView(FileOwnerAttributeView.class) : false;
         }
      } else {
         return true;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof UnixFileStore)) {
         return false;
      } else {
         UnixFileStore var2 = (UnixFileStore)var1;
         return this.dev == var2.dev && Arrays.equals(this.entry.dir(), var2.entry.dir()) && this.entry.name().equals(var2.entry.name());
      }
   }

   public int hashCode() {
      return (int)(this.dev ^ this.dev >>> 32) ^ Arrays.hashCode(this.entry.dir());
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(Util.toString(this.entry.dir()));
      var1.append(" (");
      var1.append(this.entry.name());
      var1.append(")");
      return var1.toString();
   }

   UnixFileStore.FeatureStatus checkIfFeaturePresent(String var1) {
      if (props == null) {
         synchronized(loadLock) {
            if (props == null) {
               props = (Properties)AccessController.doPrivileged(new PrivilegedAction<Properties>() {
                  public Properties run() {
                     return UnixFileStore.loadProperties();
                  }
               });
            }
         }
      }

      String var2 = props.getProperty(this.type());
      if (var2 != null) {
         String[] var3 = var2.split("\\s");
         String[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String var7 = var4[var6];
            var7 = var7.trim().toLowerCase();
            if (var7.equals(var1)) {
               return UnixFileStore.FeatureStatus.PRESENT;
            }

            if (var7.startsWith("no")) {
               var7 = var7.substring(2);
               if (var7.equals(var1)) {
                  return UnixFileStore.FeatureStatus.NOT_PRESENT;
               }
            }
         }
      }

      return UnixFileStore.FeatureStatus.UNKNOWN;
   }

   private static Properties loadProperties() {
      Properties var0 = new Properties();
      String var1 = System.getProperty("java.home") + "/lib/fstypes.properties";
      Path var2 = Paths.get(var1);

      try {
         SeekableByteChannel var3 = Files.newByteChannel(var2);
         Throwable var4 = null;

         try {
            var0.load(Channels.newReader(var3, "UTF-8"));
         } catch (Throwable var14) {
            var4 = var14;
            throw var14;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var13) {
                     var4.addSuppressed(var13);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (IOException var16) {
      }

      return var0;
   }

   static enum FeatureStatus {
      PRESENT,
      NOT_PRESENT,
      UNKNOWN;
   }
}
