package sun.security.krb5.internal.rcache;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.ReplayCache;

public class DflCache extends ReplayCache {
   private static final int KRB5_RV_VNO = 1281;
   private static final int EXCESSREPS = 30;
   private final String source;
   private static int uid;

   public DflCache(String var1) {
      this.source = var1;
   }

   private static String defaultPath() {
      return (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.io.tmpdir")));
   }

   private static String defaultFile(String var0) {
      int var1 = var0.indexOf(47);
      if (var1 == -1) {
         var1 = var0.indexOf(64);
      }

      if (var1 != -1) {
         var0 = var0.substring(0, var1);
      }

      if (uid != -1) {
         var0 = var0 + "_" + uid;
      }

      return var0;
   }

   private static Path getFileName(String var0, String var1) {
      String var2;
      String var3;
      if (var0.equals("dfl")) {
         var2 = defaultPath();
         var3 = defaultFile(var1);
      } else {
         if (!var0.startsWith("dfl:")) {
            throw new IllegalArgumentException();
         }

         var0 = var0.substring(4);
         int var4 = var0.lastIndexOf(47);
         int var5 = var0.lastIndexOf(92);
         if (var5 > var4) {
            var4 = var5;
         }

         if (var4 == -1) {
            var2 = defaultPath();
            var3 = var0;
         } else if ((new File(var0)).isDirectory()) {
            var2 = var0;
            var3 = defaultFile(var1);
         } else {
            var2 = null;
            var3 = var0;
         }
      }

      return (new File(var2, var3)).toPath();
   }

   public void checkAndStore(KerberosTime var1, AuthTimeWithHash var2) throws KrbApErrException {
      try {
         this.checkAndStore0(var1, var2);
      } catch (IOException var5) {
         KrbApErrException var4 = new KrbApErrException(60);
         var4.initCause(var5);
         throw var4;
      }
   }

   private synchronized void checkAndStore0(KerberosTime var1, AuthTimeWithHash var2) throws IOException, KrbApErrException {
      Path var3 = getFileName(this.source, var2.server);
      boolean var4 = false;
      DflCache.Storage var5 = new DflCache.Storage();
      Throwable var6 = null;

      int var19;
      try {
         try {
            var19 = var5.loadAndCheck(var3, var2, var1);
         } catch (IOException var16) {
            DflCache.Storage.create(var3);
            var19 = var5.loadAndCheck(var3, var2, var1);
         }

         var5.append(var2);
      } catch (Throwable var17) {
         var6 = var17;
         throw var17;
      } finally {
         if (var5 != null) {
            if (var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var15) {
                  var6.addSuppressed(var15);
               }
            } else {
               var5.close();
            }
         }

      }

      if (var19 > 30) {
         DflCache.Storage.expunge(var3, var1);
      }

   }

   static {
      try {
         Class var0 = Class.forName("com.sun.security.auth.module.UnixSystem");
         uid = (int)(Long)var0.getMethod("getUid").invoke(var0.newInstance());
      } catch (Exception var1) {
         uid = -1;
      }

   }

   private static class Storage implements Closeable {
      SeekableByteChannel chan;

      private Storage() {
      }

      private static void create(Path var0) throws IOException {
         SeekableByteChannel var1 = createNoClose(var0);
         Object var2 = null;
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var4) {
                  ((Throwable)var2).addSuppressed(var4);
               }
            } else {
               var1.close();
            }
         }

         makeMine(var0);
      }

      private static void makeMine(Path var0) throws IOException {
         try {
            HashSet var1 = new HashSet();
            var1.add(PosixFilePermission.OWNER_READ);
            var1.add(PosixFilePermission.OWNER_WRITE);
            Files.setPosixFilePermissions(var0, var1);
         } catch (UnsupportedOperationException var2) {
         }

      }

      private static SeekableByteChannel createNoClose(Path var0) throws IOException {
         SeekableByteChannel var1 = Files.newByteChannel(var0, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
         ByteBuffer var2 = ByteBuffer.allocate(6);
         var2.putShort((short)1281);
         var2.order(ByteOrder.nativeOrder());
         var2.putInt(KerberosTime.getDefaultSkew());
         var2.flip();
         var1.write(var2);
         return var1;
      }

      private static void expunge(Path var0, KerberosTime var1) throws IOException {
         Path var2 = Files.createTempFile(var0.getParent(), "rcache", (String)null);
         SeekableByteChannel var3 = Files.newByteChannel(var0);
         Throwable var4 = null;

         try {
            SeekableByteChannel var5 = createNoClose(var2);
            Throwable var6 = null;

            try {
               long var7 = (long)(var1.getSeconds() - readHeader(var3));

               while(true) {
                  try {
                     AuthTime var9 = AuthTime.readFrom(var3);
                     if ((long)var9.ctime > var7) {
                        ByteBuffer var10 = ByteBuffer.wrap(var9.encode(true));
                        var5.write(var10);
                     }
                  } catch (BufferUnderflowException var34) {
                     break;
                  }
               }
            } catch (Throwable var35) {
               var6 = var35;
               throw var35;
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var33) {
                        var6.addSuppressed(var33);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         } catch (Throwable var37) {
            var4 = var37;
            throw var37;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var32) {
                     var4.addSuppressed(var32);
                  }
               } else {
                  var3.close();
               }
            }

         }

         makeMine(var2);
         Files.move(var2, var0, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      }

      private int loadAndCheck(Path var1, AuthTimeWithHash var2, KerberosTime var3) throws IOException, KrbApErrException {
         int var4 = 0;
         if (Files.isSymbolicLink(var1)) {
            throw new IOException("Symlink not accepted");
         } else {
            try {
               Set var5 = Files.getPosixFilePermissions(var1);
               if (DflCache.uid != -1 && (Integer)Files.getAttribute(var1, "unix:uid") != DflCache.uid) {
                  throw new IOException("Not mine");
               }

               if (var5.contains(PosixFilePermission.GROUP_READ) || var5.contains(PosixFilePermission.GROUP_WRITE) || var5.contains(PosixFilePermission.GROUP_EXECUTE) || var5.contains(PosixFilePermission.OTHERS_READ) || var5.contains(PosixFilePermission.OTHERS_WRITE) || var5.contains(PosixFilePermission.OTHERS_EXECUTE)) {
                  throw new IOException("Accessible by someone else");
               }
            } catch (UnsupportedOperationException var12) {
            }

            this.chan = Files.newByteChannel(var1, StandardOpenOption.WRITE, StandardOpenOption.READ);
            long var13 = (long)(var3.getSeconds() - readHeader(this.chan));
            long var7 = 0L;
            boolean var9 = false;

            while(true) {
               try {
                  var7 = this.chan.position();
                  AuthTime var10 = AuthTime.readFrom(this.chan);
                  if (var10 instanceof AuthTimeWithHash) {
                     if (var2.equals(var10)) {
                        throw new KrbApErrException(34);
                     }

                     if (var2.isSameIgnoresHash(var10)) {
                        var9 = true;
                     }
                  } else if (var2.isSameIgnoresHash(var10) && !var9) {
                     throw new KrbApErrException(34);
                  }

                  if ((long)var10.ctime < var13) {
                     ++var4;
                  } else {
                     --var4;
                  }
               } catch (BufferUnderflowException var11) {
                  this.chan.position(var7);
                  return var4;
               }
            }
         }
      }

      private static int readHeader(SeekableByteChannel var0) throws IOException {
         ByteBuffer var1 = ByteBuffer.allocate(6);
         var0.read(var1);
         if (var1.getShort(0) != 1281) {
            throw new IOException("Not correct rcache version");
         } else {
            var1.order(ByteOrder.nativeOrder());
            return var1.getInt(2);
         }
      }

      private void append(AuthTimeWithHash var1) throws IOException {
         ByteBuffer var2 = ByteBuffer.wrap(var1.encode(true));
         this.chan.write(var2);
         var2 = ByteBuffer.wrap(var1.encode(false));
         this.chan.write(var2);
      }

      public void close() throws IOException {
         if (this.chan != null) {
            this.chan.close();
         }

         this.chan = null;
      }

      // $FF: synthetic method
      Storage(Object var1) {
         this();
      }
   }
}
