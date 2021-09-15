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

@InstrumentationTarget("java.io.FileInputStream")
public final class FileInputStreamRMHooks {
   private final FileDescriptor fd = null;
   private String path = null;
   private final Object closeLock = new Object();
   private volatile boolean closed = false;

   @InstrumentationMethod
   public final FileDescriptor getFD() throws IOException {
      return this.getFD();
   }

   @InstrumentationMethod
   private void open(String var1) throws FileNotFoundException {
      ResourceIdImpl var2 = ResourceIdImpl.of((Object)this.path);
      ResourceRequest var3 = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
      long var4 = 0L;

      try {
         var4 = var3.request(1L, var2);
         if (var4 < 1L) {
            throw new FileNotFoundException(var1 + ": resource limited: too many open files");
         }
      } catch (ResourceRequestDeniedException var21) {
         FileNotFoundException var7 = new FileNotFoundException(var1 + ": resource limited: too many open files");
         var7.initCause(var21);
         throw var7;
      }

      ResourceRequest var6 = null;
      long var22 = 0L;
      byte var9 = 0;

      try {
         FileDescriptor var10 = null;

         try {
            var10 = this.getFD();
         } catch (IOException var18) {
         }

         var6 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(var10);

         try {
            var22 = var6.request(1L, var2);
            if (var22 < 1L) {
               throw new FileNotFoundException(var1 + ": resource limited: too many open file descriptors");
            }
         } catch (ResourceRequestDeniedException var19) {
            FileNotFoundException var12 = new FileNotFoundException(var1 + ": resource limited: too many open file descriptors");
            var12.initCause(var19);
            throw var12;
         }

         this.open(var1);
         var9 = 1;
      } finally {
         var6.request(-(var22 - (long)var9), var2);
         var3.request(-(var4 - (long)var9), var2);
      }

   }

   @InstrumentationMethod
   public int read() throws IOException {
      ResourceIdImpl var1 = ResourceIdImpl.of((Object)this.path);
      ResourceRequest var2;
      if (this.getFD() == FileDescriptor.in) {
         var2 = ApproverGroup.STDIN_READ_GROUP.getApprover(this);
      } else {
         var2 = ApproverGroup.FILE_READ_GROUP.getApprover(this);
      }

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
         ResourceRequest var3;
         if (this.getFD() == FileDescriptor.in) {
            var3 = ApproverGroup.STDIN_READ_GROUP.getApprover(this);
         } else {
            var3 = ApproverGroup.FILE_READ_GROUP.getApprover(this);
         }

         int var4 = var1.length;
         long var5 = 0L;

         try {
            var5 = Math.max(var3.request((long)var4, var2), 0L);
         } catch (ResourceRequestDeniedException var13) {
            throw new IOException("Resource limited", var13);
         }

         int var7 = 0;
         boolean var8 = false;

         int var14;
         try {
            if (var5 < (long)var4) {
               var3.request(-var5, var2);
               var14 = this.read(var1, 0, var1.length);
               var7 = Math.max(var14, 0);
            } else {
               var14 = this.read(var1);
               var7 = Math.max(var14, 0);
            }
         } finally {
            var3.request(-(var5 - (long)var7), var2);
         }

         return var14;
      }
   }

   @InstrumentationMethod
   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (var3 < 0) {
         return this.read(var1, var2, var3);
      } else {
         ResourceIdImpl var4 = ResourceIdImpl.of((Object)this.path);
         ResourceRequest var5;
         if (this.getFD() == FileDescriptor.in) {
            var5 = ApproverGroup.STDIN_READ_GROUP.getApprover(this);
         } else {
            var5 = ApproverGroup.FILE_READ_GROUP.getApprover(this);
         }

         long var6 = 0L;

         try {
            var6 = Math.max(var5.request((long)var3, var4), 0L);
         } catch (ResourceRequestDeniedException var14) {
            throw new IOException("Resource limited", var14);
         }

         var3 = Math.min(var3, (int)var6);
         int var8 = 0;

         int var9;
         try {
            var9 = this.read(var1, var2, var3);
            var8 = Math.max(var9, 0);
         } finally {
            var5.request(-(var6 - (long)var8), var4);
         }

         return var9;
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
