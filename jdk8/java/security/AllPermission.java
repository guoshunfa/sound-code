package java.security;

public final class AllPermission extends Permission {
   private static final long serialVersionUID = -2916474571451318075L;

   public AllPermission() {
      super("<all permissions>");
   }

   public AllPermission(String var1, String var2) {
      this();
   }

   public boolean implies(Permission var1) {
      return true;
   }

   public boolean equals(Object var1) {
      return var1 instanceof AllPermission;
   }

   public int hashCode() {
      return 1;
   }

   public String getActions() {
      return "<all actions>";
   }

   public PermissionCollection newPermissionCollection() {
      return new AllPermissionCollection();
   }
}
