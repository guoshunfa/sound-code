package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.StringTokenizer;

public final class DelegationPermission extends BasicPermission implements Serializable {
   private static final long serialVersionUID = 883133252142523922L;
   private transient String subordinate;
   private transient String service;

   public DelegationPermission(String var1) {
      super(var1);
      this.init(var1);
   }

   public DelegationPermission(String var1, String var2) {
      super(var1, var2);
      this.init(var1);
   }

   private void init(String var1) {
      StringTokenizer var2 = null;
      if (!var1.startsWith("\"")) {
         throw new IllegalArgumentException("service principal [" + var1 + "] syntax invalid: improperly quoted");
      } else {
         var2 = new StringTokenizer(var1, "\"", false);
         this.subordinate = var2.nextToken();
         if (var2.countTokens() == 2) {
            var2.nextToken();
            this.service = var2.nextToken();
         } else if (var2.countTokens() > 0) {
            throw new IllegalArgumentException("service principal [" + var2.nextToken() + "] syntax invalid: improperly quoted");
         }

      }
   }

   public boolean implies(Permission var1) {
      if (!(var1 instanceof DelegationPermission)) {
         return false;
      } else {
         DelegationPermission var2 = (DelegationPermission)var1;
         return this.subordinate.equals(var2.subordinate) && this.service.equals(var2.service);
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof DelegationPermission)) {
         return false;
      } else {
         DelegationPermission var2 = (DelegationPermission)var1;
         return this.implies(var2);
      }
   }

   public int hashCode() {
      return this.getName().hashCode();
   }

   public PermissionCollection newPermissionCollection() {
      return new KrbDelegationPermissionCollection();
   }

   private synchronized void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
   }

   private synchronized void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.init(this.getName());
   }
}
