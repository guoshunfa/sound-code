package java.security.spec;

import java.security.GeneralSecurityException;

public class InvalidKeySpecException extends GeneralSecurityException {
   private static final long serialVersionUID = 3546139293998810778L;

   public InvalidKeySpecException() {
   }

   public InvalidKeySpecException(String var1) {
      super(var1);
   }

   public InvalidKeySpecException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public InvalidKeySpecException(Throwable var1) {
      super(var1);
   }
}
