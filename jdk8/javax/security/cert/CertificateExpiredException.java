package javax.security.cert;

public class CertificateExpiredException extends CertificateException {
   private static final long serialVersionUID = 5091601212177261883L;

   public CertificateExpiredException() {
   }

   public CertificateExpiredException(String var1) {
      super(var1);
   }
}
