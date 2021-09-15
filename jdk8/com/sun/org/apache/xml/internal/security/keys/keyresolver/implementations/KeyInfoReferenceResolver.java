package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
import com.sun.org.apache.xml.internal.security.keys.content.KeyInfoReference;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class KeyInfoReferenceResolver extends KeyResolverSpi {
   private static Logger log = Logger.getLogger(KeyInfoReferenceResolver.class.getName());

   public boolean engineCanResolve(Element var1, String var2, StorageResolver var3) {
      return XMLUtils.elementIsInSignature11Space(var1, "KeyInfoReference");
   }

   public PublicKey engineLookupAndResolvePublicKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Can I resolve " + var1.getTagName());
      }

      if (!this.engineCanResolve(var1, var2, var3)) {
         return null;
      } else {
         try {
            KeyInfo var4 = this.resolveReferentKeyInfo(var1, var2, var3);
            if (var4 != null) {
               return var4.getPublicKey();
            }
         } catch (XMLSecurityException var5) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)"XMLSecurityException", (Throwable)var5);
            }
         }

         return null;
      }
   }

   public X509Certificate engineLookupResolveX509Certificate(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Can I resolve " + var1.getTagName());
      }

      if (!this.engineCanResolve(var1, var2, var3)) {
         return null;
      } else {
         try {
            KeyInfo var4 = this.resolveReferentKeyInfo(var1, var2, var3);
            if (var4 != null) {
               return var4.getX509Certificate();
            }
         } catch (XMLSecurityException var5) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)"XMLSecurityException", (Throwable)var5);
            }
         }

         return null;
      }
   }

   public SecretKey engineLookupAndResolveSecretKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Can I resolve " + var1.getTagName());
      }

      if (!this.engineCanResolve(var1, var2, var3)) {
         return null;
      } else {
         try {
            KeyInfo var4 = this.resolveReferentKeyInfo(var1, var2, var3);
            if (var4 != null) {
               return var4.getSecretKey();
            }
         } catch (XMLSecurityException var5) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)"XMLSecurityException", (Throwable)var5);
            }
         }

         return null;
      }
   }

   public PrivateKey engineLookupAndResolvePrivateKey(Element var1, String var2, StorageResolver var3) throws KeyResolverException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Can I resolve " + var1.getTagName());
      }

      if (!this.engineCanResolve(var1, var2, var3)) {
         return null;
      } else {
         try {
            KeyInfo var4 = this.resolveReferentKeyInfo(var1, var2, var3);
            if (var4 != null) {
               return var4.getPrivateKey();
            }
         } catch (XMLSecurityException var5) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)"XMLSecurityException", (Throwable)var5);
            }
         }

         return null;
      }
   }

   private KeyInfo resolveReferentKeyInfo(Element var1, String var2, StorageResolver var3) throws XMLSecurityException {
      KeyInfoReference var4 = new KeyInfoReference(var1, var2);
      Attr var5 = var4.getURIAttr();
      XMLSignatureInput var6 = this.resolveInput(var5, var2, this.secureValidation);
      Element var7 = null;

      try {
         var7 = this.obtainReferenceElement(var6);
      } catch (Exception var9) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)"XMLSecurityException", (Throwable)var9);
         }

         return null;
      }

      if (var7 == null) {
         log.log(Level.FINE, "De-reference of KeyInfoReference URI returned null: " + var5.getValue());
         return null;
      } else {
         this.validateReference(var7);
         KeyInfo var8 = new KeyInfo(var7, var2);
         var8.addStorageResolver(var3);
         return var8;
      }
   }

   private void validateReference(Element var1) throws XMLSecurityException {
      if (!XMLUtils.elementIsInSignatureSpace(var1, "KeyInfo")) {
         Object[] var3 = new Object[]{new QName(var1.getNamespaceURI(), var1.getLocalName())};
         throw new XMLSecurityException("KeyInfoReferenceResolver.InvalidReferentElement.WrongType", var3);
      } else {
         KeyInfo var2 = new KeyInfo(var1, "");
         if (var2.containsKeyInfoReference()) {
            if (this.secureValidation) {
               throw new XMLSecurityException("KeyInfoReferenceResolver.InvalidReferentElement.ReferenceWithSecure");
            } else {
               throw new XMLSecurityException("KeyInfoReferenceResolver.InvalidReferentElement.ReferenceWithoutSecure");
            }
         }
      }
   }

   private XMLSignatureInput resolveInput(Attr var1, String var2, boolean var3) throws XMLSecurityException {
      ResourceResolver var4 = ResourceResolver.getInstance(var1, var2, var3);
      XMLSignatureInput var5 = var4.resolve(var1, var2, var3);
      return var5;
   }

   private Element obtainReferenceElement(XMLSignatureInput var1) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException, KeyResolverException {
      Element var2;
      if (var1.isElement()) {
         var2 = (Element)var1.getSubNode();
      } else {
         if (var1.isNodeSet()) {
            log.log(Level.FINE, "De-reference of KeyInfoReference returned an unsupported NodeSet");
            return null;
         }

         byte[] var3 = var1.getBytes();
         var2 = this.getDocFromBytes(var3);
      }

      return var2;
   }

   private Element getDocFromBytes(byte[] var1) throws KeyResolverException {
      try {
         DocumentBuilderFactory var2 = DocumentBuilderFactory.newInstance();
         var2.setNamespaceAware(true);
         var2.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
         DocumentBuilder var3 = var2.newDocumentBuilder();
         Document var4 = var3.parse((InputStream)(new ByteArrayInputStream(var1)));
         return var4.getDocumentElement();
      } catch (SAXException var5) {
         throw new KeyResolverException("empty", var5);
      } catch (IOException var6) {
         throw new KeyResolverException("empty", var6);
      } catch (ParserConfigurationException var7) {
         throw new KeyResolverException("empty", var7);
      }
   }
}
