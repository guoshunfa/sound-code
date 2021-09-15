package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

final class KrbDelegationPermissionCollection extends PermissionCollection implements Serializable {
   private transient List<Permission> perms = new ArrayList();
   private static final long serialVersionUID = -3383936936589966948L;
   private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("permissions", Vector.class)};

   public KrbDelegationPermissionCollection() {
   }

   public boolean implies(Permission var1) {
      if (!(var1 instanceof DelegationPermission)) {
         return false;
      } else {
         synchronized(this) {
            Iterator var3 = this.perms.iterator();

            Permission var4;
            do {
               if (!var3.hasNext()) {
                  return false;
               }

               var4 = (Permission)var3.next();
            } while(!var4.implies(var1));

            return true;
         }
      }
   }

   public void add(Permission var1) {
      if (!(var1 instanceof DelegationPermission)) {
         throw new IllegalArgumentException("invalid permission: " + var1);
      } else if (this.isReadOnly()) {
         throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
      } else {
         synchronized(this) {
            this.perms.add(0, var1);
         }
      }
   }

   public Enumeration<Permission> elements() {
      synchronized(this) {
         return Collections.enumeration(this.perms);
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Vector var2 = new Vector(this.perms.size());
      synchronized(this) {
         var2.addAll(this.perms);
      }

      ObjectOutputStream.PutField var3 = var1.putFields();
      var3.put("permissions", var2);
      var1.writeFields();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      Vector var3 = (Vector)var2.get("permissions", (Object)null);
      this.perms = new ArrayList(var3.size());
      this.perms.addAll(var3);
   }
}
