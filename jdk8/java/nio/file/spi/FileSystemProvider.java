package java.nio.file.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public abstract class FileSystemProvider {
   private static final Object lock = new Object();
   private static volatile List<FileSystemProvider> installedProviders;
   private static boolean loadingProviders = false;

   private static Void checkPermission() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(new RuntimePermission("fileSystemProvider"));
      }

      return null;
   }

   private FileSystemProvider(Void var1) {
   }

   protected FileSystemProvider() {
      this(checkPermission());
   }

   private static List<FileSystemProvider> loadInstalledProviders() {
      ArrayList var0 = new ArrayList();
      ServiceLoader var1 = ServiceLoader.load(FileSystemProvider.class, ClassLoader.getSystemClassLoader());
      Iterator var2 = var1.iterator();

      while(true) {
         FileSystemProvider var3;
         String var4;
         do {
            if (!var2.hasNext()) {
               return var0;
            }

            var3 = (FileSystemProvider)var2.next();
            var4 = var3.getScheme();
         } while(var4.equalsIgnoreCase("file"));

         boolean var5 = false;
         Iterator var6 = var0.iterator();

         while(var6.hasNext()) {
            FileSystemProvider var7 = (FileSystemProvider)var6.next();
            if (var7.getScheme().equalsIgnoreCase(var4)) {
               var5 = true;
               break;
            }
         }

         if (!var5) {
            var0.add(var3);
         }
      }
   }

   public static List<FileSystemProvider> installedProviders() {
      if (installedProviders == null) {
         FileSystemProvider var0 = FileSystems.getDefault().provider();
         synchronized(lock) {
            if (installedProviders == null) {
               if (loadingProviders) {
                  throw new Error("Circular loading of installed providers detected");
               }

               loadingProviders = true;
               List var2 = (List)AccessController.doPrivileged(new PrivilegedAction<List<FileSystemProvider>>() {
                  public List<FileSystemProvider> run() {
                     return FileSystemProvider.loadInstalledProviders();
                  }
               });
               var2.add(0, var0);
               installedProviders = Collections.unmodifiableList(var2);
            }
         }
      }

      return installedProviders;
   }

   public abstract String getScheme();

   public abstract FileSystem newFileSystem(URI var1, Map<String, ?> var2) throws IOException;

   public abstract FileSystem getFileSystem(URI var1);

   public abstract Path getPath(URI var1);

   public FileSystem newFileSystem(Path var1, Map<String, ?> var2) throws IOException {
      throw new UnsupportedOperationException();
   }

   public InputStream newInputStream(Path var1, OpenOption... var2) throws IOException {
      if (var2.length > 0) {
         OpenOption[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            OpenOption var6 = var3[var5];
            if (var6 == StandardOpenOption.APPEND || var6 == StandardOpenOption.WRITE) {
               throw new UnsupportedOperationException("'" + var6 + "' not allowed");
            }
         }
      }

      return Channels.newInputStream((ReadableByteChannel)Files.newByteChannel(var1, var2));
   }

   public OutputStream newOutputStream(Path var1, OpenOption... var2) throws IOException {
      int var3 = var2.length;
      HashSet var4 = new HashSet(var3 + 3);
      if (var3 == 0) {
         var4.add(StandardOpenOption.CREATE);
         var4.add(StandardOpenOption.TRUNCATE_EXISTING);
      } else {
         OpenOption[] var5 = var2;
         int var6 = var2.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            OpenOption var8 = var5[var7];
            if (var8 == StandardOpenOption.READ) {
               throw new IllegalArgumentException("READ not allowed");
            }

            var4.add(var8);
         }
      }

      var4.add(StandardOpenOption.WRITE);
      return Channels.newOutputStream((WritableByteChannel)this.newByteChannel(var1, var4));
   }

   public FileChannel newFileChannel(Path var1, Set<? extends OpenOption> var2, FileAttribute<?>... var3) throws IOException {
      throw new UnsupportedOperationException();
   }

   public AsynchronousFileChannel newAsynchronousFileChannel(Path var1, Set<? extends OpenOption> var2, ExecutorService var3, FileAttribute<?>... var4) throws IOException {
      throw new UnsupportedOperationException();
   }

   public abstract SeekableByteChannel newByteChannel(Path var1, Set<? extends OpenOption> var2, FileAttribute<?>... var3) throws IOException;

   public abstract DirectoryStream<Path> newDirectoryStream(Path var1, DirectoryStream.Filter<? super Path> var2) throws IOException;

   public abstract void createDirectory(Path var1, FileAttribute<?>... var2) throws IOException;

   public void createSymbolicLink(Path var1, Path var2, FileAttribute<?>... var3) throws IOException {
      throw new UnsupportedOperationException();
   }

   public void createLink(Path var1, Path var2) throws IOException {
      throw new UnsupportedOperationException();
   }

   public abstract void delete(Path var1) throws IOException;

   public boolean deleteIfExists(Path var1) throws IOException {
      try {
         this.delete(var1);
         return true;
      } catch (NoSuchFileException var3) {
         return false;
      }
   }

   public Path readSymbolicLink(Path var1) throws IOException {
      throw new UnsupportedOperationException();
   }

   public abstract void copy(Path var1, Path var2, CopyOption... var3) throws IOException;

   public abstract void move(Path var1, Path var2, CopyOption... var3) throws IOException;

   public abstract boolean isSameFile(Path var1, Path var2) throws IOException;

   public abstract boolean isHidden(Path var1) throws IOException;

   public abstract FileStore getFileStore(Path var1) throws IOException;

   public abstract void checkAccess(Path var1, AccessMode... var2) throws IOException;

   public abstract <V extends FileAttributeView> V getFileAttributeView(Path var1, Class<V> var2, LinkOption... var3);

   public abstract <A extends BasicFileAttributes> A readAttributes(Path var1, Class<A> var2, LinkOption... var3) throws IOException;

   public abstract Map<String, Object> readAttributes(Path var1, String var2, LinkOption... var3) throws IOException;

   public abstract void setAttribute(Path var1, String var2, Object var3, LinkOption... var4) throws IOException;
}
