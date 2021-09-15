package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.DSAKeyValue;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class DSAKeyValueResolver extends KeyResolverSpi {
   private static Logger log = Logger.getLogger(DSAKeyValueResolver.class.getName());

   public PublicKey engineLookupAndResolvePublicKey(Element var1, String var2, StorageResolver var3) {
      if (var1 == null) {
         return null;
      } else {
         Element var4 = null;
         boolean var5 = XMLUtils.elementIsInSignatureSpace(var1, "KeyValue");
         if (var5) {
            var4 = XMLUtils.selectDsNode(var1.getFirstChild(), "DSAKeyValue", 0);
         } else if (XMLUtils.elementIsInSignatureSpace(var1, "DSAKeyValue")) {
            var4 = var1;
         }

         if (var4 == null) {
            return null;
         } else {
            try {
               DSAKeyValue var6 = new DSAKeyValue(var4, var2);
               PublicKey var7 = var6.getPublicKey();
               return var7;
            } catch (XMLSecurityException var8) {
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, (String)var8.getMessage(), (Throwable)var8);
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
