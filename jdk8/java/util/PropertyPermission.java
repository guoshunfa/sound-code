package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;

public final class PropertyPermission extends BasicPermission {
   private static final int READ = 1;
   private static final int WRITE = 2;
   private static final int ALL = 3;
   private static final int NONE = 0;
   private transient int mask;
   private String actions;
   private static final long serialVersionUID = 885438825399942851L;

   private void init(int var1) {
      if ((var1 & 3) != var1) {
         throw new IllegalArgumentException("invalid actions mask");
      } else if (var1 == 0) {
         throw new IllegalArgumentException("invalid actions mask");
      } else if (this.getName() == null) {
         throw new NullPointerException("name can't be null");
      } else {
         this.mask = var1;
      }
   }

   public PropertyPermission(String var1, String var2) {
      super(var1, var2);
      this.init(getMask(var2));
   }

   public boolean implies(Permission var1) {
      if (!(var1 instanceof PropertyPermission)) {
         return false;
      } else {
         PropertyPermission var2 = (PropertyPermission)var1;
         return (this.mask & var2.mask) == var2.mask && super.implies(var2);
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PropertyPermission)) {
         return false;
      } else {
         PropertyPermission var2 = (PropertyPermission)var1;
         return this.mask == var2.mask && this.getName().equals(var2.getName());
      }
   }

   public int hashCode() {
      return this.getName().hashCode();
   }

   private static int getMask(String var0) {
      int var1 = 0;
      if (var0 == null) {
         return var1;
      } else if (var0 == "read") {
         return 1;
      } else if (var0 == "write") {
         return 2;
      } else if (var0 == "read,write") {
         return 3;
      } else {
         char[] var2 = var0.toCharArray();
         int var3 = var2.length - 1;
         if (var3 < 0) {
            return var1;
         } else {
            while(var3 != -1) {
               char var4;
               while(var3 != -1 && ((var4 = var2[var3]) == ' ' || var4 == '\r' || var4 == '\n' || var4 == '\f' || var4 == '\t')) {
                  --var3;
               }

               byte var5;
               if (var3 < 3 || var2[var3 - 3] != 'r' && var2[var3 - 3] != 'R' || var2[var3 - 2] != 'e' && var2[var3 - 2] != 'E' || var2[var3 - 1] != 'a' && var2[var3 - 1] != 'A' || var2[var3] != 'd' && var2[var3] != 'D') {
                  if (var3 < 4 || var2[var3 - 4] != 'w' && var2[var3 - 4] != 'W' || var2[var3 - 3] != 'r' && var2[var3 - 3] != 'R' || var2[var3 - 2] != 'i' && var2[var3 - 2] != 'I' || var2[var3 - 1] != 't' && var2[var3 - 1] != 'T' || var2[var3] != 'e' && var2[var3] != 'E') {
                     throw new IllegalArgumentException("invalid permission: " + var0);
                  }

                  var5 = 5;
                  var1 |= 2;
               } else {
                  var5 = 4;
                  var1 |= 1;
               }

               boolean var6 = false;

               while(var3 >= var5 && !var6) {
                  switch(var2[var3 - var5]) {
                  case ',':
                     var6 = true;
                  case '\t':
                  case '\n':
                  case '\f':
                  case '\r':
                  case ' ':
                     --var3;
                     break;
                  default:
                     throw new IllegalArgumentException("invalid permission: " + var0);
                  }
               }

               var3 -= var5;
            }

            return var1;
         }
      }
   }

   static String getActions(int var0) {
      StringBuilder var1 = new StringBuilder();
      boolean var2 = false;
      if ((var0 & 1) == 1) {
         var2 = true;
         var1.append("read");
      }

      if ((var0 & 2) == 2) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("write");
      }

      return var1.toString();
   }

   public String getActions() {
      if (this.actions == null) {
         this.actions = getActions(this.mask);
      }

      return this.actions;
   }

   int getMask() {
      return this.mask;
   }

   public PermissionCollection newPermissionCollection() {
      return new PropertyPermissionCollection();
   }

   private synchronized void writeObject(ObjectOutputStream var1) throws IOException {
      if (this.actions == null) {
         this.getActions();
      }

      var1.defaultWriteObject();
   }

   private synchronized void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.init(getMask(this.actions));
   }
}
