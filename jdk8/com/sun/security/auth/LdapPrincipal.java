package com.sun.security.auth;

import java.io.Serializable;
import java.security.Principal;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import jdk.Exported;

@Exported
public final class LdapPrincipal implements Principal, Serializable {
   private static final long serialVersionUID = 6820120005580754861L;
   private final String nameString;
   private final LdapName name;

   public LdapPrincipal(String var1) throws InvalidNameException {
      if (var1 == null) {
         throw new NullPointerException("null name is illegal");
      } else {
         this.name = this.getLdapName(var1);
         this.nameString = var1;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof LdapPrincipal) {
         try {
            return this.name.equals(this.getLdapName(((LdapPrincipal)var1).getName()));
         } catch (InvalidNameException var3) {
            return false;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public String getName() {
      return this.nameString;
   }

   public String toString() {
      return this.name.toString();
   }

   private LdapName getLdapName(String var1) throws InvalidNameException {
      return new LdapName(var1);
   }
}
