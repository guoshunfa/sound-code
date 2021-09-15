package com.sun.org.apache.xml.internal.security.transforms;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class InvalidTransformException extends XMLSecurityException {
   private static final long serialVersionUID = 1L;

   public InvalidTransformException() {
   }

   public InvalidTransformException(String var1) {
      super(var1);
   }

   public InvalidTransformException(String var1, Object[] var2) {
      super(var1, var2);
   }

   public InvalidTransformException(String var1, Exception var2) {
      super(var1, var2);
   }

   public InvalidTransformException(String var1, Object[] var2, Exception var3) {
      super(var1, var2, var3);
   }
}
