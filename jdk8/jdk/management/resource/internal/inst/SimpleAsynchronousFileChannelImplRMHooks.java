package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.CompletionHandlerWrapper;
import jdk.management.resource.internal.FutureWrapper;
import jdk.management.resource.internal.ResourceIdImpl;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;
import sun.nio.ch.ThreadPool;

@InstrumentationTarget("sun.nio.ch.SimpleAsynchronousFileChannelImpl")
public final class SimpleAsynchronousFileChannelImplRMHooks {
   protected final FileDescriptor fdObj = null;
   protected volatile boolean closed;

   @InstrumentationMethod
   public static AsynchronousFileChannel open(FileDescriptor var0, boolean var1, boolean var2, ThreadPool var3) {
      AsynchronousFileChannel var4 = open(var0, var1, var2, var3);
      JavaIOFileDescriptorAccess var5 = SharedSecrets.getJavaIOFileDescriptorAccess();

      long var6;
      try {
         var6 = var5.getHandle(var0);
         if (var6 == -1L) {
            var6 = (long)var5.get(var0);
         }
      } catch (UnsupportedOperationException var36) {
         var6 = (long)var5.get(var0);
      }

      ResourceIdImpl var8 = ResourceIdImpl.of((Object)var6);
      ResourceRequest var9 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(var0);
      long var10 = 0L;
      boolean var12 = false;

      try {
         var10 = var9.request(1L, var8);
         if (var10 < 1L) {
            throw new ResourceRequestDeniedException("Resource limited: too many open file descriptors");
         }

         var12 = true;
      } finally {
         if (!var12) {
            var9.request(-1L, var8);

            try {
               var4.close();
            } catch (IOException var33) {
            }
         }

      }

      var12 = false;
      var9 = ApproverGroup.FILE_OPEN_GROUP.getApprover(var4);

      try {
         var10 = var9.request(1L, var8);
         if (var10 < 1L) {
            try {
               var4.close();
            } catch (IOException var35) {
            }

            throw new ResourceRequestDeniedException("Resource limited: too many open files");
         }

         var12 = true;
      } finally {
         if (!var12) {
            var9.request(-1L, var8);

            try {
               var4.close();
            } catch (IOException var34) {
            }
         }

      }

      return var4;
   }

   @InstrumentationMethod
   <A> Future<Integer> implRead(ByteBuffer var1, long var2, A var4, CompletionHandler<Integer, ? super A> var5) {
      ResourceIdImpl var6 = ResourceIdImpl.of(this.fdObj);
      ResourceRequest var7 = ApproverGroup.FILE_READ_GROUP.getApprover(this);
      long var8 = 0L;
      int var10 = var1.remaining();

      try {
         var8 = Math.max(var7.request((long)var10, var6), 0L);
         if (var8 < (long)var10) {
            throw new ResourceRequestDeniedException("Resource limited: insufficient bytes approved");
         }
      } catch (ResourceRequestDeniedException var16) {
         if (var5 != null) {
            var5.failed(var16, var4);
            return null;
         }

         CompletableFuture var12 = new CompletableFuture();
         var12.completeExceptionally(var16);
         return var12;
      }

      CompletionHandlerWrapper var11 = null;
      if (var5 != null) {
         var11 = new CompletionHandlerWrapper(var5, var6, var7, var8);
      }

      Object var17 = this.implRead(var1, var2, var4, var11);
      if (var5 == null) {
         if (((Future)var17).isDone()) {
            int var13 = 0;

            try {
               var13 = (Integer)((Future)var17).get();
            } catch (ExecutionException | InterruptedException var15) {
            }

            var13 = Math.max(0, var13);
            var7.request(-(var8 - (long)var13), var6);
         } else {
            var17 = new FutureWrapper((Future)var17, var6, var7, var8);
         }
      }

      return (Future)var17;
   }

   @InstrumentationMethod
   <A> Future<Integer> implWrite(ByteBuffer var1, long var2, A var4, CompletionHandler<Integer, ? super A> var5) {
      ResourceIdImpl var6 = ResourceIdImpl.of(this.fdObj);
      ResourceRequest var7 = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
      long var8 = 0L;
      int var10 = var1.remaining();

      try {
         var8 = Math.max(var7.request((long)var10, var6), 0L);
         if (var8 < (long)var10) {
            throw new ResourceRequestDeniedException("Resource limited: insufficient bytes approved");
         }
      } catch (ResourceRequestDeniedException var16) {
         if (var5 != null) {
            var5.failed(var16, var4);
            return null;
         }

         CompletableFuture var12 = new CompletableFuture();
         var12.completeExceptionally(var16);
         return var12;
      }

      CompletionHandlerWrapper var11 = null;
      if (var5 != null) {
         var11 = new CompletionHandlerWrapper(var5, var6, var7, var8);
      }

      Object var17 = this.implWrite(var1, var2, var4, var11);
      if (var5 == null) {
         if (((Future)var17).isDone()) {
            int var13 = 0;

            try {
               var13 = (Integer)((Future)var17).get();
            } catch (ExecutionException | InterruptedException var15) {
            }

            var13 = Math.max(0, var13);
            var7.request(-(var8 - (long)var13), var6);
         } else {
            var17 = new FutureWrapper((Future)var17, var6, var7, var8);
         }
      }

      return (Future)var17;
   }

   @InstrumentationMethod
   public void close() throws IOException {
      synchronized(this.fdObj) {
         if (this.closed) {
            return;
         }
      }

      boolean var10 = false;

      try {
         var10 = true;
         this.close();
         var10 = false;
      } finally {
         if (var10) {
            JavaIOFileDescriptorAccess var5 = SharedSecrets.getJavaIOFileDescriptorAccess();
            ResourceIdImpl var6 = ResourceIdImpl.of((Object)var5.get(this.fdObj));
            ResourceRequest var7 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fdObj);
            var7.request(-1L, var6);
            var7 = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
            var7.request(-1L, var6);
         }
      }

      JavaIOFileDescriptorAccess var1 = SharedSecrets.getJavaIOFileDescriptorAccess();
      ResourceIdImpl var2 = ResourceIdImpl.of((Object)var1.get(this.fdObj));
      ResourceRequest var3 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fdObj);
      var3.request(-1L, var2);
      var3 = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
      var3.request(-1L, var2);
   }
}
