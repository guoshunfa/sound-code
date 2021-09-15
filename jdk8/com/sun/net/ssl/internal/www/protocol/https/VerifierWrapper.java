package com.sun.net.ssl.internal.www.protocol.https;

import java.io.IOException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import sun.security.util.DerValue;
import sun.security.util.HostnameChecker;
import sun.security.x509.X500Name;

class VerifierWrapper implements HostnameVerifier {
   private com.sun.net.ssl.HostnameVerifier verifier;

   VerifierWrapper(com.sun.net.ssl.HostnameVerifier var1) {
      this.verifier = var1;
   }

   public boolean verify(String var1, SSLSession var2) {
      try {
         String var3;
         if (var2.getCipherSuite().startsWith("TLS_KRB5")) {
            var3 = HostnameChecker.getServerName(this.getPeerPrincipal(var2));
         } else {
            Certificate[] var4 = var2.getPeerCertificates();
            if (var4 == null || var4.length == 0) {
               return false;
            }

            if (!(var4[0] instanceof X509Certificate)) {
               return false;
            }

            X509Certificate var5 = (X509Certificate)var4[0];
            var3 = getServername(var5);
         }

         if (var3 == null) {
            return false;
         } else {
            return this.verifier.verify(var1, var3);
         }
      } catch (SSLPeerUnverifiedException var6) {
         return false;
      }
   }

   private Principal getPeerPrincipal(SSLSession var1) throws SSLPeerUnverifiedException {
      Principal var2;
      try {
         var2 = var1.getPeerPrincipal();
      } catch (AbstractMethodError var4) {
         var2 = null;
      }

      return var2;
   }

   private static String getServername(X509Certificate var0) {
      try {
         Collection var1 = var0.getSubjectAlternativeNames();
         String var4;
         if (var1 != null) {
            Iterator var2 = var1.iterator();

            while(var2.hasNext()) {
               List var3 = (List)var2.next();
               if ((Integer)var3.get(0) == 2) {
                  var4 = (String)var3.get(1);
                  return var4;
               }
            }
         }

         X500Name var7 = HostnameChecker.getSubjectX500Name(var0);
         DerValue var8 = var7.findMostSpecificAttribute(X500Name.commonName_oid);
         if (var8 != null) {
            try {
               var4 = var8.getAsString();
               return var4;
            } catch (IOException var5) {
            }
         }
      } catch (CertificateException var6) {
      }

      return null;
   }
}
