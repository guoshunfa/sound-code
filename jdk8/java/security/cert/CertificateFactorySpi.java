package java.security.cert;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class CertificateFactorySpi {
   public abstract Certificate engineGenerateCertificate(InputStream var1) throws CertificateException;

   public CertPath engineGenerateCertPath(InputStream var1) throws CertificateException {
      throw new UnsupportedOperationException();
   }

   public CertPath engineGenerateCertPath(InputStream var1, String var2) throws CertificateException {
      throw new UnsupportedOperationException();
   }

   public CertPath engineGenerateCertPath(List<? extends Certificate> var1) throws CertificateException {
      throw new UnsupportedOperationException();
   }

   public Iterator<String> engineGetCertPathEncodings() {
      throw new UnsupportedOperationException();
   }

   public abstract Collection<? extends Certificate> engineGenerateCertificates(InputStream var1) throws CertificateException;

   public abstract CRL engineGenerateCRL(InputStream var1) throws CRLException;

   public abstract Collection<? extends CRL> engineGenerateCRLs(InputStream var1) throws CRLException;
}
