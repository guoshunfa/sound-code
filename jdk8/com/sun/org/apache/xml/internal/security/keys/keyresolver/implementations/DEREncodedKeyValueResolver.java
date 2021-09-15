package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.DEREncodedKeyValue;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class DEREncodedKeyValueResolver extends KeyResolverSpi {
   private static Logger log = Logger.getLogger(DEREncodedKeyValueResolver.class.getName());

   public boolean engineCanResolve(Element var1, String var2, StorageResolver var3) {
      return XMLUtils.elementIsInSignature11Space(var1, "DEREncodedKeyValue");
   }

   public PublicKey engineLookupAndResolvePublicKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Can I resolve " + var1.getTagName());
      }

      if (!this.engineCanResolve(var1, var2, var3)) {
         return null;
      } else {
         try {
            DEREncodedKeyValue var4 = new DEREncodedKeyValue(var1, var2);
            return var4.getPublicKey();
         } catch (XMLSecurityException var5) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)"XMLSecurityException", (Throwable)var5);
            }

            return null;
         }
      }
   }

   public X509Certificate engineLookupResolveX509Certificate(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      return null;
   }

   public SecretKey engineLookupAndResolveSecretKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      return null;
   }

   public PrivateKey engineLookupAndResolvePrivateKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      return null;
   }
}
