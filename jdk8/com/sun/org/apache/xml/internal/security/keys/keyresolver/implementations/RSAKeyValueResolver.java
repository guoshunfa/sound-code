package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.RSAKeyValue;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class RSAKeyValueResolver extends KeyResolverSpi {
   private static Logger log = Logger.getLogger(RSAKeyValueResolver.class.getName());

   public PublicKey engineLookupAndResolvePublicKey(Element var1, String var2, StorageResolver var3) {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Can I resolve " + var1.getTagName());
      }

      if (var1 == null) {
         return null;
      } else {
         boolean var4 = XMLUtils.elementIsInSignatureSpace(var1, "KeyValue");
         Element var5 = null;
         if (var4) {
            var5 = XMLUtils.selectDsNode(var1.getFirstChild(), "RSAKeyValue", 0);
         } else if (XMLUtils.elementIsInSignatureSpace(var1, "RSAKeyValue")) {
            var5 = var1;
         }

         if (var5 == null) {
            return null;
         } else {
            try {
               RSAKeyValue var6 = new RSAKeyValue(var5, var2);
               return var6.getPublicKey();
            } catch (XMLSecurityException var7) {
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, (String)"XMLSecurityException", (Throwable)var7);
               }

               return null;
            }
         }
      }
   }

   public X509Certificate engineLookupResolveX509Certificate(Element var1, String var2, StorageResolver var3) {
      return null;
   }

   public SecretKey engineLookupAndResolveSecretKey(Element var1, String var2, StorageResolver var3) {
      return null;
   }
}
