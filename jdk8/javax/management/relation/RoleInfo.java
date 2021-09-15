package javax.management.relation;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.management.NotCompliantMBeanException;

public class RoleInfo implements Serializable {
   private static final long oldSerialVersionUID = 7227256952085334351L;
   private static final long newSerialVersionUID = 2504952983494636987L;
   private static final ObjectStreamField[] oldSerialPersistentFields;
   private static final ObjectStreamField[] newSerialPersistentFields;
   private static final long serialVersionUID;
   private static final ObjectStreamField[] serialPersistentFields;
   private static boolean compat;
   public static final int ROLE_CARDINALITY_INFINITY = -1;
   private String name = null;
   private boolean isReadable;
   private boolean isWritable;
   private String description = null;
   private int minDegree;
   private int maxDegree;
   private String referencedMBeanClassName = null;

   public RoleInfo(String var1, String var2, boolean var3, boolean var4, int var5, int var6, String var7) throws IllegalArgumentException, InvalidRoleInfoException, ClassNotFoundException, NotCompliantMBeanException {
      this.init(var1, var2, var3, var4, var5, var6, var7);
   }

   public RoleInfo(String var1, String var2, boolean var3, boolean var4) throws IllegalArgumentException, ClassNotFoundException, NotCompliantMBeanException {
      try {
         this.init(var1, var2, var3, var4, 1, 1, (String)null);
      } catch (InvalidRoleInfoException var6) {
      }

   }

   public RoleInfo(String var1, String var2) throws IllegalArgumentException, ClassNotFoundException, NotCompliantMBeanException {
      try {
         this.init(var1, var2, true, true, 1, 1, (String)null);
      } catch (InvalidRoleInfoException var4) {
      }

   }

   public RoleInfo(RoleInfo var1) throws IllegalArgumentException {
      if (var1 == null) {
         String var2 = "Invalid parameter.";
         throw new IllegalArgumentException(var2);
      } else {
         try {
            this.init(var1.getName(), var1.getRefMBeanClassName(), var1.isReadable(), var1.isWritable(), var1.getMinDegree(), var1.getMaxDegree(), var1.getDescription());
         } catch (InvalidRoleInfoException var3) {
         }

      }
   }

   public String getName() {
      return this.name;
   }

   public boolean isReadable() {
      return this.isReadable;
   }

   public boolean isWritable() {
      return this.isWritable;
   }

   public String getDescription() {
      return this.description;
   }

   public int getMinDegree() {
      return this.minDegree;
   }

   public int getMaxDegree() {
      return this.maxDegree;
   }

   public String getRefMBeanClassName() {
      return this.referencedMBeanClassName;
   }

   public boolean checkMinDegree(int var1) {
      return var1 >= -1 && (this.minDegree == -1 || var1 >= this.minDegree);
   }

   public boolean checkMaxDegree(int var1) {
      return var1 >= -1 && (this.maxDegree == -1 || var1 != -1 && var1 <= this.maxDegree);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("role info name: " + this.name);
      var1.append("; isReadable: " + this.isReadable);
      var1.append("; isWritable: " + this.isWritable);
      var1.append("; description: " + this.description);
      var1.append("; minimum degree: " + this.minDegree);
      var1.append("; maximum degree: " + this.maxDegree);
      var1.append("; MBean class: " + this.referencedMBeanClassName);
      return var1.toString();
   }

   private void init(String var1, String var2, boolean var3, boolean var4, int var5, int var6, String var7) throws IllegalArgumentException, InvalidRoleInfoException {
      if (var1 != null && var2 != null) {
         this.name = var1;
         this.isReadable = var3;
         this.isWritable = var4;
         if (var7 != null) {
            this.description = var7;
         }

         boolean var10 = false;
         StringBuilder var9 = new StringBuilder();
         if (var6 == -1 || var5 != -1 && var5 <= var6) {
            if (var5 < -1 || var6 < -1) {
               var9.append("Minimum or maximum degree has an illegal value, must be [0, ROLE_CARDINALITY_INFINITY].");
               var10 = true;
            }
         } else {
            var9.append("Minimum degree ");
            var9.append(var5);
            var9.append(" is greater than maximum degree ");
            var9.append(var6);
            var10 = true;
         }

         if (var10) {
            throw new InvalidRoleInfoException(var9.toString());
         } else {
            this.minDegree = var5;
            this.maxDegree = var6;
            this.referencedMBeanClassName = var2;
         }
      } else {
         String var8 = "Invalid parameter.";
         throw new IllegalArgumentException(var8);
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      if (compat) {
         ObjectInputStream.GetField var2 = var1.readFields();
         this.name = (String)var2.get("myName", (Object)null);
         if (var2.defaulted("myName")) {
            throw new NullPointerException("myName");
         }

         this.isReadable = var2.get("myIsReadableFlg", false);
         if (var2.defaulted("myIsReadableFlg")) {
            throw new NullPointerException("myIsReadableFlg");
         }

         this.isWritable = var2.get("myIsWritableFlg", false);
         if (var2.defaulted("myIsWritableFlg")) {
            throw new NullPointerException("myIsWritableFlg");
         }

         this.description = (String)var2.get("myDescription", (Object)null);
         if (var2.defaulted("myDescription")) {
            throw new NullPointerException("myDescription");
         }

         this.minDegree = var2.get("myMinDegree", (int)0);
         if (var2.defaulted("myMinDegree")) {
            throw new NullPointerException("myMinDegree");
         }

         this.maxDegree = var2.get("myMaxDegree", (int)0);
         if (var2.defaulted("myMaxDegree")) {
            throw new NullPointerException("myMaxDegree");
         }

         this.referencedMBeanClassName = (String)var2.get("myRefMBeanClassName", (Object)null);
         if (var2.defaulted("myRefMBeanClassName")) {
            throw new NullPointerException("myRefMBeanClassName");
         }
      } else {
         var1.defaultReadObject();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (compat) {
         ObjectOutputStream.PutField var2 = var1.putFields();
         var2.put("myName", this.name);
         var2.put("myIsReadableFlg", this.isReadable);
         var2.put("myIsWritableFlg", this.isWritable);
         var2.put("myDescription", this.description);
         var2.put("myMinDegree", this.minDegree);
         var2.put("myMaxDegree", this.maxDegree);
         var2.put("myRefMBeanClassName", this.referencedMBeanClassName);
         var1.writeFields();
      } else {
         var1.defaultWriteObject();
      }

   }

   static {
      oldSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("myName", String.class), new ObjectStreamField("myIsReadableFlg", Boolean.TYPE), new ObjectStreamField("myIsWritableFlg", Boolean.TYPE), new ObjectStreamField("myDescription", String.class), new ObjectStreamField("myMinDegree", Integer.TYPE), new ObjectStreamField("myMaxDegree", Integer.TYPE), new ObjectStreamField("myRefMBeanClassName", String.class)};
      newSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("name", String.class), new ObjectStreamField("isReadable", Boolean.TYPE), new ObjectStreamField("isWritable", Boolean.TYPE), new ObjectStreamField("description", String.class), new ObjectStreamField("minDegree", Integer.TYPE), new ObjectStreamField("maxDegree", Integer.TYPE), new ObjectStreamField("referencedMBeanClassName", String.class)};
      compat = false;

      try {
         GetPropertyAction var0 = new GetPropertyAction("jmx.serial.form");
         String var1 = (String)AccessController.doPrivileged((PrivilegedAction)var0);
         compat = var1 != null && var1.equals("1.0");
      } catch (Exception var2) {
      }

      if (compat) {
         serialPersistentFields = oldSerialPersistentFields;
         serialVersionUID = 7227256952085334351L;
      } else {
         serialPersistentFields = newSerialPersistentFields;
         serialVersionUID = 2504952983494636987L;
      }

   }
}
