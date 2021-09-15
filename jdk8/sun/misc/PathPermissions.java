package sun.misc;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.PropertyPermission;
import sun.security.util.SecurityConstants;

class PathPermissions extends PermissionCollection {
   private static final long serialVersionUID = 8133287259134945693L;
   private File[] path;
   private Permissions perms;
   URL codeBase;

   PathPermissions(File[] var1) {
      this.path = var1;
      this.perms = null;
      this.codeBase = null;
   }

   URL getCodeBase() {
      return this.codeBase;
   }

   public void add(Permission var1) {
      throw new SecurityException("attempt to add a permission");
   }

   private synchronized void init() {
      if (this.perms == null) {
         this.perms = new Permissions();
         this.perms.add(SecurityConstants.CREATE_CLASSLOADER_PERMISSION);
         this.perms.add(new PropertyPermission("java.*", "read"));
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               for(int var1 = 0; var1 < PathPermissions.this.path.length; ++var1) {
                  File var2 = PathPermissions.this.path[var1];

                  String var3;
                  try {
                     var3 = var2.getCanonicalPath();
                  } catch (IOException var5) {
                     var3 = var2.getAbsolutePath();
                  }

                  if (var1 == 0) {
                     PathPermissions.this.codeBase = Launcher.getFileURL(new File(var3));
                  }

                  if (var2.isDirectory()) {
                     if (var3.endsWith(File.separator)) {
                        PathPermissions.this.perms.add(new FilePermission(var3 + "-", "read"));
                     } else {
                        PathPermissions.this.perms.add(new FilePermission(var3 + File.separator + "-", "read"));
                     }
                  } else {
                     int var4 = var3.lastIndexOf(File.separatorChar);
                     if (var4 != -1) {
                        var3 = var3.substring(0, var4 + 1) + "-";
                        PathPermissions.this.perms.add(new FilePermission(var3, "read"));
                     }
                  }
               }

               return null;
            }
         });
      }
   }

   public boolean implies(Permission var1) {
      if (this.perms == null) {
         this.init();
      }

      return this.perms.implies(var1);
   }

   public Enumeration<Permission> elements() {
      if (this.perms == null) {
         this.init();
      }

      synchronized(this.perms) {
         return this.perms.elements();
      }
   }

   public String toString() {
      if (this.perms == null) {
         this.init();
      }

      return this.perms.toString();
   }
}
