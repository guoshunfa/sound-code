package java.lang;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FilePermission;
import java.net.InetAddress;
import java.net.SocketPermission;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.Security;
import java.security.SecurityPermission;
import java.util.PropertyPermission;
import java.util.StringTokenizer;
import sun.reflect.CallerSensitive;
import sun.security.util.SecurityConstants;

public class SecurityManager {
   /** @deprecated */
   @Deprecated
   protected boolean inCheck;
   private boolean initialized = false;
   private static ThreadGroup rootGroup = getRootGroup();
   private static boolean packageAccessValid = false;
   private static String[] packageAccess;
   private static final Object packageAccessLock = new Object();
   private static boolean packageDefinitionValid = false;
   private static String[] packageDefinition;
   private static final Object packageDefinitionLock = new Object();

   private boolean hasAllPermission() {
      try {
         this.checkPermission(SecurityConstants.ALL_PERMISSION);
         return true;
      } catch (SecurityException var2) {
         return false;
      }
   }

   /** @deprecated */
   @Deprecated
   public boolean getInCheck() {
      return this.inCheck;
   }

   public SecurityManager() {
      Class var1 = SecurityManager.class;
      synchronized(SecurityManager.class) {
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            var2.checkPermission(new RuntimePermission("createSecurityManager"));
         }

         this.initialized = true;
      }
   }

   protected native Class[] getClassContext();

   /** @deprecated */
   @Deprecated
   protected ClassLoader currentClassLoader() {
      ClassLoader var1 = this.currentClassLoader0();
      if (var1 != null && this.hasAllPermission()) {
         var1 = null;
      }

      return var1;
   }

   private native ClassLoader currentClassLoader0();

   /** @deprecated */
   @Deprecated
   protected Class<?> currentLoadedClass() {
      Class var1 = this.currentLoadedClass0();
      if (var1 != null && this.hasAllPermission()) {
         var1 = null;
      }

      return var1;
   }

   /** @deprecated */
   @Deprecated
   protected native int classDepth(String var1);

   /** @deprecated */
   @Deprecated
   protected int classLoaderDepth() {
      int var1 = this.classLoaderDepth0();
      if (var1 != -1) {
         if (this.hasAllPermission()) {
            var1 = -1;
         } else {
            --var1;
         }
      }

      return var1;
   }

   private native int classLoaderDepth0();

   /** @deprecated */
   @Deprecated
   protected boolean inClass(String var1) {
      return this.classDepth(var1) >= 0;
   }

   /** @deprecated */
   @Deprecated
   protected boolean inClassLoader() {
      return this.currentClassLoader() != null;
   }

   public Object getSecurityContext() {
      return AccessController.getContext();
   }

   public void checkPermission(Permission var1) {
      AccessController.checkPermission(var1);
   }

   public void checkPermission(Permission var1, Object var2) {
      if (var2 instanceof AccessControlContext) {
         ((AccessControlContext)var2).checkPermission(var1);
      } else {
         throw new SecurityException();
      }
   }

   public void checkCreateClassLoader() {
      this.checkPermission(SecurityConstants.CREATE_CLASSLOADER_PERMISSION);
   }

   private static ThreadGroup getRootGroup() {
      ThreadGroup var0;
      for(var0 = Thread.currentThread().getThreadGroup(); var0.getParent() != null; var0 = var0.getParent()) {
      }

      return var0;
   }

   public void checkAccess(Thread var1) {
      if (var1 == null) {
         throw new NullPointerException("thread can't be null");
      } else {
         if (var1.getThreadGroup() == rootGroup) {
            this.checkPermission(SecurityConstants.MODIFY_THREAD_PERMISSION);
         }

      }
   }

   public void checkAccess(ThreadGroup var1) {
      if (var1 == null) {
         throw new NullPointerException("thread group can't be null");
      } else {
         if (var1 == rootGroup) {
            this.checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
         }

      }
   }

   public void checkExit(int var1) {
      this.checkPermission(new RuntimePermission("exitVM." + var1));
   }

   public void checkExec(String var1) {
      File var2 = new File(var1);
      if (var2.isAbsolute()) {
         this.checkPermission(new FilePermission(var1, "execute"));
      } else {
         this.checkPermission(new FilePermission("<<ALL FILES>>", "execute"));
      }

   }

   public void checkLink(String var1) {
      if (var1 == null) {
         throw new NullPointerException("library can't be null");
      } else {
         this.checkPermission(new RuntimePermission("loadLibrary." + var1));
      }
   }

   public void checkRead(FileDescriptor var1) {
      if (var1 == null) {
         throw new NullPointerException("file descriptor can't be null");
      } else {
         this.checkPermission(new RuntimePermission("readFileDescriptor"));
      }
   }

   public void checkRead(String var1) {
      this.checkPermission(new FilePermission(var1, "read"));
   }

   public void checkRead(String var1, Object var2) {
      this.checkPermission(new FilePermission(var1, "read"), var2);
   }

   public void checkWrite(FileDescriptor var1) {
      if (var1 == null) {
         throw new NullPointerException("file descriptor can't be null");
      } else {
         this.checkPermission(new RuntimePermission("writeFileDescriptor"));
      }
   }

   public void checkWrite(String var1) {
      this.checkPermission(new FilePermission(var1, "write"));
   }

   public void checkDelete(String var1) {
      this.checkPermission(new FilePermission(var1, "delete"));
   }

   public void checkConnect(String var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException("host can't be null");
      } else {
         if (!var1.startsWith("[") && var1.indexOf(58) != -1) {
            var1 = "[" + var1 + "]";
         }

         if (var2 == -1) {
            this.checkPermission(new SocketPermission(var1, "resolve"));
         } else {
            this.checkPermission(new SocketPermission(var1 + ":" + var2, "connect"));
         }

      }
   }

   public void checkConnect(String var1, int var2, Object var3) {
      if (var1 == null) {
         throw new NullPointerException("host can't be null");
      } else {
         if (!var1.startsWith("[") && var1.indexOf(58) != -1) {
            var1 = "[" + var1 + "]";
         }

         if (var2 == -1) {
            this.checkPermission(new SocketPermission(var1, "resolve"), var3);
         } else {
            this.checkPermission(new SocketPermission(var1 + ":" + var2, "connect"), var3);
         }

      }
   }

   public void checkListen(int var1) {
      this.checkPermission(new SocketPermission("localhost:" + var1, "listen"));
   }

   public void checkAccept(String var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException("host can't be null");
      } else {
         if (!var1.startsWith("[") && var1.indexOf(58) != -1) {
            var1 = "[" + var1 + "]";
         }

         this.checkPermission(new SocketPermission(var1 + ":" + var2, "accept"));
      }
   }

   public void checkMulticast(InetAddress var1) {
      String var2 = var1.getHostAddress();
      if (!var2.startsWith("[") && var2.indexOf(58) != -1) {
         var2 = "[" + var2 + "]";
      }

      this.checkPermission(new SocketPermission(var2, "connect,accept"));
   }

   /** @deprecated */
   @Deprecated
   public void checkMulticast(InetAddress var1, byte var2) {
      String var3 = var1.getHostAddress();
      if (!var3.startsWith("[") && var3.indexOf(58) != -1) {
         var3 = "[" + var3 + "]";
      }

      this.checkPermission(new SocketPermission(var3, "connect,accept"));
   }

   public void checkPropertiesAccess() {
      this.checkPermission(new PropertyPermission("*", "read,write"));
   }

   public void checkPropertyAccess(String var1) {
      this.checkPermission(new PropertyPermission(var1, "read"));
   }

   /** @deprecated */
   @Deprecated
   public boolean checkTopLevelWindow(Object var1) {
      if (var1 == null) {
         throw new NullPointerException("window can't be null");
      } else {
         Object var2 = SecurityConstants.AWT.TOPLEVEL_WINDOW_PERMISSION;
         if (var2 == null) {
            var2 = SecurityConstants.ALL_PERMISSION;
         }

         try {
            this.checkPermission((Permission)var2);
            return true;
         } catch (SecurityException var4) {
            return false;
         }
      }
   }

   public void checkPrintJobAccess() {
      this.checkPermission(new RuntimePermission("queuePrintJob"));
   }

   /** @deprecated */
   @Deprecated
   public void checkSystemClipboardAccess() {
      Object var1 = SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION;
      if (var1 == null) {
         var1 = SecurityConstants.ALL_PERMISSION;
      }

      this.checkPermission((Permission)var1);
   }

   /** @deprecated */
   @Deprecated
   public void checkAwtEventQueueAccess() {
      Object var1 = SecurityConstants.AWT.CHECK_AWT_EVENTQUEUE_PERMISSION;
      if (var1 == null) {
         var1 = SecurityConstants.ALL_PERMISSION;
      }

      this.checkPermission((Permission)var1);
   }

   private static String[] getPackages(String var0) {
      String[] var1 = null;
      if (var0 != null && !var0.equals("")) {
         StringTokenizer var2 = new StringTokenizer(var0, ",");
         int var3 = var2.countTokens();
         if (var3 > 0) {
            var1 = new String[var3];

            String var5;
            for(int var4 = 0; var2.hasMoreElements(); var1[var4++] = var5) {
               var5 = var2.nextToken().trim();
            }
         }
      }

      if (var1 == null) {
         var1 = new String[0];
      }

      return var1;
   }

   public void checkPackageAccess(String var1) {
      if (var1 == null) {
         throw new NullPointerException("package name can't be null");
      } else {
         String[] var2;
         synchronized(packageAccessLock) {
            if (!packageAccessValid) {
               String var4 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
                  public String run() {
                     return Security.getProperty("package.access");
                  }
               });
               packageAccess = getPackages(var4);
               packageAccessValid = true;
            }

            var2 = packageAccess;
         }

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var1.startsWith(var2[var3]) || var2[var3].equals(var1 + ".")) {
               this.checkPermission(new RuntimePermission("accessClassInPackage." + var1));
               break;
            }
         }

      }
   }

   public void checkPackageDefinition(String var1) {
      if (var1 == null) {
         throw new NullPointerException("package name can't be null");
      } else {
         String[] var2;
         synchronized(packageDefinitionLock) {
            if (!packageDefinitionValid) {
               String var4 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
                  public String run() {
                     return Security.getProperty("package.definition");
                  }
               });
               packageDefinition = getPackages(var4);
               packageDefinitionValid = true;
            }

            var2 = packageDefinition;
         }

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var1.startsWith(var2[var3]) || var2[var3].equals(var1 + ".")) {
               this.checkPermission(new RuntimePermission("defineClassInPackage." + var1));
               break;
            }
         }

      }
   }

   public void checkSetFactory() {
      this.checkPermission(new RuntimePermission("setFactory"));
   }

   /** @deprecated */
   @Deprecated
   @CallerSensitive
   public void checkMemberAccess(Class<?> var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException("class can't be null");
      } else {
         if (var2 != 0) {
            Class[] var3 = this.getClassContext();
            if (var3.length < 4 || var3[3].getClassLoader() != var1.getClassLoader()) {
               this.checkPermission(SecurityConstants.CHECK_MEMBER_ACCESS_PERMISSION);
            }
         }

      }
   }

   public void checkSecurityAccess(String var1) {
      this.checkPermission(new SecurityPermission(var1));
   }

   private native Class<?> currentLoadedClass0();

   public ThreadGroup getThreadGroup() {
      return Thread.currentThread().getThreadGroup();
   }
}
