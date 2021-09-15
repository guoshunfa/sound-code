package com.sun.org.apache.xml.internal.security.keys.content.x509;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLX509SKI extends SignatureElementProxy implements XMLX509DataContent {
   private static Logger log = Logger.getLogger(XMLX509SKI.class.getName());
   public static final String SKI_OID = "2.5.29.14";

   public XMLX509SKI(Document var1, byte[] var2) {
      super(var1);
      this.addBase64Text(var2);
   }

   public XMLX509SKI(Document var1, X509Certificate var2) throws XMLSecurityException {
      super(var1);
      this.addBase64Text(getSKIBytesFromCert(var2));
   }

   public XMLX509SKI(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public byte[] getSKIBytes() throws XMLSecurityException {
      return this.getBytesFromTextChild();
   }

   public static byte[] getSKIBytesFromCert(X509Certificate var0) throws XMLSecurityException {
      if (var0.getVersion() < 3) {
         Object[] var3 = new Object[]{var0.getVersion()};
         throw new XMLSecurityException("certificate.noSki.lowVersion", var3);
      } else {
         byte[] var1 = var0.getExtensionValue("2.5.29.14");
         if (var1 == null) {
            throw new XMLSecurityException("certificate.noSki.null");
         } else {
            byte[] var2 = new byte[var1.length - 4];
            System.arraycopy(var1, 4, var2, 0, var2.length);
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "Base64 of SKI is " + Base64.encode(var2));
            }

            return var2;
         }
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof XMLX509SKI)) {
         return false;
      } else {
         XMLX509SKI var2 = (XMLX509SKI)var1;

         try {
            return Arrays.equals(var2.getSKIBytes(), this.getSKIBytes());
         } catch (XMLSecurityException var4) {
            return false;
         }
      }
   }

   public int hashCode() {
      int var1 = 17;

      try {
         byte[] var2 = this.getSKIBytes();

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
      return "X509SKI";
   }
}
