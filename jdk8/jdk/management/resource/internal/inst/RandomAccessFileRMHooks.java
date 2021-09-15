package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

@InstrumentationTarget("java.io.RandomAccessFile")
public final class RandomAccessFileRMHooks {
   private FileDescriptor fd;
   private final String path = null;
   private final Object closeLock = new Object();
   private volatile boolean closed = false;

   @InstrumentationMethod
   private void open(String var1, int var2) throws FileNotFoundException {
      ResourceIdImpl var3 = ResourceIdImpl.of((Object)this.path);
      ResourceRequest var4 = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
      long var5 = 0L;
      long var7 = 0L;

      try {
         var7 = var4.request(1L, var3);
         if (var7 < 1L) {
            throw new FileNotFoundException(var1 + ": resource limited: too many open files");
         }
      } catch (ResourceRequestDeniedException var19) {
         FileNotFoundException var10 = new FileNotFoundException(var1 + ": resource limited: too many open files");
         var10.initCause(var19);
         throw var10;
      }

      ResourceRequest var9 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
      byte var20 = 0;

      try {
         try {
            var5 = var9.request(1L, var3);
            if (var5 < 1L) {
               throw new FileNotFoundException(var1 + ": resource limited: too many open file descriptors");
            }
         } catch (ResourceRequestDeniedException var17) {
            FileNotFoundException var12 = new FileNotFoundException(var1 + ": resource limited: too many open file descriptors");
            var12.initCause(var17);
            throw var12;
         }

         this.open(var1, var2);
         var20 = 1;
      } finally {
         var9.request(-(var5 - (long)var20), var3);
         var4.request(-(var7 - (long)var20), var3);
      }

   }

   @InstrumentationMethod
   public int read() throws IOException {
      ResourceIdImpl var1 = ResourceIdImpl.of((Object)this.path);
      ResourceRequest var2 = ApproverGroup.FILE_READ_GROUP.getApprover(this);
      long var3 = 0L;

      try {
         var3 = var2.request(1L, var1);
         if (var3 < 1L) {
            throw new IOException("Resource limited: insufficient bytes approved");
         }
      } catch (ResourceRequestDeniedException var10) {
         throw new IOException("Resource limited", var10);
      }

      int var5 = -1;

      try {
         var5 = this.read();
      } finally {
         var2.request(-(var3 - (long)(var5 == -1 ? 0 : 1)), var1);
      }

      return var5;
   }

   @InstrumentationMethod
   public int read(byte[] var1) throws IOException {
      if (var1 == null) {
         return this.read(var1);
      } else {
         ResourceIdImpl var2 = ResourceIdImpl.of((Object)this.path);
         ResourceRequest var3 = ApproverGroup.FILE_READ_GROUP.getApprover(this);
         int var4 = var1.length;
         long var5 = 0L;

         try {
            var5 = Math.max(var3.request((long)var4, var2), 0L);
         } catch (ResourceRequestDeniedException var13) {
            throw new IOException("Resource limited", var13);
         }

         int var7 = 0;

         int var8;
         try {
            if (var5 < (long)var4) {
               var3.request(-var5, var2);
               var8 = this.read(var1, 0, var1.length);
               var7 = Math.max(var8, 0);
            } else {
               var8 = this.read(var1);
               var7 = Math.max(var8, 0);
            }
         } finally {
            var3.request(-(var5 - (long)var7), var2);
         }

         return var8;
      }
   }

   @InstrumentationMethod
   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (var3 < 0) {
         return this.read(var1, var2, var3);
      } else {
         ResourceIdImpl var4 = ResourceIdImpl.of((Object)this.path);
         ResourceRequest var5 = ApproverGroup.FILE_READ_GROUP.getApprover(this);
         long var6 = 0L;

         try {
            var6 = Math.max(var5.request((long)var3, var4), 0L);
         } catch (ResourceRequestDeniedException var14) {
            throw new IOException("Resource limited", var14);
         }

         var3 = Math.min(var3, (int)var6);
         int var8 = 0;
         boolean var9 = false;

         int var15;
         try {
            var15 = this.read(var1, var2, var3);
            var8 = Math.max(var15, 0);
         } finally {
            var5.request(-(var6 - (long)var8), var4);
         }

         return var15;
      }
   }

   @InstrumentationMethod
   public void write(int var1) throws IOException {
      ResourceIdImpl var2 = ResourceIdImpl.of((Object)this.path);
      ResourceRequest var3 = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
      long var4 = 0L;

      try {
         var4 = var3.request(1L, var2);
         if (var4 < 1L) {
            throw new IOException("Resource limited: insufficient bytes approved");
         }
      } catch (ResourceRequestDeniedException var11) {
         throw new IOException("Resource limited", var11);
      }

      byte var6 = 0;

      try {
         this.write(var1);
         var6 = 1;
      } finally {
         var3.request(-(var4 - (long)var6), var2);
      }

   }

   @InstrumentationMethod
   public void write(byte[] var1) throws IOException {
      if (var1 == null) {
         this.write(var1);
      } else {
         ResourceIdImpl var2 = ResourceIdImpl.of((Object)this.path);
         ResourceRequest var3 = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
         int var4 = var1.length;
         long var5 = 0L;

         try {
            var5 = var3.request((long)var4, var2);
            if (var5 < (long)var4) {
               throw new IOException("Resource limited: insufficient bytes approved");
            }
         } catch (ResourceRequestDeniedException var12) {
            throw new IOException("Resource limited", var12);
         }

         int var7 = 0;

         try {
            this.write(var1);
            var7 = var4;
         } finally {
            var3.request(-(var5 - (long)var7), var2);
         }

      }
   }

   @InstrumentationMethod
   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (var3 < 0) {
         this.write(var1, var2, var3);
      } else {
         ResourceIdImpl var4 = ResourceIdImpl.of((Object)this.path);
         ResourceRequest var5 = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
         long var6 = 0L;

         try {
            var6 = var5.request((long)var3, var4);
            if (var6 < (long)var3) {
               throw new IOException("Resource limited: insufficient bytes approved");
            }
         } catch (ResourceRequestDeniedException var13) {
            throw new IOException("Resource limited", var13);
         }

         int var8 = 0;

         try {
            this.write(var1, var2, var3);
            var8 = var3;
         } finally {
            var5.request(-(var6 - (long)var8), var4);
         }

      }
   }

   @InstrumentationMethod
   public final void writeBytes(String var1) throws IOException {
      if (var1 == null) {
         this.writeBytes(var1);
      } else {
         ResourceIdImpl var2 = ResourceIdImpl.of((Object)this.path);
         ResourceRequest var3 = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
         int var4 = var1.length();
         long var5 = 0L;

         try {
            var5 = var3.request((long)var4, var2);
            if (var5 < (long)var4) {
               throw new IOException("Resource limited: insufficient bytes approved");
            }
         } catch (ResourceRequestDeniedException var12) {
            throw new IOException("Resource limited", var12);
         }

         int var7 = 0;

         try {
            this.writeBytes(var1);
            var7 = var4;
         } finally {
            var3.request(-(var5 - (long)var7), var2);
         }

      }
   }

   @InstrumentationMethod
   public final void writeChars(String var1) throws IOException {
      if (var1 == null) {
         this.writeChars(var1);
      } else {
         ResourceIdImpl var2 = ResourceIdImpl.of((Object)this.path);
         ResourceRequest var3 = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
         int var4 = 2 * var1.length();
         long var5 = 0L;

         try {
            var5 = var3.request((long)var4, var2);
            if (var5 < (long)var4) {
               throw new IOException("Resource limited: insufficient bytes approved");
            }
         } catch (ResourceRequestDeniedException var12) {
            throw new IOException("Resource limited", var12);
         }

         int var7 = 0;

         try {
            this.writeChars(var1);
            var7 = var4;
         } finally {
            var3.request(-(var5 - (long)var7), var2);
         }

      }
   }

   @InstrumentationMethod
   public void close() throws IOException {
      synchronized(this.closeLock) {
         if (this.closed) {
            return;
         }
      }

      JavaIOFileDescriptorAccess var3 = SharedSecrets.getJavaIOFileDescriptorAccess();

      long var1;
      try {
         var1 = var3.getHandle(this.fd);
         if (var1 == -1L) {
            var1 = (long)var3.get(this.fd);
         }
      } catch (UnsupportedOperationException var22) {
         var1 = (long)var3.get(this.fd);
      }

      boolean var18 = false;

      try {
         var18 = true;
         this.close();
         var18 = false;
      } finally {
         if (var18) {
            long var9;
            try {
               var9 = var3.getHandle(this.fd);
               if (var9 == -1L) {
                  var9 = (long)var3.get(this.fd);
               }
            } catch (UnsupportedOperationException var19) {
               var9 = (long)var3.get(this.fd);
            }

            ResourceIdImpl var11 = ResourceIdImpl.of((Object)this.path);
            ResourceRequest var12;
            if (var9 != var1) {
               var12 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
               var12.request(-1L, var11);
            }

            var12 = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
            var12.request(-1L, var11);
         }
      }

      long var4;
      try {
         var4 = var3.getHandle(this.fd);
         if (var4 == -1L) {
            var4 = (long)var3.get(this.fd);
         }
      } catch (UnsupportedOperationException var20) {
         var4 = (long)var3.get(this.fd);
      }

      ResourceIdImpl var6 = ResourceIdImpl.of((Object)this.path);
      ResourceRequest var7;
      if (var4 != var1) {
         var7 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
         var7.request(-1L, var6);
      }

      var7 = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
      var7.request(-1L, var6);
   }
}
