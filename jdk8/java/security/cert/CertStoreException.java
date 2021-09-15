package java.security.cert;

import java.security.GeneralSecurityException;

public class CertStoreException extends GeneralSecurityException {
   private static final long serialVersionUID = 2395296107471573245L;

   public CertStoreException() {
   }

   public CertStoreException(String var1) {
      super(var1);
   }

   public CertStoreException(Throwable var1) {
      super(var1);
   }

   public CertStoreException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
