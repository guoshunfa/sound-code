package java.security.cert;

public interface CertPathChecker {
   void init(boolean var1) throws CertPathValidatorException;

   boolean isForwardCheckingSupported();

   void check(Certificate var1) throws CertPathValidatorException;
}
