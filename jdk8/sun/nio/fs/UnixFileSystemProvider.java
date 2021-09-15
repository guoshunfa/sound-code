package sun.nio.fs;

import java.io.FilePermission;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.LinkOption;
import java.nio.file.LinkPermission;
import java.nio.file.NotDirectoryException;
import java.nio.file.NotLinkException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.spi.FileTypeDetector;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import sun.nio.ch.ThreadPool;

public abstract class UnixFileSystemProvider extends AbstractFileSystemProvider {
   private static final String USER_DIR = "user.dir";
   private final UnixFileSystem theFileSystem;

   public UnixFileSystemProvider() {
      String var1 = System.getProperty("user.dir");
      this.theFileSystem = this.newFileSystem(var1);
   }

   abstract UnixFileSystem newFileSystem(String var1);

   public final String getScheme() {
      return "file";
   }

   private void checkUri(URI var1) {
      if (!var1.getScheme().equalsIgnoreCase(this.getScheme())) {
         throw new IllegalArgumentException("URI does not match this provider");
      } else if (var1.getAuthority() != null) {
         throw new IllegalArgumentException("Authority component present");
      } else if (var1.getPath() == null) {
         throw new IllegalArgumentException("Path component is undefined");
      } else if (!var1.getPath().equals("/")) {
         throw new IllegalArgumentException("Path component should be '/'");
      } else if (var1.getQuery() != null) {
         throw new IllegalArgumentException("Query component present");
      } else if (var1.getFragment() != null) {
         throw new IllegalArgumentException("Fragment component present");
      }
   }

   public final FileSystem newFileSystem(URI var1, Map<String, ?> var2) {
      this.checkUri(var1);
      throw new FileSystemAlreadyExistsException();
   }

   public final FileSystem getFileSystem(URI var1) {
      this.checkUri(var1);
      return this.theFileSystem;
   }

   public Path getPath(URI var1) {
      return UnixUriUtils.fromUri(this.theFileSystem, var1);
   }

   UnixPath checkPath(Path var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!(var1 instanceof UnixPath)) {
         throw new ProviderMismatchException();
      } else {
         return (UnixPath)var1;
      }
   }

   public <V extends FileAttributeView> V getFileAttributeView(Path var1, Class<V> var2, LinkOption... var3) {
      UnixPath var4 = UnixPath.toUnixPath(var1);
      boolean var5 = Util.followLinks(var3);
      if (var2 == BasicFileAttributeView.class) {
         return UnixFileAttributeViews.createBasicView(var4, var5);
      } else if (var2 == PosixFileAttributeView.class) {
         return UnixFileAttributeViews.createPosixView(var4, var5);
      } else if (var2 == FileOwnerAttributeView.class) {
         return UnixFileAttributeViews.createOwnerView(var4, var5);
      } else if (var2 == null) {
         throw new NullPointerException();
      } else {
         return (FileAttributeView)null;
      }
   }

   public <A extends BasicFileAttributes> A readAttributes(Path var1, Class<A> var2, LinkOption... var3) throws IOException {
      Class var4;
      if (var2 == BasicFileAttributes.class) {
         var4 = BasicFileAttributeView.class;
      } else {
         if (var2 != PosixFileAttributes.class) {
            if (var2 == null) {
               throw new NullPointerException();
            }

            throw new UnsupportedOperationException();
         }

         var4 = PosixFileAttributeView.class;
      }

      return ((BasicFileAttributeView)this.getFileAttributeView(var1, var4, var3)).readAttributes();
   }

   protected DynamicFileAttributeView getFileAttributeView(Path var1, String var2, LinkOption... var3) {
      UnixPath var4 = UnixPath.toUnixPath(var1);
      boolean var5 = Util.followLinks(var3);
      if (var2.equals("basic")) {
         return UnixFileAttributeViews.createBasicView(var4, var5);
      } else if (var2.equals("posix")) {
         return UnixFileAttributeViews.createPosixView(var4, var5);
      } else if (var2.equals("unix")) {
         return UnixFileAttributeViews.createUnixView(var4, var5);
      } else {
         return var2.equals("owner") ? UnixFileAttributeViews.createOwnerView(var4, var5) : null;
      }
   }

   public FileChannel newFileChannel(Path var1, Set<? extends OpenOption> var2, FileAttribute<?>... var3) throws IOException {
      UnixPath var4 = this.checkPath(var1);
      int var5 = UnixFileModeAttribute.toUnixMode(438, var3);

      try {
         return UnixChannelFactory.newFileChannel(var4, var2, var5);
      } catch (UnixException var7) {
         var7.rethrowAsIOException(var4);
         return null;
      }
   }

   public AsynchronousFileChannel newAsynchronousFileChannel(Path var1, Set<? extends OpenOption> var2, ExecutorService var3, FileAttribute<?>... var4) throws IOException {
      UnixPath var5 = this.checkPath(var1);
      int var6 = UnixFileModeAttribute.toUnixMode(438, var4);
      ThreadPool var7 = var3 == null ? null : ThreadPool.wrap(var3, 0);

      try {
         return UnixChannelFactory.newAsynchronousFileChannel(var5, var2, var6, var7);
      } catch (UnixException var9) {
         var9.rethrowAsIOException(var5);
         return null;
      }
   }

   public SeekableByteChannel newByteChannel(Path var1, Set<? extends OpenOption> var2, FileAttribute<?>... var3) throws IOException {
      UnixPath var4 = UnixPath.toUnixPath(var1);
      int var5 = UnixFileModeAttribute.toUnixMode(438, var3);

      try {
         return UnixChannelFactory.newFileChannel(var4, var2, var5);
      } catch (UnixException var7) {
         var7.rethrowAsIOException(var4);
         return null;
      }
   }

   boolean implDelete(Path var1, boolean var2) throws IOException {
      UnixPath var3 = UnixPath.toUnixPath(var1);
      var3.checkDelete();
      UnixFileAttributes var4 = null;

      try {
         var4 = UnixFileAttributes.get(var3, false);
         if (var4.isDirectory()) {
            UnixNativeDispatcher.rmdir(var3);
         } else {
            UnixNativeDispatcher.unlink(var3);
         }

         return true;
      } catch (UnixException var6) {
         if (!var2 && var6.errno() == 2) {
            return false;
         } else if (var4 == null || !var4.isDirectory() || var6.errno() != 17 && var6.errno() != 66) {
            var6.rethrowAsIOException(var3);
            return false;
         } else {
            throw new DirectoryNotEmptyException(var3.getPathForExceptionMessage());
         }
      }
   }

   public void copy(Path var1, Path var2, CopyOption... var3) throws IOException {
      UnixCopyFile.copy(UnixPath.toUnixPath(var1), UnixPath.toUnixPath(var2), var3);
   }

   public void move(Path var1, Path var2, CopyOption... var3) throws IOException {
      UnixCopyFile.move(UnixPath.toUnixPath(var1), UnixPath.toUnixPath(var2), var3);
   }

   public void checkAccess(Path var1, AccessMode... var2) throws IOException {
      UnixPath var3 = UnixPath.toUnixPath(var1);
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      boolean var7 = false;
      if (var2.length == 0) {
         var4 = true;
      } else {
         AccessMode[] var8 = var2;
         int var9 = var2.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            AccessMode var11 = var8[var10];
            switch(var11) {
            case READ:
               var5 = true;
               break;
            case WRITE:
               var6 = true;
               break;
            case EXECUTE:
               var7 = true;
               break;
            default:
               throw new AssertionError("Should not get here");
            }
         }
      }

      int var13 = 0;
      if (var4 || var5) {
         var3.checkRead();
         var13 |= var5 ? 4 : 0;
      }

      if (var6) {
         var3.checkWrite();
         var13 |= 2;
      }

      if (var7) {
         SecurityManager var14 = System.getSecurityManager();
         if (var14 != null) {
            var14.checkExec(var3.getPathForPermissionCheck());
         }

         var13 |= 1;
      }

      try {
         UnixNativeDispatcher.access(var3, var13);
      } catch (UnixException var12) {
         var12.rethrowAsIOException(var3);
      }

   }

   public boolean isSameFile(Path var1, Path var2) throws IOException {
      UnixPath var3 = UnixPath.toUnixPath(var1);
      if (var3.equals(var2)) {
         return true;
      } else if (var2 == null) {
         throw new NullPointerException();
      } else if (!(var2 instanceof UnixPath)) {
         return false;
      } else {
         UnixPath var4 = (UnixPath)var2;
         var3.checkRead();
         var4.checkRead();

         UnixFileAttributes var5;
         try {
            var5 = UnixFileAttributes.get(var3, true);
         } catch (UnixException var9) {
            var9.rethrowAsIOException(var3);
            return false;
         }

         UnixFileAttributes var6;
         try {
            var6 = UnixFileAttributes.get(var4, true);
         } catch (UnixException var8) {
            var8.rethrowAsIOException(var4);
            return false;
         }

         return var5.isSameFile(var6);
      }
   }

   public boolean isHidden(Path var1) {
      UnixPath var2 = UnixPath.toUnixPath(var1);
      var2.checkRead();
      UnixPath var3 = var2.getFileName();
      if (var3 == null) {
         return false;
      } else {
         return var3.asByteArray()[0] == 46;
      }
   }

   abstract FileStore getFileStore(UnixPath var1) throws IOException;

   public FileStore getFileStore(Path var1) throws IOException {
      UnixPath var2 = UnixPath.toUnixPath(var1);
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkPermission(new RuntimePermission("getFileStoreAttributes"));
         var2.checkRead();
      }

      return this.getFileStore(var2);
   }

   public void createDirectory(Path var1, FileAttribute<?>... var2) throws IOException {
      UnixPath var3 = UnixPath.toUnixPath(var1);
      var3.checkWrite();
      int var4 = UnixFileModeAttribute.toUnixMode(511, var2);

      try {
         UnixNativeDispatcher.mkdir(var3, var4);
      } catch (UnixException var6) {
         if (var6.errno() == 21) {
            throw new FileAlreadyExistsException(var3.toString());
         }

         var6.rethrowAsIOException(var3);
      }

   }

   public DirectoryStream<Path> newDirectoryStream(Path var1, DirectoryStream.Filter<? super Path> var2) throws IOException {
      UnixPath var3 = UnixPath.toUnixPath(var1);
      var3.checkRead();
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         if (!UnixNativeDispatcher.openatSupported()) {
            try {
               long var12 = UnixNativeDispatcher.opendir(var3);
               return new UnixDirectoryStream(var3, var12, var2);
            } catch (UnixException var10) {
               if (var10.errno() == 20) {
                  throw new NotDirectoryException(var3.getPathForExceptionMessage());
               }

               var10.rethrowAsIOException(var3);
            }
         }

         byte var4 = -1;
         int var5 = -1;
         long var6 = 0L;

         try {
            int var11 = UnixNativeDispatcher.open(var3, 0, 0);
            var5 = UnixNativeDispatcher.dup(var11);
            var6 = UnixNativeDispatcher.fdopendir(var11);
         } catch (UnixException var9) {
            if (var4 != -1) {
               UnixNativeDispatcher.close(var4);
            }

            if (var5 != -1) {
               UnixNativeDispatcher.close(var5);
            }

            if (var9.errno() == 20) {
               throw new NotDirectoryException(var3.getPathForExceptionMessage());
            }

            var9.rethrowAsIOException(var3);
         }

         return new UnixSecureDirectoryStream(var3, var6, var5, var2);
      }
   }

   public void createSymbolicLink(Path var1, Path var2, FileAttribute<?>... var3) throws IOException {
      UnixPath var4 = UnixPath.toUnixPath(var1);
      UnixPath var5 = UnixPath.toUnixPath(var2);
      if (var3.length > 0) {
         UnixFileModeAttribute.toUnixMode(0, var3);
         throw new UnsupportedOperationException("Initial file attributesnot supported when creating symbolic link");
      } else {
         SecurityManager var6 = System.getSecurityManager();
         if (var6 != null) {
            var6.checkPermission(new LinkPermission("symbolic"));
            var4.checkWrite();
         }

         try {
            UnixNativeDispatcher.symlink(var5.asByteArray(), var4);
         } catch (UnixException var8) {
            var8.rethrowAsIOException(var4);
         }

      }
   }

   public void createLink(Path var1, Path var2) throws IOException {
      UnixPath var3 = UnixPath.toUnixPath(var1);
      UnixPath var4 = UnixPath.toUnixPath(var2);
      SecurityManager var5 = System.getSecurityManager();
      if (var5 != null) {
         var5.checkPermission(new LinkPermission("hard"));
         var3.checkWrite();
         var4.checkWrite();
      }

      try {
         UnixNativeDispatcher.link(var4, var3);
      } catch (UnixException var7) {
         var7.rethrowAsIOException(var3, var4);
      }

   }

   public Path readSymbolicLink(Path var1) throws IOException {
      UnixPath var2 = UnixPath.toUnixPath(var1);
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         FilePermission var4 = new FilePermission(var2.getPathForPermissionCheck(), "readlink");
         var3.checkPermission(var4);
      }

      try {
         byte[] var6 = UnixNativeDispatcher.readlink(var2);
         return new UnixPath(var2.getFileSystem(), var6);
      } catch (UnixException var5) {
         if (var5.errno() == 22) {
            throw new NotLinkException(var2.getPathForExceptionMessage());
         } else {
            var5.rethrowAsIOException(var2);
            return null;
         }
      }
   }

   FileTypeDetector getFileTypeDetector() {
      return new AbstractFileTypeDetector() {
         public String implProbeContentType(Path var1) {
            return null;
         }
      };
   }

   final FileTypeDetector chain(final AbstractFileTypeDetector... var1) {
      return new AbstractFileTypeDetector() {
         protected String implProbeContentType(Path var1x) throws IOException {
            AbstractFileTypeDetector[] var2 = var1;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               AbstractFileTypeDetector var5 = var2[var4];
               String var6 = var5.implProbeContentType(var1x);
               if (var6 != null && !var6.isEmpty()) {
                  return var6;
               }
            }

            return null;
         }
      };
   }
}
