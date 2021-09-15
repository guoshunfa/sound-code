package java.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

public final class FileDescriptor {
   private int fd;
   private Closeable parent;
   private List<Closeable> otherParents;
   private boolean closed;
   public static final FileDescriptor in = new FileDescriptor(0);
   public static final FileDescriptor out = new FileDescriptor(1);
   public static final FileDescriptor err = new FileDescriptor(2);

   public FileDescriptor() {
      this.fd = -1;
   }

   private FileDescriptor(int var1) {
      this.fd = var1;
   }

   public boolean valid() {
      return this.fd != -1;
   }

   public native void sync() throws SyncFailedException;

   private static native void initIDs();

   synchronized void attach(Closeable var1) {
      if (this.parent == null) {
         this.parent = var1;
      } else if (this.otherParents == null) {
         this.otherParents = new ArrayList();
         this.otherParents.add(this.parent);
         this.otherParents.add(var1);
      } else {
         this.otherParents.add(var1);
      }

   }

   synchronized void closeAll(Closeable var1) throws IOException {
      if (!this.closed) {
         this.closed = true;
         IOException var2 = null;

         try {
            Closeable var3 = var1;
            Throwable var4 = null;

            try {
               if (this.otherParents != null) {
                  Iterator var5 = this.otherParents.iterator();

                  while(var5.hasNext()) {
                     Closeable var6 = (Closeable)var5.next();

                     try {
                        var6.close();
                     } catch (IOException var26) {
                        if (var2 == null) {
                           var2 = var26;
                        } else {
                           var2.addSuppressed(var26);
                        }
                     }
                  }
               }
            } catch (Throwable var27) {
               var4 = var27;
               throw var27;
            } finally {
               if (var1 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var25) {
                        var4.addSuppressed(var25);
                     }
                  } else {
                     var1.close();
                  }
               }

            }
         } catch (IOException var29) {
            if (var2 != null) {
               var29.addSuppressed(var2);
            }

            var2 = var29;
         } finally {
            if (var2 != null) {
               throw var2;
            }

         }
      }

   }

   static {
      initIDs();
      SharedSecrets.setJavaIOFileDescriptorAccess(new JavaIOFileDescriptorAccess() {
         public void set(FileDescriptor var1, int var2) {
            var1.fd = var2;
         }

         public int get(FileDescriptor var1) {
            return var1.fd;
         }

         public void setHandle(FileDescriptor var1, long var2) {
            throw new UnsupportedOperationException();
         }

         public long getHandle(FileDescriptor var1) {
            throw new UnsupportedOperationException();
         }
      });
   }
}
