package com.sun.org.apache.xml.internal.security.signature;

public class ReferenceNotInitializedException extends XMLSignatureException {
   private static final long serialVersionUID = 1L;

   public ReferenceNotInitializedException() {
   }

   public ReferenceNotInitializedException(String var1) {
      super(var1);
   }

   public ReferenceNotInitializedException(String var1, Object[] var2) {
      super(var1, var2);
   }

   public ReferenceNotInitializedException(String var1, Exception var2) {
      super(var1, var2);
   }

   public ReferenceNotInitializedException(String var1, Object[] var2, Exception var3) {
      super(var1, var2, var3);
   }
}
