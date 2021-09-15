package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Certificate;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509IssuerSerial;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SubjectName;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class PrivateKeyResolver extends KeyResolverSpi {
   private static Logger log = Logger.getLogger(PrivateKeyResolver.class.getName());
   private KeyStore keyStore;
   private char[] password;

   public PrivateKeyResolver(KeyStore var1, char[] var2) {
      this.keyStore = var1;
      this.password = var2;
   }

   public boolean engineCanResolve(Element var1, String var2, StorageResolver var3) {
      return XMLUtils.elementIsInSignatureSpace(var1, "X509Data") || XMLUtils.elementIsInSignatureSpace(var1, "KeyName");
   }

   public PublicKey engineLookupAndResolvePublicKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      return null;
   }

   public X509Certificate engineLookupResolveX509Certificate(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      return null;
   }

   public SecretKey engineResolveSecretKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      return null;
   }

   public PrivateKey engineLookupAndResolvePrivateKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Can I resolve " + var1.getTagName() + "?");
      }

      if (XMLUtils.elementIsInSignatureSpace(var1, "X509Data")) {
         PrivateKey var4 = this.resolveX509Data(var1, var2);
         if (var4 != null) {
            return var4;
         }
      } else if (XMLUtils.elementIsInSignatureSpace(var1, "KeyName")) {
         log.log(Level.FINE, "Can I resolve KeyName?");
         String var7 = var1.getFirstChild().getNodeValue();

         try {
            Key var5 = this.keyStore.getKey(var7, this.password);
            if (var5 instanceof PrivateKey) {
               return (PrivateKey)var5;
            }
         } catch (Exception var6) {
            log.log(Level.FINE, (String)"Cannot recover the key", (Throwable)var6);
         }
      }

      log.log(Level.FINE, "I can't");
      return null;
   }

   private PrivateKey resolveX509Data(Element var1, String var2) {
      log.log(Level.FINE, "Can I resolve X509Data?");

      try {
         X509Data var3 = new X509Data(var1, var2);
         int var4 = var3.lengthSKI();

         int var5;
         PrivateKey var7;
         for(var5 = 0; var5 < var4; ++var5) {
            XMLX509SKI var6 = var3.itemSKI(var5);
            var7 = this.resolveX509SKI(var6);
            if (var7 != null) {
               return var7;
            }
         }

         var4 = var3.lengthIssuerSerial();

         for(var5 = 0; var5 < var4; ++var5) {
            XMLX509IssuerSerial var10 = var3.itemIssuerSerial(var5);
            var7 = this.resolveX509IssuerSerial(var10);
            if (var7 != null) {
               return var7;
            }
         }

         var4 = var3.lengthSubjectName();

         for(var5 = 0; var5 < var4; ++var5) {
            XMLX509SubjectName var11 = var3.itemSubjectName(var5);
            var7 = this.resolveX509SubjectName(var11);
            if (var7 != null) {
               return var7;
            }
         }

         var4 = var3.lengthCertificate();

         for(var5 = 0; var5 < var4; ++var5) {
            XMLX509Certificate var12 = var3.itemCertificate(var5);
            var7 = this.resolveX509Certificate(var12);
            if (var7 != null) {
               return var7;
            }
         }
      } catch (XMLSecurityException var8) {
         log.log(Level.FINE, (String)"XMLSecurityException", (Throwable)var8);
      } catch (KeyStoreException var9) {
         log.log(Level.FINE, (String)"KeyStoreException", (Throwable)var9);
      }

      return null;
   }

   private PrivateKey resolveX509SKI(XMLX509SKI var1) throws XMLSecurityException, KeyStoreException {
      log.log(Level.FINE, "Can I resolve X509SKI?");
      Enumeration var2 = this.keyStore.aliases();

      while(var2.hasMoreElements()) {
         String var3 = (String)var2.nextElement();
         if (this.keyStore.isKeyEntry(var3)) {
            Certificate var4 = this.keyStore.getCertificate(var3);
            if (var4 instanceof X509Certificate) {
               XMLX509SKI var5 = new XMLX509SKI(var1.getDocument(), (X509Certificate)var4);
               if (var5.equals(var1)) {
                  log.log(Level.FINE, "match !!! ");

                  try {
                     Key var6 = this.keyStore.getKey(var3, this.password);
                     if (var6 instanceof PrivateKey) {
                        return (PrivateKey)var6;
                     }
                  } catch (Exception var7) {
                     log.log(Level.FINE, (String)"Cannot recover the key", (Throwable)var7);
                  }
               }
            }
         }
      }

      return null;
   }

   private PrivateKey resolveX509IssuerSerial(XMLX509IssuerSerial var1) throws KeyStoreException {
      log.log(Level.FINE, "Can I resolve X509IssuerSerial?");
      Enumeration var2 = this.keyStore.aliases();

      while(var2.hasMoreElements()) {
         String var3 = (String)var2.nextElement();
         if (this.keyStore.isKeyEntry(var3)) {
            Certificate var4 = this.keyStore.getCertificate(var3);
            if (var4 instanceof X509Certificate) {
               XMLX509IssuerSerial var5 = new XMLX509IssuerSerial(var1.getDocument(), (X509Certificate)var4);
               if (var5.equals(var1)) {
                  log.log(Level.FINE, "match !!! ");

                  try {
                     Key var6 = this.keyStore.getKey(var3, this.password);
                     if (var6 instanceof PrivateKey) {
                        return (PrivateKey)var6;
                     }
                  } catch (Exception var7) {
                     log.log(Level.FINE, (String)"Cannot recover the key", (Throwable)var7);
                  }
               }
            }
         }
      }

      return null;
   }

   private PrivateKey resolveX509SubjectName(XMLX509SubjectName var1) throws KeyStoreException {
      log.log(Level.FINE, "Can I resolve X509SubjectName?");
      Enumeration var2 = this.keyStore.aliases();

      while(var2.hasMoreElements()) {
         String var3 = (String)var2.nextElement();
         if (this.keyStore.isKeyEntry(var3)) {
            Certificate var4 = this.keyStore.getCertificate(var3);
            if (var4 instanceof X509Certificate) {
               XMLX509SubjectName var5 = new XMLX509SubjectName(var1.getDocument(), (X509Certificate)var4);
               if (var5.equals(var1)) {
                  log.log(Level.FINE, "match !!! ");

                  try {
                     Key var6 = this.keyStore.getKey(var3, this.password);
                     if (var6 instanceof PrivateKey) {
                        return (PrivateKey)var6;
                     }
                  } catch (Exception var7) {
                     log.log(Level.FINE, (String)"Cannot recover the key", (Throwable)var7);
                  }
               }
            }
         }
      }

      return null;
   }

   private PrivateKey resolveX509Certificate(XMLX509Certificate var1) throws XMLSecurityException, KeyStoreException {
      log.log(Level.FINE, "Can I resolve X509Certificate?");
      byte[] var2 = var1.getCertificateBytes();
      Enumeration var3 = this.keyStore.aliases();

      while(var3.hasMoreElements()) {
         String var4 = (String)var3.nextElement();
         if (this.keyStore.isKeyEntry(var4)) {
            Certificate var5 = this.keyStore.getCertificate(var4);
            if (var5 instanceof X509Certificate) {
               byte[] var6 = null;

               try {
                  var6 = var5.getEncoded();
               } catch (CertificateEncodingException var9) {
               }

               if (var6 != null && Arrays.equals(var6, var2)) {
                  log.log(Level.FINE, "match !!! ");

                  try {
                     Key var7 = this.keyStore.getKey(var4, this.password);
                     if (var7 instanceof PrivateKey) {
                        return (PrivateKey)var7;
                     }
                  } catch (Exception var8) {
                     log.log(Level.FINE, (String)"Cannot recover the key", (Throwable)var8);
                  }
               }
            }
         }
      }

      return null;
   }
}
