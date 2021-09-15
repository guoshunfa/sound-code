package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class X509SKIResolver extends KeyResolverSpi {
   private static Logger log = Logger.getLogger(X509SKIResolver.class.getName());

   public PublicKey engineLookupAndResolvePublicKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      X509Certificate var4 = this.engineLookupResolveX509Certificate(var1, var2, var3);
      return var4 != null ? var4.getPublicKey() : null;
   }

   public X509Certificate engineLookupResolveX509Certificate(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Can I resolve " + var1.getTagName() + "?");
      }

      if (!XMLUtils.elementIsInSignatureSpace(var1, "X509Data")) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "I can't");
         }

         return null;
      } else {
         XMLX509SKI[] var4 = null;
         Element[] var5 = null;
         var5 = XMLUtils.selectDsNodes(var1.getFirstChild(), "X509SKI");
         if (var5 != null && var5.length > 0) {
            try {
               if (var3 == null) {
                  Object[] var12 = new Object[]{"X509SKI"};
                  KeyResolverException var13 = new KeyResolverException("KeyResolver.needStorageResolver", var12);
                  if (log.isLoggable(Level.FINE)) {
                     log.log(Level.FINE, (String)"", (Throwable)var13);
                  }

                  throw var13;
               } else {
                  var4 = new XMLX509SKI[var5.length];

                  for(int var6 = 0; var6 < var5.length; ++var6) {
                     var4[var6] = new XMLX509SKI(var5[var6], var2);
                  }

                  Iterator var11 = var3.getIterator();

                  while(var11.hasNext()) {
                     X509Certificate var7 = (X509Certificate)var11.next();
                     XMLX509SKI var8 = new XMLX509SKI(var1.getOwnerDocument(), var7);

                     for(int var9 = 0; var9 < var4.length; ++var9) {
                        if (var8.equals(var4[var9])) {
                           if (log.isLoggable(Level.FINE)) {
                              log.log(Level.FINE, "Return PublicKey from " + var7.getSubjectX500Principal().getName());
                           }

                           return var7;
                        }
                     }
                  }

                  return null;
               }
            } catch (XMLSecurityException var10) {
               throw new KeyResolverException("empty", var10);
            }
         } else {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "I can't");
            }

            return null;
         }
      }
   }

   public SecretKey engineLookupAndResolveSecretKey(Element var1, String var2, StorageResolver var3) {
      return null;
   }
}
