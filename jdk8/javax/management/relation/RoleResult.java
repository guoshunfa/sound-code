package javax.management.relation;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;

public class RoleResult implements Serializable {
   private static final long oldSerialVersionUID = 3786616013762091099L;
   private static final long newSerialVersionUID = -6304063118040985512L;
   private static final ObjectStreamField[] oldSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("myRoleList", RoleList.class), new ObjectStreamField("myRoleUnresList", RoleUnresolvedList.class)};
   private static final ObjectStreamField[] newSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("roleList", RoleList.class), new ObjectStreamField("unresolvedRoleList", RoleUnresolvedList.class)};
   private static final long serialVersionUID;
   private static final ObjectStreamField[] serialPersistentFields;
   private static boolean compat = false;
   private RoleList roleList = null;
   private RoleUnresolvedList unresolvedRoleList = null;

   public RoleResult(RoleList var1, RoleUnresolvedList var2) {
      this.setRoles(var1);
      this.setRolesUnresolved(var2);
   }

   public RoleList getRoles() {
      return this.roleList;
   }

   public RoleUnresolvedList getRolesUnresolved() {
      return this.unresolvedRoleList;
   }

   public void setRoles(RoleList var1) {
      if (var1 != null) {
         this.roleList = new RoleList();
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Role var3 = (Role)((Role)var2.next());
            this.roleList.add((Role)((Role)var3.clone()));
         }
      } else {
         this.roleList = null;
      }

   }

   public void setRolesUnresolved(RoleUnresolvedList var1) {
      if (var1 != null) {
         this.unresolvedRoleList = new RoleUnresolvedList();
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            RoleUnresolved var3 = (RoleUnresolved)((RoleUnresolved)var2.next());
            this.unresolvedRoleList.add((RoleUnresolved)((RoleUnresolved)var3.clone()));
         }
      } else {
         this.unresolvedRoleList = null;
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      if (compat) {
         ObjectInputStream.GetField var2 = var1.readFields();
         this.roleList = (RoleList)var2.get("myRoleList", (Object)null);
         if (var2.defaulted("myRoleList")) {
            throw new NullPointerException("myRoleList");
         }

         this.unresolvedRoleList = (RoleUnresolvedList)var2.get("myRoleUnresList", (Object)null);
         if (var2.defaulted("myRoleUnresList")) {
            throw new NullPointerException("myRoleUnresList");
         }
      } else {
         var1.defaultReadObject();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (compat) {
         ObjectOutputStream.PutField var2 = var1.putFields();
         var2.put("myRoleList", this.roleList);
         var2.put("myRoleUnresList", this.unresolvedRoleList);
         var1.writeFields();
      } else {
         var1.defaultWriteObject();
      }

   }

   static {
      try {
         GetPropertyAction var0 = new GetPropertyAction("jmx.serial.form");
         String var1 = (String)AccessController.doPrivileged((PrivilegedAction)var0);
         compat = var1 != null && var1.equals("1.0");
      } catch (Exception var2) {
      }

      if (compat) {
         serialPersistentFields = oldSerialPersistentFields;
         serialVersionUID = 3786616013762091099L;
      } else {
         serialPersistentFields = newSerialPersistentFields;
         serialVersionUID = -6304063118040985512L;
      }

   }
}
