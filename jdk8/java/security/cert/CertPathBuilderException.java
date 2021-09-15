package java.security.cert;

import java.security.GeneralSecurityException;

public class CertPathBuilderException extends GeneralSecurityException {
   private static final long serialVersionUID = 5316471420178794402L;

   public CertPathBuilderException() {
   }

   public CertPathBuilderException(String var1) {
      super(var1);
   }

   public CertPathBuilderException(Throwable var1) {
      super(var1);
   }

   public CertPathBuilderException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
