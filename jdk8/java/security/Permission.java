package java.security;

import java.io.Serializable;

public abstract class Permission implements Guard, Serializable {
   private static final long serialVersionUID = -5636570222231596674L;
   private String name;

   public Permission(String var1) {
      this.name = var1;
   }

   public void checkGuard(Object var1) throws SecurityException {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPermission(this);
      }

   }

   public abstract boolean implies(Permission var1);

   public abstract boolean equals(Object var1);

   public abstract int hashCode();

   public final String getName() {
      return this.name;
   }

   public abstract String getActions();

   public PermissionCollection newPermissionCollection() {
      return null;
   }

   public String toString() {
      String var1 = this.getActions();
      return var1 != null && var1.length() != 0 ? "(\"" + this.getClass().getName() + "\" \"" + this.name + "\" \"" + var1 + "\")" : "(\"" + this.getClass().getName() + "\" \"" + this.name + "\")";
   }
}
