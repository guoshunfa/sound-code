package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class SignedInfo extends Manifest {
   private SignatureAlgorithm signatureAlgorithm;
   private byte[] c14nizedBytes;
   private Element c14nMethod;
   private Element signatureMethod;

   public SignedInfo(Document var1) throws XMLSecurityException {
      this(var1, "http://www.w3.org/2000/09/xmldsig#dsa-sha1", "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
   }

   public SignedInfo(Document var1, String var2, String var3) throws XMLSecurityException {
      this(var1, var2, 0, var3);
   }

   public SignedInfo(Document var1, String var2, int var3, String var4) throws XMLSecurityException {
      super(var1);
      this.signatureAlgorithm = null;
      this.c14nizedBytes = null;
      this.c14nMethod = XMLUtils.createElementInSignatureSpace(this.doc, "CanonicalizationMethod");
      this.c14nMethod.setAttributeNS((String)null, "Algorithm", var4);
      this.constructionElement.appendChild(this.c14nMethod);
      XMLUtils.addReturnToElement(this.constructionElement);
      if (var3 > 0) {
         this.signatureAlgorithm = new SignatureAlgorithm(this.doc, var2, var3);
      } else {
         this.signatureAlgorithm = new SignatureAlgorithm(this.doc, var2);
      }

      this.signatureMethod = this.signatureAlgorithm.getElement();
      this.constructionElement.appendChild(this.signatureMethod);
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public SignedInfo(Document var1, Element var2, Element var3) throws XMLSecurityException {
      super(var1);
      this.signatureAlgorithm = null;
      this.c14nizedBytes = null;
      this.c14nMethod = var3;
      this.constructionElement.appendChild(this.c14nMethod);
      XMLUtils.addReturnToElement(this.constructionElement);
      this.signatureAlgorithm = new SignatureAlgorithm(var2, (String)null);
      this.signatureMethod = this.signatureAlgorithm.getElement();
      this.constructionElement.appendChild(this.signatureMethod);
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public SignedInfo(Element var1, String var2) throws XMLSecurityException {
      this(var1, var2, false);
   }

   public SignedInfo(Element var1, String var2, boolean var3) throws XMLSecurityException {
      super(reparseSignedInfoElem(var1), var2, var3);
      this.signatureAlgorithm = null;
      this.c14nizedBytes = null;
      this.c14nMethod = XMLUtils.getNextElement(var1.getFirstChild());
      this.signatureMethod = XMLUtils.getNextElement(this.c14nMethod.getNextSibling());
      this.signatureAlgorithm = new SignatureAlgorithm(this.signatureMethod, this.getBaseURI(), var3);
   }

   private static Element reparseSignedInfoElem(Element var0) throws XMLSecurityException {
      Element var1 = XMLUtils.getNextElement(var0.getFirstChild());
      String var2 = var1.getAttributeNS((String)null, "Algorithm");
      if (!var2.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315") && !var2.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments") && !var2.equals("http://www.w3.org/2001/10/xml-exc-c14n#") && !var2.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments") && !var2.equals("http://www.w3.org/2006/12/xml-c14n11") && !var2.equals("http://www.w3.org/2006/12/xml-c14n11#WithComments")) {
         try {
            Canonicalizer var3 = Canonicalizer.getInstance(var2);
            byte[] var4 = var3.canonicalizeSubtree(var0);
            DocumentBuilderFactory var5 = DocumentBuilderFactory.newInstance();
            var5.setNamespaceAware(true);
            var5.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
            DocumentBuilder var6 = var5.newDocumentBuilder();
            Document var7 = var6.parse((InputStream)(new ByteArrayInputStream(var4)));
            Node var8 = var0.getOwnerDocument().importNode(var7.getDocumentElement(), true);
            var0.getParentNode().replaceChild(var8, var0);
            return (Element)var8;
         } catch (ParserConfigurationException var9) {
            throw new XMLSecurityException("empty", var9);
         } catch (IOException var10) {
            throw new XMLSecurityException("empty", var10);
         } catch (SAXException var11) {
            throw new XMLSecurityException("empty", var11);
         }
      } else {
         return var0;
      }
   }

   public boolean verify() throws MissingResourceFailureException, XMLSecurityException {
      return super.verifyReferences(false);
   }

   public boolean verify(boolean var1) throws MissingResourceFailureException, XMLSecurityException {
      return super.verifyReferences(var1);
   }

   public byte[] getCanonicalizedOctetStream() throws CanonicalizationException, InvalidCanonicalizerException, XMLSecurityException {
      if (this.c14nizedBytes == null) {
         Canonicalizer var1 = Canonicalizer.getInstance(this.getCanonicalizationMethodURI());
         this.c14nizedBytes = var1.canonicalizeSubtree(this.constructionElement);
      }

      return (byte[])this.c14nizedBytes.clone();
   }

   public void signInOctetStream(OutputStream var1) throws CanonicalizationException, InvalidCanonicalizerException, XMLSecurityException {
      if (this.c14nizedBytes == null) {
         Canonicalizer var2 = Canonicalizer.getInstance(this.getCanonicalizationMethodURI());
         var2.setWriter(var1);
         String var3 = this.getInclusiveNamespaces();
         if (var3 == null) {
            var2.canonicalizeSubtree(this.constructionElement);
         } else {
            var2.canonicalizeSubtree(this.constructionElement, var3);
         }
      } else {
         try {
            var1.write(this.c14nizedBytes);
         } catch (IOException var4) {
            throw new RuntimeException(var4);
         }
      }

   }

   public String getCanonicalizationMethodURI() {
      return this.c14nMethod.getAttributeNS((String)null, "Algorithm");
   }

   public String getSignatureMethodURI() {
      Element var1 = this.getSignatureMethodElement();
      return var1 != null ? var1.getAttributeNS((String)null, "Algorithm") : null;
   }

   public Element getSignatureMethodElement() {
      return this.signatureMethod;
   }

   public SecretKey createSecretKey(byte[] var1) {
      return new SecretKeySpec(var1, this.signatureAlgorithm.getJCEAlgorithmString());
   }

   protected SignatureAlgorithm getSignatureAlgorithm() {
      return this.signatureAlgorithm;
   }

   public String getBaseLocalName() {
      return "SignedInfo";
   }

   public String getInclusiveNamespaces() {
      String var1 = this.c14nMethod.getAttributeNS((String)null, "Algorithm");
      if (!var1.equals("http://www.w3.org/2001/10/xml-exc-c14n#") && !var1.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")) {
         return null;
      } else {
         Element var2 = XMLUtils.getNextElement(this.c14nMethod.getFirstChild());
         if (var2 != null) {
            try {
               String var3 = (new InclusiveNamespaces(var2, "http://www.w3.org/2001/10/xml-exc-c14n#")).getInclusiveNamespaces();
               return var3;
            } catch (XMLSecurityException var4) {
               return null;
            }
         } else {
            return null;
         }
      }
   }
}
