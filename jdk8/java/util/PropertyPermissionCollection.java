package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;

final class PropertyPermissionCollection extends PermissionCollection implements Serializable {
   private transient Map<String, PropertyPermission> perms = new HashMap(32);
   private boolean all_allowed = false;
   private static final long serialVersionUID = 7015263904581634791L;
   private static final ObjectStreamField[] serialPersistentFields;

   public PropertyPermissionCollection() {
   }

   public void add(Permission var1) {
      if (!(var1 instanceof PropertyPermission)) {
         throw new IllegalArgumentException("invalid permission: " + var1);
      } else if (this.isReadOnly()) {
         throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
      } else {
         PropertyPermission var2 = (PropertyPermission)var1;
         String var3 = var2.getName();
         synchronized(this) {
            PropertyPermission var5 = (PropertyPermission)this.perms.get(var3);
            if (var5 != null) {
               int var6 = var5.getMask();
               int var7 = var2.getMask();
               if (var6 != var7) {
                  int var8 = var6 | var7;
                  String var9 = PropertyPermission.getActions(var8);
                  this.perms.put(var3, new PropertyPermission(var3, var9));
               }
            } else {
               this.perms.put(var3, var2);
            }
         }

         if (!this.all_allowed && var3.equals("*")) {
            this.all_allowed = true;
         }

      }
   }

   public boolean implies(Permission var1) {
      if (!(var1 instanceof PropertyPermission)) {
         return false;
      } else {
         PropertyPermission var2 = (PropertyPermission)var1;
         int var4 = var2.getMask();
         int var5 = 0;
         PropertyPermission var3;
         if (this.all_allowed) {
            synchronized(this) {
               var3 = (PropertyPermission)this.perms.get("*");
            }

            if (var3 != null) {
               var5 |= var3.getMask();
               if ((var5 & var4) == var4) {
                  return true;
               }
            }
         }

         String var6 = var2.getName();
         synchronized(this) {
            var3 = (PropertyPermission)this.perms.get(var6);
         }

         if (var3 != null) {
            var5 |= var3.getMask();
            if ((var5 & var4) == var4) {
               return true;
            }
         }

         int var7;
         for(int var8 = var6.length() - 1; (var7 = var6.lastIndexOf(".", var8)) != -1; var8 = var7 - 1) {
            var6 = var6.substring(0, var7 + 1) + "*";
            synchronized(this) {
               var3 = (PropertyPermission)this.perms.get(var6);
            }

            if (var3 != null) {
               var5 |= var3.getMask();
               if ((var5 & var4) == var4) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public Enumeration<Permission> elements() {
      synchronized(this) {
         return Collections.enumeration(this.perms.values());
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Hashtable var2 = new Hashtable(this.perms.size() * 2);
      synchronized(this) {
         var2.putAll(this.perms);
      }

      ObjectOutputStream.PutField var3 = var1.putFields();
      var3.put("all_allowed", this.all_allowed);
      var3.put("permissions", var2);
      var1.writeFields();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      this.all_allowed = var2.get("all_allowed", false);
      Hashtable var3 = (Hashtable)var2.get("permissions", (Object)null);
      this.perms = new HashMap(var3.size() * 2);
      this.perms.putAll(var3);
   }

   static {
      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("permissions", Hashtable.class), new ObjectStreamField("all_allowed", Boolean.TYPE)};
   }
}
