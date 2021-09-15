package java.security.cert;

import java.security.InvalidAlgorithmParameterException;
import java.util.Collection;

public abstract class CertStoreSpi {
   public CertStoreSpi(CertStoreParameters var1) throws InvalidAlgorithmParameterException {
   }

   public abstract Collection<? extends Certificate> engineGetCertificates(CertSelector var1) throws CertStoreException;

   public abstract Collection<? extends CRL> engineGetCRLs(CRLSelector var1) throws CertStoreException;
}
