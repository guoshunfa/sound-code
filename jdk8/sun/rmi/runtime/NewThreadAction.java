package sun.rmi.runtime;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.util.SecurityConstants;

public final class NewThreadAction implements PrivilegedAction<Thread> {
   static final ThreadGroup systemThreadGroup = (ThreadGroup)AccessController.doPrivileged(new PrivilegedAction<ThreadGroup>() {
      public ThreadGroup run() {
         ThreadGroup var1;
         ThreadGroup var2;
         for(var1 = Thread.currentThread().getThreadGroup(); (var2 = var1.getParent()) != null; var1 = var2) {
         }

         return var1;
      }
   });
   static final ThreadGroup userThreadGroup = (ThreadGroup)AccessController.doPrivileged(new PrivilegedAction<ThreadGroup>() {
      public ThreadGroup run() {
         return new ThreadGroup(NewThreadAction.systemThreadGroup, "RMI Runtime");
      }
   });
   private final ThreadGroup group;
   private final Runnable runnable;
   private final String name;
   private final boolean daemon;

   NewThreadAction(ThreadGroup var1, Runnable var2, String var3, boolean var4) {
      this.group = var1;
      this.runnable = var2;
      this.name = var3;
      this.daemon = var4;
   }

   public NewThreadAction(Runnable var1, String var2, boolean var3) {
      this(systemThreadGroup, var1, var2, var3);
   }

   public NewThreadAction(Runnable var1, String var2, boolean var3, boolean var4) {
      this(var4 ? userThreadGroup : systemThreadGroup, var1, var2, var3);
   }

   public Thread run() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
      }

      Thread var2 = new Thread(this.group, this.runnable, "RMI " + this.name);
      var2.setContextClassLoader(ClassLoader.getSystemClassLoader());
      var2.setDaemon(this.daemon);
      return var2;
   }
}
