package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Certificate;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class X509CertificateResolver extends KeyResolverSpi {
   private static Logger log = Logger.getLogger(X509CertificateResolver.class.getName());

   public PublicKey engineLookupAndResolvePublicKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      X509Certificate var4 = this.engineLookupResolveX509Certificate(var1, var2, var3);
      return var4 != null ? var4.getPublicKey() : null;
   }

   public X509Certificate engineLookupResolveX509Certificate(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      try {
         Element[] var4 = XMLUtils.selectDsNodes(var1.getFirstChild(), "X509Certificate");
         if (var4 != null && var4.length != 0) {
            for(int var9 = 0; var9 < var4.length; ++var9) {
               XMLX509Certificate var6 = new XMLX509Certificate(var4[var9], var2);
               X509Certificate var7 = var6.getX509Certificate();
               if (var7 != null) {
                  return var7;
               }
            }

            return null;
         } else {
            Element var5 = XMLUtils.selectDsNode(var1.getFirstChild(), "X509Data", 0);
            return var5 != null ? this.engineLookupResolveX509Certificate(var5, var2, var3) : null;
         }
      } catch (XMLSecurityException var8) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)"XMLSecurityException", (Throwable)var8);
         }

         throw new KeyResolverException("generic.EmptyMessage", var8);
      }
   }

   public SecretKey engineLookupAndResolveSecretKey(Element var1, String var2, StorageResolver var3) {
      return null;
   }
}
