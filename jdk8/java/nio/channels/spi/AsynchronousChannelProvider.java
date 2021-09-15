package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import sun.nio.ch.DefaultAsynchronousChannelProvider;

public abstract class AsynchronousChannelProvider {
   private static Void checkPermission() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(new RuntimePermission("asynchronousChannelProvider"));
      }

      return null;
   }

   private AsynchronousChannelProvider(Void var1) {
   }

   protected AsynchronousChannelProvider() {
      this(checkPermission());
   }

   public static AsynchronousChannelProvider provider() {
      return AsynchronousChannelProvider.ProviderHolder.provider;
   }

   public abstract AsynchronousChannelGroup openAsynchronousChannelGroup(int var1, ThreadFactory var2) throws IOException;

   public abstract AsynchronousChannelGroup openAsynchronousChannelGroup(ExecutorService var1, int var2) throws IOException;

   public abstract AsynchronousServerSocketChannel openAsynchronousServerSocketChannel(AsynchronousChannelGroup var1) throws IOException;

   public abstract AsynchronousSocketChannel openAsynchronousSocketChannel(AsynchronousChannelGroup var1) throws IOException;

   private static class ProviderHolder {
      static final AsynchronousChannelProvider provider = load();

      private static AsynchronousChannelProvider load() {
         return (AsynchronousChannelProvider)AccessController.doPrivileged(new PrivilegedAction<AsynchronousChannelProvider>() {
            public AsynchronousChannelProvider run() {
               AsynchronousChannelProvider var1 = AsynchronousChannelProvider.ProviderHolder.loadProviderFromProperty();
               if (var1 != null) {
                  return var1;
               } else {
                  var1 = AsynchronousChannelProvider.ProviderHolder.loadProviderAsService();
                  return var1 != null ? var1 : DefaultAsynchronousChannelProvider.create();
               }
            }
         });
      }

      private static AsynchronousChannelProvider loadProviderFromProperty() {
         String var0 = System.getProperty("java.nio.channels.spi.AsynchronousChannelProvider");
         if (var0 == null) {
            return null;
         } else {
            try {
               Class var1 = Class.forName(var0, true, ClassLoader.getSystemClassLoader());
               return (AsynchronousChannelProvider)var1.newInstance();
            } catch (ClassNotFoundException var2) {
               throw new ServiceConfigurationError((String)null, var2);
            } catch (IllegalAccessException var3) {
               throw new ServiceConfigurationError((String)null, var3);
            } catch (InstantiationException var4) {
               throw new ServiceConfigurationError((String)null, var4);
            } catch (SecurityException var5) {
               throw new ServiceConfigurationError((String)null, var5);
            }
         }
      }

      private static AsynchronousChannelProvider loadProviderAsService() {
         ServiceLoader var0 = ServiceLoader.load(AsynchronousChannelProvider.class, ClassLoader.getSystemClassLoader());
         Iterator var1 = var0.iterator();

         while(true) {
            try {
               return var1.hasNext() ? (AsynchronousChannelProvider)var1.next() : null;
            } catch (ServiceConfigurationError var3) {
               if (!(var3.getCause() instanceof SecurityException)) {
                  throw var3;
               }
            }
         }
      }
   }
}
