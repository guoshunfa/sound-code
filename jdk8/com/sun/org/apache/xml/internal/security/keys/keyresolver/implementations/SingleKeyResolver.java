package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

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

public class SingleKeyResolver extends KeyResolverSpi {
   private static Logger log = Logger.getLogger(SingleKeyResolver.class.getName());
   private String keyName;
   private PublicKey publicKey;
   private PrivateKey privateKey;
   private SecretKey secretKey;

   public SingleKeyResolver(String var1, PublicKey var2) {
      this.keyName = var1;
      this.publicKey = var2;
   }

   public SingleKeyResolver(String var1, PrivateKey var2) {
      this.keyName = var1;
      this.privateKey = var2;
   }

   public SingleKeyResolver(String var1, SecretKey var2) {
      this.keyName = var1;
      this.secretKey = var2;
   }

   public boolean engineCanResolve(Element var1, String var2, StorageResolver var3) {
      return XMLUtils.elementIsInSignatureSpace(var1, "KeyName");
   }

   public PublicKey engineLookupAndResolvePublicKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Can I resolve " + var1.getTagName() + "?");
      }

      if (this.publicKey != null && XMLUtils.elementIsInSignatureSpace(var1, "KeyName")) {
         String var4 = var1.getFirstChild().getNodeValue();
         if (this.keyName.equals(var4)) {
            return this.publicKey;
         }
      }

      log.log(Level.FINE, "I can't");
      return null;
   }

   public X509Certificate engineLookupResolveX509Certificate(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      return null;
   }

   public SecretKey engineResolveSecretKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Can I resolve " + var1.getTagName() + "?");
      }

      if (this.secretKey != null && XMLUtils.elementIsInSignatureSpace(var1, "KeyName")) {
         String var4 = var1.getFirstChild().getNodeValue();
         if (this.keyName.equals(var4)) {
            return this.secretKey;
         }
      }

      log.log(Level.FINE, "I can't");
      return null;
   }

   public PrivateKey engineLookupAndResolvePrivateKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Can I resolve " + var1.getTagName() + "?");
      }

      if (this.privateKey != null && XMLUtils.elementIsInSignatureSpace(var1, "KeyName")) {
         String var4 = var1.getFirstChild().getNodeValue();
         if (this.keyName.equals(var4)) {
            return this.privateKey;
         }
      }

      log.log(Level.FINE, "I can't");
      return null;
   }
}
