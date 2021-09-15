package javax.net.ssl;

import java.security.Principal;
import java.security.cert.Certificate;
import java.util.EventObject;
import javax.security.cert.X509Certificate;

public class HandshakeCompletedEvent extends EventObject {
   private static final long serialVersionUID = 7914963744257769778L;
   private transient SSLSession session;

   public HandshakeCompletedEvent(SSLSocket var1, SSLSession var2) {
      super(var1);
      this.session = var2;
   }

   public SSLSession getSession() {
      return this.session;
   }

   public String getCipherSuite() {
      return this.session.getCipherSuite();
   }

   public Certificate[] getLocalCertificates() {
      return this.session.getLocalCertificates();
   }

   public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
      return this.session.getPeerCertificates();
   }

   public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
      return this.session.getPeerCertificateChain();
   }

   public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
      Object var1;
      try {
         var1 = this.session.getPeerPrincipal();
      } catch (AbstractMethodError var4) {
         Certificate[] var3 = this.getPeerCertificates();
         var1 = ((java.security.cert.X509Certificate)var3[0]).getSubjectX500Principal();
      }

      return (Principal)var1;
   }

   public Principal getLocalPrincipal() {
      Object var1;
      try {
         var1 = this.session.getLocalPrincipal();
      } catch (AbstractMethodError var4) {
         var1 = null;
         Certificate[] var3 = this.getLocalCertificates();
         if (var3 != null) {
            var1 = ((java.security.cert.X509Certificate)var3[0]).getSubjectX500Principal();
         }
      }

      return (Principal)var1;
   }

   public SSLSocket getSocket() {
      return (SSLSocket)this.getSource();
   }
}
