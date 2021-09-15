package javax.net.ssl;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.SecureRandom;

public abstract class SSLContextSpi {
   protected abstract void engineInit(KeyManager[] var1, TrustManager[] var2, SecureRandom var3) throws KeyManagementException;

   protected abstract SSLSocketFactory engineGetSocketFactory();

   protected abstract SSLServerSocketFactory engineGetServerSocketFactory();

   protected abstract SSLEngine engineCreateSSLEngine();

   protected abstract SSLEngine engineCreateSSLEngine(String var1, int var2);

   protected abstract SSLSessionContext engineGetServerSessionContext();

   protected abstract SSLSessionContext engineGetClientSessionContext();

   private SSLSocket getDefaultSocket() {
      try {
         SSLSocketFactory var1 = this.engineGetSocketFactory();
         return (SSLSocket)var1.createSocket();
      } catch (IOException var2) {
         throw new UnsupportedOperationException("Could not obtain parameters", var2);
      }
   }

   protected SSLParameters engineGetDefaultSSLParameters() {
      SSLSocket var1 = this.getDefaultSocket();
      return var1.getSSLParameters();
   }

   protected SSLParameters engineGetSupportedSSLParameters() {
      SSLSocket var1 = this.getDefaultSocket();
      SSLParameters var2 = new SSLParameters();
      var2.setCipherSuites(var1.getSupportedCipherSuites());
      var2.setProtocols(var1.getSupportedProtocols());
      return var2;
   }
}
