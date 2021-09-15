package java.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.WeakHashMap;
import sun.misc.JavaSecurityAccess;
import sun.misc.JavaSecurityProtectionDomainAccess;
import sun.misc.SharedSecrets;
import sun.security.util.Debug;
import sun.security.util.SecurityConstants;

public class ProtectionDomain {
   private CodeSource codesource;
   private ClassLoader classloader;
   private Principal[] principals;
   private PermissionCollection permissions;
   private boolean hasAllPerm = false;
   private boolean staticPermissions;
   final ProtectionDomain.Key key = new ProtectionDomain.Key();
   private static final Debug debug;

   public ProtectionDomain(CodeSource var1, PermissionCollection var2) {
      this.codesource = var1;
      if (var2 != null) {
         this.permissions = var2;
         this.permissions.setReadOnly();
         if (var2 instanceof Permissions && ((Permissions)var2).allPermission != null) {
            this.hasAllPerm = true;
         }
      }

      this.classloader = null;
      this.principals = new Principal[0];
      this.staticPermissions = true;
   }

   public ProtectionDomain(CodeSource var1, PermissionCollection var2, ClassLoader var3, Principal[] var4) {
      this.codesource = var1;
      if (var2 != null) {
         this.permissions = var2;
         this.permissions.setReadOnly();
         if (var2 instanceof Permissions && ((Permissions)var2).allPermission != null) {
            this.hasAllPerm = true;
         }
      }

      this.classloader = var3;
      this.principals = var4 != null ? (Principal[])var4.clone() : new Principal[0];
      this.staticPermissions = false;
   }

   public final CodeSource getCodeSource() {
      return this.codesource;
   }

   public final ClassLoader getClassLoader() {
      return this.classloader;
   }

   public final Principal[] getPrincipals() {
      return (Principal[])this.principals.clone();
   }

   public final PermissionCollection getPermissions() {
      return this.permissions;
   }

   public boolean implies(Permission var1) {
      if (this.hasAllPerm) {
         return true;
      } else if (!this.staticPermissions && Policy.getPolicyNoCheck().implies(this, var1)) {
         return true;
      } else {
         return this.permissions != null ? this.permissions.implies(var1) : false;
      }
   }

   boolean impliesCreateAccessControlContext() {
      return this.implies(SecurityConstants.CREATE_ACC_PERMISSION);
   }

   public String toString() {
      String var1 = "<no principals>";
      if (this.principals != null && this.principals.length > 0) {
         StringBuilder var2 = new StringBuilder("(principals ");

         for(int var3 = 0; var3 < this.principals.length; ++var3) {
            var2.append(this.principals[var3].getClass().getName() + " \"" + this.principals[var3].getName() + "\"");
            if (var3 < this.principals.length - 1) {
               var2.append(",\n");
            } else {
               var2.append(")\n");
            }
         }

         var1 = var2.toString();
      }

      PermissionCollection var4 = Policy.isSet() && seeAllp() ? this.mergePermissions() : this.getPermissions();
      return "ProtectionDomain  " + this.codesource + "\n " + this.classloader + "\n " + var1 + "\n " + var4 + "\n";
   }

   private static boolean seeAllp() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 == null) {
         return true;
      } else {
         if (debug != null) {
            if (var0.getClass().getClassLoader() == null && Policy.getPolicyNoCheck().getClass().getClassLoader() == null) {
               return true;
            }
         } else {
            try {
               var0.checkPermission(SecurityConstants.GET_POLICY_PERMISSION);
               return true;
            } catch (SecurityException var2) {
            }
         }

         return false;
      }
   }

   private PermissionCollection mergePermissions() {
      if (this.staticPermissions) {
         return this.permissions;
      } else {
         PermissionCollection var1 = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction<PermissionCollection>() {
            public PermissionCollection run() {
               Policy var1 = Policy.getPolicyNoCheck();
               return var1.getPermissions(ProtectionDomain.this);
            }
         });
         Permissions var2 = new Permissions();
         byte var3 = 32;
         int var4 = 8;
         ArrayList var6 = new ArrayList(var4);
         ArrayList var7 = new ArrayList(var3);
         Enumeration var5;
         if (this.permissions != null) {
            synchronized(this.permissions) {
               var5 = this.permissions.elements();

               while(var5.hasMoreElements()) {
                  var6.add(var5.nextElement());
               }
            }
         }

         if (var1 != null) {
            synchronized(var1) {
               for(var5 = var1.elements(); var5.hasMoreElements(); ++var4) {
                  var7.add(var5.nextElement());
               }
            }
         }

         if (var1 != null && this.permissions != null) {
            synchronized(this.permissions) {
               var5 = this.permissions.elements();

               label83:
               while(true) {
                  while(true) {
                     if (!var5.hasMoreElements()) {
                        break label83;
                     }

                     Permission var9 = (Permission)var5.nextElement();
                     Class var10 = var9.getClass();
                     String var11 = var9.getActions();
                     String var12 = var9.getName();

                     for(int var13 = 0; var13 < var7.size(); ++var13) {
                        Permission var14 = (Permission)var7.get(var13);
                        if (var10.isInstance(var14) && var12.equals(var14.getName()) && var11.equals(var14.getActions())) {
                           var7.remove(var13);
                           break;
                        }
                     }
                  }
               }
            }
         }

         int var8;
         if (var1 != null) {
            for(var8 = var7.size() - 1; var8 >= 0; --var8) {
               var2.add((Permission)var7.get(var8));
            }
         }

         if (this.permissions != null) {
            for(var8 = var6.size() - 1; var8 >= 0; --var8) {
               var2.add((Permission)var6.get(var8));
            }
         }

         return var2;
      }
   }

   static {
      SharedSecrets.setJavaSecurityAccess(new ProtectionDomain.JavaSecurityAccessImpl());
      debug = Debug.getInstance("domain");
      SharedSecrets.setJavaSecurityProtectionDomainAccess(new JavaSecurityProtectionDomainAccess() {
         public JavaSecurityProtectionDomainAccess.ProtectionDomainCache getProtectionDomainCache() {
            return new JavaSecurityProtectionDomainAccess.ProtectionDomainCache() {
               private final Map<ProtectionDomain.Key, PermissionCollection> map = Collections.synchronizedMap(new WeakHashMap());

               public void put(ProtectionDomain var1, PermissionCollection var2) {
                  this.map.put(var1 == null ? null : var1.key, var2);
               }

               public PermissionCollection get(ProtectionDomain var1) {
                  return var1 == null ? (PermissionCollection)this.map.get((Object)null) : (PermissionCollection)this.map.get(var1.key);
               }
            };
         }

         public boolean getStaticPermissionsField(ProtectionDomain var1) {
            return var1.staticPermissions;
         }
      });
   }

   final class Key {
   }

   private static class JavaSecurityAccessImpl implements JavaSecurityAccess {
      private JavaSecurityAccessImpl() {
      }

      public <T> T doIntersectionPrivilege(PrivilegedAction<T> var1, AccessControlContext var2, AccessControlContext var3) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            return AccessController.doPrivileged(var1, getCombinedACC(var3, var2));
         }
      }

      public <T> T doIntersectionPrivilege(PrivilegedAction<T> var1, AccessControlContext var2) {
         return this.doIntersectionPrivilege(var1, AccessController.getContext(), var2);
      }

      private static AccessControlContext getCombinedACC(AccessControlContext var0, AccessControlContext var1) {
         AccessControlContext var2 = new AccessControlContext(var0, var1.getCombiner(), true);
         return (new AccessControlContext(var1.getContext(), var2)).optimize();
      }

      // $FF: synthetic method
      JavaSecurityAccessImpl(Object var1) {
         this();
      }
   }
}
