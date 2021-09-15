package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;

public final class ServicePermission extends Permission implements Serializable {
   private static final long serialVersionUID = -1227585031618624935L;
   private static final int INITIATE = 1;
   private static final int ACCEPT = 2;
   private static final int ALL = 3;
   private static final int NONE = 0;
   private transient int mask;
   private String actions;

   public ServicePermission(String var1, String var2) {
      super(var1);
      this.init(var1, getMask(var2));
   }

   private void init(String var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException("service principal can't be null");
      } else if ((var2 & 3) != var2) {
         throw new IllegalArgumentException("invalid actions mask");
      } else {
         this.mask = var2;
      }
   }

   public boolean implies(Permission var1) {
      if (!(var1 instanceof ServicePermission)) {
         return false;
      } else {
         ServicePermission var2 = (ServicePermission)var1;
         return (this.mask & var2.mask) == var2.mask && this.impliesIgnoreMask(var2);
      }
   }

   boolean impliesIgnoreMask(ServicePermission var1) {
      return this.getName().equals("*") || this.getName().equals(var1.getName()) || var1.getName().startsWith("@") && this.getName().endsWith(var1.getName());
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ServicePermission)) {
         return false;
      } else {
         ServicePermission var2 = (ServicePermission)var1;
         return (this.mask & var2.mask) == var2.mask && this.getName().equals(var2.getName());
      }
   }

   public int hashCode() {
      return this.getName().hashCode() ^ this.mask;
   }

   private static String getActions(int var0) {
      StringBuilder var1 = new StringBuilder();
      boolean var2 = false;
      if ((var0 & 1) == 1) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("initiate");
      }

      if ((var0 & 2) == 2) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("accept");
      }

      return var1.toString();
   }

   public String getActions() {
      if (this.actions == null) {
         this.actions = getActions(this.mask);
      }

      return this.actions;
   }

   public PermissionCollection newPermissionCollection() {
      return new KrbServicePermissionCollection();
   }

   int getMask() {
      return this.mask;
   }

   private static int getMask(String var0) {
      if (var0 == null) {
         throw new NullPointerException("action can't be null");
      } else if (var0.equals("")) {
         throw new IllegalArgumentException("action can't be empty");
      } else {
         int var1 = 0;
         char[] var2 = var0.toCharArray();
         if (var2.length == 1 && var2[0] == '-') {
            return var1;
         } else {
            byte var5;
            for(int var3 = var2.length - 1; var3 != -1; var3 -= var5) {
               char var4;
               while(var3 != -1 && ((var4 = var2[var3]) == ' ' || var4 == '\r' || var4 == '\n' || var4 == '\f' || var4 == '\t')) {
                  --var3;
               }

               if (var3 < 7 || var2[var3 - 7] != 'i' && var2[var3 - 7] != 'I' || var2[var3 - 6] != 'n' && var2[var3 - 6] != 'N' || var2[var3 - 5] != 'i' && var2[var3 - 5] != 'I' || var2[var3 - 4] != 't' && var2[var3 - 4] != 'T' || var2[var3 - 3] != 'i' && var2[var3 - 3] != 'I' || var2[var3 - 2] != 'a' && var2[var3 - 2] != 'A' || var2[var3 - 1] != 't' && var2[var3 - 1] != 'T' || var2[var3] != 'e' && var2[var3] != 'E') {
                  if (var3 < 5 || var2[var3 - 5] != 'a' && var2[var3 - 5] != 'A' || var2[var3 - 4] != 'c' && var2[var3 - 4] != 'C' || var2[var3 - 3] != 'c' && var2[var3 - 3] != 'C' || var2[var3 - 2] != 'e' && var2[var3 - 2] != 'E' || var2[var3 - 1] != 'p' && var2[var3 - 1] != 'P' || var2[var3] != 't' && var2[var3] != 'T') {
                     throw new IllegalArgumentException("invalid permission: " + var0);
                  }

                  var5 = 6;
                  var1 |= 2;
               } else {
                  var5 = 8;
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
            }

            return var1;
         }
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (this.actions == null) {
         this.getActions();
      }

      var1.defaultWriteObject();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.init(this.getName(), getMask(this.actions));
   }
}
