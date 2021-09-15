package javax.security.auth;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.util.Objects;
import sun.security.util.Debug;
import sun.security.util.ResourcesMgr;

/** @deprecated */
@Deprecated
public abstract class Policy {
   private static Policy policy;
   private static final String AUTH_POLICY = "sun.security.provider.AuthPolicyFile";
   private final AccessControlContext acc = AccessController.getContext();
   private static boolean isCustomPolicy;

   protected Policy() {
   }

   public static Policy getPolicy() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(new AuthPermission("getPolicy"));
      }

      return getPolicyNoCheck();
   }

   static Policy getPolicyNoCheck() {
      if (policy == null) {
         Class var0 = Policy.class;
         synchronized(Policy.class) {
            if (policy == null) {
               final String var1 = null;
               var1 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
                  public String run() {
                     return Security.getProperty("auth.policy.provider");
                  }
               });
               if (var1 == null) {
                  var1 = "sun.security.provider.AuthPolicyFile";
               }

               try {
                  final Policy var3 = (Policy)AccessController.doPrivileged(new PrivilegedExceptionAction<Policy>() {
                     public Policy run() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
                        Class var1x = Class.forName(var1, false, Thread.currentThread().getContextClassLoader()).asSubclass(Policy.class);
                        return (Policy)var1x.newInstance();
                     }
                  });
                  AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                     public Void run() {
                        Policy.setPolicy(var3);
                        Policy.isCustomPolicy = !var1.equals("sun.security.provider.AuthPolicyFile");
                        return null;
                     }
                  }, (AccessControlContext)Objects.requireNonNull(var3.acc));
               } catch (Exception var5) {
                  throw new SecurityException(ResourcesMgr.getString("unable.to.instantiate.Subject.based.policy"));
               }
            }
         }
      }

      return policy;
   }

   public static void setPolicy(Policy var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new AuthPermission("setPolicy"));
      }

      policy = var0;
      isCustomPolicy = var0 != null;
   }

   static boolean isCustomPolicySet(Debug var0) {
      if (policy != null) {
         if (var0 != null && isCustomPolicy) {
            var0.println("Providing backwards compatibility for javax.security.auth.policy implementation: " + policy.toString());
         }

         return isCustomPolicy;
      } else {
         String var1 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
               return Security.getProperty("auth.policy.provider");
            }
         });
         if (var1 != null && !var1.equals("sun.security.provider.AuthPolicyFile")) {
            if (var0 != null) {
               var0.println("Providing backwards compatibility for javax.security.auth.policy implementation: " + var1);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public abstract PermissionCollection getPermissions(Subject var1, CodeSource var2);

   public abstract void refresh();
}
