package jdk.internal.instrumentation;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public final class Tracer {
   private final List<Tracer.InstrumentationData> items = new ArrayList();
   private static final Tracer singleton;

   private Tracer() {
   }

   @CallerSensitive
   public static Tracer getInstance() {
      Class var0 = Reflection.getCallerClass();
      if (!VM.isSystemDomainLoader(var0.getClassLoader())) {
         throw new SecurityException("Only classes in the system domain can get a Tracer instance");
      } else {
         return singleton;
      }
   }

   public synchronized void addInstrumentations(List<Class<?>> var1, Logger var2) throws ClassNotFoundException {
      if (var2 == null) {
         throw new IllegalArgumentException("logger can't be null");
      } else {
         ArrayList var3 = new ArrayList();
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            Class var5 = (Class)var4.next();
            InstrumentationTarget var6 = (InstrumentationTarget)var5.getAnnotation(InstrumentationTarget.class);
            Tracer.InstrumentationData var7 = new Tracer.InstrumentationData();
            var7.instrumentation = var5;
            var7.target = Class.forName(var6.value(), true, var5.getClassLoader());
            var7.logger = var2;
            var3.add(var7.target);
            this.items.add(var7);
         }

         retransformClasses0((Class[])var3.toArray(new Class[0]));
      }
   }

   private byte[] transform(Class<?> var1, byte[] var2) {
      byte[] var3 = var2;
      Iterator var4 = this.items.iterator();

      while(var4.hasNext()) {
         Tracer.InstrumentationData var5 = (Tracer.InstrumentationData)var4.next();
         if (var5.target.equals(var1)) {
            try {
               var5.logger.trace("Processing instrumentation class: " + var5.instrumentation);
               var3 = (new ClassInstrumentation(var5.instrumentation, var1.getName(), var3, var5.logger)).getNewBytes();
            } catch (Throwable var7) {
               var5.logger.error("Failure during class instrumentation:", var7);
            }
         }
      }

      if (var3 == var2) {
         return null;
      } else {
         return var3;
      }
   }

   private static native void retransformClasses0(Class<?>[] var0);

   private static byte[] retransformCallback(Class<?> var0, byte[] var1) {
      return singleton.transform(var0, var1);
   }

   private static native void init();

   static {
      AccessController.doPrivileged((PrivilegedAction)(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("bci");
            return null;
         }
      }), (AccessControlContext)null, new RuntimePermission("loadLibrary.bci"));
      singleton = new Tracer();
      init();
   }

   private final class InstrumentationData {
      Class<?> instrumentation;
      Class<?> target;
      Logger logger;

      private InstrumentationData() {
      }

      // $FF: synthetic method
      InstrumentationData(Object var2) {
         this();
      }
   }
}
