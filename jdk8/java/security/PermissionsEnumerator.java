package java.security;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class PermissionsEnumerator implements Enumeration<Permission> {
   private Iterator<PermissionCollection> perms;
   private Enumeration<Permission> permset;

   PermissionsEnumerator(Iterator<PermissionCollection> var1) {
      this.perms = var1;
      this.permset = this.getNextEnumWithMore();
   }

   public boolean hasMoreElements() {
      if (this.permset == null) {
         return false;
      } else if (this.permset.hasMoreElements()) {
         return true;
      } else {
         this.permset = this.getNextEnumWithMore();
         return this.permset != null;
      }
   }

   public Permission nextElement() {
      if (this.hasMoreElements()) {
         return (Permission)this.permset.nextElement();
      } else {
         throw new NoSuchElementException("PermissionsEnumerator");
      }
   }

   private Enumeration<Permission> getNextEnumWithMore() {
      while(true) {
         if (this.perms.hasNext()) {
            PermissionCollection var1 = (PermissionCollection)this.perms.next();
            Enumeration var2 = var1.elements();
            if (!var2.hasMoreElements()) {
               continue;
            }

            return var2;
         }

         return null;
      }
   }
}
