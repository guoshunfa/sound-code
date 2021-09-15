package sun.applet;

import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import sun.awt.AWTSecurityManager;
import sun.awt.AppContext;
import sun.security.util.SecurityConstants;

public class AppletSecurity extends AWTSecurityManager {
   private static Field facc = null;
   private static Field fcontext = null;
   private HashSet restrictedPackages = new HashSet();
   private boolean inThreadGroupCheck = false;

   public AppletSecurity() {
      this.reset();
   }

   public void reset() {
      this.restrictedPackages.clear();
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            Enumeration var1 = System.getProperties().propertyNames();

            while(var1.hasMoreElements()) {
               String var2 = (String)var1.nextElement();
               if (var2 != null && var2.startsWith("package.restrict.access.")) {
                  String var3 = System.getProperty(var2);
                  if (var3 != null && var3.equalsIgnoreCase("true")) {
                     String var4 = var2.substring(24);
                     AppletSecurity.this.restrictedPackages.add(var4);
                  }
               }
            }

            return null;
         }
      });
   }

   private AppletClassLoader currentAppletClassLoader() {
      ClassLoader var1 = this.currentClassLoader();
      if (var1 != null && !(var1 instanceof AppletClassLoader)) {
         Class[] var2 = this.getClassContext();

         int var3;
         for(var3 = 0; var3 < var2.length; ++var3) {
            var1 = var2[var3].getClassLoader();
            if (var1 instanceof AppletClassLoader) {
               return (AppletClassLoader)var1;
            }
         }

         for(var3 = 0; var3 < var2.length; ++var3) {
            final ClassLoader var4 = var2[var3].getClassLoader();
            if (var4 instanceof URLClassLoader) {
               var1 = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
                  public Object run() {
                     AccessControlContext var1 = null;
                     ProtectionDomain[] var2 = null;

                     try {
                        var1 = (AccessControlContext)AppletSecurity.facc.get(var4);
                        if (var1 == null) {
                           return null;
                        }

                        var2 = (ProtectionDomain[])((ProtectionDomain[])AppletSecurity.fcontext.get(var1));
                        if (var2 == null) {
                           return null;
                        }
                     } catch (Exception var5) {
                        throw new UnsupportedOperationException(var5);
                     }

                     for(int var3 = 0; var3 < var2.length; ++var3) {
                        ClassLoader var4x = var2[var3].getClassLoader();
                        if (var4x instanceof AppletClassLoader) {
                           return var4x;
                        }
                     }

                     return null;
                  }
               });
               if (var1 != null) {
                  return (AppletClassLoader)var1;
               }
            }
         }

         var1 = Thread.currentThread().getContextClassLoader();
         if (var1 instanceof AppletClassLoader) {
            return (AppletClassLoader)var1;
         } else {
            return (AppletClassLoader)null;
         }
      } else {
         return (AppletClassLoader)var1;
      }
   }

   protected boolean inThreadGroup(ThreadGroup var1) {
      return this.currentAppletClassLoader() == null ? false : this.getThreadGroup().parentOf(var1);
   }

   protected boolean inThreadGroup(Thread var1) {
      return this.inThreadGroup(var1.getThreadGroup());
   }

   public void checkAccess(Thread var1) {
      if (var1.getState() != Thread.State.TERMINATED && !this.inThreadGroup(var1)) {
         this.checkPermission(SecurityConstants.MODIFY_THREAD_PERMISSION);
      }

   }

   public synchronized void checkAccess(ThreadGroup var1) {
      if (this.inThreadGroupCheck) {
         this.checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
      } else {
         try {
            this.inThreadGroupCheck = true;
            if (!this.inThreadGroup(var1)) {
               this.checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
            }
         } finally {
            this.inThreadGroupCheck = false;
         }
      }

   }

   public void checkPackageAccess(String var1) {
      super.checkPackageAccess(var1);
      Iterator var2 = this.restrictedPackages.iterator();

      while(true) {
         String var3;
         do {
            if (!var2.hasNext()) {
               return;
            }

            var3 = (String)var2.next();
         } while(!var1.equals(var3) && !var1.startsWith(var3 + "."));

         this.checkPermission(new RuntimePermission("accessClassInPackage." + var1));
      }
   }

   public void checkAwtEventQueueAccess() {
      AppContext var1 = AppContext.getAppContext();
      AppletClassLoader var2 = this.currentAppletClassLoader();
      if (AppContext.isMainContext(var1) && var2 != null) {
         super.checkPermission(SecurityConstants.AWT.CHECK_AWT_EVENTQUEUE_PERMISSION);
      }

   }

   public ThreadGroup getThreadGroup() {
      AppletClassLoader var1 = this.currentAppletClassLoader();
      ThreadGroup var2 = var1 == null ? null : var1.getThreadGroup();
      return var2 != null ? var2 : super.getThreadGroup();
   }

   public AppContext getAppContext() {
      AppletClassLoader var1 = this.currentAppletClassLoader();
      if (var1 == null) {
         return null;
      } else {
         AppContext var2 = var1.getAppContext();
         if (var2 == null) {
            throw new SecurityException("Applet classloader has invalid AppContext");
         } else {
            return var2;
         }
      }
   }

   static {
      try {
         facc = URLClassLoader.class.getDeclaredField("acc");
         facc.setAccessible(true);
         fcontext = AccessControlContext.class.getDeclaredField("context");
         fcontext.setAccessible(true);
      } catch (NoSuchFieldException var1) {
         throw new UnsupportedOperationException(var1);
      }
   }
}
