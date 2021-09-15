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
public class SolarisPrincipal implements Principal, Serializable {
   private static final long serialVersionUID = -7840670002439379038L;
   private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
      public ResourceBundle run() {
         return ResourceBundle.getBundle("sun.security.util.AuthResources");
      }
   });
   private String name;

   public SolarisPrincipal(String var1) {
      if (var1 == null) {
         throw new NullPointerException(rb.getString("provided.null.name"));
      } else {
         this.name = var1;
      }
   }

   public String getName() {
      return this.name;
   }

   public String toString() {
      return rb.getString("SolarisPrincipal.") + this.name;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (!(var1 instanceof SolarisPrincipal)) {
         return false;
      } else {
         SolarisPrincipal var2 = (SolarisPrincipal)var1;
         return this.getName().equals(var2.getName());
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }
}
