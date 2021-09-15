package javax.management.relation;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.management.Notification;
import javax.management.ObjectName;

public class RelationNotification extends Notification {
   private static final long oldSerialVersionUID = -2126464566505527147L;
   private static final long newSerialVersionUID = -6871117877523310399L;
   private static final ObjectStreamField[] oldSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("myNewRoleValue", ArrayList.class), new ObjectStreamField("myOldRoleValue", ArrayList.class), new ObjectStreamField("myRelId", String.class), new ObjectStreamField("myRelObjName", ObjectName.class), new ObjectStreamField("myRelTypeName", String.class), new ObjectStreamField("myRoleName", String.class), new ObjectStreamField("myUnregMBeanList", ArrayList.class)};
   private static final ObjectStreamField[] newSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("newRoleValue", List.class), new ObjectStreamField("oldRoleValue", List.class), new ObjectStreamField("relationId", String.class), new ObjectStreamField("relationObjName", ObjectName.class), new ObjectStreamField("relationTypeName", String.class), new ObjectStreamField("roleName", String.class), new ObjectStreamField("unregisterMBeanList", List.class)};
   private static final long serialVersionUID;
   private static final ObjectStreamField[] serialPersistentFields;
   private static boolean compat = false;
   public static final String RELATION_BASIC_CREATION = "jmx.relation.creation.basic";
   public static final String RELATION_MBEAN_CREATION = "jmx.relation.creation.mbean";
   public static final String RELATION_BASIC_UPDATE = "jmx.relation.update.basic";
   public static final String RELATION_MBEAN_UPDATE = "jmx.relation.update.mbean";
   public static final String RELATION_BASIC_REMOVAL = "jmx.relation.removal.basic";
   public static final String RELATION_MBEAN_REMOVAL = "jmx.relation.removal.mbean";
   private String relationId = null;
   private String relationTypeName = null;
   private ObjectName relationObjName = null;
   private List<ObjectName> unregisterMBeanList = null;
   private String roleName = null;
   private List<ObjectName> oldRoleValue = null;
   private List<ObjectName> newRoleValue = null;

   public RelationNotification(String var1, Object var2, long var3, long var5, String var7, String var8, String var9, ObjectName var10, List<ObjectName> var11) throws IllegalArgumentException {
      super(var1, var2, var3, var5, var7);
      if (this.isValidBasicStrict(var1, var2, var8, var9) && this.isValidCreate(var1)) {
         this.relationId = var8;
         this.relationTypeName = var9;
         this.relationObjName = this.safeGetObjectName(var10);
         this.unregisterMBeanList = this.safeGetObjectNameList(var11);
      } else {
         throw new IllegalArgumentException("Invalid parameter.");
      }
   }

   public RelationNotification(String var1, Object var2, long var3, long var5, String var7, String var8, String var9, ObjectName var10, String var11, List<ObjectName> var12, List<ObjectName> var13) throws IllegalArgumentException {
      super(var1, var2, var3, var5, var7);
      if (this.isValidBasicStrict(var1, var2, var8, var9) && this.isValidUpdate(var1, var11, var12, var13)) {
         this.relationId = var8;
         this.relationTypeName = var9;
         this.relationObjName = this.safeGetObjectName(var10);
         this.roleName = var11;
         this.oldRoleValue = this.safeGetObjectNameList(var13);
         this.newRoleValue = this.safeGetObjectNameList(var12);
      } else {
         throw new IllegalArgumentException("Invalid parameter.");
      }
   }

   public String getRelationId() {
      return this.relationId;
   }

   public String getRelationTypeName() {
      return this.relationTypeName;
   }

   public ObjectName getObjectName() {
      return this.relationObjName;
   }

   public List<ObjectName> getMBeansToUnregister() {
      Object var1;
      if (this.unregisterMBeanList != null) {
         var1 = new ArrayList(this.unregisterMBeanList);
      } else {
         var1 = Collections.emptyList();
      }

      return (List)var1;
   }

   public String getRoleName() {
      String var1 = null;
      if (this.roleName != null) {
         var1 = this.roleName;
      }

      return var1;
   }

   public List<ObjectName> getOldRoleValue() {
      Object var1;
      if (this.oldRoleValue != null) {
         var1 = new ArrayList(this.oldRoleValue);
      } else {
         var1 = Collections.emptyList();
      }

      return (List)var1;
   }

   public List<ObjectName> getNewRoleValue() {
      Object var1;
      if (this.newRoleValue != null) {
         var1 = new ArrayList(this.newRoleValue);
      } else {
         var1 = Collections.emptyList();
      }

      return (List)var1;
   }

   private boolean isValidBasicStrict(String var1, Object var2, String var3, String var4) {
      return var2 == null ? false : this.isValidBasic(var1, var2, var3, var4);
   }

   private boolean isValidBasic(String var1, Object var2, String var3, String var4) {
      if (var1 != null && var3 != null && var4 != null) {
         return var2 == null || var2 instanceof RelationService || var2 instanceof ObjectName;
      } else {
         return false;
      }
   }

   private boolean isValidCreate(String var1) {
      String[] var2 = new String[]{"jmx.relation.creation.basic", "jmx.relation.creation.mbean", "jmx.relation.removal.basic", "jmx.relation.removal.mbean"};
      HashSet var3 = new HashSet(Arrays.asList(var2));
      return var3.contains(var1);
   }

   private boolean isValidUpdate(String var1, String var2, List<ObjectName> var3, List<ObjectName> var4) {
      if (!var1.equals("jmx.relation.update.basic") && !var1.equals("jmx.relation.update.mbean")) {
         return false;
      } else {
         return var2 != null && var4 != null && var3 != null;
      }
   }

   private ArrayList<ObjectName> safeGetObjectNameList(List<ObjectName> var1) {
      ArrayList var2 = null;
      if (var1 != null) {
         var2 = new ArrayList();
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            ObjectName var4 = (ObjectName)var3.next();
            var2.add(ObjectName.getInstance(var4));
         }
      }

      return var2;
   }

   private ObjectName safeGetObjectName(ObjectName var1) {
      ObjectName var2 = null;
      if (var1 != null) {
         var2 = ObjectName.getInstance(var1);
      }

      return var2;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var9 = var1.readFields();
      String var2;
      String var3;
      String var4;
      ObjectName var5;
      List var6;
      List var7;
      List var8;
      if (compat) {
         var2 = (String)var9.get("myRelId", (Object)null);
         var3 = (String)var9.get("myRelTypeName", (Object)null);
         var4 = (String)var9.get("myRoleName", (Object)null);
         var5 = (ObjectName)var9.get("myRelObjName", (Object)null);
         var6 = (List)Util.cast(var9.get("myNewRoleValue", (Object)null));
         var7 = (List)Util.cast(var9.get("myOldRoleValue", (Object)null));
         var8 = (List)Util.cast(var9.get("myUnregMBeanList", (Object)null));
      } else {
         var2 = (String)var9.get("relationId", (Object)null);
         var3 = (String)var9.get("relationTypeName", (Object)null);
         var4 = (String)var9.get("roleName", (Object)null);
         var5 = (ObjectName)var9.get("relationObjName", (Object)null);
         var6 = (List)Util.cast(var9.get("newRoleValue", (Object)null));
         var7 = (List)Util.cast(var9.get("oldRoleValue", (Object)null));
         var8 = (List)Util.cast(var9.get("unregisterMBeanList", (Object)null));
      }

      String var10 = super.getType();
      if (this.isValidBasic(var10, super.getSource(), var2, var3) && (this.isValidCreate(var10) || this.isValidUpdate(var10, var4, var6, var7))) {
         this.relationObjName = this.safeGetObjectName(var5);
         this.newRoleValue = this.safeGetObjectNameList(var6);
         this.oldRoleValue = this.safeGetObjectNameList(var7);
         this.unregisterMBeanList = this.safeGetObjectNameList(var8);
         this.relationId = var2;
         this.relationTypeName = var3;
         this.roleName = var4;
      } else {
         super.setSource((Object)null);
         throw new InvalidObjectException("Invalid object read");
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (compat) {
         ObjectOutputStream.PutField var2 = var1.putFields();
         var2.put("myNewRoleValue", this.newRoleValue);
         var2.put("myOldRoleValue", this.oldRoleValue);
         var2.put("myRelId", this.relationId);
         var2.put("myRelObjName", this.relationObjName);
         var2.put("myRelTypeName", this.relationTypeName);
         var2.put("myRoleName", this.roleName);
         var2.put("myUnregMBeanList", this.unregisterMBeanList);
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
         serialVersionUID = -2126464566505527147L;
      } else {
         serialPersistentFields = newSerialPersistentFields;
         serialVersionUID = -6871117877523310399L;
      }

   }
}
