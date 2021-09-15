package sun.nio.fs;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

class UnixFileAttributes implements PosixFileAttributes {
   private int st_mode;
   private long st_ino;
   private long st_dev;
   private long st_rdev;
   private int st_nlink;
   private int st_uid;
   private int st_gid;
   private long st_size;
   private long st_atime_sec;
   private long st_atime_nsec;
   private long st_mtime_sec;
   private long st_mtime_nsec;
   private long st_ctime_sec;
   private long st_ctime_nsec;
   private long st_birthtime_sec;
   private volatile UserPrincipal owner;
   private volatile GroupPrincipal group;
   private volatile UnixFileKey key;

   private UnixFileAttributes() {
   }

   static UnixFileAttributes get(UnixPath var0, boolean var1) throws UnixException {
      UnixFileAttributes var2 = new UnixFileAttributes();
      if (var1) {
         UnixNativeDispatcher.stat(var0, var2);
      } else {
         UnixNativeDispatcher.lstat(var0, var2);
      }

      return var2;
   }

   static UnixFileAttributes get(int var0) throws UnixException {
      UnixFileAttributes var1 = new UnixFileAttributes();
      UnixNativeDispatcher.fstat(var0, var1);
      return var1;
   }

   static UnixFileAttributes get(int var0, UnixPath var1, boolean var2) throws UnixException {
      UnixFileAttributes var3 = new UnixFileAttributes();
      int var4 = var2 ? 0 : 0;
      UnixNativeDispatcher.fstatat(var0, var1.asByteArray(), var4, var3);
      return var3;
   }

   boolean isSameFile(UnixFileAttributes var1) {
      return this.st_ino == var1.st_ino && this.st_dev == var1.st_dev;
   }

   int mode() {
      return this.st_mode;
   }

   long ino() {
      return this.st_ino;
   }

   long dev() {
      return this.st_dev;
   }

   long rdev() {
      return this.st_rdev;
   }

   int nlink() {
      return this.st_nlink;
   }

   int uid() {
      return this.st_uid;
   }

   int gid() {
      return this.st_gid;
   }

   private static FileTime toFileTime(long var0, long var2) {
      if (var2 == 0L) {
         return FileTime.from(var0, TimeUnit.SECONDS);
      } else {
         long var4 = var0 * 1000000L + var2 / 1000L;
         return FileTime.from(var4, TimeUnit.MICROSECONDS);
      }
   }

   FileTime ctime() {
      return toFileTime(this.st_ctime_sec, this.st_ctime_nsec);
   }

   boolean isDevice() {
      int var1 = this.st_mode & '\uf000';
      return var1 == 8192 || var1 == 24576 || var1 == 4096;
   }

   public FileTime lastModifiedTime() {
      return toFileTime(this.st_mtime_sec, this.st_mtime_nsec);
   }

   public FileTime lastAccessTime() {
      return toFileTime(this.st_atime_sec, this.st_atime_nsec);
   }

   public FileTime creationTime() {
      return UnixNativeDispatcher.birthtimeSupported() ? FileTime.from(this.st_birthtime_sec, TimeUnit.SECONDS) : this.lastModifiedTime();
   }

   public boolean isRegularFile() {
      return (this.st_mode & '\uf000') == 32768;
   }

   public boolean isDirectory() {
      return (this.st_mode & '\uf000') == 16384;
   }

   public boolean isSymbolicLink() {
      return (this.st_mode & '\uf000') == 40960;
   }

   public boolean isOther() {
      int var1 = this.st_mode & '\uf000';
      return var1 != 32768 && var1 != 16384 && var1 != 40960;
   }

   public long size() {
      return this.st_size;
   }

   public UnixFileKey fileKey() {
      if (this.key == null) {
         synchronized(this) {
            if (this.key == null) {
               this.key = new UnixFileKey(this.st_dev, this.st_ino);
            }
         }
      }

      return this.key;
   }

   public UserPrincipal owner() {
      if (this.owner == null) {
         synchronized(this) {
            if (this.owner == null) {
               this.owner = UnixUserPrincipals.fromUid(this.st_uid);
            }
         }
      }

      return this.owner;
   }

   public GroupPrincipal group() {
      if (this.group == null) {
         synchronized(this) {
            if (this.group == null) {
               this.group = UnixUserPrincipals.fromGid(this.st_gid);
            }
         }
      }

      return this.group;
   }

   public Set<PosixFilePermission> permissions() {
      int var1 = this.st_mode & 511;
      HashSet var2 = new HashSet();
      if ((var1 & 256) > 0) {
         var2.add(PosixFilePermission.OWNER_READ);
      }

      if ((var1 & 128) > 0) {
         var2.add(PosixFilePermission.OWNER_WRITE);
      }

      if ((var1 & 64) > 0) {
         var2.add(PosixFilePermission.OWNER_EXECUTE);
      }

      if ((var1 & 32) > 0) {
         var2.add(PosixFilePermission.GROUP_READ);
      }

      if ((var1 & 16) > 0) {
         var2.add(PosixFilePermission.GROUP_WRITE);
      }

      if ((var1 & 8) > 0) {
         var2.add(PosixFilePermission.GROUP_EXECUTE);
      }

      if ((var1 & 4) > 0) {
         var2.add(PosixFilePermission.OTHERS_READ);
      }

      if ((var1 & 2) > 0) {
         var2.add(PosixFilePermission.OTHERS_WRITE);
      }

      if ((var1 & 1) > 0) {
         var2.add(PosixFilePermission.OTHERS_EXECUTE);
      }

      return var2;
   }

   BasicFileAttributes asBasicFileAttributes() {
      return UnixFileAttributes.UnixAsBasicFileAttributes.wrap(this);
   }

   static UnixFileAttributes toUnixFileAttributes(BasicFileAttributes var0) {
      if (var0 instanceof UnixFileAttributes) {
         return (UnixFileAttributes)var0;
      } else {
         return var0 instanceof UnixFileAttributes.UnixAsBasicFileAttributes ? ((UnixFileAttributes.UnixAsBasicFileAttributes)var0).unwrap() : null;
      }
   }

   private static class UnixAsBasicFileAttributes implements BasicFileAttributes {
      private final UnixFileAttributes attrs;

      private UnixAsBasicFileAttributes(UnixFileAttributes var1) {
         this.attrs = var1;
      }

      static UnixFileAttributes.UnixAsBasicFileAttributes wrap(UnixFileAttributes var0) {
         return new UnixFileAttributes.UnixAsBasicFileAttributes(var0);
      }

      UnixFileAttributes unwrap() {
         return this.attrs;
      }

      public FileTime lastModifiedTime() {
         return this.attrs.lastModifiedTime();
      }

      public FileTime lastAccessTime() {
         return this.attrs.lastAccessTime();
      }

      public FileTime creationTime() {
         return this.attrs.creationTime();
      }

      public boolean isRegularFile() {
         return this.attrs.isRegularFile();
      }

      public boolean isDirectory() {
         return this.attrs.isDirectory();
      }

      public boolean isSymbolicLink() {
         return this.attrs.isSymbolicLink();
      }

      public boolean isOther() {
         return this.attrs.isOther();
      }

      public long size() {
         return this.attrs.size();
      }

      public Object fileKey() {
         return this.attrs.fileKey();
      }
   }
}
