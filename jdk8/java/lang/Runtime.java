package java.lang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public class Runtime {
   private static Runtime currentRuntime = new Runtime();

   public static Runtime getRuntime() {
      return currentRuntime;
   }

   private Runtime() {
   }

   public void exit(int var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkExit(var1);
      }

      Shutdown.exit(var1);
   }

   public void addShutdownHook(Thread var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPermission(new RuntimePermission("shutdownHooks"));
      }

      ApplicationShutdownHooks.add(var1);
   }

   public boolean removeShutdownHook(Thread var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPermission(new RuntimePermission("shutdownHooks"));
      }

      return ApplicationShutdownHooks.remove(var1);
   }

   public void halt(int var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkExit(var1);
      }

      Shutdown.halt(var1);
   }

   /** @deprecated */
   @Deprecated
   public static void runFinalizersOnExit(boolean var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         try {
            var1.checkExit(0);
         } catch (SecurityException var3) {
            throw new SecurityException("runFinalizersOnExit");
         }
      }

      Shutdown.setRunFinalizersOnExit(var0);
   }

   public Process exec(String var1) throws IOException {
      return this.exec((String)var1, (String[])null, (File)null);
   }

   public Process exec(String var1, String[] var2) throws IOException {
      return this.exec((String)var1, var2, (File)null);
   }

   public Process exec(String var1, String[] var2, File var3) throws IOException {
      if (var1.length() == 0) {
         throw new IllegalArgumentException("Empty command");
      } else {
         StringTokenizer var4 = new StringTokenizer(var1);
         String[] var5 = new String[var4.countTokens()];

         for(int var6 = 0; var4.hasMoreTokens(); ++var6) {
            var5[var6] = var4.nextToken();
         }

         return this.exec(var5, var2, var3);
      }
   }

   public Process exec(String[] var1) throws IOException {
      return this.exec((String[])var1, (String[])null, (File)null);
   }

   public Process exec(String[] var1, String[] var2) throws IOException {
      return this.exec((String[])var1, var2, (File)null);
   }

   public Process exec(String[] var1, String[] var2, File var3) throws IOException {
      return (new ProcessBuilder(var1)).environment(var2).directory(var3).start();
   }

   public native int availableProcessors();

   public native long freeMemory();

   public native long totalMemory();

   public native long maxMemory();

   public native void gc();

   private static native void runFinalization0();

   public void runFinalization() {
      runFinalization0();
   }

   public native void traceInstructions(boolean var1);

   public native void traceMethodCalls(boolean var1);

   @CallerSensitive
   public void load(String var1) {
      this.load0(Reflection.getCallerClass(), var1);
   }

   synchronized void load0(Class<?> var1, String var2) {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkLink(var2);
      }

      if (!(new File(var2)).isAbsolute()) {
         throw new UnsatisfiedLinkError("Expecting an absolute path of the library: " + var2);
      } else {
         ClassLoader.loadLibrary(var1, var2, true);
      }
   }

   @CallerSensitive
   public void loadLibrary(String var1) {
      this.loadLibrary0(Reflection.getCallerClass(), var1);
   }

   synchronized void loadLibrary0(Class<?> var1, String var2) {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkLink(var2);
      }

      if (var2.indexOf(File.separatorChar) != -1) {
         throw new UnsatisfiedLinkError("Directory separator should not appear in library name: " + var2);
      } else {
         ClassLoader.loadLibrary(var1, var2, false);
      }
   }

   /** @deprecated */
   @Deprecated
   public InputStream getLocalizedInputStream(InputStream var1) {
      return var1;
   }

   /** @deprecated */
   @Deprecated
   public OutputStream getLocalizedOutputStream(OutputStream var1) {
      return var1;
   }
}
