package com.sun.security.auth;

import java.io.Serializable;
import java.security.Principal;
import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class UnixPrincipal implements Principal, Serializable {
   private static final long serialVersionUID = -2951667807323493631L;
   private String name;

   public UnixPrincipal(String var1) {
      if (var1 == null) {
         MessageFormat var2 = new MessageFormat(ResourcesMgr.getString("invalid.null.input.value", "sun.security.util.AuthResources"));
         Object[] var3 = new Object[]{"name"};
         throw new NullPointerException(var2.format(var3));
      } else {
         this.name = var1;
      }
   }

   public String getName() {
      return this.name;
   }

   public String toString() {
      MessageFormat var1 = new MessageFormat(ResourcesMgr.getString("UnixPrincipal.name", "sun.security.util.AuthResources"));
      Object[] var2 = new Object[]{this.name};
      return var1.format(var2);
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (!(var1 instanceof UnixPrincipal)) {
         return false;
      } else {
         UnixPrincipal var2 = (UnixPrincipal)var1;
         return this.getName().equals(var2.getName());
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }
}
