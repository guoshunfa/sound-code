package sun.security.acl;

import java.security.Principal;
import java.security.acl.AclEntry;
import java.security.acl.Group;
import java.security.acl.Permission;
import java.util.Enumeration;
import java.util.Vector;

public class AclEntryImpl implements AclEntry {
   private Principal user = null;
   private Vector<Permission> permissionSet = new Vector(10, 10);
   private boolean negative = false;

   public AclEntryImpl(Principal var1) {
      this.user = var1;
   }

   public AclEntryImpl() {
   }

   public boolean setPrincipal(Principal var1) {
      if (this.user != null) {
         return false;
      } else {
         this.user = var1;
         return true;
      }
   }

   public void setNegativePermissions() {
      this.negative = true;
   }

   public boolean isNegative() {
      return this.negative;
   }

   public boolean addPermission(Permission var1) {
      if (this.permissionSet.contains(var1)) {
         return false;
      } else {
         this.permissionSet.addElement(var1);
         return true;
      }
   }

   public boolean removePermission(Permission var1) {
      return this.permissionSet.removeElement(var1);
   }

   public boolean checkPermission(Permission var1) {
      return this.permissionSet.contains(var1);
   }

   public Enumeration<Permission> permissions() {
      return this.permissionSet.elements();
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      if (this.negative) {
         var1.append("-");
      } else {
         var1.append("+");
      }

      if (this.user instanceof Group) {
         var1.append("Group.");
      } else {
         var1.append("User.");
      }

      var1.append(this.user + "=");
      Enumeration var2 = this.permissions();

      while(var2.hasMoreElements()) {
         Permission var3 = (Permission)var2.nextElement();
         var1.append((Object)var3);
         if (var2.hasMoreElements()) {
            var1.append(",");
         }
      }

      return new String(var1);
   }

   public synchronized Object clone() {
      AclEntryImpl var1 = new AclEntryImpl(this.user);
      var1.permissionSet = (Vector)this.permissionSet.clone();
      var1.negative = this.negative;
      return var1;
   }

   public Principal getPrincipal() {
      return this.user;
   }
}
