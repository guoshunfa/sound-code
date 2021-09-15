package java.security;

import java.io.Serializable;
import java.util.Enumeration;
import sun.security.util.SecurityConstants;

final class AllPermissionCollection extends PermissionCollection implements Serializable {
   private static final long serialVersionUID = -4023755556366636806L;
   private boolean all_allowed = false;

   public AllPermissionCollection() {
   }

   public void add(Permission var1) {
      if (!(var1 instanceof AllPermission)) {
         throw new IllegalArgumentException("invalid permission: " + var1);
      } else if (this.isReadOnly()) {
         throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
      } else {
         this.all_allowed = true;
      }
   }

   public boolean implies(Permission var1) {
      return this.all_allowed;
   }

   public Enumeration<Permission> elements() {
      return new Enumeration<Permission>() {
         private boolean hasMore;

         {
            this.hasMore = AllPermissionCollection.this.all_allowed;
         }

         public boolean hasMoreElements() {
            return this.hasMore;
         }

         public Permission nextElement() {
            this.hasMore = false;
            return SecurityConstants.ALL_PERMISSION;
         }
      };
   }
}
