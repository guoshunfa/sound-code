package com.sun.corba.se.impl.orbutil;

import java.security.PrivilegedAction;

public class GetPropertyAction implements PrivilegedAction {
   private String theProp;
   private String defaultVal;

   public GetPropertyAction(String var1) {
      this.theProp = var1;
   }

   public GetPropertyAction(String var1, String var2) {
      this.theProp = var1;
      this.defaultVal = var2;
   }

   public Object run() {
      String var1 = System.getProperty(this.theProp);
      return var1 == null ? this.defaultVal : var1;
   }
}
