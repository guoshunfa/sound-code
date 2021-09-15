package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.FileChannelImpl")
public final class FileChannelImplRMHooks {
   private final FileDescriptor fd = null;
   private String path = null;

   @InstrumentationMethod
   public static FileChannel open(FileDescriptor var0, String var1, boolean var2, boolean var3, Object var4) {
      long var5 = 0L;
      byte var7 = 0;
      FileChannel var8 = null;
      ResourceIdImpl var9 = null;
      ResourceRequest var10 = null;

      try {
         var8 = open(var0, var1, var2, var3, var4);
         var9 = ResourceIdImpl.of((Object)var1);
         var10 = ApproverGroup.FILE_OPEN_GROUP.getApprover(var8);
         boolean var11 = false;

         try {
            var5 = var10.request(1L, var9);
            if (var5 < 1L) {
               throw new ResourceRequestDeniedException(var1 + ": resource limited: too many open files");
            }

            var11 = true;
         } finally {
            if (!var11) {
               try {
                  var8.close();
               } catch (IOException var24) {
               }
            }

         }

         var7 = 1;
      } finally {
         if (var10 != null) {
            var10.request(-(var5 - (long)var7), var9);
         }

      }

      return var8;
   }

   @InstrumentationMethod
   public static FileChannel open(FileDescriptor var0, String var1, boolean var2, boolean var3, boolean var4, Object var5) {
      FileChannel var6 = open(var0, var1, var2, var3, var4, var5);
      ResourceIdImpl var7 = ResourceIdImpl.of((Object)var1);
      ResourceRequest var8 = null;
      long var9 = 0L;
      boolean var11 = false;
      if (var5 == null) {
         var8 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(var0);

         try {
            var9 = var8.request(1L, var7);
            if (var9 < 1L) {
               throw new ResourceRequestDeniedException(var1 + ": resource limited: too many open file descriptors");
            }

            var11 = true;
         } finally {
            if (!var11) {
               var8.request(-1L, var7);

               try {
                  var6.close();
               } catch (IOException var30) {
               }
            }

         }
      }

      var11 = false;
      var8 = ApproverGroup.FILE_OPEN_GROUP.getApprover(var6);

      try {
         var9 = var8.request(1L, var7);
         if (var9 < 1L) {
            try {
               var6.close();
            } catch (IOException var32) {
            }

            throw new ResourceRequestDeniedException(var1 + ": resource limited: too many open files");
         }

         var11 = true;
      } finally {
         if (!var11) {
            var8.request(-1L, var7);

            try {
               var6.close();
            } catch (IOException var31) {
            }
         }

      }

      return var6;
   }

   @InstrumentationMethod
   public int read(ByteBuffer var1) throws IOException {
      ResourceIdImpl var2 = ResourceIdImpl.of((Object)this.path);
      ResourceRequest var3 = ApproverGroup.FILE_READ_GROUP.getApprover(this);
      long var4 = 0L;
      int var6 = var1.remaining();

      try {
         var4 = Math.max(var3.request((long)var6, var2), 0L);
         if (var4 < (long)var6) {
            throw new IOException("Resource limited: insufficient bytes approved");
         }
      } catch (ResourceRequestDeniedException var13) {
         throw new IOException("Resource limited", var13);
      }

      int var7 = 0;
      boolean var8 = false;

      int var14;
      try {
         var14 = this.read(var1);
         var7 = Math.max(var14, 0);
      } finally {
         var3.request(-(var4 - (long)var7), var2);
      }

      return var14;
   }

   @InstrumentationMethod
   public long read(ByteBuffer[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         ResourceIdImpl var4 = ResourceIdImpl.of((Object)this.path);
         ResourceRequest var5 = ApproverGroup.FILE_READ_GROUP.getApprover(this);
         long var6 = 0L;
         int var8 = 0;
         int var9 = var2 + var3;

         for(int var10 = var2; var10 < var9; ++var10) {
            var8 += var1[var10].remaining();
         }

         try {
            var6 = Math.max(var5.request((long)var8, var4), 0L);
            if (var6 < (long)var8) {
               throw new IOException("Resource limited: insufficient bytes approved");
            }
         } catch (ResourceRequestDeniedException var18) {
            throw new IOException("Resource limited", var18);
         }

         long var19 = 0L;
         long var12 = 0L;

         try {
            var12 = this.read(var1, var2, var3);
            var19 = Math.max(var12, 0L);
         } finally {
            var5.request(-(var6 - var19), var4);
         }

         return var12;
      } else {
         return this.read(var1, var2, var3);
      }
   }

   @InstrumentationMethod
   public int read(ByteBuffer var1, long var2) throws IOException {
      ResourceIdImpl var4 = ResourceIdImpl.of((Object)this.path);
      ResourceRequest var5 = ApproverGroup.FILE_READ_GROUP.getApprover(this);
      long var6 = 0L;
      int var8 = var1.remaining();

      try {
         var6 = Math.max(var5.request((long)var8, var4), 0L);
         if (var6 < (long)var8) {
            throw new IOException("Resource limited: insufficient bytes approved");
         }
      } catch (ResourceRequestDeniedException var15) {
         throw new IOException("Resource limited", var15);
      }

      int var9 = 0;
      boolean var10 = false;

      int var16;
      try {
         var16 = this.read(var1, var2);
         var9 = Math.max(var16, 0);
      } finally {
         var5.request(-(var6 - (long)var9), var4);
      }

      return var16;
   }

   @InstrumentationMethod
   public int write(ByteBuffer var1) throws IOException {
      ResourceIdImpl var2 = ResourceIdImpl.of((Object)this.path);
      ResourceRequest var3 = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
      long var4 = 0L;
      int var6 = var1.remaining();

      try {
         var4 = Math.max(var3.request((long)var6, var2), 0L);
         if (var4 < (long)var6) {
            throw new IOException("Resource limited: insufficient bytes approved");
         }
      } catch (ResourceRequestDeniedException var12) {
         throw new IOException("Resource limited", var12);
      }

      int var7 = 0;

      try {
         var7 = this.write(var1);
      } finally {
         var3.request(-(var4 - (long)var7), var2);
      }

      return var7;
   }

   @InstrumentationMethod
   public long write(ByteBuffer[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         ResourceIdImpl var4 = ResourceIdImpl.of((Object)this.path);
         ResourceRequest var5 = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
         long var6 = 0L;
         int var8 = 0;
         int var9 = var2 + var3;

         for(int var10 = var2; var10 < var9; ++var10) {
            var8 += var1[var10].remaining();
         }

         try {
            var6 = Math.max(var5.request((long)var8, var4), 0L);
            if (var6 < (long)var8) {
               throw new IOException("Resource limited: insufficient bytes approved");
            }
         } catch (ResourceRequestDeniedException var16) {
            throw new IOException("Resource limited", var16);
         }

         long var17 = 0L;

         try {
            var17 = Math.max(this.write(var1, var2, var3), 0L);
         } finally {
            var5.request(-(var6 - var17), var4);
         }

         return var17;
      } else {
         return this.write(var1, var2, var3);
      }
   }

   @InstrumentationMethod
   public int write(ByteBuffer var1, long var2) throws IOException {
      ResourceIdImpl var4 = ResourceIdImpl.of((Object)this.path);
      ResourceRequest var5 = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
      long var6 = 0L;
      int var8 = var1.remaining();

      try {
         var6 = Math.max(var5.request((long)var8, var4), 0L);
         if (var6 < (long)var8) {
            throw new IOException("Resource limited: insufficient bytes approved");
         }
      } catch (ResourceRequestDeniedException var14) {
         throw new IOException("Resource limited", var14);
      }

      int var9 = 0;

      try {
         var9 = this.write(var1, var2);
      } finally {
         var5.request(-(var6 - (long)var9), var4);
      }

      return var9;
   }

   @InstrumentationMethod
   protected void implCloseChannel() throws IOException {
      boolean var7 = false;

      try {
         var7 = true;
         this.implCloseChannel();
         var7 = false;
      } finally {
         if (var7) {
            ResourceIdImpl var4 = ResourceIdImpl.of((Object)this.path);
            ResourceRequest var5 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
            var5.request(-1L, var4);
            var5 = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
            var5.request(-1L, var4);
         }
      }

      ResourceIdImpl var1 = ResourceIdImpl.of((Object)this.path);
      ResourceRequest var2 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
      var2.request(-1L, var1);
      var2 = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
      var2.request(-1L, var1);
   }
}
