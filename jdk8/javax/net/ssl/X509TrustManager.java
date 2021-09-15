package javax.net.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public interface X509TrustManager extends TrustManager {
   void checkClientTrusted(X509Certificate[] var1, String var2) throws CertificateException;

   void checkServerTrusted(X509Certificate[] var1, String var2) throws CertificateException;

   X509Certificate[] getAcceptedIssuers();
}
