package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509IssuerSerial;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class X509IssuerSerialResolver extends KeyResolverSpi {
   private static Logger log = Logger.getLogger(X509IssuerSerialResolver.class.getName());

   public PublicKey engineLookupAndResolvePublicKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      X509Certificate var4 = this.engineLookupResolveX509Certificate(var1, var2, var3);
      return var4 != null ? var4.getPublicKey() : null;
   }

   public X509Certificate engineLookupResolveX509Certificate(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Can I resolve " + var1.getTagName() + "?");
      }

      X509Data var4 = null;

      try {
         var4 = new X509Data(var1, var2);
      } catch (XMLSignatureException var11) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "I can't");
         }

         return null;
      } catch (XMLSecurityException var12) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "I can't");
         }

         return null;
      }

      if (!var4.containsIssuerSerial()) {
         return null;
      } else {
         try {
            if (var3 == null) {
               Object[] var14 = new Object[]{"X509IssuerSerial"};
               KeyResolverException var15 = new KeyResolverException("KeyResolver.needStorageResolver", var14);
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, (String)"", (Throwable)var15);
               }

               throw var15;
            } else {
               int var5 = var4.lengthIssuerSerial();
               Iterator var6 = var3.getIterator();

               while(var6.hasNext()) {
                  X509Certificate var7 = (X509Certificate)var6.next();
                  XMLX509IssuerSerial var8 = new XMLX509IssuerSerial(var1.getOwnerDocument(), var7);
                  if (log.isLoggable(Level.FINE)) {
                     log.log(Level.FINE, "Found Certificate Issuer: " + var8.getIssuerName());
                     log.log(Level.FINE, "Found Certificate Serial: " + var8.getSerialNumber().toString());
                  }

                  for(int var9 = 0; var9 < var5; ++var9) {
                     XMLX509IssuerSerial var10 = var4.itemIssuerSerial(var9);
                     if (log.isLoggable(Level.FINE)) {
                        log.log(Level.FINE, "Found Element Issuer:     " + var10.getIssuerName());
                        log.log(Level.FINE, "Found Element Serial:     " + var10.getSerialNumber().toString());
                     }

                     if (var8.equals(var10)) {
                        if (log.isLoggable(Level.FINE)) {
                           log.log(Level.FINE, "match !!! ");
                        }

                        return var7;
                     }

                     if (log.isLoggable(Level.FINE)) {
                        log.log(Level.FINE, "no match...");
                     }
                  }
               }

               return null;
            }
         } catch (XMLSecurityException var13) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)"XMLSecurityException", (Throwable)var13);
            }

            throw new KeyResolverException("generic.EmptyMessage", var13);
         }
      }
   }

   public SecretKey engineLookupAndResolveSecretKey(Element var1, String var2, StorageResolver var3) {
      return null;
   }
}
