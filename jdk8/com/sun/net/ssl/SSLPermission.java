package com.sun.net.ssl;

import java.security.BasicPermission;

/** @deprecated */
@Deprecated
public final class SSLPermission extends BasicPermission {
   private static final long serialVersionUID = -2583684302506167542L;

   public SSLPermission(String var1) {
      super(var1);
   }

   public SSLPermission(String var1, String var2) {
      super(var1, var2);
   }
}
