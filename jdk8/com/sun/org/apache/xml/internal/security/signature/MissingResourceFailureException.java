package com.sun.org.apache.xml.internal.security.signature;

public class MissingResourceFailureException extends XMLSignatureException {
   private static final long serialVersionUID = 1L;
   private Reference uninitializedReference = null;

   public MissingResourceFailureException(String var1, Reference var2) {
      super(var1);
      this.uninitializedReference = var2;
   }

   public MissingResourceFailureException(String var1, Object[] var2, Reference var3) {
      super(var1, var2);
      this.uninitializedReference = var3;
   }

   public MissingResourceFailureException(String var1, Exception var2, Reference var3) {
      super(var1, var2);
      this.uninitializedReference = var3;
   }

   public MissingResourceFailureException(String var1, Object[] var2, Exception var3, Reference var4) {
      super(var1, var2, var3);
      this.uninitializedReference = var4;
   }

   public void setReference(Reference var1) {
      this.uninitializedReference = var1;
   }

   public Reference getReference() {
      return this.uninitializedReference;
   }
}
