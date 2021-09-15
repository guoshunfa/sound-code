package jdk.management.resource.internal.inst;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.PropertyPermission;
import jdk.internal.instrumentation.Logger;
import jdk.internal.instrumentation.Tracer;
import sun.security.action.GetPropertyAction;

public final class InitInstrumentation implements Runnable {
   volatile boolean initialized = false;
   static final Class<?>[] hooks;

   public synchronized void run() {
      if (!this.initialized) {
         try {
            Tracer var1 = Tracer.getInstance();
            var1.addInstrumentations(Arrays.asList(hooks), InitInstrumentation.TestLogger.tlogger);
         } catch (ClassNotFoundException var2) {
            InitInstrumentation.TestLogger.tlogger.error("Unable to load class: " + var2.getMessage(), var2);
         } catch (Exception var3) {
            InitInstrumentation.TestLogger.tlogger.error("Unable to load class: " + var3.getMessage(), var3);
         }

         this.initialized = true;
      }

   }

   static {
      Class[] var0 = new Class[]{AbstractInterruptibleChannelRMHooks.class, AbstractPlainDatagramSocketImplRMHooks.class, AbstractPlainSocketImplRMHooks.class, AsynchronousServerSocketChannelImplRMHooks.class, AsynchronousSocketChannelImplRMHooks.class, BaseSSLSocketImplRMHooks.class, DatagramChannelImplRMHooks.class, DatagramDispatcherRMHooks.class, DatagramSocketRMHooks.class, FileChannelImplRMHooks.class, FileInputStreamRMHooks.class, FileOutputStreamRMHooks.class, NetRMHooks.class, RandomAccessFileRMHooks.class, ServerSocketRMHooks.class, ServerSocketChannelImplRMHooks.class, SocketChannelImplRMHooks.class, SocketDispatcherRMHooks.class, SocketInputStreamRMHooks.class, SocketOutputStreamRMHooks.class, SocketRMHooks.class, SSLSocketImplRMHooks.class, SSLServerSocketImplRMHooks.class, ThreadRMHooks.class, WrapInstrumentationRMHooks.class};
      String var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("os.name")), (AccessControlContext)null, new PropertyPermission("os.name", "read"));
      Class[] var1;
      if (var2.startsWith("Windows")) {
         var1 = new Class[]{WindowsAsynchronousFileChannelImplRMHooks.class, WindowsAsynchronousServerSocketChannelImplRMHooks.class, WindowsAsynchronousSocketChannelImplRMHooks.class};
      } else {
         var1 = new Class[]{SimpleAsynchronousFileChannelImplRMHooks.class, UnixAsynchronousServerSocketChannelImplRMHooks.class, UnixAsynchronousSocketChannelImplRMHooks.class};
      }

      hooks = new Class[var0.length + var1.length];
      System.arraycopy(var0, 0, hooks, 0, var0.length);
      System.arraycopy(var1, 0, hooks, var0.length, var1.length);
   }

   static class TestLogger implements Logger {
      static final InitInstrumentation.TestLogger tlogger = new InitInstrumentation.TestLogger();

      public void debug(String var1) {
         System.out.printf("TestLogger debug: %s%n", var1);
      }

      public void error(String var1) {
         System.out.printf("TestLogger error: %s%n", var1);
      }

      public void error(String var1, Throwable var2) {
         System.out.printf("TestLogger error: %s, ex: %s%n", var1, var2);
         var2.printStackTrace();
      }

      public void info(String var1) {
         System.out.printf("TestLogger info: %s%n", var1);
      }

      public void trace(String var1) {
      }

      public void warn(String var1) {
         System.out.printf("TestLogger warning: %s%n", var1);
      }
   }
}
