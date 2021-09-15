package sun.security.provider;

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.util.Enumeration;
import java.util.Vector;

class PolicyPermissions extends PermissionCollection {
   private static final long serialVersionUID = -1954188373270545523L;
   private CodeSource codesource;
   private Permissions perms;
   private AuthPolicyFile policy;
   private boolean notInit;
   private Vector<Permission> additionalPerms;

   PolicyPermissions(AuthPolicyFile var1, CodeSource var2) {
      this.codesource = var2;
      this.policy = var1;
      this.perms = null;
      this.notInit = true;
      this.additionalPerms = null;
   }

   public void add(Permission var1) {
      if (this.isReadOnly()) {
         throw new SecurityException(AuthPolicyFile.rb.getString("attempt.to.add.a.Permission.to.a.readonly.PermissionCollection"));
      } else {
         if (this.perms == null) {
            if (this.additionalPerms == null) {
               this.additionalPerms = new Vector();
            }

            this.additionalPerms.add(var1);
         } else {
            this.perms.add(var1);
         }

      }
   }

   private synchronized void init() {
      if (this.notInit) {
         if (this.perms == null) {
            this.perms = new Permissions();
         }

         if (this.additionalPerms != null) {
            Enumeration var1 = this.additionalPerms.elements();

            while(var1.hasMoreElements()) {
               this.perms.add((Permission)var1.nextElement());
            }

            this.additionalPerms = null;
         }

         this.policy.getPermissions(this.perms, this.codesource);
         this.notInit = false;
      }

   }

   public boolean implies(Permission var1) {
      if (this.notInit) {
         this.init();
      }

      return this.perms.implies(var1);
   }

   public Enumeration<Permission> elements() {
      if (this.notInit) {
         this.init();
      }

      return this.perms.elements();
   }

   public String toString() {
      if (this.notInit) {
         this.init();
      }

      return this.perms.toString();
   }
}
