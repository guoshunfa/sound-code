package java.security;

import java.util.Enumeration;

/** @deprecated */
@Deprecated
public abstract class IdentityScope extends Identity {
   private static final long serialVersionUID = -2337346281189773310L;
   private static IdentityScope scope;

   private static void initializeSystemScope() {
      String var0 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return Security.getProperty("system.scope");
         }
      });
      if (var0 != null) {
         try {
            Class.forName(var0);
         } catch (ClassNotFoundException var2) {
            var2.printStackTrace();
         }

      }
   }

   protected IdentityScope() {
      this("restoring...");
   }

   public IdentityScope(String var1) {
      super(var1);
   }

   public IdentityScope(String var1, IdentityScope var2) throws KeyManagementException {
      super(var1, var2);
   }

   public static IdentityScope getSystemScope() {
      if (scope == null) {
         initializeSystemScope();
      }

      return scope;
   }

   protected static void setSystemScope(IdentityScope var0) {
      check("setSystemScope");
      scope = var0;
   }

   public abstract int size();

   public abstract Identity getIdentity(String var1);

   public Identity getIdentity(Principal var1) {
      return this.getIdentity(var1.getName());
   }

   public abstract Identity getIdentity(PublicKey var1);

   public abstract void addIdentity(Identity var1) throws KeyManagementException;

   public abstract void removeIdentity(Identity var1) throws KeyManagementException;

   public abstract Enumeration<Identity> identities();

   public String toString() {
      return super.toString() + "[" + this.size() + "]";
   }

   private static void check(String var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkSecurityAccess(var0);
      }

   }
}
