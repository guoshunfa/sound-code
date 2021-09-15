package com.sun.org.apache.xml.internal.security.exceptions;

public class AlgorithmAlreadyRegisteredException extends XMLSecurityException {
   private static final long serialVersionUID = 1L;

   public AlgorithmAlreadyRegisteredException() {
   }

   public AlgorithmAlreadyRegisteredException(String var1) {
      super(var1);
   }

   public AlgorithmAlreadyRegisteredException(String var1, Object[] var2) {
      super(var1, var2);
   }

   public AlgorithmAlreadyRegisteredException(String var1, Exception var2) {
      super(var1, var2);
   }

   public AlgorithmAlreadyRegisteredException(String var1, Object[] var2, Exception var3) {
      super(var1, var2, var3);
   }
}
