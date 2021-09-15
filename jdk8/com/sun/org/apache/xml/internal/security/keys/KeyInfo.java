package com.sun.org.apache.xml.internal.security.keys;

import com.sun.org.apache.xml.internal.security.encryption.EncryptedKey;
import com.sun.org.apache.xml.internal.security.encryption.XMLCipher;
import com.sun.org.apache.xml.internal.security.encryption.XMLEncryptionException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.DEREncodedKeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.KeyInfoReference;
import com.sun.org.apache.xml.internal.security.keys.content.KeyName;
import com.sun.org.apache.xml.internal.security.keys.content.KeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.MgmtData;
import com.sun.org.apache.xml.internal.security.keys.content.PGPData;
import com.sun.org.apache.xml.internal.security.keys.content.RetrievalMethod;
import com.sun.org.apache.xml.internal.security.keys.content.SPKIData;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.DSAKeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.RSAKeyValue;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class KeyInfo extends SignatureElementProxy {
   private static Logger log = Logger.getLogger(KeyInfo.class.getName());
   private List<X509Data> x509Datas = null;
   private List<EncryptedKey> encryptedKeys = null;
   private static final List<StorageResolver> nullList;
   private List<StorageResolver> storageResolvers;
   private List<KeyResolverSpi> internalKeyResolvers;
   private boolean secureValidation;

   public KeyInfo(Document var1) {
      super(var1);
      this.storageResolvers = nullList;
      this.internalKeyResolvers = new ArrayList();
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public KeyInfo(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
      this.storageResolvers = nullList;
      this.internalKeyResolvers = new ArrayList();
      Attr var3 = var1.getAttributeNodeNS((String)null, "Id");
      if (var3 != null) {
         var1.setIdAttributeNode(var3, true);
      }

   }

   public void setSecureValidation(boolean var1) {
      this.secureValidation = var1;
   }

   public void setId(String var1) {
      if (var1 != null) {
         this.constructionElement.setAttributeNS((String)null, "Id", var1);
         this.constructionElement.setIdAttributeNS((String)null, "Id", true);
      }

   }

   public String getId() {
      return this.constructionElement.getAttributeNS((String)null, "Id");
   }

   public void addKeyName(String var1) {
      this.add(new KeyName(this.doc, var1));
   }

   public void add(KeyName var1) {
      this.constructionElement.appendChild(var1.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public void addKeyValue(PublicKey var1) {
      this.add(new KeyValue(this.doc, var1));
   }

   public void addKeyValue(Element var1) {
      this.add(new KeyValue(this.doc, var1));
   }

   public void add(DSAKeyValue var1) {
      this.add(new KeyValue(this.doc, var1));
   }

   public void add(RSAKeyValue var1) {
      this.add(new KeyValue(this.doc, var1));
   }

   public void add(PublicKey var1) {
      this.add(new KeyValue(this.doc, var1));
   }

   public void add(KeyValue var1) {
      this.constructionElement.appendChild(var1.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public void addMgmtData(String var1) {
      this.add(new MgmtData(this.doc, var1));
   }

   public void add(MgmtData var1) {
      this.constructionElement.appendChild(var1.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public void add(PGPData var1) {
      this.constructionElement.appendChild(var1.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public void addRetrievalMethod(String var1, Transforms var2, String var3) {
      this.add(new RetrievalMethod(this.doc, var1, var2, var3));
   }

   public void add(RetrievalMethod var1) {
      this.constructionElement.appendChild(var1.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public void add(SPKIData var1) {
      this.constructionElement.appendChild(var1.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public void add(X509Data var1) {
      if (this.x509Datas == null) {
         this.x509Datas = new ArrayList();
      }

      this.x509Datas.add(var1);
      this.constructionElement.appendChild(var1.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public void add(EncryptedKey var1) throws XMLEncryptionException {
      if (this.encryptedKeys == null) {
         this.encryptedKeys = new ArrayList();
      }

      this.encryptedKeys.add(var1);
      XMLCipher var2 = XMLCipher.getInstance();
      this.constructionElement.appendChild(var2.martial(var1));
   }

   public void addDEREncodedKeyValue(PublicKey var1) throws XMLSecurityException {
      this.add(new DEREncodedKeyValue(this.doc, var1));
   }

   public void add(DEREncodedKeyValue var1) {
      this.constructionElement.appendChild(var1.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public void addKeyInfoReference(String var1) throws XMLSecurityException {
      this.add(new KeyInfoReference(this.doc, var1));
   }

   public void add(KeyInfoReference var1) {
      this.constructionElement.appendChild(var1.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public void addUnknownElement(Element var1) {
      this.constructionElement.appendChild(var1);
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public int lengthKeyName() {
      return this.length("http://www.w3.org/2000/09/xmldsig#", "KeyName");
   }

   public int lengthKeyValue() {
      return this.length("http://www.w3.org/2000/09/xmldsig#", "KeyValue");
   }

   public int lengthMgmtData() {
      return this.length("http://www.w3.org/2000/09/xmldsig#", "MgmtData");
   }

   public int lengthPGPData() {
      return this.length("http://www.w3.org/2000/09/xmldsig#", "PGPData");
   }

   public int lengthRetrievalMethod() {
      return this.length("http://www.w3.org/2000/09/xmldsig#", "RetrievalMethod");
   }

   public int lengthSPKIData() {
      return this.length("http://www.w3.org/2000/09/xmldsig#", "SPKIData");
   }

   public int lengthX509Data() {
      return this.x509Datas != null ? this.x509Datas.size() : this.length("http://www.w3.org/2000/09/xmldsig#", "X509Data");
   }

   public int lengthDEREncodedKeyValue() {
      return this.length("http://www.w3.org/2009/xmldsig11#", "DEREncodedKeyValue");
   }

   public int lengthKeyInfoReference() {
      return this.length("http://www.w3.org/2009/xmldsig11#", "KeyInfoReference");
   }

   public int lengthUnknownElement() {
      int var1 = 0;
      NodeList var2 = this.constructionElement.getChildNodes();

      for(int var3 = 0; var3 < var2.getLength(); ++var3) {
         Node var4 = var2.item(var3);
         if (var4.getNodeType() == 1 && var4.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")) {
            ++var1;
         }
      }

      return var1;
   }

   public KeyName itemKeyName(int var1) throws XMLSecurityException {
      Element var2 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "KeyName", var1);
      return var2 != null ? new KeyName(var2, this.baseURI) : null;
   }

   public KeyValue itemKeyValue(int var1) throws XMLSecurityException {
      Element var2 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "KeyValue", var1);
      return var2 != null ? new KeyValue(var2, this.baseURI) : null;
   }

   public MgmtData itemMgmtData(int var1) throws XMLSecurityException {
      Element var2 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "MgmtData", var1);
      return var2 != null ? new MgmtData(var2, this.baseURI) : null;
   }

   public PGPData itemPGPData(int var1) throws XMLSecurityException {
      Element var2 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "PGPData", var1);
      return var2 != null ? new PGPData(var2, this.baseURI) : null;
   }

   public RetrievalMethod itemRetrievalMethod(int var1) throws XMLSecurityException {
      Element var2 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "RetrievalMethod", var1);
      return var2 != null ? new RetrievalMethod(var2, this.baseURI) : null;
   }

   public SPKIData itemSPKIData(int var1) throws XMLSecurityException {
      Element var2 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "SPKIData", var1);
      return var2 != null ? new SPKIData(var2, this.baseURI) : null;
   }

   public X509Data itemX509Data(int var1) throws XMLSecurityException {
      if (this.x509Datas != null) {
         return (X509Data)this.x509Datas.get(var1);
      } else {
         Element var2 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "X509Data", var1);
         return var2 != null ? new X509Data(var2, this.baseURI) : null;
      }
   }

   public EncryptedKey itemEncryptedKey(int var1) throws XMLSecurityException {
      if (this.encryptedKeys != null) {
         return (EncryptedKey)this.encryptedKeys.get(var1);
      } else {
         Element var2 = XMLUtils.selectXencNode(this.constructionElement.getFirstChild(), "EncryptedKey", var1);
         if (var2 != null) {
            XMLCipher var3 = XMLCipher.getInstance();
            var3.init(4, (Key)null);
            return var3.loadEncryptedKey(var2);
         } else {
            return null;
         }
      }
   }

   public DEREncodedKeyValue itemDEREncodedKeyValue(int var1) throws XMLSecurityException {
      Element var2 = XMLUtils.selectDs11Node(this.constructionElement.getFirstChild(), "DEREncodedKeyValue", var1);
      return var2 != null ? new DEREncodedKeyValue(var2, this.baseURI) : null;
   }

   public KeyInfoReference itemKeyInfoReference(int var1) throws XMLSecurityException {
      Element var2 = XMLUtils.selectDs11Node(this.constructionElement.getFirstChild(), "KeyInfoReference", var1);
      return var2 != null ? new KeyInfoReference(var2, this.baseURI) : null;
   }

   public Element itemUnknownElement(int var1) {
      NodeList var2 = this.constructionElement.getChildNodes();
      int var3 = 0;

      for(int var4 = 0; var4 < var2.getLength(); ++var4) {
         Node var5 = var2.item(var4);
         if (var5.getNodeType() == 1 && var5.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")) {
            ++var3;
            if (var3 == var1) {
               return (Element)var5;
            }
         }
      }

      return null;
   }

   public boolean isEmpty() {
      return this.constructionElement.getFirstChild() == null;
   }

   public boolean containsKeyName() {
      return this.lengthKeyName() > 0;
   }

   public boolean containsKeyValue() {
      return this.lengthKeyValue() > 0;
   }

   public boolean containsMgmtData() {
      return this.lengthMgmtData() > 0;
   }

   public boolean containsPGPData() {
      return this.lengthPGPData() > 0;
   }

   public boolean containsRetrievalMethod() {
      return this.lengthRetrievalMethod() > 0;
   }

   public boolean containsSPKIData() {
      return this.lengthSPKIData() > 0;
   }

   public boolean containsUnknownElement() {
      return this.lengthUnknownElement() > 0;
   }

   public boolean containsX509Data() {
      return this.lengthX509Data() > 0;
   }

   public boolean containsDEREncodedKeyValue() {
      return this.lengthDEREncodedKeyValue() > 0;
   }

   public boolean containsKeyInfoReference() {
      return this.lengthKeyInfoReference() > 0;
   }

   public PublicKey getPublicKey() throws KeyResolverException {
      PublicKey var1 = this.getPublicKeyFromInternalResolvers();
      if (var1 != null) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "I could find a key using the per-KeyInfo key resolvers");
         }

         return var1;
      } else {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "I couldn't find a key using the per-KeyInfo key resolvers");
         }

         var1 = this.getPublicKeyFromStaticResolvers();
         if (var1 != null) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "I could find a key using the system-wide key resolvers");
            }

            return var1;
         } else {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "I couldn't find a key using the system-wide key resolvers");
            }

            return null;
         }
      }
   }

   PublicKey getPublicKeyFromStaticResolvers() throws KeyResolverException {
      Iterator var1 = KeyResolver.iterator();

      while(var1.hasNext()) {
         KeyResolverSpi var2 = (KeyResolverSpi)var1.next();
         var2.setSecureValidation(this.secureValidation);
         Node var3 = this.constructionElement.getFirstChild();

         for(String var4 = this.getBaseURI(); var3 != null; var3 = var3.getNextSibling()) {
            if (var3.getNodeType() == 1) {
               Iterator var5 = this.storageResolvers.iterator();

               while(var5.hasNext()) {
                  StorageResolver var6 = (StorageResolver)var5.next();
                  PublicKey var7 = var2.engineLookupAndResolvePublicKey((Element)var3, var4, var6);
                  if (var7 != null) {
                     return var7;
                  }
               }
            }
         }
      }

      return null;
   }

   PublicKey getPublicKeyFromInternalResolvers() throws KeyResolverException {
      Iterator var1 = this.internalKeyResolvers.iterator();

      while(var1.hasNext()) {
         KeyResolverSpi var2 = (KeyResolverSpi)var1.next();
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Try " + var2.getClass().getName());
         }

         var2.setSecureValidation(this.secureValidation);
         Node var3 = this.constructionElement.getFirstChild();

         for(String var4 = this.getBaseURI(); var3 != null; var3 = var3.getNextSibling()) {
            if (var3.getNodeType() == 1) {
               Iterator var5 = this.storageResolvers.iterator();

               while(var5.hasNext()) {
                  StorageResolver var6 = (StorageResolver)var5.next();
                  PublicKey var7 = var2.engineLookupAndResolvePublicKey((Element)var3, var4, var6);
                  if (var7 != null) {
                     return var7;
                  }
               }
            }
         }
      }

      return null;
   }

   public X509Certificate getX509Certificate() throws KeyResolverException {
      X509Certificate var1 = this.getX509CertificateFromInternalResolvers();
      if (var1 != null) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "I could find a X509Certificate using the per-KeyInfo key resolvers");
         }

         return var1;
      } else {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "I couldn't find a X509Certificate using the per-KeyInfo key resolvers");
         }

         var1 = this.getX509CertificateFromStaticResolvers();
         if (var1 != null) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "I could find a X509Certificate using the system-wide key resolvers");
            }

            return var1;
         } else {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "I couldn't find a X509Certificate using the system-wide key resolvers");
            }

            return null;
         }
      }
   }

   X509Certificate getX509CertificateFromStaticResolvers() throws KeyResolverException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Start getX509CertificateFromStaticResolvers() with " + KeyResolver.length() + " resolvers");
      }

      String var1 = this.getBaseURI();
      Iterator var2 = KeyResolver.iterator();

      X509Certificate var4;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         KeyResolverSpi var3 = (KeyResolverSpi)var2.next();
         var3.setSecureValidation(this.secureValidation);
         var4 = this.applyCurrentResolver(var1, var3);
      } while(var4 == null);

      return var4;
   }

   private X509Certificate applyCurrentResolver(String var1, KeyResolverSpi var2) throws KeyResolverException {
      for(Node var3 = this.constructionElement.getFirstChild(); var3 != null; var3 = var3.getNextSibling()) {
         if (var3.getNodeType() == 1) {
            Iterator var4 = this.storageResolvers.iterator();

            while(var4.hasNext()) {
               StorageResolver var5 = (StorageResolver)var4.next();
               X509Certificate var6 = var2.engineLookupResolveX509Certificate((Element)var3, var1, var5);
               if (var6 != null) {
                  return var6;
               }
            }
         }
      }

      return null;
   }

   X509Certificate getX509CertificateFromInternalResolvers() throws KeyResolverException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Start getX509CertificateFromInternalResolvers() with " + this.lengthInternalKeyResolver() + " resolvers");
      }

      String var1 = this.getBaseURI();
      Iterator var2 = this.internalKeyResolvers.iterator();

      X509Certificate var4;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         KeyResolverSpi var3 = (KeyResolverSpi)var2.next();
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Try " + var3.getClass().getName());
         }

         var3.setSecureValidation(this.secureValidation);
         var4 = this.applyCurrentResolver(var1, var3);
      } while(var4 == null);

      return var4;
   }

   public SecretKey getSecretKey() throws KeyResolverException {
      SecretKey var1 = this.getSecretKeyFromInternalResolvers();
      if (var1 != null) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "I could find a secret key using the per-KeyInfo key resolvers");
         }

         return var1;
      } else {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "I couldn't find a secret key using the per-KeyInfo key resolvers");
         }

         var1 = this.getSecretKeyFromStaticResolvers();
         if (var1 != null) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "I could find a secret key using the system-wide key resolvers");
            }

            return var1;
         } else {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "I couldn't find a secret key using the system-wide key resolvers");
            }

            return null;
         }
      }
   }

   SecretKey getSecretKeyFromStaticResolvers() throws KeyResolverException {
      Iterator var1 = KeyResolver.iterator();

      while(var1.hasNext()) {
         KeyResolverSpi var2 = (KeyResolverSpi)var1.next();
         var2.setSecureValidation(this.secureValidation);
         Node var3 = this.constructionElement.getFirstChild();

         for(String var4 = this.getBaseURI(); var3 != null; var3 = var3.getNextSibling()) {
            if (var3.getNodeType() == 1) {
               Iterator var5 = this.storageResolvers.iterator();

               while(var5.hasNext()) {
                  StorageResolver var6 = (StorageResolver)var5.next();
                  SecretKey var7 = var2.engineLookupAndResolveSecretKey((Element)var3, var4, var6);
                  if (var7 != null) {
                     return var7;
                  }
               }
            }
         }
      }

      return null;
   }

   SecretKey getSecretKeyFromInternalResolvers() throws KeyResolverException {
      Iterator var1 = this.internalKeyResolvers.iterator();

      while(var1.hasNext()) {
         KeyResolverSpi var2 = (KeyResolverSpi)var1.next();
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Try " + var2.getClass().getName());
         }

         var2.setSecureValidation(this.secureValidation);
         Node var3 = this.constructionElement.getFirstChild();

         for(String var4 = this.getBaseURI(); var3 != null; var3 = var3.getNextSibling()) {
            if (var3.getNodeType() == 1) {
               Iterator var5 = this.storageResolvers.iterator();

               while(var5.hasNext()) {
                  StorageResolver var6 = (StorageResolver)var5.next();
                  SecretKey var7 = var2.engineLookupAndResolveSecretKey((Element)var3, var4, var6);
                  if (var7 != null) {
                     return var7;
                  }
               }
            }
         }
      }

      return null;
   }

   public PrivateKey getPrivateKey() throws KeyResolverException {
      PrivateKey var1 = this.getPrivateKeyFromInternalResolvers();
      if (var1 != null) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "I could find a private key using the per-KeyInfo key resolvers");
         }

         return var1;
      } else {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "I couldn't find a secret key using the per-KeyInfo key resolvers");
         }

         var1 = this.getPrivateKeyFromStaticResolvers();
         if (var1 != null) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "I could find a private key using the system-wide key resolvers");
            }

            return var1;
         } else {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "I couldn't find a private key using the system-wide key resolvers");
            }

            return null;
         }
      }
   }

   PrivateKey getPrivateKeyFromStaticResolvers() throws KeyResolverException {
      Iterator var1 = KeyResolver.iterator();

      while(var1.hasNext()) {
         KeyResolverSpi var2 = (KeyResolverSpi)var1.next();
         var2.setSecureValidation(this.secureValidation);
         Node var3 = this.constructionElement.getFirstChild();

         for(String var4 = this.getBaseURI(); var3 != null; var3 = var3.getNextSibling()) {
            if (var3.getNodeType() == 1) {
               PrivateKey var5 = var2.engineLookupAndResolvePrivateKey((Element)var3, var4, (StorageResolver)null);
               if (var5 != null) {
                  return var5;
               }
            }
         }
      }

      return null;
   }

   PrivateKey getPrivateKeyFromInternalResolvers() throws KeyResolverException {
      Iterator var1 = this.internalKeyResolvers.iterator();

      while(var1.hasNext()) {
         KeyResolverSpi var2 = (KeyResolverSpi)var1.next();
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Try " + var2.getClass().getName());
         }

         var2.setSecureValidation(this.secureValidation);
         Node var3 = this.constructionElement.getFirstChild();

         for(String var4 = this.getBaseURI(); var3 != null; var3 = var3.getNextSibling()) {
            if (var3.getNodeType() == 1) {
               PrivateKey var5 = var2.engineLookupAndResolvePrivateKey((Element)var3, var4, (StorageResolver)null);
               if (var5 != null) {
                  return var5;
               }
            }
         }
      }

      return null;
   }

   public void registerInternalKeyResolver(KeyResolverSpi var1) {
      this.internalKeyResolvers.add(var1);
   }

   int lengthInternalKeyResolver() {
      return this.internalKeyResolvers.size();
   }

   KeyResolverSpi itemInternalKeyResolver(int var1) {
      return (KeyResolverSpi)this.internalKeyResolvers.get(var1);
   }

   public void addStorageResolver(StorageResolver var1) {
      if (this.storageResolvers == nullList) {
         this.storageResolvers = new ArrayList();
      }

      this.storageResolvers.add(var1);
   }

   public String getBaseLocalName() {
      return "KeyInfo";
   }

   static {
      ArrayList var0 = new ArrayList(1);
      var0.add((Object)null);
      nullList = Collections.unmodifiableList(var0);
   }
}
