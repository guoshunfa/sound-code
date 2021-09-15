package java.security.cert;

import java.security.GeneralSecurityException;

public class CertificateException extends GeneralSecurityException {
   private static final long serialVersionUID = 3192535253797119798L;

   public CertificateException() {
   }

   public CertificateException(String var1) {
      super(var1);
   }

   public CertificateException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public CertificateException(Throwable var1) {
      super(var1);
   }
}
