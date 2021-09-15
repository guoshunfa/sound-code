package com.sun.org.apache.xml.internal.security.signature;

public class InvalidSignatureValueException extends XMLSignatureException {
   private static final long serialVersionUID = 1L;

   public InvalidSignatureValueException() {
   }

   public InvalidSignatureValueException(String var1) {
      super(var1);
   }

   public InvalidSignatureValueException(String var1, Object[] var2) {
      super(var1, var2);
   }

   public InvalidSignatureValueException(String var1, Exception var2) {
      super(var1, var2);
   }

   public InvalidSignatureValueException(String var1, Object[] var2, Exception var3) {
      super(var1, var2, var3);
   }
}
