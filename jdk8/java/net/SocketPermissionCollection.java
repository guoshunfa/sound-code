package java.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

final class SocketPermissionCollection extends PermissionCollection implements Serializable {
   private transient List<SocketPermission> perms = new ArrayList();
   private static final long serialVersionUID = 2787186408602843674L;
   private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("permissions", Vector.class)};

   public SocketPermissionCollection() {
   }

   public void add(Permission var1) {
      if (!(var1 instanceof SocketPermission)) {
         throw new IllegalArgumentException("invalid permission: " + var1);
      } else if (this.isReadOnly()) {
         throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
      } else {
         synchronized(this) {
            this.perms.add(0, (SocketPermission)var1);
         }
      }
   }

   public boolean implies(Permission var1) {
      if (!(var1 instanceof SocketPermission)) {
         return false;
      } else {
         SocketPermission var2 = (SocketPermission)var1;
         int var3 = var2.getMask();
         int var4 = 0;
         int var5 = var3;
         synchronized(this) {
            int var7 = this.perms.size();

            for(int var8 = 0; var8 < var7; ++var8) {
               SocketPermission var9 = (SocketPermission)this.perms.get(var8);
               if ((var5 & var9.getMask()) != 0 && var9.impliesIgnoreMask(var2)) {
                  var4 |= var9.getMask();
                  if ((var4 & var3) == var3) {
                     return true;
                  }

                  var5 = var3 ^ var4;
               }
            }

            return false;
         }
      }
   }

   public Enumeration<Permission> elements() {
      synchronized(this) {
         return Collections.enumeration(this.perms);
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Vector var2 = new Vector(this.perms.size());
      synchronized(this) {
         var2.addAll(this.perms);
      }

      ObjectOutputStream.PutField var3 = var1.putFields();
      var3.put("permissions", var2);
      var1.writeFields();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      Vector var3 = (Vector)var2.get("permissions", (Object)null);
      this.perms = new ArrayList(var3.size());
      this.perms.addAll(var3);
   }
}
