package com.sun.org.apache.xml.internal.security.c14n;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class CanonicalizationException extends XMLSecurityException {
   private static final long serialVersionUID = 1L;

   public CanonicalizationException() {
   }

   public CanonicalizationException(String var1) {
      super(var1);
   }

   public CanonicalizationException(String var1, Object[] var2) {
      super(var1, var2);
   }

   public CanonicalizationException(String var1, Exception var2) {
      super(var1, var2);
   }

   public CanonicalizationException(String var1, Object[] var2, Exception var3) {
      super(var1, var2, var3);
   }
}
