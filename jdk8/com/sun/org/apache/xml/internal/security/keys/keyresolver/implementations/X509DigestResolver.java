package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Digest;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class X509DigestResolver extends KeyResolverSpi {
   private static Logger log = Logger.getLogger(X509DigestResolver.class.getName());

   public boolean engineCanResolve(Element var1, String var2, StorageResolver var3) {
      if (XMLUtils.elementIsInSignatureSpace(var1, "X509Data")) {
         try {
            X509Data var4 = new X509Data(var1, var2);
            return var4.containsDigest();
         } catch (XMLSecurityException var5) {
            return false;
         }
      } else {
         return false;
      }
   }

   public PublicKey engineLookupAndResolvePublicKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      X509Certificate var4 = this.engineLookupResolveX509Certificate(var1, var2, var3);
      return var4 != null ? var4.getPublicKey() : null;
   }

   public X509Certificate engineLookupResolveX509Certificate(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Can I resolve " + var1.getTagName());
      }

      if (!this.engineCanResolve(var1, var2, var3)) {
         return null;
      } else {
         try {
            return this.resolveCertificate(var1, var2, var3);
         } catch (XMLSecurityException var5) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)"XMLSecurityException", (Throwable)var5);
            }

            return null;
         }
      }
   }

   public SecretKey engineLookupAndResolveSecretKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      return null;
   }

   private X509Certificate resolveCertificate(Element var1, String var2, StorageResolver var3) throws XMLSecurityException {
      XMLX509Digest[] var4 = null;
      Element[] var5 = XMLUtils.selectDs11Nodes(var1.getFirstChild(), "X509Digest");
      if (var5 != null && var5.length > 0) {
         try {
            this.checkStorage(var3);
            var4 = new XMLX509Digest[var5.length];

            for(int var6 = 0; var6 < var5.length; ++var6) {
               var4[var6] = new XMLX509Digest(var5[var6], var2);
            }

            Iterator var12 = var3.getIterator();

            while(var12.hasNext()) {
               X509Certificate var7 = (X509Certificate)var12.next();

               for(int var8 = 0; var8 < var4.length; ++var8) {
                  XMLX509Digest var9 = var4[var8];
                  byte[] var10 = XMLX509Digest.getDigestBytesFromCert(var7, var9.getAlgorithm());
                  if (Arrays.equals(var9.getDigestBytes(), var10)) {
                     if (log.isLoggable(Level.FINE)) {
                        log.log(Level.FINE, "Found certificate with: " + var7.getSubjectX500Principal().getName());
                     }

                     return var7;
                  }
               }
            }

            return null;
         } catch (XMLSecurityException var11) {
            throw new KeyResolverException("empty", var11);
         }
      } else {
         return null;
      }
   }

   private void checkStorage(StorageResolver var1) throws KeyResolverException {
      if (var1 == null) {
         Object[] var2 = new Object[]{"X509Digest"};
         KeyResolverException var3 = new KeyResolverException("KeyResolver.needStorageResolver", var2);
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)"", (Throwable)var3);
         }

         throw var3;
      }
   }
}
