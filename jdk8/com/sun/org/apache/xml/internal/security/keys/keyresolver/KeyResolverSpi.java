package com.sun.org.apache.xml.internal.security.keys.keyresolver;

import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public abstract class KeyResolverSpi {
   protected Map<String, String> properties = null;
   protected boolean globalResolver = false;
   protected boolean secureValidation;

   public void setSecureValidation(boolean var1) {
      this.secureValidation = var1;
   }

   public boolean engineCanResolve(Element var1, String var2, StorageResolver var3) {
      throw new UnsupportedOperationException();
   }

   public PublicKey engineResolvePublicKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      throw new UnsupportedOperationException();
   }

   public PublicKey engineLookupAndResolvePublicKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      KeyResolverSpi var4 = this.cloneIfNeeded();
      return !var4.engineCanResolve(var1, var2, var3) ? null : var4.engineResolvePublicKey(var1, var2, var3);
   }

   private KeyResolverSpi cloneIfNeeded() throws KeyResolverException {
      KeyResolverSpi var1 = this;
      if (this.globalResolver) {
         try {
            var1 = (KeyResolverSpi)this.getClass().newInstance();
         } catch (InstantiationException var3) {
            throw new KeyResolverException("", var3);
         } catch (IllegalAccessException var4) {
            throw new KeyResolverException("", var4);
         }
      }

      return var1;
   }

   public X509Certificate engineResolveX509Certificate(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      throw new UnsupportedOperationException();
   }

   public X509Certificate engineLookupResolveX509Certificate(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      KeyResolverSpi var4 = this.cloneIfNeeded();
      return !var4.engineCanResolve(var1, var2, var3) ? null : var4.engineResolveX509Certificate(var1, var2, var3);
   }

   public SecretKey engineResolveSecretKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      throw new UnsupportedOperationException();
   }

   public SecretKey engineLookupAndResolveSecretKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      KeyResolverSpi var4 = this.cloneIfNeeded();
      return !var4.engineCanResolve(var1, var2, var3) ? null : var4.engineResolveSecretKey(var1, var2, var3);
   }

   public PrivateKey engineLookupAndResolvePrivateKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      return null;
   }

   public void engineSetProperty(String var1, String var2) {
      if (this.properties == null) {
         this.properties = new HashMap();
      }

      this.properties.put(var1, var2);
   }

   public String engineGetProperty(String var1) {
      return this.properties == null ? null : (String)this.properties.get(var1);
   }

   public boolean understandsProperty(String var1) {
      if (this.properties == null) {
         return false;
      } else {
         return this.properties.get(var1) != null;
      }
   }

   public void setGlobalResolver(boolean var1) {
      this.globalResolver = var1;
   }
}
