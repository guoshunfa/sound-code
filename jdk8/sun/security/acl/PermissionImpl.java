package sun.security.acl;

import java.security.acl.Permission;

public class PermissionImpl implements Permission {
   private String permission;

   public PermissionImpl(String var1) {
      this.permission = var1;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof Permission) {
         Permission var2 = (Permission)var1;
         return this.permission.equals(var2.toString());
      } else {
         return false;
      }
   }

   public String toString() {
      return this.permission;
   }

   public int hashCode() {
      return this.toString().hashCode();
   }
}
