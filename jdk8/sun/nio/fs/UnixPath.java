package sun.nio.fs;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.file.FileSystemException;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.Objects;

class UnixPath extends AbstractPath {
   private static ThreadLocal<SoftReference<CharsetEncoder>> encoder = new ThreadLocal();
   private final UnixFileSystem fs;
   private final byte[] path;
   private volatile String stringValue;
   private int hash;
   private volatile int[] offsets;

   UnixPath(UnixFileSystem var1, byte[] var2) {
      this.fs = var1;
      this.path = var2;
   }

   UnixPath(UnixFileSystem var1, String var2) {
      this(var1, encode(var1, normalizeAndCheck(var2)));
   }

   static String normalizeAndCheck(String var0) {
      int var1 = var0.length();
      char var2 = 0;

      for(int var3 = 0; var3 < var1; ++var3) {
         char var4 = var0.charAt(var3);
         if (var4 == '/' && var2 == '/') {
            return normalize(var0, var1, var3 - 1);
         }

         checkNotNul(var0, var4);
         var2 = var4;
      }

      if (var2 == '/') {
         return normalize(var0, var1, var1 - 1);
      } else {
         return var0;
      }
   }

   private static void checkNotNul(String var0, char var1) {
      if (var1 == 0) {
         throw new InvalidPathException(var0, "Nul character not allowed");
      }
   }

   private static String normalize(String var0, int var1, int var2) {
      if (var1 == 0) {
         return var0;
      } else {
         int var3;
         for(var3 = var1; var3 > 0 && var0.charAt(var3 - 1) == '/'; --var3) {
         }

         if (var3 == 0) {
            return "/";
         } else {
            StringBuilder var4 = new StringBuilder(var0.length());
            if (var2 > 0) {
               var4.append(var0.substring(0, var2));
            }

            char var5 = 0;

            for(int var6 = var2; var6 < var3; ++var6) {
               char var7 = var0.charAt(var6);
               if (var7 != '/' || var5 != '/') {
                  checkNotNul(var0, var7);
                  var4.append(var7);
                  var5 = var7;
               }
            }

            return var4.toString();
         }
      }
   }

   private static byte[] encode(UnixFileSystem var0, String var1) {
      SoftReference var2 = (SoftReference)encoder.get();
      CharsetEncoder var3 = var2 != null ? (CharsetEncoder)var2.get() : null;
      if (var3 == null) {
         var3 = Util.jnuEncoding().newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
         encoder.set(new SoftReference(var3));
      }

      char[] var4 = var0.normalizeNativePath(var1.toCharArray());
      byte[] var5 = new byte[(int)((double)var4.length * (double)var3.maxBytesPerChar())];
      ByteBuffer var6 = ByteBuffer.wrap(var5);
      CharBuffer var7 = CharBuffer.wrap(var4);
      var3.reset();
      CoderResult var8 = var3.encode(var7, var6, true);
      boolean var9;
      if (!var8.isUnderflow()) {
         var9 = true;
      } else {
         var8 = var3.flush(var6);
         var9 = !var8.isUnderflow();
      }

      if (var9) {
         throw new InvalidPathException(var1, "Malformed input or input contains unmappable characters");
      } else {
         int var10 = var6.position();
         if (var10 != var5.length) {
            var5 = Arrays.copyOf(var5, var10);
         }

         return var5;
      }
   }

   byte[] asByteArray() {
      return this.path;
   }

   byte[] getByteArrayForSysCalls() {
      if (this.getFileSystem().needToResolveAgainstDefaultDirectory()) {
         return resolve(this.getFileSystem().defaultDirectory(), this.path);
      } else if (!this.isEmpty()) {
         return this.path;
      } else {
         byte[] var1 = new byte[]{46};
         return var1;
      }
   }

   String getPathForExceptionMessage() {
      return this.toString();
   }

   String getPathForPermissionCheck() {
      return this.getFileSystem().needToResolveAgainstDefaultDirectory() ? Util.toString(this.getByteArrayForSysCalls()) : this.toString();
   }

   static UnixPath toUnixPath(Path var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else if (!(var0 instanceof UnixPath)) {
         throw new ProviderMismatchException();
      } else {
         return (UnixPath)var0;
      }
   }

   private void initOffsets() {
      if (this.offsets == null) {
         int var1 = 0;
         int var2 = 0;
         if (this.isEmpty()) {
            var1 = 1;
         } else {
            label69:
            while(true) {
               byte var3;
               do {
                  if (var2 >= this.path.length) {
                     break label69;
                  }

                  var3 = this.path[var2++];
               } while(var3 == 47);

               ++var1;

               while(var2 < this.path.length && this.path[var2] != 47) {
                  ++var2;
               }
            }
         }

         int[] var7 = new int[var1];
         var1 = 0;
         var2 = 0;

         while(true) {
            while(var2 < this.path.length) {
               byte var4 = this.path[var2];
               if (var4 == 47) {
                  ++var2;
               } else {
                  for(var7[var1++] = var2++; var2 < this.path.length && this.path[var2] != 47; ++var2) {
                  }
               }
            }

            synchronized(this) {
               if (this.offsets == null) {
                  this.offsets = var7;
               }
               break;
            }
         }
      }

   }

   private boolean isEmpty() {
      return this.path.length == 0;
   }

   private UnixPath emptyPath() {
      return new UnixPath(this.getFileSystem(), new byte[0]);
   }

   public UnixFileSystem getFileSystem() {
      return this.fs;
   }

   public UnixPath getRoot() {
      return this.path.length > 0 && this.path[0] == 47 ? this.getFileSystem().rootDirectory() : null;
   }

   public UnixPath getFileName() {
      this.initOffsets();
      int var1 = this.offsets.length;
      if (var1 == 0) {
         return null;
      } else if (var1 == 1 && this.path.length > 0 && this.path[0] != 47) {
         return this;
      } else {
         int var2 = this.offsets[var1 - 1];
         int var3 = this.path.length - var2;
         byte[] var4 = new byte[var3];
         System.arraycopy(this.path, var2, var4, 0, var3);
         return new UnixPath(this.getFileSystem(), var4);
      }
   }

   public UnixPath getParent() {
      this.initOffsets();
      int var1 = this.offsets.length;
      if (var1 == 0) {
         return null;
      } else {
         int var2 = this.offsets[var1 - 1] - 1;
         if (var2 <= 0) {
            return this.getRoot();
         } else {
            byte[] var3 = new byte[var2];
            System.arraycopy(this.path, 0, var3, 0, var2);
            return new UnixPath(this.getFileSystem(), var3);
         }
      }
   }

   public int getNameCount() {
      this.initOffsets();
      return this.offsets.length;
   }

   public UnixPath getName(int var1) {
      this.initOffsets();
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else if (var1 >= this.offsets.length) {
         throw new IllegalArgumentException();
      } else {
         int var2 = this.offsets[var1];
         int var3;
         if (var1 == this.offsets.length - 1) {
            var3 = this.path.length - var2;
         } else {
            var3 = this.offsets[var1 + 1] - var2 - 1;
         }

         byte[] var4 = new byte[var3];
         System.arraycopy(this.path, var2, var4, 0, var3);
         return new UnixPath(this.getFileSystem(), var4);
      }
   }

   public UnixPath subpath(int var1, int var2) {
      this.initOffsets();
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else if (var1 >= this.offsets.length) {
         throw new IllegalArgumentException();
      } else if (var2 > this.offsets.length) {
         throw new IllegalArgumentException();
      } else if (var1 >= var2) {
         throw new IllegalArgumentException();
      } else {
         int var3 = this.offsets[var1];
         int var4;
         if (var2 == this.offsets.length) {
            var4 = this.path.length - var3;
         } else {
            var4 = this.offsets[var2] - var3 - 1;
         }

         byte[] var5 = new byte[var4];
         System.arraycopy(this.path, var3, var5, 0, var4);
         return new UnixPath(this.getFileSystem(), var5);
      }
   }

   public boolean isAbsolute() {
      return this.path.length > 0 && this.path[0] == 47;
   }

   private static byte[] resolve(byte[] var0, byte[] var1) {
      int var2 = var0.length;
      int var3 = var1.length;
      if (var3 == 0) {
         return var0;
      } else if (var2 != 0 && var1[0] != 47) {
         byte[] var4;
         if (var2 == 1 && var0[0] == 47) {
            var4 = new byte[var3 + 1];
            var4[0] = 47;
            System.arraycopy(var1, 0, var4, 1, var3);
         } else {
            var4 = new byte[var2 + 1 + var3];
            System.arraycopy(var0, 0, var4, 0, var2);
            var4[var0.length] = 47;
            System.arraycopy(var1, 0, var4, var2 + 1, var3);
         }

         return var4;
      } else {
         return var1;
      }
   }

   public UnixPath resolve(Path var1) {
      byte[] var2 = toUnixPath(var1).path;
      if (var2.length > 0 && var2[0] == 47) {
         return (UnixPath)var1;
      } else {
         byte[] var3 = resolve(this.path, var2);
         return new UnixPath(this.getFileSystem(), var3);
      }
   }

   UnixPath resolve(byte[] var1) {
      return this.resolve((Path)(new UnixPath(this.getFileSystem(), var1)));
   }

   public UnixPath relativize(Path var1) {
      UnixPath var2 = toUnixPath(var1);
      if (var2.equals(this)) {
         return this.emptyPath();
      } else if (this.isAbsolute() != var2.isAbsolute()) {
         throw new IllegalArgumentException("'other' is different type of Path");
      } else if (this.isEmpty()) {
         return var2;
      } else {
         int var3 = this.getNameCount();
         int var4 = var2.getNameCount();
         int var5 = var3 > var4 ? var4 : var3;

         int var6;
         for(var6 = 0; var6 < var5 && this.getName(var6).equals(var2.getName(var6)); ++var6) {
         }

         int var7 = var3 - var6;
         if (var6 < var4) {
            UnixPath var13 = var2.subpath(var6, var4);
            if (var7 == 0) {
               return var13;
            } else {
               boolean var14 = var2.isEmpty();
               int var10 = var7 * 3 + var13.path.length;
               if (var14) {
                  assert var13.isEmpty();

                  --var10;
               }

               byte[] var11 = new byte[var10];

               int var12;
               for(var12 = 0; var7 > 0; --var7) {
                  var11[var12++] = 46;
                  var11[var12++] = 46;
                  if (var14) {
                     if (var7 > 1) {
                        var11[var12++] = 47;
                     }
                  } else {
                     var11[var12++] = 47;
                  }
               }

               System.arraycopy(var13.path, 0, var11, var12, var13.path.length);
               return new UnixPath(this.getFileSystem(), var11);
            }
         } else {
            byte[] var8 = new byte[var7 * 3 - 1];

            for(int var9 = 0; var7 > 0; --var7) {
               var8[var9++] = 46;
               var8[var9++] = 46;
               if (var7 > 1) {
                  var8[var9++] = 47;
               }
            }

            return new UnixPath(this.getFileSystem(), var8);
         }
      }
   }

   public Path normalize() {
      int var1 = this.getNameCount();
      if (var1 != 0 && !this.isEmpty()) {
         boolean[] var2 = new boolean[var1];
         int[] var3 = new int[var1];
         int var4 = var1;
         boolean var5 = false;
         boolean var6 = this.isAbsolute();

         int var7;
         int var8;
         int var9;
         for(var7 = 0; var7 < var1; ++var7) {
            var8 = this.offsets[var7];
            if (var7 == this.offsets.length - 1) {
               var9 = this.path.length - var8;
            } else {
               var9 = this.offsets[var7 + 1] - var8 - 1;
            }

            var3[var7] = var9;
            if (this.path[var8] == 46) {
               if (var9 == 1) {
                  var2[var7] = true;
                  --var4;
               } else if (this.path[var8 + 1] == 46) {
                  var5 = true;
               }
            }
         }

         int var10;
         if (var5) {
            do {
               var8 = -1;

               for(var9 = 0; var9 < var1; ++var9) {
                  if (!var2[var9]) {
                     if (var3[var9] != 2) {
                        var8 = var9;
                     } else {
                        var10 = this.offsets[var9];
                        if (this.path[var10] == 46 && this.path[var10 + 1] == 46) {
                           if (var8 >= 0) {
                              var2[var8] = true;
                              var2[var9] = true;
                              var4 -= 2;
                              var8 = -1;
                           } else if (var6) {
                              boolean var11 = false;

                              for(int var12 = 0; var12 < var9; ++var12) {
                                 if (!var2[var12]) {
                                    var11 = true;
                                    break;
                                 }
                              }

                              if (!var11) {
                                 var2[var9] = true;
                                 --var4;
                              }
                           }
                        } else {
                           var8 = var9;
                        }
                     }
                  }
               }
            } while(var4 > var4);
         }

         if (var4 == var1) {
            return this;
         } else if (var4 == 0) {
            return var6 ? this.getFileSystem().rootDirectory() : this.emptyPath();
         } else {
            var7 = var4 - 1;
            if (var6) {
               ++var7;
            }

            for(var8 = 0; var8 < var1; ++var8) {
               if (!var2[var8]) {
                  var7 += var3[var8];
               }
            }

            byte[] var13 = new byte[var7];
            var9 = 0;
            if (var6) {
               var13[var9++] = 47;
            }

            for(var10 = 0; var10 < var1; ++var10) {
               if (!var2[var10]) {
                  System.arraycopy(this.path, this.offsets[var10], var13, var9, var3[var10]);
                  var9 += var3[var10];
                  --var4;
                  if (var4 > 0) {
                     var13[var9++] = 47;
                  }
               }
            }

            return new UnixPath(this.getFileSystem(), var13);
         }
      } else {
         return this;
      }
   }

   public boolean startsWith(Path var1) {
      if (!(Objects.requireNonNull(var1) instanceof UnixPath)) {
         return false;
      } else {
         UnixPath var2 = (UnixPath)var1;
         if (var2.path.length > this.path.length) {
            return false;
         } else {
            int var3 = this.getNameCount();
            int var4 = var2.getNameCount();
            if (var4 == 0 && this.isAbsolute()) {
               return !var2.isEmpty();
            } else if (var4 > var3) {
               return false;
            } else if (var4 == var3 && this.path.length != var2.path.length) {
               return false;
            } else {
               int var5;
               for(var5 = 0; var5 < var4; ++var5) {
                  Integer var6 = this.offsets[var5];
                  Integer var7 = var2.offsets[var5];
                  if (!var6.equals(var7)) {
                     return false;
                  }
               }

               for(var5 = 0; var5 < var2.path.length; ++var5) {
                  if (this.path[var5] != var2.path[var5]) {
                     return false;
                  }
               }

               if (var5 < this.path.length && this.path[var5] != 47) {
                  return false;
               } else {
                  return true;
               }
            }
         }
      }
   }

   public boolean endsWith(Path var1) {
      if (!(Objects.requireNonNull(var1) instanceof UnixPath)) {
         return false;
      } else {
         UnixPath var2 = (UnixPath)var1;
         int var3 = this.path.length;
         int var4 = var2.path.length;
         if (var4 > var3) {
            return false;
         } else if (var3 > 0 && var4 == 0) {
            return false;
         } else if (var2.isAbsolute() && !this.isAbsolute()) {
            return false;
         } else {
            int var5 = this.getNameCount();
            int var6 = var2.getNameCount();
            if (var6 > var5) {
               return false;
            } else {
               int var7;
               if (var6 == var5) {
                  if (var5 == 0) {
                     return true;
                  }

                  var7 = var3;
                  if (this.isAbsolute() && !var2.isAbsolute()) {
                     var7 = var3 - 1;
                  }

                  if (var4 != var7) {
                     return false;
                  }
               } else if (var2.isAbsolute()) {
                  return false;
               }

               var7 = this.offsets[var5 - var6];
               int var8 = var2.offsets[0];
               if (var4 - var8 != var3 - var7) {
                  return false;
               } else {
                  do {
                     if (var8 >= var4) {
                        return true;
                     }
                  } while(this.path[var7++] == var2.path[var8++]);

                  return false;
               }
            }
         }
      }
   }

   public int compareTo(Path var1) {
      int var2 = this.path.length;
      int var3 = ((UnixPath)var1).path.length;
      int var4 = Math.min(var2, var3);
      byte[] var5 = this.path;
      byte[] var6 = ((UnixPath)var1).path;

      for(int var7 = 0; var7 < var4; ++var7) {
         int var8 = var5[var7] & 255;
         int var9 = var6[var7] & 255;
         if (var8 != var9) {
            return var8 - var9;
         }
      }

      return var2 - var3;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof UnixPath) {
         return this.compareTo((Path)var1) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.hash;
      if (var1 == 0) {
         for(int var2 = 0; var2 < this.path.length; ++var2) {
            var1 = 31 * var1 + (this.path[var2] & 255);
         }

         this.hash = var1;
      }

      return var1;
   }

   public String toString() {
      if (this.stringValue == null) {
         this.stringValue = this.fs.normalizeJavaPath(Util.toString(this.path));
      }

      return this.stringValue;
   }

   int openForAttributeAccess(boolean var1) throws IOException {
      int var2 = 0;
      if (!var1) {
         var2 |= 256;
      }

      try {
         return UnixNativeDispatcher.open(this, var2, 0);
      } catch (UnixException var4) {
         if (this.getFileSystem().isSolaris() && var4.errno() == 22) {
            var4.setError(62);
         }

         if (var4.errno() == 62) {
            throw new FileSystemException(this.getPathForExceptionMessage(), (String)null, var4.getMessage() + " or unable to access attributes of symbolic link");
         } else {
            var4.rethrowAsIOException(this);
            return -1;
         }
      }
   }

   void checkRead() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkRead(this.getPathForPermissionCheck());
      }

   }

   void checkWrite() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkWrite(this.getPathForPermissionCheck());
      }

   }

   void checkDelete() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkDelete(this.getPathForPermissionCheck());
      }

   }

   public UnixPath toAbsolutePath() {
      if (this.isAbsolute()) {
         return this;
      } else {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            var1.checkPropertyAccess("user.dir");
         }

         return new UnixPath(this.getFileSystem(), resolve(this.getFileSystem().defaultDirectory(), this.path));
      }
   }

   public Path toRealPath(LinkOption... var1) throws IOException {
      this.checkRead();
      UnixPath var2 = this.toAbsolutePath();
      if (Util.followLinks(var1)) {
         try {
            byte[] var11 = UnixNativeDispatcher.realpath(var2);
            return new UnixPath(this.getFileSystem(), var11);
         } catch (UnixException var10) {
            var10.rethrowAsIOException(this);
         }
      }

      UnixPath var3 = this.fs.rootDirectory();

      for(int var4 = 0; var4 < var2.getNameCount(); ++var4) {
         UnixPath var5 = var2.getName(var4);
         if (var5.asByteArray().length != 1 || var5.asByteArray()[0] != 46) {
            if (var5.asByteArray().length == 2 && var5.asByteArray()[0] == 46 && var5.asByteArray()[1] == 46) {
               UnixFileAttributes var6 = null;

               try {
                  var6 = UnixFileAttributes.get(var3, false);
               } catch (UnixException var9) {
                  var9.rethrowAsIOException(var3);
               }

               if (!var6.isSymbolicLink()) {
                  var3 = var3.getParent();
                  if (var3 == null) {
                     var3 = this.fs.rootDirectory();
                  }
                  continue;
               }
            }

            var3 = var3.resolve((Path)var5);
         }
      }

      try {
         UnixFileAttributes.get(var3, false);
      } catch (UnixException var8) {
         var8.rethrowAsIOException(var3);
      }

      return var3;
   }

   public URI toUri() {
      return UnixUriUtils.toUri(this);
   }

   public WatchKey register(WatchService var1, WatchEvent.Kind<?>[] var2, WatchEvent.Modifier... var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!(var1 instanceof AbstractWatchService)) {
         throw new ProviderMismatchException();
      } else {
         this.checkRead();
         return ((AbstractWatchService)var1).register(this, var2, var3);
      }
   }
}
