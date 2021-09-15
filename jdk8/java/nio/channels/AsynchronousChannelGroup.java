package java.nio.channels;

import java.io.IOException;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public abstract class AsynchronousChannelGroup {
   private final AsynchronousChannelProvider provider;

   protected AsynchronousChannelGroup(AsynchronousChannelProvider var1) {
      this.provider = var1;
   }

   public final AsynchronousChannelProvider provider() {
      return this.provider;
   }

   public static AsynchronousChannelGroup withFixedThreadPool(int var0, ThreadFactory var1) throws IOException {
      return AsynchronousChannelProvider.provider().openAsynchronousChannelGroup(var0, var1);
   }

   public static AsynchronousChannelGroup withCachedThreadPool(ExecutorService var0, int var1) throws IOException {
      return AsynchronousChannelProvider.provider().openAsynchronousChannelGroup(var0, var1);
   }

   public static AsynchronousChannelGroup withThreadPool(ExecutorService var0) throws IOException {
      return AsynchronousChannelProvider.provider().openAsynchronousChannelGroup(var0, 0);
   }

   public abstract boolean isShutdown();

   public abstract boolean isTerminated();

   public abstract void shutdown();

   public abstract void shutdownNow() throws IOException;

   public abstract boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException;
}
