package com.oracle.nio;

import java.security.BasicPermission;

public final class BufferSecretsPermission extends BasicPermission {
   private static final long serialVersionUID = 0L;

   public BufferSecretsPermission(String var1) {
      super(var1);
      if (!var1.equals("access")) {
         throw new IllegalArgumentException();
      }
   }
}
