package java.lang;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class Compiler {
   private Compiler() {
   }

   private static native void initialize();

   private static native void registerNatives();

   public static native boolean compileClass(Class<?> var0);

   public static native boolean compileClasses(String var0);

   public static native Object command(Object var0);

   public static native void enable();

   public static native void disable();

   static {
      registerNatives();
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            boolean var1 = false;
            String var2 = System.getProperty("java.compiler");
            if (var2 != null && !var2.equals("NONE") && !var2.equals("")) {
               try {
                  System.loadLibrary(var2);
                  Compiler.initialize();
                  var1 = true;
               } catch (UnsatisfiedLinkError var4) {
                  System.err.println("Warning: JIT compiler \"" + var2 + "\" not found. Will use interpreter.");
               }
            }

            String var3 = System.getProperty("java.vm.info");
            if (var1) {
               System.setProperty("java.vm.info", var3 + ", " + var2);
            } else {
               System.setProperty("java.vm.info", var3 + ", nojit");
            }

            return null;
         }
      });
   }
}
