package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

final class UnresolvedPermissionCollection extends PermissionCollection implements Serializable {
   private transient Map<String, List<UnresolvedPermission>> perms = new HashMap(11);
   private static final long serialVersionUID = -7176153071733132400L;
   private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("permissions", Hashtable.class)};

   public UnresolvedPermissionCollection() {
   }

   public void add(Permission var1) {
      if (!(var1 instanceof UnresolvedPermission)) {
         throw new IllegalArgumentException("invalid permission: " + var1);
      } else {
         UnresolvedPermission var2 = (UnresolvedPermission)var1;
         Object var3;
         synchronized(this) {
            var3 = (List)this.perms.get(var2.getName());
            if (var3 == null) {
               var3 = new ArrayList();
               this.perms.put(var2.getName(), var3);
            }
         }

         synchronized(var3) {
            ((List)var3).add(var2);
         }
      }
   }

   List<UnresolvedPermission> getUnresolvedPermissions(Permission var1) {
      synchronized(this) {
         return (List)this.perms.get(var1.getClass().getName());
      }
   }

   public boolean implies(Permission var1) {
      return false;
   }

   public Enumeration<Permission> elements() {
      ArrayList var1 = new ArrayList();
      synchronized(this) {
         Iterator var3 = this.perms.values().iterator();

         while(var3.hasNext()) {
            List var4 = (List)var3.next();
            synchronized(var4) {
               var1.addAll(var4);
            }
         }

         return Collections.enumeration(var1);
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Hashtable var2 = new Hashtable(this.perms.size() * 2);
      synchronized(this) {
         Set var4 = this.perms.entrySet();
         Iterator var5 = var4.iterator();

         while(true) {
            if (!var5.hasNext()) {
               break;
            }

            Map.Entry var6 = (Map.Entry)var5.next();
            List var7 = (List)var6.getValue();
            Vector var8 = new Vector(var7.size());
            synchronized(var7) {
               var8.addAll(var7);
            }

            var2.put(var6.getKey(), var8);
         }
      }

      ObjectOutputStream.PutField var3 = var1.putFields();
      var3.put("permissions", var2);
      var1.writeFields();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      Hashtable var3 = (Hashtable)var2.get("permissions", (Object)null);
      this.perms = new HashMap(var3.size() * 2);
      Set var4 = var3.entrySet();
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         Map.Entry var6 = (Map.Entry)var5.next();
         Vector var7 = (Vector)var6.getValue();
         ArrayList var8 = new ArrayList(var7.size());
         var8.addAll(var7);
         this.perms.put(var6.getKey(), var8);
      }

   }
}
