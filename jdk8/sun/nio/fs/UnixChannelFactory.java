package sun.nio.fs;

import java.io.FileDescriptor;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Set;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;
import sun.nio.ch.FileChannelImpl;
import sun.nio.ch.SimpleAsynchronousFileChannelImpl;
import sun.nio.ch.ThreadPool;

class UnixChannelFactory {
   private static final JavaIOFileDescriptorAccess fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();

   protected UnixChannelFactory() {
   }

   static FileChannel newFileChannel(int var0, String var1, boolean var2, boolean var3) {
      FileDescriptor var4 = new FileDescriptor();
      fdAccess.set(var4, var0);
      return FileChannelImpl.open(var4, var1, var2, var3, (Object)null);
   }

   static FileChannel newFileChannel(int var0, UnixPath var1, String var2, Set<? extends OpenOption> var3, int var4) throws UnixException {
      UnixChannelFactory.Flags var5 = UnixChannelFactory.Flags.toFlags(var3);
      if (!var5.read && !var5.write) {
         if (var5.append) {
            var5.write = true;
         } else {
            var5.read = true;
         }
      }

      if (var5.read && var5.append) {
         throw new IllegalArgumentException("READ + APPEND not allowed");
      } else if (var5.append && var5.truncateExisting) {
         throw new IllegalArgumentException("APPEND + TRUNCATE_EXISTING not allowed");
      } else {
         FileDescriptor var6 = open(var0, var1, var2, var5, var4);
         return FileChannelImpl.open(var6, var1.toString(), var5.read, var5.write, var5.append, (Object)null);
      }
   }

   static FileChannel newFileChannel(UnixPath var0, Set<? extends OpenOption> var1, int var2) throws UnixException {
      return newFileChannel(-1, var0, (String)null, var1, var2);
   }

   static AsynchronousFileChannel newAsynchronousFileChannel(UnixPath var0, Set<? extends OpenOption> var1, int var2, ThreadPool var3) throws UnixException {
      UnixChannelFactory.Flags var4 = UnixChannelFactory.Flags.toFlags(var1);
      if (!var4.read && !var4.write) {
         var4.read = true;
      }

      if (var4.append) {
         throw new UnsupportedOperationException("APPEND not allowed");
      } else {
         FileDescriptor var5 = open(-1, var0, (String)null, var4, var2);
         return SimpleAsynchronousFileChannelImpl.open(var5, var4.read, var4.write, var3);
      }
   }

   protected static FileDescriptor open(int var0, UnixPath var1, String var2, UnixChannelFactory.Flags var3, int var4) throws UnixException {
      int var5;
      if (var3.read && var3.write) {
         var5 = 2;
      } else {
         var5 = var3.write ? 1 : 0;
      }

      if (var3.write) {
         if (var3.truncateExisting) {
            var5 |= 1024;
         }

         if (var3.append) {
            var5 |= 8;
         }

         if (var3.createNew) {
            byte[] var6 = var1.asByteArray();
            if (var6[var6.length - 1] == 46 && (var6.length == 1 || var6[var6.length - 2] == 47)) {
               throw new UnixException(17);
            }

            var5 |= 2560;
         } else if (var3.create) {
            var5 |= 512;
         }
      }

      boolean var12 = true;
      if (!var3.createNew && (var3.noFollowLinks || var3.deleteOnClose)) {
         if (var3.deleteOnClose) {
         }

         var12 = false;
         var5 |= 256;
      }

      if (var3.dsync) {
         var5 |= 4194304;
      }

      if (var3.sync) {
         var5 |= 128;
      }

      SecurityManager var7 = System.getSecurityManager();
      if (var7 != null) {
         if (var2 == null) {
            var2 = var1.getPathForPermissionCheck();
         }

         if (var3.read) {
            var7.checkRead(var2);
         }

         if (var3.write) {
            var7.checkWrite(var2);
         }

         if (var3.deleteOnClose) {
            var7.checkDelete(var2);
         }
      }

      int var8;
      try {
         if (var0 >= 0) {
            var8 = UnixNativeDispatcher.openat(var0, var1.asByteArray(), var5, var4);
         } else {
            var8 = UnixNativeDispatcher.open(var1, var5, var4);
         }
      } catch (UnixException var11) {
         UnixException var9 = var11;
         if (var3.createNew && var11.errno() == 21) {
            var11.setError(17);
         }

         if (!var12 && var11.errno() == 62) {
            var9 = new UnixException(var11.getMessage() + " (NOFOLLOW_LINKS specified)");
         }

         throw var9;
      }

      if (var3.deleteOnClose) {
         try {
            if (var0 >= 0) {
               UnixNativeDispatcher.unlinkat(var0, var1.asByteArray(), 0);
            } else {
               UnixNativeDispatcher.unlink(var1);
            }
         } catch (UnixException var10) {
         }
      }

      FileDescriptor var13 = new FileDescriptor();
      fdAccess.set(var13, var8);
      return var13;
   }

   protected static class Flags {
      boolean read;
      boolean write;
      boolean append;
      boolean truncateExisting;
      boolean noFollowLinks;
      boolean create;
      boolean createNew;
      boolean deleteOnClose;
      boolean sync;
      boolean dsync;

      static UnixChannelFactory.Flags toFlags(Set<? extends OpenOption> var0) {
         UnixChannelFactory.Flags var1 = new UnixChannelFactory.Flags();
         Iterator var2 = var0.iterator();

         while(var2.hasNext()) {
            OpenOption var3 = (OpenOption)var2.next();
            if (var3 instanceof StandardOpenOption) {
               switch((StandardOpenOption)var3) {
               case READ:
                  var1.read = true;
                  break;
               case WRITE:
                  var1.write = true;
                  break;
               case APPEND:
                  var1.append = true;
                  break;
               case TRUNCATE_EXISTING:
                  var1.truncateExisting = true;
                  break;
               case CREATE:
                  var1.create = true;
                  break;
               case CREATE_NEW:
                  var1.createNew = true;
                  break;
               case DELETE_ON_CLOSE:
                  var1.deleteOnClose = true;
               case SPARSE:
                  break;
               case SYNC:
                  var1.sync = true;
                  break;
               case DSYNC:
                  var1.dsync = true;
                  break;
               default:
                  throw new UnsupportedOperationException();
               }
            } else {
               if (var3 != LinkOption.NOFOLLOW_LINKS) {
                  if (var3 == null) {
                     throw new NullPointerException();
                  }

                  throw new UnsupportedOperationException(var3 + " not supported");
               }

               var1.noFollowLinks = true;
            }
         }

         return var1;
      }
   }
}
