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

public class Role implements Serializable {
   private static final long oldSerialVersionUID = -1959486389343113026L;
   private static final long newSerialVersionUID = -279985518429862552L;
   private static final ObjectStreamField[] oldSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("myName", String.class), new ObjectStreamField("myObjNameList", ArrayList.class)};
   private static final ObjectStreamField[] newSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("name", String.class), new ObjectStreamField("objectNameList", List.class)};
   private static final long serialVersionUID;
   private static final ObjectStreamField[] serialPersistentFields;
   private static boolean compat = false;
   private String name = null;
   private List<ObjectName> objectNameList = new ArrayList();

   public Role(String var1, List<ObjectName> var2) throws IllegalArgumentException {
      if (var1 != null && var2 != null) {
         this.setRoleName(var1);
         this.setRoleValue(var2);
      } else {
         String var3 = "Invalid parameter";
         throw new IllegalArgumentException(var3);
      }
   }

   public String getRoleName() {
      return this.name;
   }

   public List<ObjectName> getRoleValue() {
      return this.objectNameList;
   }

   public void setRoleName(String var1) throws IllegalArgumentException {
      if (var1 == null) {
         String var2 = "Invalid parameter.";
         throw new IllegalArgumentException(var2);
      } else {
         this.name = var1;
      }
   }

   public void setRoleValue(List<ObjectName> var1) throws IllegalArgumentException {
      if (var1 == null) {
         String var2 = "Invalid parameter.";
         throw new IllegalArgumentException(var2);
      } else {
         this.objectNameList = new ArrayList(var1);
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("role name: " + this.name + "; role value: ");
      Iterator var2 = this.objectNameList.iterator();

      while(var2.hasNext()) {
         ObjectName var3 = (ObjectName)var2.next();
         var1.append(var3.toString());
         if (var2.hasNext()) {
            var1.append(", ");
         }
      }

      return var1.toString();
   }

   public Object clone() {
      try {
         return new Role(this.name, this.objectNameList);
      } catch (IllegalArgumentException var2) {
         return null;
      }
   }

   public static String roleValueToString(List<ObjectName> var0) throws IllegalArgumentException {
      if (var0 == null) {
         String var4 = "Invalid parameter";
         throw new IllegalArgumentException(var4);
      } else {
         StringBuilder var1 = new StringBuilder();

         ObjectName var3;
         for(Iterator var2 = var0.iterator(); var2.hasNext(); var1.append(var3.toString())) {
            var3 = (ObjectName)var2.next();
            if (var1.length() > 0) {
               var1.append("\n");
            }
         }

         return var1.toString();
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      if (compat) {
         ObjectInputStream.GetField var2 = var1.readFields();
         this.name = (String)var2.get("myName", (Object)null);
         if (var2.defaulted("myName")) {
            throw new NullPointerException("myName");
         }

         this.objectNameList = (List)Util.cast(var2.get("myObjNameList", (Object)null));
         if (var2.defaulted("myObjNameList")) {
            throw new NullPointerException("myObjNameList");
         }
      } else {
         var1.defaultReadObject();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (compat) {
         ObjectOutputStream.PutField var2 = var1.putFields();
         var2.put("myName", this.name);
         var2.put("myObjNameList", this.objectNameList);
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
         serialVersionUID = -1959486389343113026L;
      } else {
         serialPersistentFields = newSerialPersistentFields;
         serialVersionUID = -279985518429862552L;
      }

   }
}
