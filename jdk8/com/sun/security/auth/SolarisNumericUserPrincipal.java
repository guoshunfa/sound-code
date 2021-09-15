package com.sun.security.auth;

import java.io.Serializable;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ResourceBundle;
import jdk.Exported;

/** @deprecated */
@Exported(false)
@Deprecated
public class SolarisNumericUserPrincipal implements Principal, Serializable {
   private static final long serialVersionUID = -3178578484679887104L;
   private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
      public ResourceBundle run() {
         return ResourceBundle.getBundle("sun.security.util.AuthResources");
      }
   });
   private String name;

   public SolarisNumericUserPrincipal(String var1) {
      if (var1 == null) {
         throw new NullPointerException(rb.getString("provided.null.name"));
      } else {
         this.name = var1;
      }
   }

   public SolarisNumericUserPrincipal(long var1) {
      this.name = (new Long(var1)).toString();
   }

   public String getName() {
      return this.name;
   }

   public long longValue() {
      return new Long(this.name);
   }

   public String toString() {
      return rb.getString("SolarisNumericUserPrincipal.") + this.name;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (!(var1 instanceof SolarisNumericUserPrincipal)) {
         return false;
      } else {
         SolarisNumericUserPrincipal var2 = (SolarisNumericUserPrincipal)var1;
         return this.getName().equals(var2.getName());
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }
}
