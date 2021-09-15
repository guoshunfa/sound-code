package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public final class Permissions extends PermissionCollection implements Serializable {
   private transient Map<Class<?>, PermissionCollection> permsMap = new HashMap(11);
   private transient boolean hasUnresolved = false;
   PermissionCollection allPermission = null;
   private static final long serialVersionUID = 4858622370623524688L;
   private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("perms", Hashtable.class), new ObjectStreamField("allPermission", PermissionCollection.class)};

   public void add(Permission var1) {
      if (this.isReadOnly()) {
         throw new SecurityException("attempt to add a Permission to a readonly Permissions object");
      } else {
         PermissionCollection var2;
         synchronized(this) {
            var2 = this.getPermissionCollection(var1, true);
            var2.add(var1);
         }

         if (var1 instanceof AllPermission) {
            this.allPermission = var2;
         }

         if (var1 instanceof UnresolvedPermission) {
            this.hasUnresolved = true;
         }

      }
   }

   public boolean implies(Permission var1) {
      if (this.allPermission != null) {
         return true;
      } else {
         synchronized(this) {
            PermissionCollection var3 = this.getPermissionCollection(var1, false);
            return var3 != null ? var3.implies(var1) : false;
         }
      }
   }

   public Enumeration<Permission> elements() {
      synchronized(this) {
         return new PermissionsEnumerator(this.permsMap.values().iterator());
      }
   }

   private PermissionCollection getPermissionCollection(Permission var1, boolean var2) {
      Class var3 = var1.getClass();
      Object var4 = (PermissionCollection)this.permsMap.get(var3);
      if (!this.hasUnresolved && !var2) {
         return (PermissionCollection)var4;
      } else {
         if (var4 == null) {
            var4 = this.hasUnresolved ? this.getUnresolvedPermissions(var1) : null;
            if (var4 == null && var2) {
               var4 = var1.newPermissionCollection();
               if (var4 == null) {
                  var4 = new PermissionsHash();
               }
            }

            if (var4 != null) {
               this.permsMap.put(var3, var4);
            }
         }

         return (PermissionCollection)var4;
      }
   }

   private PermissionCollection getUnresolvedPermissions(Permission var1) {
      UnresolvedPermissionCollection var2 = (UnresolvedPermissionCollection)this.permsMap.get(UnresolvedPermission.class);
      if (var2 == null) {
         return null;
      } else {
         List var3 = var2.getUnresolvedPermissions(var1);
         if (var3 == null) {
            return null;
         } else {
            java.security.cert.Certificate[] var4 = null;
            Object[] var5 = var1.getClass().getSigners();
            int var6 = 0;
            if (var5 != null) {
               int var7;
               for(var7 = 0; var7 < var5.length; ++var7) {
                  if (var5[var7] instanceof java.security.cert.Certificate) {
                     ++var6;
                  }
               }

               var4 = new java.security.cert.Certificate[var6];
               var6 = 0;

               for(var7 = 0; var7 < var5.length; ++var7) {
                  if (var5[var7] instanceof java.security.cert.Certificate) {
                     var4[var6++] = (java.security.cert.Certificate)var5[var7];
                  }
               }
            }

            Object var15 = null;
            synchronized(var3) {
               int var9 = var3.size();

               for(int var10 = 0; var10 < var9; ++var10) {
                  UnresolvedPermission var11 = (UnresolvedPermission)var3.get(var10);
                  Permission var12 = var11.resolve(var1, var4);
                  if (var12 != null) {
                     if (var15 == null) {
                        var15 = var1.newPermissionCollection();
                        if (var15 == null) {
                           var15 = new PermissionsHash();
                        }
                     }

                     ((PermissionCollection)var15).add(var12);
                  }
               }

               return (PermissionCollection)var15;
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Hashtable var2 = new Hashtable(this.permsMap.size() * 2);
      synchronized(this) {
         var2.putAll(this.permsMap);
      }

      ObjectOutputStream.PutField var3 = var1.putFields();
      var3.put("allPermission", this.allPermission);
      var3.put("perms", var2);
      var1.writeFields();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      this.allPermission = (PermissionCollection)var2.get("allPermission", (Object)null);
      Hashtable var3 = (Hashtable)var2.get("perms", (Object)null);
      this.permsMap = new HashMap(var3.size() * 2);
      this.permsMap.putAll(var3);
      UnresolvedPermissionCollection var4 = (UnresolvedPermissionCollection)this.permsMap.get(UnresolvedPermission.class);
      this.hasUnresolved = var4 != null && var4.elements().hasMoreElements();
   }
}
