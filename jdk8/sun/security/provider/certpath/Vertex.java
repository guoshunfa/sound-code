package sun.security.provider.certpath;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import sun.security.util.Debug;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.SubjectKeyIdentifierExtension;
import sun.security.x509.X509CertImpl;

public class Vertex {
   private static final Debug debug = Debug.getInstance("certpath");
   private X509Certificate cert;
   private int index;
   private Throwable throwable;

   Vertex(X509Certificate var1) {
      this.cert = var1;
      this.index = -1;
   }

   public X509Certificate getCertificate() {
      return this.cert;
   }

   public int getIndex() {
      return this.index;
   }

   void setIndex(int var1) {
      this.index = var1;
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   void setThrowable(Throwable var1) {
      this.throwable = var1;
   }

   public String toString() {
      return this.certToString() + this.throwableToString() + this.indexToString();
   }

   public String certToString() {
      StringBuilder var1 = new StringBuilder();
      X509CertImpl var2 = null;

      try {
         var2 = X509CertImpl.toImpl(this.cert);
      } catch (CertificateException var9) {
         if (debug != null) {
            debug.println("Vertex.certToString() unexpected exception");
            var9.printStackTrace();
         }

         return var1.toString();
      }

      var1.append("Issuer:     ").append((Object)var2.getIssuerX500Principal()).append("\n");
      var1.append("Subject:    ").append((Object)var2.getSubjectX500Principal()).append("\n");
      var1.append("SerialNum:  ").append(var2.getSerialNumber().toString(16)).append("\n");
      var1.append("Expires:    ").append(var2.getNotAfter().toString()).append("\n");
      boolean[] var3 = var2.getIssuerUniqueID();
      boolean[] var4;
      int var6;
      if (var3 != null) {
         var1.append("IssuerUID:  ");
         var4 = var3;
         int var5 = var3.length;

         for(var6 = 0; var6 < var5; ++var6) {
            boolean var7 = var4[var6];
            var1.append(var7 ? 1 : 0);
         }

         var1.append("\n");
      }

      var4 = var2.getSubjectUniqueID();
      if (var4 != null) {
         var1.append("SubjectUID: ");
         boolean[] var11 = var4;
         var6 = var4.length;

         for(int var14 = 0; var14 < var6; ++var14) {
            boolean var8 = var11[var14];
            var1.append(var8 ? 1 : 0);
         }

         var1.append("\n");
      }

      try {
         SubjectKeyIdentifierExtension var12 = var2.getSubjectKeyIdentifierExtension();
         if (var12 != null) {
            KeyIdentifier var13 = var12.get("key_id");
            var1.append("SubjKeyID:  ").append(var13.toString());
         }

         AuthorityKeyIdentifierExtension var15 = var2.getAuthorityKeyIdentifierExtension();
         if (var15 != null) {
            KeyIdentifier var16 = (KeyIdentifier)var15.get("key_id");
            var1.append("AuthKeyID:  ").append(var16.toString());
         }
      } catch (IOException var10) {
         if (debug != null) {
            debug.println("Vertex.certToString() unexpected exception");
            var10.printStackTrace();
         }
      }

      return var1.toString();
   }

   public String throwableToString() {
      StringBuilder var1 = new StringBuilder("Exception:  ");
      if (this.throwable != null) {
         var1.append(this.throwable.toString());
      } else {
         var1.append("null");
      }

      var1.append("\n");
      return var1.toString();
   }

   public String moreToString() {
      StringBuilder var1 = new StringBuilder("Last cert?  ");
      var1.append(this.index == -1 ? "Yes" : "No");
      var1.append("\n");
      return var1.toString();
   }

   public String indexToString() {
      return "Index:      " + this.index + "\n";
   }
}
