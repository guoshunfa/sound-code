package com.sun.rmi.rmid;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Hashtable;

public final class ExecOptionPermission extends Permission {
   private transient boolean wildcard;
   private transient String name;
   private static final long serialVersionUID = 5842294756823092756L;

   public ExecOptionPermission(String var1) {
      super(var1);
      this.init(var1);
   }

   public ExecOptionPermission(String var1, String var2) {
      this(var1);
   }

   public boolean implies(Permission var1) {
      if (!(var1 instanceof ExecOptionPermission)) {
         return false;
      } else {
         ExecOptionPermission var2 = (ExecOptionPermission)var1;
         if (this.wildcard) {
            if (var2.wildcard) {
               return var2.name.startsWith(this.name);
            } else {
               return var2.name.length() > this.name.length() && var2.name.startsWith(this.name);
            }
         } else {
            return var2.wildcard ? false : this.name.equals(var2.name);
         }
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 != null && var1.getClass() == this.getClass()) {
         ExecOptionPermission var2 = (ExecOptionPermission)var1;
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
      return new ExecOptionPermission.ExecOptionPermissionCollection();
   }

   private synchronized void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.init(this.getName());
   }

   private void init(String var1) {
      if (var1 == null) {
         throw new NullPointerException("name can't be null");
      } else if (var1.equals("")) {
         throw new IllegalArgumentException("name can't be empty");
      } else {
         if (!var1.endsWith(".*") && !var1.endsWith("=*") && !var1.equals("*")) {
            this.name = var1;
         } else {
            this.wildcard = true;
            if (var1.length() == 1) {
               this.name = "";
            } else {
               this.name = var1.substring(0, var1.length() - 1);
            }
         }

      }
   }

   private static class ExecOptionPermissionCollection extends PermissionCollection implements Serializable {
      private Hashtable<String, Permission> permissions = new Hashtable(11);
      private boolean all_allowed = false;
      private static final long serialVersionUID = -1242475729790124375L;

      public ExecOptionPermissionCollection() {
      }

      public void add(Permission var1) {
         if (!(var1 instanceof ExecOptionPermission)) {
            throw new IllegalArgumentException("invalid permission: " + var1);
         } else if (this.isReadOnly()) {
            throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
         } else {
            ExecOptionPermission var2 = (ExecOptionPermission)var1;
            this.permissions.put(var2.getName(), var1);
            if (!this.all_allowed && var2.getName().equals("*")) {
               this.all_allowed = true;
            }

         }
      }

      public boolean implies(Permission var1) {
         if (!(var1 instanceof ExecOptionPermission)) {
            return false;
         } else {
            ExecOptionPermission var2 = (ExecOptionPermission)var1;
            if (this.all_allowed) {
               return true;
            } else {
               String var3 = var2.getName();
               Permission var4 = (Permission)this.permissions.get(var3);
               if (var4 != null) {
                  return var4.implies(var1);
               } else {
                  int var5;
                  int var6;
                  for(var6 = var3.length() - 1; (var5 = var3.lastIndexOf(".", var6)) != -1; var6 = var5 - 1) {
                     var3 = var3.substring(0, var5 + 1) + "*";
                     var4 = (Permission)this.permissions.get(var3);
                     if (var4 != null) {
                        return var4.implies(var1);
                     }
                  }

                  var3 = var2.getName();

                  for(var6 = var3.length() - 1; (var5 = var3.lastIndexOf("=", var6)) != -1; var6 = var5 - 1) {
                     var3 = var3.substring(0, var5 + 1) + "*";
                     var4 = (Permission)this.permissions.get(var3);
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
         return this.permissions.elements();
      }
   }
}
