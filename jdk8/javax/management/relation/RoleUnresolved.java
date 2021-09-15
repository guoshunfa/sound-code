package javax.management.relation;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.management.ObjectName;

public class RoleUnresolved implements Serializable {
   private static final long oldSerialVersionUID = -9026457686611660144L;
   private static final long newSerialVersionUID = -48350262537070138L;
   private static final ObjectStreamField[] oldSerialPersistentFields;
   private static final ObjectStreamField[] newSerialPersistentFields;
   private static final long serialVersionUID;
   private static final ObjectStreamField[] serialPersistentFields;
   private static boolean compat;
   private String roleName = null;
   private List<ObjectName> roleValue = null;
   private int problemType;

   public RoleUnresolved(String var1, List<ObjectName> var2, int var3) throws IllegalArgumentException {
      if (var1 == null) {
         String var4 = "Invalid parameter.";
         throw new IllegalArgumentException(var4);
      } else {
         this.setRoleName(var1);
         this.setRoleValue(var2);
         this.setProblemType(var3);
      }
   }

   public String getRoleName() {
      return this.roleName;
   }

   public List<ObjectName> getRoleValue() {
      return this.roleValue;
   }

   public int getProblemType() {
      return this.problemType;
   }

   public void setRoleName(String var1) throws IllegalArgumentException {
      if (var1 == null) {
         String var2 = "Invalid parameter.";
         throw new IllegalArgumentException(var2);
      } else {
         this.roleName = var1;
      }
   }

   public void setRoleValue(List<ObjectName> var1) {
      if (var1 != null) {
         this.roleValue = new ArrayList(var1);
      } else {
         this.roleValue = null;
      }

   }

   public void setProblemType(int var1) throws IllegalArgumentException {
      if (!RoleStatus.isRoleStatus(var1)) {
         String var2 = "Incorrect problem type.";
         throw new IllegalArgumentException(var2);
      } else {
         this.problemType = var1;
      }
   }

   public Object clone() {
      try {
         return new RoleUnresolved(this.roleName, this.roleValue, this.problemType);
      } catch (IllegalArgumentException var2) {
         return null;
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("role name: " + this.roleName);
      if (this.roleValue != null) {
         var1.append("; value: ");
         Iterator var2 = this.roleValue.iterator();

         while(var2.hasNext()) {
            ObjectName var3 = (ObjectName)var2.next();
            var1.append(var3.toString());
            if (var2.hasNext()) {
               var1.append(", ");
            }
         }
      }

      var1.append("; problem type: " + this.problemType);
      return var1.toString();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      if (compat) {
         ObjectInputStream.GetField var2 = var1.readFields();
         this.roleName = (String)var2.get("myRoleName", (Object)null);
         if (var2.defaulted("myRoleName")) {
            throw new NullPointerException("myRoleName");
         }

         this.roleValue = (List)Util.cast(var2.get("myRoleValue", (Object)null));
         if (var2.defaulted("myRoleValue")) {
            throw new NullPointerException("myRoleValue");
         }

         this.problemType = var2.get("myPbType", (int)0);
         if (var2.defaulted("myPbType")) {
            throw new NullPointerException("myPbType");
         }
      } else {
         var1.defaultReadObject();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (compat) {
         ObjectOutputStream.PutField var2 = var1.putFields();
         var2.put("myRoleName", this.roleName);
         var2.put("myRoleValue", this.roleValue);
         var2.put("myPbType", this.problemType);
         var1.writeFields();
      } else {
         var1.defaultWriteObject();
      }

   }

   static {
      oldSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("myRoleName", String.class), new ObjectStreamField("myRoleValue", ArrayList.class), new ObjectStreamField("myPbType", Integer.TYPE)};
      newSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("roleName", String.class), new ObjectStreamField("roleValue", List.class), new ObjectStreamField("problemType", Integer.TYPE)};
      compat = false;

      try {
         GetPropertyAction var0 = new GetPropertyAction("jmx.serial.form");
         String var1 = (String)AccessController.doPrivileged((PrivilegedAction)var0);
         compat = var1 != null && var1.equals("1.0");
      } catch (Exception var2) {
      }

      if (compat) {
         serialPersistentFields = oldSerialPersistentFields;
         serialVersionUID = -9026457686611660144L;
      } else {
         serialPersistentFields = newSerialPersistentFields;
         serialVersionUID = -48350262537070138L;
      }

   }
}
