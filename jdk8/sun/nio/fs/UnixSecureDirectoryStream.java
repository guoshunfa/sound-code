package sun.nio.fs;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.ClosedDirectoryStreamException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.LinkOption;
import java.nio.file.NotDirectoryException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.SecureDirectoryStream;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

class UnixSecureDirectoryStream implements SecureDirectoryStream<Path> {
   private final UnixDirectoryStream ds;
   private final int dfd;

   UnixSecureDirectoryStream(UnixPath var1, long var2, int var4, DirectoryStream.Filter<? super Path> var5) {
      this.ds = new UnixDirectoryStream(var1, var2, var5);
      this.dfd = var4;
   }

   public void close() throws IOException {
      this.ds.writeLock().lock();

      try {
         if (this.ds.closeImpl()) {
            UnixNativeDispatcher.close(this.dfd);
         }
      } finally {
         this.ds.writeLock().unlock();
      }

   }

   public Iterator<Path> iterator() {
      return this.ds.iterator(this);
   }

   private UnixPath getName(Path var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!(var1 instanceof UnixPath)) {
         throw new ProviderMismatchException();
      } else {
         return (UnixPath)var1;
      }
   }

   public SecureDirectoryStream<Path> newDirectoryStream(Path var1, LinkOption... var2) throws IOException {
      UnixPath var3 = this.getName(var1);
      UnixPath var4 = this.ds.directory().resolve((Path)var3);
      boolean var5 = Util.followLinks(var2);
      SecurityManager var6 = System.getSecurityManager();
      if (var6 != null) {
         var4.checkRead();
      }

      this.ds.readLock().lock();

      UnixSecureDirectoryStream var18;
      try {
         if (!this.ds.isOpen()) {
            throw new ClosedDirectoryStreamException();
         }

         byte var7 = -1;
         int var8 = -1;
         long var9 = 0L;

         try {
            int var11 = 0;
            if (!var5) {
               var11 |= 256;
            }

            int var17 = UnixNativeDispatcher.openat(this.dfd, var3.asByteArray(), var11, 0);
            var8 = UnixNativeDispatcher.dup(var17);
            var9 = UnixNativeDispatcher.fdopendir(var17);
         } catch (UnixException var15) {
            if (var7 != -1) {
               UnixNativeDispatcher.close(var7);
            }

            if (var8 != -1) {
               UnixNativeDispatcher.close(var8);
            }

            if (var15.errno() == 20) {
               throw new NotDirectoryException(var3.toString());
            }

            var15.rethrowAsIOException(var3);
         }

         var18 = new UnixSecureDirectoryStream(var4, var9, var8, (DirectoryStream.Filter)null);
      } finally {
         this.ds.readLock().unlock();
      }

      return var18;
   }

   public SeekableByteChannel newByteChannel(Path var1, Set<? extends OpenOption> var2, FileAttribute<?>... var3) throws IOException {
      UnixPath var4 = this.getName(var1);
      int var5 = UnixFileModeAttribute.toUnixMode(438, var3);
      String var6 = this.ds.directory().resolve((Path)var4).getPathForPermissionCheck();
      this.ds.readLock().lock();

      FileChannel var7;
      try {
         if (!this.ds.isOpen()) {
            throw new ClosedDirectoryStreamException();
         }

         try {
            var7 = UnixChannelFactory.newFileChannel(this.dfd, var4, var6, var2, var5);
         } catch (UnixException var12) {
            var12.rethrowAsIOException(var4);
            Object var8 = null;
            return (SeekableByteChannel)var8;
         }
      } finally {
         this.ds.readLock().unlock();
      }

      return var7;
   }

   private void implDelete(Path var1, boolean var2, int var3) throws IOException {
      UnixPath var4 = this.getName(var1);
      SecurityManager var5 = System.getSecurityManager();
      if (var5 != null) {
         this.ds.directory().resolve((Path)var4).checkDelete();
      }

      this.ds.readLock().lock();

      try {
         if (!this.ds.isOpen()) {
            throw new ClosedDirectoryStreamException();
         }

         if (!var2) {
            UnixFileAttributes var6 = null;

            try {
               var6 = UnixFileAttributes.get(this.dfd, var4, false);
            } catch (UnixException var12) {
               var12.rethrowAsIOException(var4);
            }

            var3 = var6.isDirectory() ? 0 : 0;
         }

         try {
            UnixNativeDispatcher.unlinkat(this.dfd, var4.asByteArray(), var3);
         } catch (UnixException var13) {
            if ((var3 & 0) != 0 && (var13.errno() == 17 || var13.errno() == 66)) {
               throw new DirectoryNotEmptyException((String)null);
            }

            var13.rethrowAsIOException(var4);
         }
      } finally {
         this.ds.readLock().unlock();
      }

   }

   public void deleteFile(Path var1) throws IOException {
      this.implDelete(var1, true, 0);
   }

   public void deleteDirectory(Path var1) throws IOException {
      this.implDelete(var1, true, 0);
   }

   public void move(Path var1, SecureDirectoryStream<Path> var2, Path var3) throws IOException {
      UnixPath var4 = this.getName(var1);
      UnixPath var5 = this.getName(var3);
      if (var2 == null) {
         throw new NullPointerException();
      } else if (!(var2 instanceof UnixSecureDirectoryStream)) {
         throw new ProviderMismatchException();
      } else {
         UnixSecureDirectoryStream var6 = (UnixSecureDirectoryStream)var2;
         SecurityManager var7 = System.getSecurityManager();
         if (var7 != null) {
            this.ds.directory().resolve((Path)var4).checkWrite();
            var6.ds.directory().resolve((Path)var5).checkWrite();
         }

         this.ds.readLock().lock();

         try {
            var6.ds.readLock().lock();

            try {
               if (!this.ds.isOpen() || !var6.ds.isOpen()) {
                  throw new ClosedDirectoryStreamException();
               }

               try {
                  UnixNativeDispatcher.renameat(this.dfd, var4.asByteArray(), var6.dfd, var5.asByteArray());
               } catch (UnixException var17) {
                  if (var17.errno() == 18) {
                     throw new AtomicMoveNotSupportedException(var4.toString(), var5.toString(), var17.errorString());
                  }

                  var17.rethrowAsIOException(var4, var5);
               }
            } finally {
               var6.ds.readLock().unlock();
            }
         } finally {
            this.ds.readLock().unlock();
         }

      }
   }

   private <V extends FileAttributeView> V getFileAttributeViewImpl(UnixPath var1, Class<V> var2, boolean var3) {
      if (var2 == null) {
         throw new NullPointerException();
      } else if (var2 == BasicFileAttributeView.class) {
         return new UnixSecureDirectoryStream.BasicFileAttributeViewImpl(var1, var3);
      } else {
         return (FileAttributeView)(var2 != PosixFileAttributeView.class && var2 != FileOwnerAttributeView.class ? (FileAttributeView)null : new UnixSecureDirectoryStream.PosixFileAttributeViewImpl(var1, var3));
      }
   }

   public <V extends FileAttributeView> V getFileAttributeView(Class<V> var1) {
      return this.getFileAttributeViewImpl((UnixPath)null, var1, false);
   }

   public <V extends FileAttributeView> V getFileAttributeView(Path var1, Class<V> var2, LinkOption... var3) {
      UnixPath var4 = this.getName(var1);
      boolean var5 = Util.followLinks(var3);
      return this.getFileAttributeViewImpl(var4, var2, var5);
   }

   private class PosixFileAttributeViewImpl extends UnixSecureDirectoryStream.BasicFileAttributeViewImpl implements PosixFileAttributeView {
      PosixFileAttributeViewImpl(UnixPath var2, boolean var3) {
         super(var2, var3);
      }

      private void checkWriteAndUserAccess() {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            super.checkWriteAccess();
            var1.checkPermission(new RuntimePermission("accessUserInformation"));
         }

      }

      public String name() {
         return "posix";
      }

      public PosixFileAttributes readAttributes() throws IOException {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            if (this.file == null) {
               UnixSecureDirectoryStream.this.ds.directory().checkRead();
            } else {
               UnixSecureDirectoryStream.this.ds.directory().resolve((Path)this.file).checkRead();
            }

            var1.checkPermission(new RuntimePermission("accessUserInformation"));
         }

         UnixSecureDirectoryStream.this.ds.readLock().lock();

         UnixFileAttributes var3;
         try {
            if (!UnixSecureDirectoryStream.this.ds.isOpen()) {
               throw new ClosedDirectoryStreamException();
            }

            try {
               UnixFileAttributes var2 = this.file == null ? UnixFileAttributes.get(UnixSecureDirectoryStream.this.dfd) : UnixFileAttributes.get(UnixSecureDirectoryStream.this.dfd, this.file, this.followLinks);
               var3 = var2;
               return var3;
            } catch (UnixException var7) {
               var7.rethrowAsIOException(this.file);
               var3 = null;
            }
         } finally {
            UnixSecureDirectoryStream.this.ds.readLock().unlock();
         }

         return var3;
      }

      public void setPermissions(Set<PosixFilePermission> var1) throws IOException {
         this.checkWriteAndUserAccess();
         UnixSecureDirectoryStream.this.ds.readLock().lock();

         try {
            if (!UnixSecureDirectoryStream.this.ds.isOpen()) {
               throw new ClosedDirectoryStreamException();
            }

            int var2 = this.file == null ? UnixSecureDirectoryStream.this.dfd : this.open();

            try {
               UnixNativeDispatcher.fchmod(var2, UnixFileModeAttribute.toUnixMode(var1));
            } catch (UnixException var12) {
               var12.rethrowAsIOException(this.file);
            } finally {
               if (this.file != null && var2 >= 0) {
                  UnixNativeDispatcher.close(var2);
               }

            }
         } finally {
            UnixSecureDirectoryStream.this.ds.readLock().unlock();
         }

      }

      private void setOwners(int var1, int var2) throws IOException {
         this.checkWriteAndUserAccess();
         UnixSecureDirectoryStream.this.ds.readLock().lock();

         try {
            if (!UnixSecureDirectoryStream.this.ds.isOpen()) {
               throw new ClosedDirectoryStreamException();
            }

            int var3 = this.file == null ? UnixSecureDirectoryStream.this.dfd : this.open();

            try {
               UnixNativeDispatcher.fchown(var3, var1, var2);
            } catch (UnixException var13) {
               var13.rethrowAsIOException(this.file);
            } finally {
               if (this.file != null && var3 >= 0) {
                  UnixNativeDispatcher.close(var3);
               }

            }
         } finally {
            UnixSecureDirectoryStream.this.ds.readLock().unlock();
         }

      }

      public UserPrincipal getOwner() throws IOException {
         return this.readAttributes().owner();
      }

      public void setOwner(UserPrincipal var1) throws IOException {
         if (!(var1 instanceof UnixUserPrincipals.User)) {
            throw new ProviderMismatchException();
         } else if (var1 instanceof UnixUserPrincipals.Group) {
            throw new IOException("'owner' parameter can't be a group");
         } else {
            int var2 = ((UnixUserPrincipals.User)var1).uid();
            this.setOwners(var2, -1);
         }
      }

      public void setGroup(GroupPrincipal var1) throws IOException {
         if (!(var1 instanceof UnixUserPrincipals.Group)) {
            throw new ProviderMismatchException();
         } else {
            int var2 = ((UnixUserPrincipals.Group)var1).gid();
            this.setOwners(-1, var2);
         }
      }
   }

   private class BasicFileAttributeViewImpl implements BasicFileAttributeView {
      final UnixPath file;
      final boolean followLinks;

      BasicFileAttributeViewImpl(UnixPath var2, boolean var3) {
         this.file = var2;
         this.followLinks = var3;
      }

      int open() throws IOException {
         int var1 = 0;
         if (!this.followLinks) {
            var1 |= 256;
         }

         try {
            return UnixNativeDispatcher.openat(UnixSecureDirectoryStream.this.dfd, this.file.asByteArray(), var1, 0);
         } catch (UnixException var3) {
            var3.rethrowAsIOException(this.file);
            return -1;
         }
      }

      private void checkWriteAccess() {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            if (this.file == null) {
               UnixSecureDirectoryStream.this.ds.directory().checkWrite();
            } else {
               UnixSecureDirectoryStream.this.ds.directory().resolve((Path)this.file).checkWrite();
            }
         }

      }

      public String name() {
         return "basic";
      }

      public BasicFileAttributes readAttributes() throws IOException {
         UnixSecureDirectoryStream.this.ds.readLock().lock();

         BasicFileAttributes var3;
         try {
            if (!UnixSecureDirectoryStream.this.ds.isOpen()) {
               throw new ClosedDirectoryStreamException();
            }

            SecurityManager var1 = System.getSecurityManager();
            if (var1 != null) {
               if (this.file == null) {
                  UnixSecureDirectoryStream.this.ds.directory().checkRead();
               } else {
                  UnixSecureDirectoryStream.this.ds.directory().resolve((Path)this.file).checkRead();
               }
            }

            try {
               UnixFileAttributes var2 = this.file == null ? UnixFileAttributes.get(UnixSecureDirectoryStream.this.dfd) : UnixFileAttributes.get(UnixSecureDirectoryStream.this.dfd, this.file, this.followLinks);
               var3 = var2.asBasicFileAttributes();
               return var3;
            } catch (UnixException var7) {
               var7.rethrowAsIOException(this.file);
               var3 = null;
            }
         } finally {
            UnixSecureDirectoryStream.this.ds.readLock().unlock();
         }

         return var3;
      }

      public void setTimes(FileTime var1, FileTime var2, FileTime var3) throws IOException {
         this.checkWriteAccess();
         UnixSecureDirectoryStream.this.ds.readLock().lock();

         try {
            if (!UnixSecureDirectoryStream.this.ds.isOpen()) {
               throw new ClosedDirectoryStreamException();
            }

            int var4 = this.file == null ? UnixSecureDirectoryStream.this.dfd : this.open();

            try {
               if (var1 == null || var2 == null) {
                  try {
                     UnixFileAttributes var5 = UnixFileAttributes.get(var4);
                     if (var1 == null) {
                        var1 = var5.lastModifiedTime();
                     }

                     if (var2 == null) {
                        var2 = var5.lastAccessTime();
                     }
                  } catch (UnixException var17) {
                     var17.rethrowAsIOException(this.file);
                  }
               }

               try {
                  UnixNativeDispatcher.futimes(var4, var2.to(TimeUnit.MICROSECONDS), var1.to(TimeUnit.MICROSECONDS));
               } catch (UnixException var16) {
                  var16.rethrowAsIOException(this.file);
               }
            } finally {
               if (this.file != null) {
                  UnixNativeDispatcher.close(var4);
               }

            }
         } finally {
            UnixSecureDirectoryStream.this.ds.readLock().unlock();
         }

      }
   }
}
