package com.sun.rmi.rmid;

import java.io.FilePermission;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Vector;

public final class ExecPermission extends Permission {
   private static final long serialVersionUID = -6208470287358147919L;
   private transient FilePermission fp;

   public ExecPermission(String var1) {
      super(var1);
      this.init(var1);
   }

   public ExecPermission(String var1, String var2) {
      this(var1);
   }

   public boolean implies(Permission var1) {
      if (!(var1 instanceof ExecPermission)) {
         return false;
      } else {
         ExecPermission var2 = (ExecPermission)var1;
         return this.fp.implies(var2.fp);
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ExecPermission)) {
         return false;
      } else {
         ExecPermission var2 = (ExecPermission)var1;
         return this.fp.equals(var2.fp);
      }
   }

   public int hashCode() {
      return this.fp.hashCode();
   }

   public String getActions() {
      return "";
   }

   public PermissionCollection newPermissionCollection() {
      return new ExecPermission.ExecPermissionCollection();
   }

   private synchronized void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.init(this.getName());
   }

   private void init(String var1) {
      this.fp = new FilePermission(var1, "execute");
   }

   private static class ExecPermissionCollection extends PermissionCollection implements Serializable {
      private Vector<Permission> permissions = new Vector();
      private static final long serialVersionUID = -3352558508888368273L;

      public ExecPermissionCollection() {
      }

      public void add(Permission var1) {
         if (!(var1 instanceof ExecPermission)) {
            throw new IllegalArgumentException("invalid permission: " + var1);
         } else if (this.isReadOnly()) {
            throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
         } else {
            this.permissions.addElement(var1);
         }
      }

      public boolean implies(Permission var1) {
         if (!(var1 instanceof ExecPermission)) {
            return false;
         } else {
            Enumeration var2 = this.permissions.elements();

            ExecPermission var3;
            do {
               if (!var2.hasMoreElements()) {
                  return false;
               }

               var3 = (ExecPermission)var2.nextElement();
            } while(!var3.implies(var1));

            return true;
         }
      }

      public Enumeration<Permission> elements() {
         return this.permissions.elements();
      }
   }
}
