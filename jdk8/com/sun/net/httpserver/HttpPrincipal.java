package com.sun.net.httpserver;

import java.security.Principal;
import jdk.Exported;

@Exported
public class HttpPrincipal implements Principal {
   private String username;
   private String realm;

   public HttpPrincipal(String var1, String var2) {
      if (var1 != null && var2 != null) {
         this.username = var1;
         this.realm = var2;
      } else {
         throw new NullPointerException();
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof HttpPrincipal)) {
         return false;
      } else {
         HttpPrincipal var2 = (HttpPrincipal)var1;
         return this.username.equals(var2.username) && this.realm.equals(var2.realm);
      }
   }

   public String getName() {
      return this.username;
   }

   public String getUsername() {
      return this.username;
   }

   public String getRealm() {
      return this.realm;
   }

   public int hashCode() {
      return (this.username + this.realm).hashCode();
   }

   public String toString() {
      return this.getName();
   }
}
