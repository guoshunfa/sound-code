package javax.management.relation;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class RelationTypeSupport implements RelationType {
   private static final long oldSerialVersionUID = -8179019472410837190L;
   private static final long newSerialVersionUID = 4611072955724144607L;
   private static final ObjectStreamField[] oldSerialPersistentFields;
   private static final ObjectStreamField[] newSerialPersistentFields;
   private static final long serialVersionUID;
   private static final ObjectStreamField[] serialPersistentFields;
   private static boolean compat;
   private String typeName = null;
   private Map<String, RoleInfo> roleName2InfoMap = new HashMap();
   private boolean isInRelationService = false;

   public RelationTypeSupport(String var1, RoleInfo[] var2) throws IllegalArgumentException, InvalidRelationTypeException {
      if (var1 != null && var2 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "RelationTypeSupport", (Object)var1);
         this.initMembers(var1, var2);
         JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "RelationTypeSupport");
      } else {
         String var3 = "Invalid parameter.";
         throw new IllegalArgumentException(var3);
      }
   }

   protected RelationTypeSupport(String var1) {
      if (var1 == null) {
         String var2 = "Invalid parameter.";
         throw new IllegalArgumentException(var2);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "RelationTypeSupport", (Object)var1);
         this.typeName = var1;
         JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "RelationTypeSupport");
      }
   }

   public String getRelationTypeName() {
      return this.typeName;
   }

   public List<RoleInfo> getRoleInfos() {
      return new ArrayList(this.roleName2InfoMap.values());
   }

   public RoleInfo getRoleInfo(String var1) throws IllegalArgumentException, RoleInfoNotFoundException {
      if (var1 == null) {
         String var5 = "Invalid parameter.";
         throw new IllegalArgumentException(var5);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "getRoleInfo", (Object)var1);
         RoleInfo var2 = (RoleInfo)this.roleName2InfoMap.get(var1);
         if (var2 == null) {
            StringBuilder var3 = new StringBuilder();
            String var4 = "No role info for role ";
            var3.append(var4);
            var3.append(var1);
            throw new RoleInfoNotFoundException(var3.toString());
         } else {
            JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "getRoleInfo");
            return var2;
         }
      }
   }

   protected void addRoleInfo(RoleInfo var1) throws IllegalArgumentException, InvalidRelationTypeException {
      String var2;
      if (var1 == null) {
         var2 = "Invalid parameter.";
         throw new IllegalArgumentException(var2);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "addRoleInfo", (Object)var1);
         if (this.isInRelationService) {
            var2 = "Relation type cannot be updated as it is declared in the Relation Service.";
            throw new RuntimeException(var2);
         } else {
            var2 = var1.getName();
            if (this.roleName2InfoMap.containsKey(var2)) {
               StringBuilder var3 = new StringBuilder();
               String var4 = "Two role infos provided for role ";
               var3.append(var4);
               var3.append(var2);
               throw new InvalidRelationTypeException(var3.toString());
            } else {
               this.roleName2InfoMap.put(var2, new RoleInfo(var1));
               JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "addRoleInfo");
            }
         }
      }
   }

   void setRelationServiceFlag(boolean var1) {
      this.isInRelationService = var1;
   }

   private void initMembers(String var1, RoleInfo[] var2) throws IllegalArgumentException, InvalidRelationTypeException {
      if (var1 != null && var2 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "initMembers", (Object)var1);
         this.typeName = var1;
         checkRoleInfos(var2);

         for(int var5 = 0; var5 < var2.length; ++var5) {
            RoleInfo var4 = var2[var5];
            this.roleName2InfoMap.put(var4.getName(), new RoleInfo(var4));
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "initMembers");
      } else {
         String var3 = "Invalid parameter.";
         throw new IllegalArgumentException(var3);
      }
   }

   static void checkRoleInfos(RoleInfo[] var0) throws IllegalArgumentException, InvalidRelationTypeException {
      String var7;
      if (var0 == null) {
         var7 = "Invalid parameter.";
         throw new IllegalArgumentException(var7);
      } else if (var0.length == 0) {
         var7 = "No role info provided.";
         throw new InvalidRelationTypeException(var7);
      } else {
         HashSet var1 = new HashSet();

         for(int var2 = 0; var2 < var0.length; ++var2) {
            RoleInfo var3 = var0[var2];
            String var4;
            if (var3 == null) {
               var4 = "Null role info provided.";
               throw new InvalidRelationTypeException(var4);
            }

            var4 = var3.getName();
            if (var1.contains(var4)) {
               StringBuilder var5 = new StringBuilder();
               String var6 = "Two role infos provided for role ";
               var5.append(var6);
               var5.append(var4);
               throw new InvalidRelationTypeException(var5.toString());
            }

            var1.add(var4);
         }

      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      if (compat) {
         ObjectInputStream.GetField var2 = var1.readFields();
         this.typeName = (String)var2.get("myTypeName", (Object)null);
         if (var2.defaulted("myTypeName")) {
            throw new NullPointerException("myTypeName");
         }

         this.roleName2InfoMap = (Map)Util.cast(var2.get("myRoleName2InfoMap", (Object)null));
         if (var2.defaulted("myRoleName2InfoMap")) {
            throw new NullPointerException("myRoleName2InfoMap");
         }

         this.isInRelationService = var2.get("myIsInRelServFlg", false);
         if (var2.defaulted("myIsInRelServFlg")) {
            throw new NullPointerException("myIsInRelServFlg");
         }
      } else {
         var1.defaultReadObject();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (compat) {
         ObjectOutputStream.PutField var2 = var1.putFields();
         var2.put("myTypeName", this.typeName);
         var2.put("myRoleName2InfoMap", this.roleName2InfoMap);
         var2.put("myIsInRelServFlg", this.isInRelationService);
         var1.writeFields();
      } else {
         var1.defaultWriteObject();
      }

   }

   static {
      oldSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("myTypeName", String.class), new ObjectStreamField("myRoleName2InfoMap", HashMap.class), new ObjectStreamField("myIsInRelServFlg", Boolean.TYPE)};
      newSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("typeName", String.class), new ObjectStreamField("roleName2InfoMap", Map.class), new ObjectStreamField("isInRelationService", Boolean.TYPE)};
      compat = false;

      try {
         GetPropertyAction var0 = new GetPropertyAction("jmx.serial.form");
         String var1 = (String)AccessController.doPrivileged((PrivilegedAction)var0);
         compat = var1 != null && var1.equals("1.0");
      } catch (Exception var2) {
      }

      if (compat) {
         serialPersistentFields = oldSerialPersistentFields;
         serialVersionUID = -8179019472410837190L;
      } else {
         serialPersistentFields = newSerialPersistentFields;
         serialVersionUID = 4611072955724144607L;
      }

   }
}
