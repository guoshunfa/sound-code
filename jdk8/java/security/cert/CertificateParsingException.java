package java.security.cert;

public class CertificateParsingException extends CertificateException {
   private static final long serialVersionUID = -7989222416793322029L;

   public CertificateParsingException() {
   }

   public CertificateParsingException(String var1) {
      super(var1);
   }

   public CertificateParsingException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public CertificateParsingException(Throwable var1) {
      super(var1);
   }
}
