package java.security;

public class AccessControlException extends SecurityException {
   private static final long serialVersionUID = 5138225684096988535L;
   private Permission perm;

   public AccessControlException(String var1) {
      super(var1);
   }

   public AccessControlException(String var1, Permission var2) {
      super(var1);
      this.perm = var2;
   }

   public Permission getPermission() {
      return this.perm;
   }
}
