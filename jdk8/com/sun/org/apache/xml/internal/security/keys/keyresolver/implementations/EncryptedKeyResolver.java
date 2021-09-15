package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.encryption.EncryptedKey;
import com.sun.org.apache.xml.internal.security.encryption.XMLCipher;
import com.sun.org.apache.xml.internal.security.encryption.XMLEncryptionException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class EncryptedKeyResolver extends KeyResolverSpi {
   private static Logger log = Logger.getLogger(EncryptedKeyResolver.class.getName());
   private Key kek;
   private String algorithm;
   private List<KeyResolverSpi> internalKeyResolvers;

   public EncryptedKeyResolver(String var1) {
      this.kek = null;
      this.algorithm = var1;
   }

   public EncryptedKeyResolver(String var1, Key var2) {
      this.algorithm = var1;
      this.kek = var2;
   }

   public void registerInternalKeyResolver(KeyResolverSpi var1) {
      if (this.internalKeyResolvers == null) {
         this.internalKeyResolvers = new ArrayList();
      }

      this.internalKeyResolvers.add(var1);
   }

   public PublicKey engineLookupAndResolvePublicKey(Element var1, String var2, StorageResolver var3) {
      return null;
   }

   public X509Certificate engineLookupResolveX509Certificate(Element var1, String var2, StorageResolver var3) {
      return null;
   }

   public SecretKey engineLookupAndResolveSecretKey(Element var1, String var2, StorageResolver var3) {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "EncryptedKeyResolver - Can I resolve " + var1.getTagName());
      }

      if (var1 == null) {
         return null;
      } else {
         SecretKey var4 = null;
         boolean var5 = XMLUtils.elementIsInEncryptionSpace(var1, "EncryptedKey");
         if (var5) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "Passed an Encrypted Key");
            }

            try {
               XMLCipher var6 = XMLCipher.getInstance();
               var6.init(4, this.kek);
               if (this.internalKeyResolvers != null) {
                  int var7 = this.internalKeyResolvers.size();

                  for(int var8 = 0; var8 < var7; ++var8) {
                     var6.registerInternalKeyResolver((KeyResolverSpi)this.internalKeyResolvers.get(var8));
                  }
               }

               EncryptedKey var10 = var6.loadEncryptedKey(var1);
               var4 = (SecretKey)var6.decryptKey(var10, this.algorithm);
            } catch (XMLEncryptionException var9) {
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, (String)var9.getMessage(), (Throwable)var9);
               }
            }
         }

         return var4;
      }
   }
}
