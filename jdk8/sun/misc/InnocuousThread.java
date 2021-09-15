package sun.misc;

import java.security.AccessControlContext;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;

public final class InnocuousThread extends Thread {
   private static final Unsafe UNSAFE;
   private static final ThreadGroup THREADGROUP;
   private static final AccessControlContext ACC;
   private static final long THREADLOCALS;
   private static final long INHERITABLETHREADLOCALS;
   private static final long INHERITEDACCESSCONTROLCONTEXT;
   private volatile boolean hasRun;

   public InnocuousThread(Runnable var1) {
      super(THREADGROUP, var1, "anInnocuousThread");
      UNSAFE.putOrderedObject(this, INHERITEDACCESSCONTROLCONTEXT, ACC);
      this.eraseThreadLocals();
   }

   public ClassLoader getContextClassLoader() {
      return ClassLoader.getSystemClassLoader();
   }

   public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler var1) {
   }

   public void setContextClassLoader(ClassLoader var1) {
      throw new SecurityException("setContextClassLoader");
   }

   public void run() {
      if (Thread.currentThread() == this && !this.hasRun) {
         this.hasRun = true;
         super.run();
      }

   }

   public void eraseThreadLocals() {
      UNSAFE.putObject(this, THREADLOCALS, (Object)null);
      UNSAFE.putObject(this, INHERITABLETHREADLOCALS, (Object)null);
   }

   static {
      try {
         ACC = new AccessControlContext(new ProtectionDomain[]{new ProtectionDomain((CodeSource)null, (PermissionCollection)null)});
         UNSAFE = Unsafe.getUnsafe();
         Class var0 = Thread.class;
         Class var1 = ThreadGroup.class;
         THREADLOCALS = UNSAFE.objectFieldOffset(var0.getDeclaredField("threadLocals"));
         INHERITABLETHREADLOCALS = UNSAFE.objectFieldOffset(var0.getDeclaredField("inheritableThreadLocals"));
         INHERITEDACCESSCONTROLCONTEXT = UNSAFE.objectFieldOffset(var0.getDeclaredField("inheritedAccessControlContext"));
         long var2 = UNSAFE.objectFieldOffset(var0.getDeclaredField("group"));
         long var4 = UNSAFE.objectFieldOffset(var1.getDeclaredField("parent"));

         ThreadGroup var6;
         ThreadGroup var7;
         for(var6 = (ThreadGroup)UNSAFE.getObject(Thread.currentThread(), var2); var6 != null; var6 = var7) {
            var7 = (ThreadGroup)UNSAFE.getObject(var6, var4);
            if (var7 == null) {
               break;
            }
         }

         THREADGROUP = new ThreadGroup(var6, "InnocuousThreadGroup");
      } catch (Exception var8) {
         throw new Error(var8);
      }
   }
}
