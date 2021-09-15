package sun.nio.fs;

class UnixConstants {
   static final int O_RDONLY = 0;
   static final int O_WRONLY = 1;
   static final int O_RDWR = 2;
   static final int O_APPEND = 8;
   static final int O_CREAT = 512;
   static final int O_EXCL = 2048;
   static final int O_TRUNC = 1024;
   static final int O_SYNC = 128;
   static final int O_DSYNC = 4194304;
   static final int O_NOFOLLOW = 256;
   static final int S_IAMB = 511;
   static final int S_IRUSR = 256;
   static final int S_IWUSR = 128;
   static final int S_IXUSR = 64;
   static final int S_IRGRP = 32;
   static final int S_IWGRP = 16;
   static final int S_IXGRP = 8;
   static final int S_IROTH = 4;
   static final int S_IWOTH = 2;
   static final int S_IXOTH = 1;
   static final int S_IFMT = 61440;
   static final int S_IFREG = 32768;
   static final int S_IFDIR = 16384;
   static final int S_IFLNK = 40960;
   static final int S_IFCHR = 8192;
   static final int S_IFBLK = 24576;
   static final int S_IFIFO = 4096;
   static final int R_OK = 4;
   static final int W_OK = 2;
   static final int X_OK = 1;
   static final int F_OK = 0;
   static final int ENOENT = 2;
   static final int EACCES = 13;
   static final int EEXIST = 17;
   static final int ENOTDIR = 20;
   static final int EINVAL = 22;
   static final int EXDEV = 18;
   static final int EISDIR = 21;
   static final int ENOTEMPTY = 66;
   static final int ENOSPC = 28;
   static final int EAGAIN = 35;
   static final int ENOSYS = 78;
   static final int ELOOP = 62;
   static final int EROFS = 30;
   static final int ENODATA = 96;
   static final int ERANGE = 34;
   static final int EMFILE = 24;
   static final int AT_SYMLINK_NOFOLLOW = 0;
   static final int AT_REMOVEDIR = 0;

   private UnixConstants() {
   }
}
