package sun.nio.fs;

import com.sun.nio.file.ExtendedCopyOption;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.LinkOption;
import java.nio.file.LinkPermission;
import java.nio.file.StandardCopyOption;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

class UnixCopyFile {
   private UnixCopyFile() {
   }

   private static void copyDirectory(UnixPath var0, UnixFileAttributes var1, UnixPath var2, UnixCopyFile.Flags var3) throws IOException {
      try {
         UnixNativeDispatcher.mkdir(var2, var1.mode());
      } catch (UnixException var21) {
         var21.rethrowAsIOException(var2);
      }

      if (var3.copyBasicAttributes || var3.copyPosixAttributes || var3.copyNonPosixAttributes) {
         int var4 = -1;

         try {
            var4 = UnixNativeDispatcher.open(var2, 0, 0);
         } catch (UnixException var26) {
            if (var3.copyNonPosixAttributes && var3.failIfUnableToCopyNonPosix) {
               try {
                  UnixNativeDispatcher.rmdir(var2);
               } catch (UnixException var20) {
               }

               var26.rethrowAsIOException(var2);
            }
         }

         boolean var5 = false;

         try {
            if (var3.copyPosixAttributes) {
               try {
                  if (var4 >= 0) {
                     UnixNativeDispatcher.fchown(var4, var1.uid(), var1.gid());
                     UnixNativeDispatcher.fchmod(var4, var1.mode());
                  } else {
                     UnixNativeDispatcher.chown(var2, var1.uid(), var1.gid());
                     UnixNativeDispatcher.chmod(var2, var1.mode());
                  }
               } catch (UnixException var23) {
                  if (var3.failIfUnableToCopyPosix) {
                     var23.rethrowAsIOException(var2);
                  }
               }
            }

            if (var3.copyNonPosixAttributes && var4 >= 0) {
               int var6 = -1;

               try {
                  var6 = UnixNativeDispatcher.open(var0, 0, 0);
               } catch (UnixException var24) {
                  if (var3.failIfUnableToCopyNonPosix) {
                     var24.rethrowAsIOException(var0);
                  }
               }

               if (var6 >= 0) {
                  var0.getFileSystem().copyNonPosixAttributes(var6, var4);
                  UnixNativeDispatcher.close(var6);
               }
            }

            if (var3.copyBasicAttributes) {
               try {
                  if (var4 >= 0 && UnixNativeDispatcher.futimesSupported()) {
                     UnixNativeDispatcher.futimes(var4, var1.lastAccessTime().to(TimeUnit.MICROSECONDS), var1.lastModifiedTime().to(TimeUnit.MICROSECONDS));
                  } else {
                     UnixNativeDispatcher.utimes(var2, var1.lastAccessTime().to(TimeUnit.MICROSECONDS), var1.lastModifiedTime().to(TimeUnit.MICROSECONDS));
                  }
               } catch (UnixException var22) {
                  if (var3.failIfUnableToCopyBasic) {
                     var22.rethrowAsIOException(var2);
                  }
               }
            }

            var5 = true;
         } finally {
            if (var4 >= 0) {
               UnixNativeDispatcher.close(var4);
            }

            if (!var5) {
               try {
                  UnixNativeDispatcher.rmdir(var2);
               } catch (UnixException var19) {
               }
            }

         }

      }
   }

   private static void copyFile(UnixPath var0, UnixFileAttributes var1, UnixPath var2, UnixCopyFile.Flags var3, long var4) throws IOException {
      int var6 = -1;

      try {
         var6 = UnixNativeDispatcher.open(var0, 0, 0);
      } catch (UnixException var34) {
         var34.rethrowAsIOException(var0);
      }

      try {
         int var7 = -1;

         try {
            var7 = UnixNativeDispatcher.open(var2, 2561, var1.mode());
         } catch (UnixException var33) {
            var33.rethrowAsIOException(var2);
         }

         boolean var8 = false;

         try {
            try {
               transfer(var7, var6, var4);
            } catch (UnixException var32) {
               var32.rethrowAsIOException(var0, var2);
            }

            if (var3.copyPosixAttributes) {
               try {
                  UnixNativeDispatcher.fchown(var7, var1.uid(), var1.gid());
                  UnixNativeDispatcher.fchmod(var7, var1.mode());
               } catch (UnixException var36) {
                  if (var3.failIfUnableToCopyPosix) {
                     var36.rethrowAsIOException(var2);
                  }
               }
            }

            if (var3.copyNonPosixAttributes) {
               var0.getFileSystem().copyNonPosixAttributes(var6, var7);
            }

            if (var3.copyBasicAttributes) {
               try {
                  if (UnixNativeDispatcher.futimesSupported()) {
                     UnixNativeDispatcher.futimes(var7, var1.lastAccessTime().to(TimeUnit.MICROSECONDS), var1.lastModifiedTime().to(TimeUnit.MICROSECONDS));
                  } else {
                     UnixNativeDispatcher.utimes(var2, var1.lastAccessTime().to(TimeUnit.MICROSECONDS), var1.lastModifiedTime().to(TimeUnit.MICROSECONDS));
                  }
               } catch (UnixException var35) {
                  if (var3.failIfUnableToCopyBasic) {
                     var35.rethrowAsIOException(var2);
                  }
               }
            }

            var8 = true;
         } finally {
            UnixNativeDispatcher.close(var7);
            if (!var8) {
               try {
                  UnixNativeDispatcher.unlink(var2);
               } catch (UnixException var31) {
               }
            }

         }
      } finally {
         UnixNativeDispatcher.close(var6);
      }

   }

   private static void copyLink(UnixPath var0, UnixFileAttributes var1, UnixPath var2, UnixCopyFile.Flags var3) throws IOException {
      byte[] var4 = null;

      try {
         var4 = UnixNativeDispatcher.readlink(var0);
      } catch (UnixException var8) {
         var8.rethrowAsIOException(var0);
      }

      try {
         UnixNativeDispatcher.symlink(var4, var2);
         if (var3.copyPosixAttributes) {
            try {
               UnixNativeDispatcher.lchown(var2, var1.uid(), var1.gid());
            } catch (UnixException var6) {
            }
         }
      } catch (UnixException var7) {
         var7.rethrowAsIOException(var2);
      }

   }

   private static void copySpecial(UnixPath var0, UnixFileAttributes var1, UnixPath var2, UnixCopyFile.Flags var3) throws IOException {
      try {
         UnixNativeDispatcher.mknod(var2, var1.mode(), var1.rdev());
      } catch (UnixException var15) {
         var15.rethrowAsIOException(var2);
      }

      boolean var4 = false;

      try {
         if (var3.copyPosixAttributes) {
            try {
               UnixNativeDispatcher.chown(var2, var1.uid(), var1.gid());
               UnixNativeDispatcher.chmod(var2, var1.mode());
            } catch (UnixException var17) {
               if (var3.failIfUnableToCopyPosix) {
                  var17.rethrowAsIOException(var2);
               }
            }
         }

         if (var3.copyBasicAttributes) {
            try {
               UnixNativeDispatcher.utimes(var2, var1.lastAccessTime().to(TimeUnit.MICROSECONDS), var1.lastModifiedTime().to(TimeUnit.MICROSECONDS));
            } catch (UnixException var16) {
               if (var3.failIfUnableToCopyBasic) {
                  var16.rethrowAsIOException(var2);
               }
            }
         }

         var4 = true;
      } finally {
         if (!var4) {
            try {
               UnixNativeDispatcher.unlink(var2);
            } catch (UnixException var14) {
            }
         }

      }

   }

   static void move(UnixPath var0, UnixPath var1, CopyOption... var2) throws IOException {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var0.checkWrite();
         var1.checkWrite();
      }

      UnixCopyFile.Flags var4 = UnixCopyFile.Flags.fromMoveOptions(var2);
      if (var4.atomicMove) {
         try {
            UnixNativeDispatcher.rename(var0, var1);
         } catch (UnixException var15) {
            if (var15.errno() == 18) {
               throw new AtomicMoveNotSupportedException(var0.getPathForExceptionMessage(), var1.getPathForExceptionMessage(), var15.errorString());
            }

            var15.rethrowAsIOException(var0, var1);
         }

      } else {
         UnixFileAttributes var5 = null;
         UnixFileAttributes var6 = null;

         try {
            var5 = UnixFileAttributes.get(var0, false);
         } catch (UnixException var12) {
            var12.rethrowAsIOException(var0);
         }

         try {
            var6 = UnixFileAttributes.get(var1, false);
         } catch (UnixException var11) {
         }

         boolean var7 = var6 != null;
         if (var7) {
            if (var5.isSameFile(var6)) {
               return;
            }

            if (!var4.replaceExisting) {
               throw new FileAlreadyExistsException(var1.getPathForExceptionMessage());
            }

            try {
               if (var6.isDirectory()) {
                  UnixNativeDispatcher.rmdir(var1);
               } else {
                  UnixNativeDispatcher.unlink(var1);
               }
            } catch (UnixException var16) {
               if (var6.isDirectory() && (var16.errno() == 17 || var16.errno() == 66)) {
                  throw new DirectoryNotEmptyException(var1.getPathForExceptionMessage());
               }

               var16.rethrowAsIOException(var1);
            }
         }

         try {
            UnixNativeDispatcher.rename(var0, var1);
         } catch (UnixException var14) {
            if (var14.errno() != 18 && var14.errno() != 21) {
               var14.rethrowAsIOException(var0, var1);
            }

            if (var5.isDirectory()) {
               copyDirectory(var0, var5, var1, var4);
            } else if (var5.isSymbolicLink()) {
               copyLink(var0, var5, var1, var4);
            } else if (var5.isDevice()) {
               copySpecial(var0, var5, var1, var4);
            } else {
               copyFile(var0, var5, var1, var4, 0L);
            }

            try {
               if (var5.isDirectory()) {
                  UnixNativeDispatcher.rmdir(var0);
               } else {
                  UnixNativeDispatcher.unlink(var0);
               }
            } catch (UnixException var13) {
               try {
                  if (var5.isDirectory()) {
                     UnixNativeDispatcher.rmdir(var1);
                  } else {
                     UnixNativeDispatcher.unlink(var1);
                  }
               } catch (UnixException var10) {
               }

               if (var5.isDirectory() && (var13.errno() == 17 || var13.errno() == 66)) {
                  throw new DirectoryNotEmptyException(var0.getPathForExceptionMessage());
               }

               var13.rethrowAsIOException(var0);
            }

         }
      }
   }

   static void copy(final UnixPath var0, final UnixPath var1, CopyOption... var2) throws IOException {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var0.checkRead();
         var1.checkWrite();
      }

      final UnixCopyFile.Flags var4 = UnixCopyFile.Flags.fromCopyOptions(var2);
      final UnixFileAttributes var5 = null;
      UnixFileAttributes var6 = null;

      try {
         var5 = UnixFileAttributes.get(var0, var4.followLinks);
      } catch (UnixException var13) {
         var13.rethrowAsIOException(var0);
      }

      if (var3 != null && var5.isSymbolicLink()) {
         var3.checkPermission(new LinkPermission("symbolic"));
      }

      try {
         var6 = UnixFileAttributes.get(var1, false);
      } catch (UnixException var12) {
      }

      boolean var7 = var6 != null;
      if (var7) {
         if (var5.isSameFile(var6)) {
            return;
         }

         if (!var4.replaceExisting) {
            throw new FileAlreadyExistsException(var1.getPathForExceptionMessage());
         }

         try {
            if (var6.isDirectory()) {
               UnixNativeDispatcher.rmdir(var1);
            } else {
               UnixNativeDispatcher.unlink(var1);
            }
         } catch (UnixException var15) {
            if (var6.isDirectory() && (var15.errno() == 17 || var15.errno() == 66)) {
               throw new DirectoryNotEmptyException(var1.getPathForExceptionMessage());
            }

            var15.rethrowAsIOException(var1);
         }
      }

      if (var5.isDirectory()) {
         copyDirectory(var0, var5, var1, var4);
      } else if (var5.isSymbolicLink()) {
         copyLink(var0, var5, var1, var4);
      } else if (!var4.interruptible) {
         copyFile(var0, var5, var1, var4, 0L);
      } else {
         Cancellable var9 = new Cancellable() {
            public void implRun() throws IOException {
               UnixCopyFile.copyFile(var0, var5, var1, var4, this.addressToPollForCancel());
            }
         };

         try {
            Cancellable.runInterruptibly(var9);
         } catch (ExecutionException var14) {
            Throwable var11 = var14.getCause();
            if (var11 instanceof IOException) {
               throw (IOException)var11;
            } else {
               throw new IOException(var11);
            }
         }
      }
   }

   static native void transfer(int var0, int var1, long var2) throws UnixException;

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("nio");
            return null;
         }
      });
   }

   private static class Flags {
      boolean replaceExisting;
      boolean atomicMove;
      boolean followLinks;
      boolean interruptible;
      boolean copyBasicAttributes;
      boolean copyPosixAttributes;
      boolean copyNonPosixAttributes;
      boolean failIfUnableToCopyBasic;
      boolean failIfUnableToCopyPosix;
      boolean failIfUnableToCopyNonPosix;

      static UnixCopyFile.Flags fromCopyOptions(CopyOption... var0) {
         UnixCopyFile.Flags var1 = new UnixCopyFile.Flags();
         var1.followLinks = true;
         CopyOption[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CopyOption var5 = var2[var4];
            if (var5 == StandardCopyOption.REPLACE_EXISTING) {
               var1.replaceExisting = true;
            } else if (var5 == LinkOption.NOFOLLOW_LINKS) {
               var1.followLinks = false;
            } else if (var5 == StandardCopyOption.COPY_ATTRIBUTES) {
               var1.copyBasicAttributes = true;
               var1.copyPosixAttributes = true;
               var1.copyNonPosixAttributes = true;
               var1.failIfUnableToCopyBasic = true;
            } else {
               if (var5 != ExtendedCopyOption.INTERRUPTIBLE) {
                  if (var5 == null) {
                     throw new NullPointerException();
                  }

                  throw new UnsupportedOperationException("Unsupported copy option");
               }

               var1.interruptible = true;
            }
         }

         return var1;
      }

      static UnixCopyFile.Flags fromMoveOptions(CopyOption... var0) {
         UnixCopyFile.Flags var1 = new UnixCopyFile.Flags();
         CopyOption[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CopyOption var5 = var2[var4];
            if (var5 == StandardCopyOption.ATOMIC_MOVE) {
               var1.atomicMove = true;
            } else if (var5 == StandardCopyOption.REPLACE_EXISTING) {
               var1.replaceExisting = true;
            } else if (var5 != LinkOption.NOFOLLOW_LINKS) {
               if (var5 == null) {
                  throw new NullPointerException();
               }

               throw new UnsupportedOperationException("Unsupported copy option");
            }
         }

         var1.copyBasicAttributes = true;
         var1.copyPosixAttributes = true;
         var1.copyNonPosixAttributes = true;
         var1.failIfUnableToCopyBasic = true;
         return var1;
      }
   }
}
