package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

final class BasicPermissionCollection extends PermissionCollection implements Serializable {
   private static final long serialVersionUID = 739301742472979399L;
   private transient Map<String, Permission> perms = new HashMap(11);
   private boolean all_allowed = false;
   private Class<?> permClass;
   private static final ObjectStreamField[] serialPersistentFields;

   public BasicPermissionCollection(Class<?> var1) {
      this.permClass = var1;
   }

   public void add(Permission var1) {
      if (!(var1 instanceof BasicPermission)) {
         throw new IllegalArgumentException("invalid permission: " + var1);
      } else if (this.isReadOnly()) {
         throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
      } else {
         BasicPermission var2 = (BasicPermission)var1;
         if (this.permClass == null) {
            this.permClass = var2.getClass();
         } else if (var2.getClass() != this.permClass) {
            throw new IllegalArgumentException("invalid permission: " + var1);
         }

         synchronized(this) {
            this.perms.put(var2.getCanonicalName(), var1);
         }

         if (!this.all_allowed && var2.getCanonicalName().equals("*")) {
            this.all_allowed = true;
         }

      }
   }

   public boolean implies(Permission var1) {
      if (!(var1 instanceof BasicPermission)) {
         return false;
      } else {
         BasicPermission var2 = (BasicPermission)var1;
         if (var2.getClass() != this.permClass) {
            return false;
         } else if (this.all_allowed) {
            return true;
         } else {
            String var3 = var2.getCanonicalName();
            Permission var4;
            synchronized(this) {
               var4 = (Permission)this.perms.get(var3);
            }

            if (var4 != null) {
               return var4.implies(var1);
            } else {
               int var5;
               for(int var6 = var3.length() - 1; (var5 = var3.lastIndexOf(".", var6)) != -1; var6 = var5 - 1) {
                  var3 = var3.substring(0, var5 + 1) + "*";
                  synchronized(this) {
                     var4 = (Permission)this.perms.get(var3);
                  }

                  if (var4 != null) {
                     return var4.implies(var1);
                  }
               }

               return false;
            }
         }
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
      var3.put("permClass", this.permClass);
      var1.writeFields();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      Hashtable var3 = (Hashtable)var2.get("permissions", (Object)null);
      this.perms = new HashMap(var3.size() * 2);
      this.perms.putAll(var3);
      this.all_allowed = var2.get("all_allowed", false);
      this.permClass = (Class)var2.get("permClass", (Object)null);
      if (this.permClass == null) {
         Enumeration var4 = var3.elements();
         if (var4.hasMoreElements()) {
            Permission var5 = (Permission)var4.nextElement();
            this.permClass = var5.getClass();
         }
      }

   }

   static {
      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("permissions", Hashtable.class), new ObjectStreamField("all_allowed", Boolean.TYPE), new ObjectStreamField("permClass", Class.class)};
   }
}
