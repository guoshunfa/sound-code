package javax.net.ssl;

import java.security.Principal;
import java.security.cert.Certificate;
import javax.security.cert.X509Certificate;

public interface SSLSession {
   byte[] getId();

   SSLSessionContext getSessionContext();

   long getCreationTime();

   long getLastAccessedTime();

   void invalidate();

   boolean isValid();

   void putValue(String var1, Object var2);

   Object getValue(String var1);

   void removeValue(String var1);

   String[] getValueNames();

   Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException;

   Certificate[] getLocalCertificates();

   X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException;

   Principal getPeerPrincipal() throws SSLPeerUnverifiedException;

   Principal getLocalPrincipal();

   String getCipherSuite();

   String getProtocol();

   String getPeerHost();

   int getPeerPort();

   int getPacketBufferSize();

   int getApplicationBufferSize();
}
