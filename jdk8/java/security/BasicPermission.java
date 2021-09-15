package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public abstract class BasicPermission extends Permission implements Serializable {
   private static final long serialVersionUID = 6279438298436773498L;
   private transient boolean wildcard;
   private transient String path;
   private transient boolean exitVM;

   private void init(String var1) {
      if (var1 == null) {
         throw new NullPointerException("name can't be null");
      } else {
         int var2 = var1.length();
         if (var2 == 0) {
            throw new IllegalArgumentException("name can't be empty");
         } else {
            char var3 = var1.charAt(var2 - 1);
            if (var3 == '*' && (var2 == 1 || var1.charAt(var2 - 2) == '.')) {
               this.wildcard = true;
               if (var2 == 1) {
                  this.path = "";
               } else {
                  this.path = var1.substring(0, var2 - 1);
               }
            } else if (var1.equals("exitVM")) {
               this.wildcard = true;
               this.path = "exitVM.";
               this.exitVM = true;
            } else {
               this.path = var1;
            }

         }
      }
   }

   public BasicPermission(String var1) {
      super(var1);
      this.init(var1);
   }

   public BasicPermission(String var1, String var2) {
      super(var1);
      this.init(var1);
   }

   public boolean implies(Permission var1) {
      if (var1 != null && var1.getClass() == this.getClass()) {
         BasicPermission var2 = (BasicPermission)var1;
         if (this.wildcard) {
            if (var2.wildcard) {
               return var2.path.startsWith(this.path);
            } else {
               return var2.path.length() > this.path.length() && var2.path.startsWith(this.path);
            }
         } else {
            return var2.wildcard ? false : this.path.equals(var2.path);
         }
      } else {
         return false;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 != null && var1.getClass() == this.getClass()) {
         BasicPermission var2 = (BasicPermission)var1;
         return this.getName().equals(var2.getName());
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.getName().hashCode();
   }

   public String getActions() {
      return "";
   }

   public PermissionCollection newPermissionCollection() {
      return new BasicPermissionCollection(this.getClass());
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.init(this.getName());
   }

   final String getCanonicalName() {
      return this.exitVM ? "exitVM.*" : this.getName();
   }
}
