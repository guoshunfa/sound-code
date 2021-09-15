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

@InstrumentationTarget("java.io.FileOutputStream")
public final class FileOutputStreamRMHooks {
   private final FileDescriptor fd = null;
   private final String path = null;
   private final Object closeLock = new Object();
   private volatile boolean closed = false;

   @InstrumentationMethod
   private void open(String var1, boolean var2) throws FileNotFoundException {
      ResourceIdImpl var3 = ResourceIdImpl.of((Object)this.path);
      ResourceRequest var4 = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
      long var5 = 0L;

      try {
         var5 = var4.request(1L, var3);
         if (var5 < 1L) {
            throw new FileNotFoundException(var1 + ": resource limited: too many open files");
         }
      } catch (ResourceRequestDeniedException var22) {
         FileNotFoundException var8 = new FileNotFoundException(var1 + ": resource limited: too many open files");
         var8.initCause(var22);
         throw var8;
      }

      ResourceRequest var7 = null;
      long var23 = 0L;
      byte var10 = 0;

      try {
         FileDescriptor var11 = null;

         try {
            var11 = this.getFD();
         } catch (IOException var19) {
         }

         var7 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(var11);

         try {
            var23 = var7.request(1L, var3);
            if (var23 < 1L) {
               throw new FileNotFoundException(var1 + ": resource limited: too many open file descriptors");
            }
         } catch (ResourceRequestDeniedException var20) {
            FileNotFoundException var13 = new FileNotFoundException(var1 + ": resource limited: too many open file descriptors");
            var13.initCause(var20);
            throw var13;
         }

         this.open(var1, var2);
         var10 = 1;
      } finally {
         var7.request(-(var23 - (long)var10), var3);
         var4.request(-(var5 - (long)var10), var3);
      }

   }

   @InstrumentationMethod
   public final FileDescriptor getFD() throws IOException {
      return this.getFD();
   }

   @InstrumentationMethod
   public void write(int var1) throws IOException {
      ResourceIdImpl var2 = ResourceIdImpl.of((Object)this.path);
      FileDescriptor var4 = this.getFD();
      ResourceRequest var3;
      if (var4 == FileDescriptor.err) {
         var3 = ApproverGroup.STDERR_WRITE_GROUP.getApprover(this);
      } else if (var4 == FileDescriptor.out) {
         var3 = ApproverGroup.STDOUT_WRITE_GROUP.getApprover(this);
      } else {
         var3 = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
      }

      long var5 = 0L;

      try {
         var5 = var3.request(1L, var2);
         if (var5 < 1L) {
            throw new IOException("Resource limited: insufficient bytes approved");
         }
      } catch (ResourceRequestDeniedException var12) {
         throw new IOException("Resource limited", var12);
      }

      byte var7 = 0;

      try {
         this.write(var1);
         var7 = 1;
      } finally {
         var3.request(-(var5 - (long)var7), var2);
      }

   }

   @InstrumentationMethod
   public void write(byte[] var1) throws IOException {
      if (var1 == null) {
         this.write(var1);
      } else {
         ResourceIdImpl var2 = ResourceIdImpl.of((Object)this.path);
         FileDescriptor var4 = this.getFD();
         ResourceRequest var3;
         if (var4 == FileDescriptor.err) {
            var3 = ApproverGroup.STDERR_WRITE_GROUP.getApprover(this);
         } else if (var4 == FileDescriptor.out) {
            var3 = ApproverGroup.STDOUT_WRITE_GROUP.getApprover(this);
         } else {
            var3 = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
         }

         int var5 = var1.length;
         long var6 = 0L;

         try {
            var6 = var3.request((long)var5, var2);
            if (var6 < (long)var5) {
               throw new IOException("Resource limited: insufficient bytes approved");
            }
         } catch (ResourceRequestDeniedException var13) {
            throw new IOException("Resource limited", var13);
         }

         int var8 = 0;

         try {
            this.write(var1);
            var8 = var5;
         } finally {
            var3.request(-(var6 - (long)var8), var2);
         }

      }
   }

   @InstrumentationMethod
   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (var3 < 0) {
         this.write(var1, var2, var3);
      } else {
         ResourceIdImpl var4 = ResourceIdImpl.of((Object)this.path);
         FileDescriptor var6 = this.getFD();
         ResourceRequest var5;
         if (var6 == FileDescriptor.err) {
            var5 = ApproverGroup.STDERR_WRITE_GROUP.getApprover(this);
         } else if (var6 == FileDescriptor.out) {
            var5 = ApproverGroup.STDOUT_WRITE_GROUP.getApprover(this);
         } else {
            var5 = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
         }

         long var7 = 0L;

         try {
            var7 = var5.request((long)var3, var4);
            if (var7 < (long)var3) {
               throw new IOException("Resource limited: insufficient bytes approved");
            }
         } catch (ResourceRequestDeniedException var14) {
            throw new IOException("Resource limited", var14);
         }

         int var9 = 0;

         try {
            this.write(var1, var2, var3);
            var9 = var3;
         } finally {
            var5.request(-(var7 - (long)var9), var4);
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
