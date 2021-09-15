package com.sun.security.auth;

import java.io.Serializable;
import java.security.Principal;
import jdk.Exported;

@Exported
public final class UserPrincipal implements Principal, Serializable {
   private static final long serialVersionUID = 892106070870210969L;
   private final String name;

   public UserPrincipal(String var1) {
      if (var1 == null) {
         throw new NullPointerException("null name is illegal");
      } else {
         this.name = var1;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof UserPrincipal ? this.name.equals(((UserPrincipal)var1).getName()) : false;
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public String getName() {
      return this.name;
   }

   public String toString() {
      return this.name;
   }
}
