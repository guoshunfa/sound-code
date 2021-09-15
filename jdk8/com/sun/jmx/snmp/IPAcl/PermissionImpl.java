package com.sun.jmx.snmp.IPAcl;

import java.io.Serializable;
import java.security.acl.Permission;

class PermissionImpl implements Permission, Serializable {
   private static final long serialVersionUID = 4478110422746916589L;
   private String perm = null;

   public PermissionImpl(String var1) {
      this.perm = var1;
   }

   public int hashCode() {
      return super.hashCode();
   }

   public boolean equals(Object var1) {
      return var1 instanceof PermissionImpl ? this.perm.equals(((PermissionImpl)var1).getString()) : false;
   }

   public String toString() {
      return this.perm;
   }

   public String getString() {
      return this.perm;
   }
}
