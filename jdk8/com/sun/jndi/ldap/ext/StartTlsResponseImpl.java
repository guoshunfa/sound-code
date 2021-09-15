package com.sun.jndi.ldap.ext;

import com.sun.jndi.ldap.Connection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import sun.security.util.HostnameChecker;

public final class StartTlsResponseImpl extends StartTlsResponse {
   private static final boolean debug = false;
   private static final int DNSNAME_TYPE = 2;
   private transient String hostname = null;
   private transient Connection ldapConnection = null;
   private transient InputStream originalInputStream = null;
   private transient OutputStream originalOutputStream = null;
   private transient SSLSocket sslSocket = null;
   private transient SSLSocketFactory defaultFactory = null;
   private transient SSLSocketFactory currentFactory = null;
   private transient String[] suites = null;
   private transient HostnameVerifier verifier = null;
   private transient boolean isClosed = true;
   private static final long serialVersionUID = -1126624615143411328L;

   public void setEnabledCipherSuites(String[] var1) {
      this.suites = var1 == null ? null : (String[])var1.clone();
   }

   public void setHostnameVerifier(HostnameVerifier var1) {
      this.verifier = var1;
   }

   public SSLSession negotiate() throws IOException {
      return this.negotiate((SSLSocketFactory)null);
   }

   public SSLSession negotiate(SSLSocketFactory var1) throws IOException {
      if (this.isClosed && this.sslSocket != null) {
         throw new IOException("TLS connection is closed.");
      } else {
         if (var1 == null) {
            var1 = this.getDefaultFactory();
         }

         SSLSession var2 = this.startHandshake(var1).getSession();
         SSLPeerUnverifiedException var3 = null;

         try {
            if (this.verify(this.hostname, var2)) {
               this.isClosed = false;
               return var2;
            }
         } catch (SSLPeerUnverifiedException var5) {
            var3 = var5;
         }

         if (this.verifier != null && this.verifier.verify(this.hostname, var2)) {
            this.isClosed = false;
            return var2;
         } else {
            this.close();
            var2.invalidate();
            if (var3 == null) {
               var3 = new SSLPeerUnverifiedException("hostname of the server '" + this.hostname + "' does not match the hostname in the server's certificate.");
            }

            throw var3;
         }
      }
   }

   public void close() throws IOException {
      if (!this.isClosed) {
         this.ldapConnection.replaceStreams(this.originalInputStream, this.originalOutputStream);
         this.sslSocket.close();
         this.isClosed = true;
      }
   }

   public void setConnection(Connection var1, String var2) {
      this.ldapConnection = var1;
      this.hostname = var2 != null ? var2 : var1.host;
      this.originalInputStream = var1.inStream;
      this.originalOutputStream = var1.outStream;
   }

   private SSLSocketFactory getDefaultFactory() throws IOException {
      return this.defaultFactory != null ? this.defaultFactory : (this.defaultFactory = (SSLSocketFactory)SSLSocketFactory.getDefault());
   }

   private SSLSocket startHandshake(SSLSocketFactory var1) throws IOException {
      if (this.ldapConnection == null) {
         throw new IllegalStateException("LDAP connection has not been set. TLS requires an existing LDAP connection.");
      } else {
         if (var1 != this.currentFactory) {
            this.sslSocket = (SSLSocket)var1.createSocket(this.ldapConnection.sock, this.ldapConnection.host, this.ldapConnection.port, false);
            this.currentFactory = var1;
         }

         if (this.suites != null) {
            this.sslSocket.setEnabledCipherSuites(this.suites);
         }

         try {
            this.sslSocket.startHandshake();
            this.ldapConnection.replaceStreams(this.sslSocket.getInputStream(), this.sslSocket.getOutputStream());
         } catch (IOException var3) {
            this.sslSocket.close();
            this.isClosed = true;
            throw var3;
         }

         return this.sslSocket;
      }
   }

   private boolean verify(String var1, SSLSession var2) throws SSLPeerUnverifiedException {
      Certificate[] var3 = null;
      if (var1 != null && var1.startsWith("[") && var1.endsWith("]")) {
         var1 = var1.substring(1, var1.length() - 1);
      }

      try {
         HostnameChecker var4 = HostnameChecker.getInstance((byte)2);
         if (var2.getCipherSuite().startsWith("TLS_KRB5")) {
            Principal var8 = getPeerPrincipal(var2);
            if (!HostnameChecker.match(var1, var8)) {
               throw new SSLPeerUnverifiedException("hostname of the kerberos principal:" + var8 + " does not match the hostname:" + var1);
            }
         } else {
            var3 = var2.getPeerCertificates();
            if (!(var3[0] instanceof X509Certificate)) {
               throw new SSLPeerUnverifiedException("Received a non X509Certificate from the server");
            }

            X509Certificate var9 = (X509Certificate)var3[0];
            var4.match(var1, var9);
         }

         return true;
      } catch (SSLPeerUnverifiedException var6) {
         String var5 = var2.getCipherSuite();
         if (var5 != null && var5.indexOf("_anon_") != -1) {
            return true;
         } else {
            throw var6;
         }
      } catch (CertificateException var7) {
         throw (SSLPeerUnverifiedException)(new SSLPeerUnverifiedException("hostname of the server '" + var1 + "' does not match the hostname in the server's certificate.")).initCause(var7);
      }
   }

   private static Principal getPeerPrincipal(SSLSession var0) throws SSLPeerUnverifiedException {
      Principal var1;
      try {
         var1 = var0.getPeerPrincipal();
      } catch (AbstractMethodError var3) {
         var1 = null;
      }

      return var1;
   }
}
