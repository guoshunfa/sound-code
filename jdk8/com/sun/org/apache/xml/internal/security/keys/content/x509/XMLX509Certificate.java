package com.sun.org.apache.xml.internal.security.keys.content.x509;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLX509Certificate extends SignatureElementProxy implements XMLX509DataContent {
   public static final String JCA_CERT_ID = "X.509";

   public XMLX509Certificate(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public XMLX509Certificate(Document var1, byte[] var2) {
      super(var1);
      this.addBase64Text(var2);
   }

   public XMLX509Certificate(Document var1, X509Certificate var2) throws XMLSecurityException {
      super(var1);

      try {
         this.addBase64Text(var2.getEncoded());
      } catch (CertificateEncodingException var4) {
         throw new XMLSecurityException("empty", var4);
      }
   }

   public byte[] getCertificateBytes() throws XMLSecurityException {
      return this.getBytesFromTextChild();
   }

   public X509Certificate getX509Certificate() throws XMLSecurityException {
      try {
         byte[] var1 = this.getCertificateBytes();
         CertificateFactory var2 = CertificateFactory.getInstance("X.509");
         X509Certificate var3 = (X509Certificate)var2.generateCertificate(new ByteArrayInputStream(var1));
         return var3 != null ? var3 : null;
      } catch (CertificateException var4) {
         throw new XMLSecurityException("empty", var4);
      }
   }

   public PublicKey getPublicKey() throws XMLSecurityException {
      X509Certificate var1 = this.getX509Certificate();
      return var1 != null ? var1.getPublicKey() : null;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof XMLX509Certificate)) {
         return false;
      } else {
         XMLX509Certificate var2 = (XMLX509Certificate)var1;

         try {
            return Arrays.equals(var2.getCertificateBytes(), this.getCertificateBytes());
         } catch (XMLSecurityException var4) {
            return false;
         }
      }
   }

   public int hashCode() {
      int var1 = 17;

      try {
         byte[] var2 = this.getCertificateBytes();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            var1 = 31 * var1 + var2[var3];
         }
      } catch (XMLSecurityException var4) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)var4.getMessage(), (Throwable)var4);
         }
      }

      return var1;
   }

   public String getBaseLocalName() {
      return "X509Certificate";
   }
}
