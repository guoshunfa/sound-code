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
import java.util.Iterator;
import java.util.Map;

final class PermissionsHash extends PermissionCollection implements Serializable {
   private transient Map<Permission, Permission> permsMap = new HashMap(11);
   private static final long serialVersionUID = -8491988220802933440L;
   private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("perms", Hashtable.class)};

   public void add(Permission var1) {
      synchronized(this) {
         this.permsMap.put(var1, var1);
      }
   }

   public boolean implies(Permission var1) {
      synchronized(this) {
         Permission var3 = (Permission)this.permsMap.get(var1);
         if (var3 == null) {
            Iterator var4 = this.permsMap.values().iterator();

            Permission var5;
            do {
               if (!var4.hasNext()) {
                  return false;
               }

               var5 = (Permission)var4.next();
            } while(!var5.implies(var1));

            return true;
         } else {
            return true;
         }
      }
   }

   public Enumeration<Permission> elements() {
      synchronized(this) {
         return Collections.enumeration(this.permsMap.values());
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Hashtable var2 = new Hashtable(this.permsMap.size() * 2);
      synchronized(this) {
         var2.putAll(this.permsMap);
      }

      ObjectOutputStream.PutField var3 = var1.putFields();
      var3.put("perms", var2);
      var1.writeFields();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      Hashtable var3 = (Hashtable)var2.get("perms", (Object)null);
      this.permsMap = new HashMap(var3.size() * 2);
      this.permsMap.putAll(var3);
   }
}
