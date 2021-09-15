package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.SignerOutputStream;
import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.io.IOException;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public final class XMLSignature extends SignatureElementProxy {
   public static final String ALGO_ID_MAC_HMAC_SHA1 = "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
   public static final String ALGO_ID_SIGNATURE_DSA = "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
   public static final String ALGO_ID_SIGNATURE_DSA_SHA256 = "http://www.w3.org/2009/xmldsig11#dsa-sha256";
   public static final String ALGO_ID_SIGNATURE_RSA = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
   public static final String ALGO_ID_SIGNATURE_RSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
   public static final String ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5 = "http://www.w3.org/2001/04/xmldsig-more#rsa-md5";
   public static final String ALGO_ID_SIGNATURE_RSA_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
   public static final String ALGO_ID_SIGNATURE_RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
   public static final String ALGO_ID_SIGNATURE_RSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
   public static final String ALGO_ID_SIGNATURE_RSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
   public static final String ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5 = "http://www.w3.org/2001/04/xmldsig-more#hmac-md5";
   public static final String ALGO_ID_MAC_HMAC_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160";
   public static final String ALGO_ID_MAC_HMAC_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
   public static final String ALGO_ID_MAC_HMAC_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
   public static final String ALGO_ID_MAC_HMAC_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
   public static final String ALGO_ID_SIGNATURE_ECDSA_SHA1 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
   public static final String ALGO_ID_SIGNATURE_ECDSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
   public static final String ALGO_ID_SIGNATURE_ECDSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
   public static final String ALGO_ID_SIGNATURE_ECDSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
   private static Logger log = Logger.getLogger(XMLSignature.class.getName());
   private SignedInfo signedInfo;
   private KeyInfo keyInfo;
   private boolean followManifestsDuringValidation;
   private Element signatureValueElement;
   private static final int MODE_SIGN = 0;
   private static final int MODE_VERIFY = 1;
   private int state;

   public XMLSignature(Document var1, String var2, String var3) throws XMLSecurityException {
      this(var1, var2, var3, 0, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
   }

   public XMLSignature(Document var1, String var2, String var3, int var4) throws XMLSecurityException {
      this(var1, var2, var3, var4, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
   }

   public XMLSignature(Document var1, String var2, String var3, String var4) throws XMLSecurityException {
      this(var1, var2, var3, 0, var4);
   }

   public XMLSignature(Document var1, String var2, String var3, int var4, String var5) throws XMLSecurityException {
      super(var1);
      this.followManifestsDuringValidation = false;
      this.state = 0;
      String var6 = getDefaultPrefix("http://www.w3.org/2000/09/xmldsig#");
      if (var6 != null && var6.length() != 0) {
         this.constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + var6, "http://www.w3.org/2000/09/xmldsig#");
      } else {
         this.constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
      }

      XMLUtils.addReturnToElement(this.constructionElement);
      this.baseURI = var2;
      this.signedInfo = new SignedInfo(this.doc, var3, var4, var5);
      this.constructionElement.appendChild(this.signedInfo.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
      this.signatureValueElement = XMLUtils.createElementInSignatureSpace(this.doc, "SignatureValue");
      this.constructionElement.appendChild(this.signatureValueElement);
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public XMLSignature(Document var1, String var2, Element var3, Element var4) throws XMLSecurityException {
      super(var1);
      this.followManifestsDuringValidation = false;
      this.state = 0;
      String var5 = getDefaultPrefix("http://www.w3.org/2000/09/xmldsig#");
      if (var5 != null && var5.length() != 0) {
         this.constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + var5, "http://www.w3.org/2000/09/xmldsig#");
      } else {
         this.constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
      }

      XMLUtils.addReturnToElement(this.constructionElement);
      this.baseURI = var2;
      this.signedInfo = new SignedInfo(this.doc, var3, var4);
      this.constructionElement.appendChild(this.signedInfo.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
      this.signatureValueElement = XMLUtils.createElementInSignatureSpace(this.doc, "SignatureValue");
      this.constructionElement.appendChild(this.signatureValueElement);
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public XMLSignature(Element var1, String var2) throws XMLSignatureException, XMLSecurityException {
      this(var1, var2, false);
   }

   public XMLSignature(Element var1, String var2, boolean var3) throws XMLSignatureException, XMLSecurityException {
      super(var1, var2);
      this.followManifestsDuringValidation = false;
      this.state = 0;
      Element var4 = XMLUtils.getNextElement(var1.getFirstChild());
      Object[] var15;
      if (var4 == null) {
         var15 = new Object[]{"SignedInfo", "Signature"};
         throw new XMLSignatureException("xml.WrongContent", var15);
      } else {
         this.signedInfo = new SignedInfo(var4, var2, var3);
         var4 = XMLUtils.getNextElement(var1.getFirstChild());
         this.signatureValueElement = XMLUtils.getNextElement(var4.getNextSibling());
         if (this.signatureValueElement == null) {
            var15 = new Object[]{"SignatureValue", "Signature"};
            throw new XMLSignatureException("xml.WrongContent", var15);
         } else {
            Attr var5 = this.signatureValueElement.getAttributeNodeNS((String)null, "Id");
            if (var5 != null) {
               this.signatureValueElement.setIdAttributeNode(var5, true);
            }

            Element var6 = XMLUtils.getNextElement(this.signatureValueElement.getNextSibling());
            if (var6 != null && var6.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#") && var6.getLocalName().equals("KeyInfo")) {
               this.keyInfo = new KeyInfo(var6, var2);
               this.keyInfo.setSecureValidation(var3);
            }

            for(Element var7 = XMLUtils.getNextElement(this.signatureValueElement.getNextSibling()); var7 != null; var7 = XMLUtils.getNextElement(var7.getNextSibling())) {
               Attr var8 = var7.getAttributeNodeNS((String)null, "Id");
               if (var8 != null) {
                  var7.setIdAttributeNode(var8, true);
               }

               NodeList var9 = var7.getChildNodes();
               int var10 = var9.getLength();

               for(int var11 = 0; var11 < var10; ++var11) {
                  Node var12 = var9.item(var11);
                  if (var12.getNodeType() == 1) {
                     Element var13 = (Element)var12;
                     String var14 = var13.getLocalName();
                     if (var14.equals("Manifest")) {
                        new Manifest(var13, var2);
                     } else if (var14.equals("SignatureProperties")) {
                        new SignatureProperties(var13, var2);
                     }
                  }
               }
            }

            this.state = 1;
         }
      }
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

   public SignedInfo getSignedInfo() {
      return this.signedInfo;
   }

   public byte[] getSignatureValue() throws XMLSignatureException {
      try {
         return Base64.decode(this.signatureValueElement);
      } catch (Base64DecodingException var2) {
         throw new XMLSignatureException("empty", var2);
      }
   }

   private void setSignatureValueElement(byte[] var1) {
      while(this.signatureValueElement.hasChildNodes()) {
         this.signatureValueElement.removeChild(this.signatureValueElement.getFirstChild());
      }

      String var2 = Base64.encode(var1);
      if (var2.length() > 76 && !XMLUtils.ignoreLineBreaks()) {
         var2 = "\n" + var2 + "\n";
      }

      Text var3 = this.doc.createTextNode(var2);
      this.signatureValueElement.appendChild(var3);
   }

   public KeyInfo getKeyInfo() {
      if (this.state == 0 && this.keyInfo == null) {
         this.keyInfo = new KeyInfo(this.doc);
         Element var1 = this.keyInfo.getElement();
         Element var2 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "Object", 0);
         if (var2 != null) {
            this.constructionElement.insertBefore(var1, var2);
            XMLUtils.addReturnBeforeChild(this.constructionElement, var2);
         } else {
            this.constructionElement.appendChild(var1);
            XMLUtils.addReturnToElement(this.constructionElement);
         }
      }

      return this.keyInfo;
   }

   public void appendObject(ObjectContainer var1) throws XMLSignatureException {
      this.constructionElement.appendChild(var1.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public ObjectContainer getObjectItem(int var1) {
      Element var2 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "Object", var1);

      try {
         return new ObjectContainer(var2, this.baseURI);
      } catch (XMLSecurityException var4) {
         return null;
      }
   }

   public int getObjectLength() {
      return this.length("http://www.w3.org/2000/09/xmldsig#", "Object");
   }

   public void sign(Key var1) throws XMLSignatureException {
      if (var1 instanceof PublicKey) {
         throw new IllegalArgumentException(I18n.translate("algorithms.operationOnlyVerification"));
      } else {
         try {
            SignedInfo var2 = this.getSignedInfo();
            SignatureAlgorithm var3 = var2.getSignatureAlgorithm();
            UnsyncBufferedOutputStream var4 = null;

            try {
               var3.initSign(var1);
               var2.generateDigestValues();
               var4 = new UnsyncBufferedOutputStream(new SignerOutputStream(var3));
               var2.signInOctetStream(var4);
            } catch (XMLSecurityException var16) {
               throw var16;
            } finally {
               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (IOException var17) {
                     if (log.isLoggable(Level.FINE)) {
                        log.log(Level.FINE, (String)var17.getMessage(), (Throwable)var17);
                     }
                  }
               }

            }

            this.setSignatureValueElement(var3.sign());
         } catch (XMLSignatureException var19) {
            throw var19;
         } catch (CanonicalizationException var20) {
            throw new XMLSignatureException("empty", var20);
         } catch (InvalidCanonicalizerException var21) {
            throw new XMLSignatureException("empty", var21);
         } catch (XMLSecurityException var22) {
            throw new XMLSignatureException("empty", var22);
         }
      }
   }

   public void addResourceResolver(ResourceResolver var1) {
      this.getSignedInfo().addResourceResolver(var1);
   }

   public void addResourceResolver(ResourceResolverSpi var1) {
      this.getSignedInfo().addResourceResolver(var1);
   }

   public boolean checkSignatureValue(X509Certificate var1) throws XMLSignatureException {
      if (var1 != null) {
         return this.checkSignatureValue((Key)var1.getPublicKey());
      } else {
         Object[] var2 = new Object[]{"Didn't get a certificate"};
         throw new XMLSignatureException("empty", var2);
      }
   }

   public boolean checkSignatureValue(Key var1) throws XMLSignatureException {
      if (var1 == null) {
         Object[] var11 = new Object[]{"Didn't get a key"};
         throw new XMLSignatureException("empty", var11);
      } else {
         try {
            SignedInfo var2 = this.getSignedInfo();
            SignatureAlgorithm var3 = var2.getSignatureAlgorithm();
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "signatureMethodURI = " + var3.getAlgorithmURI());
               log.log(Level.FINE, "jceSigAlgorithm    = " + var3.getJCEAlgorithmString());
               log.log(Level.FINE, "jceSigProvider     = " + var3.getJCEProviderName());
               log.log(Level.FINE, "PublicKey = " + var1);
            }

            byte[] var4 = null;

            try {
               var3.initVerify(var1);
               SignerOutputStream var5 = new SignerOutputStream(var3);
               UnsyncBufferedOutputStream var6 = new UnsyncBufferedOutputStream(var5);
               var2.signInOctetStream(var6);
               var6.close();
               var4 = this.getSignatureValue();
            } catch (IOException var7) {
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, (String)var7.getMessage(), (Throwable)var7);
               }
            } catch (XMLSecurityException var8) {
               throw var8;
            }

            if (!var3.verify(var4)) {
               log.log(Level.WARNING, "Signature verification failed.");
               return false;
            } else {
               return var2.verify(this.followManifestsDuringValidation);
            }
         } catch (XMLSignatureException var9) {
            throw var9;
         } catch (XMLSecurityException var10) {
            throw new XMLSignatureException("empty", var10);
         }
      }
   }

   public void addDocument(String var1, Transforms var2, String var3, String var4, String var5) throws XMLSignatureException {
      this.signedInfo.addDocument(this.baseURI, var1, var2, var3, var4, var5);
   }

   public void addDocument(String var1, Transforms var2, String var3) throws XMLSignatureException {
      this.signedInfo.addDocument(this.baseURI, var1, var2, var3, (String)null, (String)null);
   }

   public void addDocument(String var1, Transforms var2) throws XMLSignatureException {
      this.signedInfo.addDocument(this.baseURI, var1, var2, "http://www.w3.org/2000/09/xmldsig#sha1", (String)null, (String)null);
   }

   public void addDocument(String var1) throws XMLSignatureException {
      this.signedInfo.addDocument(this.baseURI, var1, (Transforms)null, "http://www.w3.org/2000/09/xmldsig#sha1", (String)null, (String)null);
   }

   public void addKeyInfo(X509Certificate var1) throws XMLSecurityException {
      X509Data var2 = new X509Data(this.doc);
      var2.addCertificate(var1);
      this.getKeyInfo().add(var2);
   }

   public void addKeyInfo(PublicKey var1) {
      this.getKeyInfo().add(var1);
   }

   public SecretKey createSecretKey(byte[] var1) {
      return this.getSignedInfo().createSecretKey(var1);
   }

   public void setFollowNestedManifests(boolean var1) {
      this.followManifestsDuringValidation = var1;
   }

   public String getBaseLocalName() {
      return "Signature";
   }
}
