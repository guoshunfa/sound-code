package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class XMLSignatureException extends XMLSecurityException {
   private static final long serialVersionUID = 1L;

   public XMLSignatureException() {
   }

   public XMLSignatureException(String var1) {
      super(var1);
   }

   public XMLSignatureException(String var1, Object[] var2) {
      super(var1, var2);
   }

   public XMLSignatureException(String var1, Exception var2) {
      super(var1, var2);
   }

   public XMLSignatureException(String var1, Object[] var2, Exception var3) {
      super(var1, var2, var3);
   }
}
