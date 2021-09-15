package sun.nio.fs;

import java.security.AccessController;
import java.security.PrivilegedAction;

class UnixNativeDispatcher {
   private static final int SUPPORTS_OPENAT = 2;
   private static final int SUPPORTS_FUTIMES = 4;
   private static final int SUPPORTS_BIRTHTIME = 65536;
   private static final int capabilities;

   protected UnixNativeDispatcher() {
   }

   private static NativeBuffer copyToNativeBuffer(UnixPath var0) {
      byte[] var1 = var0.getByteArrayForSysCalls();
      int var2 = var1.length + 1;
      NativeBuffer var3 = NativeBuffers.getNativeBufferFromCache(var2);
      if (var3 == null) {
         var3 = NativeBuffers.allocNativeBuffer(var2);
      } else if (var3.owner() == var0) {
         return var3;
      }

      NativeBuffers.copyCStringToNativeBuffer(var1, var3);
      var3.setOwner(var0);
      return var3;
   }

   static native byte[] getcwd();

   static native int dup(int var0) throws UnixException;

   static int open(UnixPath var0, int var1, int var2) throws UnixException {
      NativeBuffer var3 = copyToNativeBuffer(var0);

      int var4;
      try {
         var4 = open0(var3.address(), var1, var2);
      } finally {
         var3.release();
      }

      return var4;
   }

   private static native int open0(long var0, int var2, int var3) throws UnixException;

   static int openat(int var0, byte[] var1, int var2, int var3) throws UnixException {
      NativeBuffer var4 = NativeBuffers.asNativeBuffer(var1);

      int var5;
      try {
         var5 = openat0(var0, var4.address(), var2, var3);
      } finally {
         var4.release();
      }

      return var5;
   }

   private static native int openat0(int var0, long var1, int var3, int var4) throws UnixException;

   static native void close(int var0);

   static long fopen(UnixPath var0, String var1) throws UnixException {
      NativeBuffer var2 = copyToNativeBuffer(var0);
      NativeBuffer var3 = NativeBuffers.asNativeBuffer(Util.toBytes(var1));

      long var4;
      try {
         var4 = fopen0(var2.address(), var3.address());
      } finally {
         var3.release();
         var2.release();
      }

      return var4;
   }

   private static native long fopen0(long var0, long var2) throws UnixException;

   static native void fclose(long var0) throws UnixException;

   static void link(UnixPath var0, UnixPath var1) throws UnixException {
      NativeBuffer var2 = copyToNativeBuffer(var0);
      NativeBuffer var3 = copyToNativeBuffer(var1);

      try {
         link0(var2.address(), var3.address());
      } finally {
         var3.release();
         var2.release();
      }

   }

   private static native void link0(long var0, long var2) throws UnixException;

   static void unlink(UnixPath var0) throws UnixException {
      NativeBuffer var1 = copyToNativeBuffer(var0);

      try {
         unlink0(var1.address());
      } finally {
         var1.release();
      }

   }

   private static native void unlink0(long var0) throws UnixException;

   static void unlinkat(int var0, byte[] var1, int var2) throws UnixException {
      NativeBuffer var3 = NativeBuffers.asNativeBuffer(var1);

      try {
         unlinkat0(var0, var3.address(), var2);
      } finally {
         var3.release();
      }

   }

   private static native void unlinkat0(int var0, long var1, int var3) throws UnixException;

   static void mknod(UnixPath var0, int var1, long var2) throws UnixException {
      NativeBuffer var4 = copyToNativeBuffer(var0);

      try {
         mknod0(var4.address(), var1, var2);
      } finally {
         var4.release();
      }

   }

   private static native void mknod0(long var0, int var2, long var3) throws UnixException;

   static void rename(UnixPath var0, UnixPath var1) throws UnixException {
      NativeBuffer var2 = copyToNativeBuffer(var0);
      NativeBuffer var3 = copyToNativeBuffer(var1);

      try {
         rename0(var2.address(), var3.address());
      } finally {
         var3.release();
         var2.release();
      }

   }

   private static native void rename0(long var0, long var2) throws UnixException;

   static void renameat(int var0, byte[] var1, int var2, byte[] var3) throws UnixException {
      NativeBuffer var4 = NativeBuffers.asNativeBuffer(var1);
      NativeBuffer var5 = NativeBuffers.asNativeBuffer(var3);

      try {
         renameat0(var0, var4.address(), var2, var5.address());
      } finally {
         var5.release();
         var4.release();
      }

   }

   private static native void renameat0(int var0, long var1, int var3, long var4) throws UnixException;

   static void mkdir(UnixPath var0, int var1) throws UnixException {
      NativeBuffer var2 = copyToNativeBuffer(var0);

      try {
         mkdir0(var2.address(), var1);
      } finally {
         var2.release();
      }

   }

   private static native void mkdir0(long var0, int var2) throws UnixException;

   static void rmdir(UnixPath var0) throws UnixException {
      NativeBuffer var1 = copyToNativeBuffer(var0);

      try {
         rmdir0(var1.address());
      } finally {
         var1.release();
      }

   }

   private static native void rmdir0(long var0) throws UnixException;

   static byte[] readlink(UnixPath var0) throws UnixException {
      NativeBuffer var1 = copyToNativeBuffer(var0);

      byte[] var2;
      try {
         var2 = readlink0(var1.address());
      } finally {
         var1.release();
      }

      return var2;
   }

   private static native byte[] readlink0(long var0) throws UnixException;

   static byte[] realpath(UnixPath var0) throws UnixException {
      NativeBuffer var1 = copyToNativeBuffer(var0);

      byte[] var2;
      try {
         var2 = realpath0(var1.address());
      } finally {
         var1.release();
      }

      return var2;
   }

   private static native byte[] realpath0(long var0) throws UnixException;

   static void symlink(byte[] var0, UnixPath var1) throws UnixException {
      NativeBuffer var2 = NativeBuffers.asNativeBuffer(var0);
      NativeBuffer var3 = copyToNativeBuffer(var1);

      try {
         symlink0(var2.address(), var3.address());
      } finally {
         var3.release();
         var2.release();
      }

   }

   private static native void symlink0(long var0, long var2) throws UnixException;

   static void stat(UnixPath var0, UnixFileAttributes var1) throws UnixException {
      NativeBuffer var2 = copyToNativeBuffer(var0);

      try {
         stat0(var2.address(), var1);
      } finally {
         var2.release();
      }

   }

   private static native void stat0(long var0, UnixFileAttributes var2) throws UnixException;

   static void lstat(UnixPath var0, UnixFileAttributes var1) throws UnixException {
      NativeBuffer var2 = copyToNativeBuffer(var0);

      try {
         lstat0(var2.address(), var1);
      } finally {
         var2.release();
      }

   }

   private static native void lstat0(long var0, UnixFileAttributes var2) throws UnixException;

   static native void fstat(int var0, UnixFileAttributes var1) throws UnixException;

   static void fstatat(int var0, byte[] var1, int var2, UnixFileAttributes var3) throws UnixException {
      NativeBuffer var4 = NativeBuffers.asNativeBuffer(var1);

      try {
         fstatat0(var0, var4.address(), var2, var3);
      } finally {
         var4.release();
      }

   }

   private static native void fstatat0(int var0, long var1, int var3, UnixFileAttributes var4) throws UnixException;

   static void chown(UnixPath var0, int var1, int var2) throws UnixException {
      NativeBuffer var3 = copyToNativeBuffer(var0);

      try {
         chown0(var3.address(), var1, var2);
      } finally {
         var3.release();
      }

   }

   private static native void chown0(long var0, int var2, int var3) throws UnixException;

   static void lchown(UnixPath var0, int var1, int var2) throws UnixException {
      NativeBuffer var3 = copyToNativeBuffer(var0);

      try {
         lchown0(var3.address(), var1, var2);
      } finally {
         var3.release();
      }

   }

   private static native void lchown0(long var0, int var2, int var3) throws UnixException;

   static native void fchown(int var0, int var1, int var2) throws UnixException;

   static void chmod(UnixPath var0, int var1) throws UnixException {
      NativeBuffer var2 = copyToNativeBuffer(var0);

      try {
         chmod0(var2.address(), var1);
      } finally {
         var2.release();
      }

   }

   private static native void chmod0(long var0, int var2) throws UnixException;

   static native void fchmod(int var0, int var1) throws UnixException;

   static void utimes(UnixPath var0, long var1, long var3) throws UnixException {
      NativeBuffer var5 = copyToNativeBuffer(var0);

      try {
         utimes0(var5.address(), var1, var3);
      } finally {
         var5.release();
      }

   }

   private static native void utimes0(long var0, long var2, long var4) throws UnixException;

   static native void futimes(int var0, long var1, long var3) throws UnixException;

   static long opendir(UnixPath var0) throws UnixException {
      NativeBuffer var1 = copyToNativeBuffer(var0);

      long var2;
      try {
         var2 = opendir0(var1.address());
      } finally {
         var1.release();
      }

      return var2;
   }

   private static native long opendir0(long var0) throws UnixException;

   static native long fdopendir(int var0) throws UnixException;

   static native void closedir(long var0) throws UnixException;

   static native byte[] readdir(long var0) throws UnixException;

   static native int read(int var0, long var1, int var3) throws UnixException;

   static native int write(int var0, long var1, int var3) throws UnixException;

   static void access(UnixPath var0, int var1) throws UnixException {
      NativeBuffer var2 = copyToNativeBuffer(var0);

      try {
         access0(var2.address(), var1);
      } finally {
         var2.release();
      }

   }

   private static native void access0(long var0, int var2) throws UnixException;

   static native byte[] getpwuid(int var0) throws UnixException;

   static native byte[] getgrgid(int var0) throws UnixException;

   static int getpwnam(String var0) throws UnixException {
      NativeBuffer var1 = NativeBuffers.asNativeBuffer(Util.toBytes(var0));

      int var2;
      try {
         var2 = getpwnam0(var1.address());
      } finally {
         var1.release();
      }

      return var2;
   }

   private static native int getpwnam0(long var0) throws UnixException;

   static int getgrnam(String var0) throws UnixException {
      NativeBuffer var1 = NativeBuffers.asNativeBuffer(Util.toBytes(var0));

      int var2;
      try {
         var2 = getgrnam0(var1.address());
      } finally {
         var1.release();
      }

      return var2;
   }

   private static native int getgrnam0(long var0) throws UnixException;

   static void statvfs(UnixPath var0, UnixFileStoreAttributes var1) throws UnixException {
      NativeBuffer var2 = copyToNativeBuffer(var0);

      try {
         statvfs0(var2.address(), var1);
      } finally {
         var2.release();
      }

   }

   private static native void statvfs0(long var0, UnixFileStoreAttributes var2) throws UnixException;

   static long pathconf(UnixPath var0, int var1) throws UnixException {
      NativeBuffer var2 = copyToNativeBuffer(var0);

      long var3;
      try {
         var3 = pathconf0(var2.address(), var1);
      } finally {
         var2.release();
      }

      return var3;
   }

   private static native long pathconf0(long var0, int var2) throws UnixException;

   static native long fpathconf(int var0, int var1) throws UnixException;

   static native byte[] strerror(int var0);

   static boolean openatSupported() {
      return (capabilities & 2) != 0;
   }

   static boolean futimesSupported() {
      return (capabilities & 4) != 0;
   }

   static boolean birthtimeSupported() {
      return (capabilities & 65536) != 0;
   }

   private static native int init();

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("nio");
            return null;
         }
      });
      capabilities = init();
   }
}
