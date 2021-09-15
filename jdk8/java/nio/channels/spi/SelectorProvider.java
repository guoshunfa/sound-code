package java.nio.channels.spi;

import java.io.IOException;
import java.net.ProtocolFamily;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Pipe;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import sun.nio.ch.DefaultSelectorProvider;

public abstract class SelectorProvider {
   private static final Object lock = new Object();
   private static SelectorProvider provider = null;

   protected SelectorProvider() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new RuntimePermission("selectorProvider"));
      }

   }

   private static boolean loadProviderFromProperty() {
      String var0 = System.getProperty("java.nio.channels.spi.SelectorProvider");
      if (var0 == null) {
         return false;
      } else {
         try {
            Class var1 = Class.forName(var0, true, ClassLoader.getSystemClassLoader());
            provider = (SelectorProvider)var1.newInstance();
            return true;
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

   private static boolean loadProviderAsService() {
      ServiceLoader var0 = ServiceLoader.load(SelectorProvider.class, ClassLoader.getSystemClassLoader());
      Iterator var1 = var0.iterator();

      while(true) {
         try {
            if (!var1.hasNext()) {
               return false;
            }

            provider = (SelectorProvider)var1.next();
            return true;
         } catch (ServiceConfigurationError var3) {
            if (!(var3.getCause() instanceof SecurityException)) {
               throw var3;
            }
         }
      }
   }

   public static SelectorProvider provider() {
      synchronized(lock) {
         return provider != null ? provider : (SelectorProvider)AccessController.doPrivileged(new PrivilegedAction<SelectorProvider>() {
            public SelectorProvider run() {
               if (SelectorProvider.loadProviderFromProperty()) {
                  return SelectorProvider.provider;
               } else if (SelectorProvider.loadProviderAsService()) {
                  return SelectorProvider.provider;
               } else {
                  SelectorProvider.provider = DefaultSelectorProvider.create();
                  return SelectorProvider.provider;
               }
            }
         });
      }
   }

   public abstract DatagramChannel openDatagramChannel() throws IOException;

   public abstract DatagramChannel openDatagramChannel(ProtocolFamily var1) throws IOException;

   public abstract Pipe openPipe() throws IOException;

   public abstract AbstractSelector openSelector() throws IOException;

   public abstract ServerSocketChannel openServerSocketChannel() throws IOException;

   public abstract SocketChannel openSocketChannel() throws IOException;

   public Channel inheritedChannel() throws IOException {
      return null;
   }
}
