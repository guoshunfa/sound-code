package sun.security.provider.certpath;

import java.io.IOException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

interface State extends Cloneable {
   void updateState(X509Certificate var1) throws CertificateException, IOException, CertPathValidatorException;

   Object clone();

   boolean isInitial();

   boolean keyParamsNeeded();
}
