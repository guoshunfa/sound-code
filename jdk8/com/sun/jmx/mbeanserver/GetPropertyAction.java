package com.sun.jmx.mbeanserver;

import java.security.PrivilegedAction;

public class GetPropertyAction implements PrivilegedAction<String> {
   private final String key;

   public GetPropertyAction(String var1) {
      this.key = var1;
   }

   public String run() {
      return System.getProperty(this.key);
   }
}
