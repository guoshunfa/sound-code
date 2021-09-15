package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.RetrievalMethod;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class RetrievalMethodResolver extends KeyResolverSpi {
   private static Logger log = Logger.getLogger(RetrievalMethodResolver.class.getName());

   public PublicKey engineLookupAndResolvePublicKey(Element var1, String var2, StorageResolver var3) {
      if (!XMLUtils.elementIsInSignatureSpace(var1, "RetrievalMethod")) {
         return null;
      } else {
         try {
            RetrievalMethod var4 = new RetrievalMethod(var1, var2);
            String var5 = var4.getType();
            XMLSignatureInput var6 = resolveInput(var4, var2, this.secureValidation);
            if ("http://www.w3.org/2000/09/xmldsig#rawX509Certificate".equals(var5)) {
               X509Certificate var16 = getRawCertificate(var6);
               if (var16 != null) {
                  return var16.getPublicKey();
               }

               return null;
            }

            Element var7 = obtainReferenceElement(var6);
            if (XMLUtils.elementIsInSignatureSpace(var7, "RetrievalMethod")) {
               if (this.secureValidation) {
                  String var17 = "Error: It is forbidden to have one RetrievalMethod point to another with secure validation";
                  if (log.isLoggable(Level.FINE)) {
                     log.log(Level.FINE, var17);
                  }

                  return null;
               }

               RetrievalMethod var8 = new RetrievalMethod(var7, var2);
               XMLSignatureInput var9 = resolveInput(var8, var2, this.secureValidation);
               Element var10 = obtainReferenceElement(var9);
               if (var10 == var1) {
                  if (log.isLoggable(Level.FINE)) {
                     log.log(Level.FINE, "Error: Can't have RetrievalMethods pointing to each other");
                  }

                  return null;
               }
            }

            return resolveKey(var7, var2, var3);
         } catch (XMLSecurityException var11) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)"XMLSecurityException", (Throwable)var11);
            }
         } catch (CertificateException var12) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)"CertificateException", (Throwable)var12);
            }
         } catch (IOException var13) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)"IOException", (Throwable)var13);
            }
         } catch (ParserConfigurationException var14) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)"ParserConfigurationException", (Throwable)var14);
            }
         } catch (SAXException var15) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)"SAXException", (Throwable)var15);
            }
         }

         return null;
      }
   }

   public X509Certificate engineLookupResolveX509Certificate(Element var1, String var2, StorageResolver var3) {
      if (!XMLUtils.elementIsInSignatureSpace(var1, "RetrievalMethod")) {
         return null;
      } else {
         try {
            RetrievalMethod var4 = new RetrievalMethod(var1, var2);
            String var5 = var4.getType();
            XMLSignatureInput var6 = resolveInput(var4, var2, this.secureValidation);
            if ("http://www.w3.org/2000/09/xmldsig#rawX509Certificate".equals(var5)) {
               return getRawCertificate(var6);
            }

            Element var7 = obtainReferenceElement(var6);
            if (XMLUtils.elementIsInSignatureSpace(var7, "RetrievalMethod")) {
               if (this.secureValidation) {
                  String var16 = "Error: It is forbidden to have one RetrievalMethod point to another with secure validation";
                  if (log.isLoggable(Level.FINE)) {
                     log.log(Level.FINE, var16);
                  }

                  return null;
               }

               RetrievalMethod var8 = new RetrievalMethod(var7, var2);
               XMLSignatureInput var9 = resolveInput(var8, var2, this.secureValidation);
               Element var10 = obtainReferenceElement(var9);
               if (var10 == var1) {
                  if (log.isLoggable(Level.FINE)) {
                     log.log(Level.FINE, "Error: Can't have RetrievalMethods pointing to each other");
                  }

                  return null;
               }
            }

            return resolveCertificate(var7, var2, var3);
         } catch (XMLSecurityException var11) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)"XMLSecurityException", (Throwable)var11);
            }
         } catch (CertificateException var12) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)"CertificateException", (Throwable)var12);
            }
         } catch (IOException var13) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)"IOException", (Throwable)var13);
            }
         } catch (ParserConfigurationException var14) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)"ParserConfigurationException", (Throwable)var14);
            }
         } catch (SAXException var15) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, (String)"SAXException", (Throwable)var15);
            }
         }

         return null;
      }
   }

   private static X509Certificate resolveCertificate(Element var0, String var1, StorageResolver var2) throws KeyResolverException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Now we have a {" + var0.getNamespaceURI() + "}" + var0.getLocalName() + " Element");
      }

      return var0 != null ? KeyResolver.getX509Certificate(var0, var1, var2) : null;
   }

   private static PublicKey resolveKey(Element var0, String var1, StorageResolver var2) throws KeyResolverException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Now we have a {" + var0.getNamespaceURI() + "}" + var0.getLocalName() + " Element");
      }

      return var0 != null ? KeyResolver.getPublicKey(var0, var1, var2) : null;
   }

   private static Element obtainReferenceElement(XMLSignatureInput var0) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException, KeyResolverException {
      Element var1;
      if (var0.isElement()) {
         var1 = (Element)var0.getSubNode();
      } else if (var0.isNodeSet()) {
         var1 = getDocumentElement(var0.getNodeSet());
      } else {
         byte[] var2 = var0.getBytes();
         var1 = getDocFromBytes(var2);
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "we have to parse " + var2.length + " bytes");
         }
      }

      return var1;
   }

   private static X509Certificate getRawCertificate(XMLSignatureInput var0) throws CanonicalizationException, IOException, CertificateException {
      byte[] var1 = var0.getBytes();
      CertificateFactory var2 = CertificateFactory.getInstance("X.509");
      X509Certificate var3 = (X509Certificate)var2.generateCertificate(new ByteArrayInputStream(var1));
      return var3;
   }

   private static XMLSignatureInput resolveInput(RetrievalMethod var0, String var1, boolean var2) throws XMLSecurityException {
      Attr var3 = var0.getURIAttr();
      Transforms var4 = var0.getTransforms();
      ResourceResolver var5 = ResourceResolver.getInstance(var3, var1, var2);
      XMLSignatureInput var6 = var5.resolve(var3, var1, var2);
      if (var4 != null) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "We have Transforms");
         }

         var6 = var4.performTransforms(var6);
      }

      return var6;
   }

   private static Element getDocFromBytes(byte[] var0) throws KeyResolverException {
      try {
         DocumentBuilderFactory var1 = DocumentBuilderFactory.newInstance();
         var1.setNamespaceAware(true);
         var1.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
         DocumentBuilder var2 = var1.newDocumentBuilder();
         Document var3 = var2.parse((InputStream)(new ByteArrayInputStream(var0)));
         return var3.getDocumentElement();
      } catch (SAXException var4) {
         throw new KeyResolverException("empty", var4);
      } catch (IOException var5) {
         throw new KeyResolverException("empty", var5);
      } catch (ParserConfigurationException var6) {
         throw new KeyResolverException("empty", var6);
      }
   }

   public SecretKey engineLookupAndResolveSecretKey(Element var1, String var2, StorageResolver var3) {
      return null;
   }

   private static Element getDocumentElement(Set<Node> var0) {
      Iterator var1 = var0.iterator();
      Element var2 = null;

      while(var1.hasNext()) {
         Node var3 = (Node)var1.next();
         if (var3 != null && 1 == var3.getNodeType()) {
            var2 = (Element)var3;
            break;
         }
      }

      Node var4;
      ArrayList var6;
      for(var6 = new ArrayList(); var2 != null; var2 = (Element)var4) {
         var6.add(var2);
         var4 = var2.getParentNode();
         if (var4 == null || 1 != var4.getNodeType()) {
            break;
         }
      }

      ListIterator var7 = var6.listIterator(var6.size() - 1);
      Element var5 = null;

      do {
         if (!var7.hasPrevious()) {
            return null;
         }

         var5 = (Element)var7.previous();
      } while(!var0.contains(var5));

      return var5;
   }
}
