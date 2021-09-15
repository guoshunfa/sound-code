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
public class SolarisNumericGroupPrincipal implements Principal, Serializable {
   private static final long serialVersionUID = 2345199581042573224L;
   private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
      public ResourceBundle run() {
         return ResourceBundle.getBundle("sun.security.util.AuthResources");
      }
   });
   private String name;
   private boolean primaryGroup;

   public SolarisNumericGroupPrincipal(String var1, boolean var2) {
      if (var1 == null) {
         throw new NullPointerException(rb.getString("provided.null.name"));
      } else {
         this.name = var1;
         this.primaryGroup = var2;
      }
   }

   public SolarisNumericGroupPrincipal(long var1, boolean var3) {
      this.name = (new Long(var1)).toString();
      this.primaryGroup = var3;
   }

   public String getName() {
      return this.name;
   }

   public long longValue() {
      return new Long(this.name);
   }

   public boolean isPrimaryGroup() {
      return this.primaryGroup;
   }

   public String toString() {
      return this.primaryGroup ? rb.getString("SolarisNumericGroupPrincipal.Primary.Group.") + this.name : rb.getString("SolarisNumericGroupPrincipal.Supplementary.Group.") + this.name;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (!(var1 instanceof SolarisNumericGroupPrincipal)) {
         return false;
      } else {
         SolarisNumericGroupPrincipal var2 = (SolarisNumericGroupPrincipal)var1;
         return this.getName().equals(var2.getName()) && this.isPrimaryGroup() == var2.isPrimaryGroup();
      }
   }

   public int hashCode() {
      return this.toString().hashCode();
   }
}
