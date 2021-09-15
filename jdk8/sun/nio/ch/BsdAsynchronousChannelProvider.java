package sun.nio.ch;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.IllegalChannelGroupException;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public class BsdAsynchronousChannelProvider extends AsynchronousChannelProvider {
   private static volatile KQueuePort defaultPort;

   private KQueuePort defaultEventPort() throws IOException {
      if (defaultPort == null) {
         Class var1 = BsdAsynchronousChannelProvider.class;
         synchronized(BsdAsynchronousChannelProvider.class) {
            if (defaultPort == null) {
               defaultPort = (new KQueuePort(this, ThreadPool.getDefault())).start();
            }
         }
      }

      return defaultPort;
   }

   public AsynchronousChannelGroup openAsynchronousChannelGroup(int var1, ThreadFactory var2) throws IOException {
      return (new KQueuePort(this, ThreadPool.create(var1, var2))).start();
   }

   public AsynchronousChannelGroup openAsynchronousChannelGroup(ExecutorService var1, int var2) throws IOException {
      return (new KQueuePort(this, ThreadPool.wrap(var1, var2))).start();
   }

   private Port toPort(AsynchronousChannelGroup var1) throws IOException {
      if (var1 == null) {
         return this.defaultEventPort();
      } else if (!(var1 instanceof KQueuePort)) {
         throw new IllegalChannelGroupException();
      } else {
         return (Port)var1;
      }
   }

   public AsynchronousServerSocketChannel openAsynchronousServerSocketChannel(AsynchronousChannelGroup var1) throws IOException {
      return new UnixAsynchronousServerSocketChannelImpl(this.toPort(var1));
   }

   public AsynchronousSocketChannel openAsynchronousSocketChannel(AsynchronousChannelGroup var1) throws IOException {
      return new UnixAsynchronousSocketChannelImpl(this.toPort(var1));
   }
}
