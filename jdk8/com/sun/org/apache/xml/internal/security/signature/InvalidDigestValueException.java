package com.sun.org.apache.xml.internal.security.signature;

public class InvalidDigestValueException extends XMLSignatureException {
   private static final long serialVersionUID = 1L;

   public InvalidDigestValueException() {
   }

   public InvalidDigestValueException(String var1) {
      super(var1);
   }

   public InvalidDigestValueException(String var1, Object[] var2) {
      super(var1, var2);
   }

   public InvalidDigestValueException(String var1, Exception var2) {
      super(var1, var2);
   }

   public InvalidDigestValueException(String var1, Object[] var2, Exception var3) {
      super(var1, var2, var3);
   }
}
