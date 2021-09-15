package javax.management;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

class MBeanServerPermissionCollection extends PermissionCollection {
   private MBeanServerPermission collectionPermission;
   private static final long serialVersionUID = -5661980843569388590L;

   public synchronized void add(Permission var1) {
      if (!(var1 instanceof MBeanServerPermission)) {
         String var4 = "Permission not an MBeanServerPermission: " + var1;
         throw new IllegalArgumentException(var4);
      } else if (this.isReadOnly()) {
         throw new SecurityException("Read-only permission collection");
      } else {
         MBeanServerPermission var2 = (MBeanServerPermission)var1;
         if (this.collectionPermission == null) {
            this.collectionPermission = var2;
         } else if (!this.collectionPermission.implies(var1)) {
            int var3 = this.collectionPermission.mask | var2.mask;
            this.collectionPermission = new MBeanServerPermission(var3);
         }

      }
   }

   public synchronized boolean implies(Permission var1) {
      return this.collectionPermission != null && this.collectionPermission.implies(var1);
   }

   public synchronized Enumeration<Permission> elements() {
      Set var1;
      if (this.collectionPermission == null) {
         var1 = Collections.emptySet();
      } else {
         var1 = Collections.singleton(this.collectionPermission);
      }

      return Collections.enumeration(var1);
   }
}
