package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class SecretKeyResolver extends KeyResolverSpi {
   private static Logger log = Logger.getLogger(SecretKeyResolver.class.getName());
   private KeyStore keyStore;
   private char[] password;

   public SecretKeyResolver(KeyStore var1, char[] var2) {
      this.keyStore = var1;
      this.password = var2;
   }

   public boolean engineCanResolve(Element var1, String var2, StorageResolver var3) {
      return XMLUtils.elementIsInSignatureSpace(var1, "KeyName");
   }

   public PublicKey engineLookupAndResolvePublicKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      return null;
   }

   public X509Certificate engineLookupResolveX509Certificate(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      return null;
   }

   public SecretKey engineResolveSecretKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Can I resolve " + var1.getTagName() + "?");
      }

      if (XMLUtils.elementIsInSignatureSpace(var1, "KeyName")) {
         String var4 = var1.getFirstChild().getNodeValue();

         try {
            Key var5 = this.keyStore.getKey(var4, this.password);
            if (var5 instanceof SecretKey) {
               return (SecretKey)var5;
            }
         } catch (Exception var6) {
            log.log(Level.FINE, (String)"Cannot recover the key", (Throwable)var6);
         }
      }

      log.log(Level.FINE, "I can't");
      return null;
   }

   public PrivateKey engineLookupAndResolvePrivateKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      return null;
   }
}
