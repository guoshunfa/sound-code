package sun.security.provider.certpath.ssl;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

public final class SSLServerCertStore extends CertStoreSpi {
   private final URI uri;
   private static final SSLServerCertStore.GetChainTrustManager trustManager = new SSLServerCertStore.GetChainTrustManager();
   private static final SSLSocketFactory socketFactory;
   private static final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
      public boolean verify(String var1, SSLSession var2) {
         return true;
      }
   };

   SSLServerCertStore(URI var1) throws InvalidAlgorithmParameterException {
      super((CertStoreParameters)null);
      this.uri = var1;
   }

   public Collection<X509Certificate> engineGetCertificates(CertSelector var1) throws CertStoreException {
      try {
         URLConnection var2 = this.uri.toURL().openConnection();
         if (var2 instanceof HttpsURLConnection) {
            if (socketFactory == null) {
               throw new CertStoreException("No initialized SSLSocketFactory");
            }

            HttpsURLConnection var3 = (HttpsURLConnection)var2;
            var3.setSSLSocketFactory(socketFactory);
            var3.setHostnameVerifier(hostnameVerifier);
            synchronized(trustManager) {
               List var6;
               try {
                  var3.connect();
                  List var5 = getMatchingCerts(trustManager.serverChain, var1);
                  return var5;
               } catch (IOException var13) {
                  if (!trustManager.exchangedServerCerts) {
                     throw var13;
                  }

                  var6 = getMatchingCerts(trustManager.serverChain, var1);
               } finally {
                  trustManager.cleanup();
               }

               return var6;
            }
         }
      } catch (IOException var16) {
         throw new CertStoreException(var16);
      }

      return Collections.emptySet();
   }

   private static List<X509Certificate> getMatchingCerts(List<X509Certificate> var0, CertSelector var1) {
      if (var1 == null) {
         return var0;
      } else {
         ArrayList var2 = new ArrayList(var0.size());
         Iterator var3 = var0.iterator();

         while(var3.hasNext()) {
            X509Certificate var4 = (X509Certificate)var3.next();
            if (var1.match(var4)) {
               var2.add(var4);
            }
         }

         return var2;
      }
   }

   public Collection<X509CRL> engineGetCRLs(CRLSelector var1) throws CertStoreException {
      throw new UnsupportedOperationException();
   }

   static CertStore getInstance(URI var0) throws InvalidAlgorithmParameterException {
      return new SSLServerCertStore.CS(new SSLServerCertStore(var0), (Provider)null, "SSLServer", (CertStoreParameters)null);
   }

   static {
      SSLSocketFactory var0;
      try {
         SSLContext var1 = SSLContext.getInstance("SSL");
         var1.init((KeyManager[])null, new TrustManager[]{trustManager}, (SecureRandom)null);
         var0 = var1.getSocketFactory();
      } catch (GeneralSecurityException var2) {
         var0 = null;
      }

      socketFactory = var0;
   }

   private static class CS extends CertStore {
      protected CS(CertStoreSpi var1, Provider var2, String var3, CertStoreParameters var4) {
         super(var1, var2, var3, var4);
      }
   }

   private static class GetChainTrustManager extends X509ExtendedTrustManager {
      private List<X509Certificate> serverChain;
      private boolean exchangedServerCerts;

      private GetChainTrustManager() {
         this.serverChain = Collections.emptyList();
         this.exchangedServerCerts = false;
      }

      public X509Certificate[] getAcceptedIssuers() {
         return new X509Certificate[0];
      }

      public void checkClientTrusted(X509Certificate[] var1, String var2) throws CertificateException {
         throw new UnsupportedOperationException();
      }

      public void checkClientTrusted(X509Certificate[] var1, String var2, Socket var3) throws CertificateException {
         throw new UnsupportedOperationException();
      }

      public void checkClientTrusted(X509Certificate[] var1, String var2, SSLEngine var3) throws CertificateException {
         throw new UnsupportedOperationException();
      }

      public void checkServerTrusted(X509Certificate[] var1, String var2) throws CertificateException {
         this.exchangedServerCerts = true;
         this.serverChain = var1 == null ? Collections.emptyList() : Arrays.asList(var1);
      }

      public void checkServerTrusted(X509Certificate[] var1, String var2, Socket var3) throws CertificateException {
         this.checkServerTrusted(var1, var2);
      }

      public void checkServerTrusted(X509Certificate[] var1, String var2, SSLEngine var3) throws CertificateException {
         this.checkServerTrusted(var1, var2);
      }

      void cleanup() {
         this.exchangedServerCerts = false;
         this.serverChain = Collections.emptyList();
      }

      // $FF: synthetic method
      GetChainTrustManager(Object var1) {
         this();
      }
   }
}
