package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignatureProperties;
import javax.xml.crypto.dsig.SignatureProperty;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMXMLSignatureFactory extends XMLSignatureFactory {
   public XMLSignature newXMLSignature(SignedInfo var1, KeyInfo var2) {
      return new DOMXMLSignature(var1, var2, (List)null, (String)null, (String)null);
   }

   public XMLSignature newXMLSignature(SignedInfo var1, KeyInfo var2, List var3, String var4, String var5) {
      return new DOMXMLSignature(var1, var2, var3, var4, var5);
   }

   public Reference newReference(String var1, DigestMethod var2) {
      return this.newReference(var1, var2, (List)null, (String)null, (String)null);
   }

   public Reference newReference(String var1, DigestMethod var2, List var3, String var4, String var5) {
      return new DOMReference(var1, var4, var2, var3, var5, this.getProvider());
   }

   public Reference newReference(String var1, DigestMethod var2, List var3, Data var4, List var5, String var6, String var7) {
      if (var3 == null) {
         throw new NullPointerException("appliedTransforms cannot be null");
      } else if (var3.isEmpty()) {
         throw new NullPointerException("appliedTransforms cannot be empty");
      } else if (var4 == null) {
         throw new NullPointerException("result cannot be null");
      } else {
         return new DOMReference(var1, var6, var2, var3, var4, var5, var7, this.getProvider());
      }
   }

   public Reference newReference(String var1, DigestMethod var2, List var3, String var4, String var5, byte[] var6) {
      if (var6 == null) {
         throw new NullPointerException("digestValue cannot be null");
      } else {
         return new DOMReference(var1, var4, var2, (List)null, (Data)null, var3, var5, var6, this.getProvider());
      }
   }

   public SignedInfo newSignedInfo(CanonicalizationMethod var1, SignatureMethod var2, List var3) {
      return this.newSignedInfo(var1, var2, var3, (String)null);
   }

   public SignedInfo newSignedInfo(CanonicalizationMethod var1, SignatureMethod var2, List var3, String var4) {
      return new DOMSignedInfo(var1, var2, var3, var4);
   }

   public XMLObject newXMLObject(List var1, String var2, String var3, String var4) {
      return new DOMXMLObject(var1, var2, var3, var4);
   }

   public Manifest newManifest(List var1) {
      return this.newManifest(var1, (String)null);
   }

   public Manifest newManifest(List var1, String var2) {
      return new DOMManifest(var1, var2);
   }

   public SignatureProperties newSignatureProperties(List var1, String var2) {
      return new DOMSignatureProperties(var1, var2);
   }

   public SignatureProperty newSignatureProperty(List var1, String var2, String var3) {
      return new DOMSignatureProperty(var1, var2, var3);
   }

   public XMLSignature unmarshalXMLSignature(XMLValidateContext var1) throws MarshalException {
      if (var1 == null) {
         throw new NullPointerException("context cannot be null");
      } else {
         return this.unmarshal(((DOMValidateContext)var1).getNode(), var1);
      }
   }

   public XMLSignature unmarshalXMLSignature(XMLStructure var1) throws MarshalException {
      if (var1 == null) {
         throw new NullPointerException("xmlStructure cannot be null");
      } else if (!(var1 instanceof javax.xml.crypto.dom.DOMStructure)) {
         throw new ClassCastException("xmlStructure must be of type DOMStructure");
      } else {
         return this.unmarshal(((javax.xml.crypto.dom.DOMStructure)var1).getNode(), new DOMXMLSignatureFactory.UnmarshalContext());
      }
   }

   private XMLSignature unmarshal(Node var1, XMLCryptoContext var2) throws MarshalException {
      var1.normalize();
      Element var3 = null;
      if (var1.getNodeType() == 9) {
         var3 = ((Document)var1).getDocumentElement();
      } else {
         if (var1.getNodeType() != 1) {
            throw new MarshalException("Signature element is not a proper Node");
         }

         var3 = (Element)var1;
      }

      String var4 = var3.getLocalName();
      if (var4 == null) {
         throw new MarshalException("Document implementation must support DOM Level 2 and be namespace aware");
      } else if (var4.equals("Signature")) {
         return new DOMXMLSignature(var3, var2, this.getProvider());
      } else {
         throw new MarshalException("invalid Signature tag: " + var4);
      }
   }

   public boolean isFeatureSupported(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return false;
      }
   }

   public DigestMethod newDigestMethod(String var1, DigestMethodParameterSpec var2) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var1.equals("http://www.w3.org/2000/09/xmldsig#sha1")) {
         return new DOMDigestMethod.SHA1(var2);
      } else if (var1.equals("http://www.w3.org/2001/04/xmlenc#sha256")) {
         return new DOMDigestMethod.SHA256(var2);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#sha384")) {
         return new DOMDigestMethod.SHA384(var2);
      } else if (var1.equals("http://www.w3.org/2001/04/xmlenc#sha512")) {
         return new DOMDigestMethod.SHA512(var2);
      } else {
         throw new NoSuchAlgorithmException("unsupported algorithm");
      }
   }

   public SignatureMethod newSignatureMethod(String var1, SignatureMethodParameterSpec var2) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var1.equals("http://www.w3.org/2000/09/xmldsig#rsa-sha1")) {
         return new DOMSignatureMethod.SHA1withRSA(var2);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256")) {
         return new DOMSignatureMethod.SHA256withRSA(var2);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384")) {
         return new DOMSignatureMethod.SHA384withRSA(var2);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512")) {
         return new DOMSignatureMethod.SHA512withRSA(var2);
      } else if (var1.equals("http://www.w3.org/2000/09/xmldsig#dsa-sha1")) {
         return new DOMSignatureMethod.SHA1withDSA(var2);
      } else if (var1.equals("http://www.w3.org/2009/xmldsig11#dsa-sha256")) {
         return new DOMSignatureMethod.SHA256withDSA(var2);
      } else if (var1.equals("http://www.w3.org/2000/09/xmldsig#hmac-sha1")) {
         return new DOMHMACSignatureMethod.SHA1(var2);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256")) {
         return new DOMHMACSignatureMethod.SHA256(var2);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384")) {
         return new DOMHMACSignatureMethod.SHA384(var2);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512")) {
         return new DOMHMACSignatureMethod.SHA512(var2);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1")) {
         return new DOMSignatureMethod.SHA1withECDSA(var2);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256")) {
         return new DOMSignatureMethod.SHA256withECDSA(var2);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384")) {
         return new DOMSignatureMethod.SHA384withECDSA(var2);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512")) {
         return new DOMSignatureMethod.SHA512withECDSA(var2);
      } else {
         throw new NoSuchAlgorithmException("unsupported algorithm");
      }
   }

   public Transform newTransform(String var1, TransformParameterSpec var2) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
      TransformService var3;
      if (this.getProvider() == null) {
         var3 = TransformService.getInstance(var1, "DOM");
      } else {
         try {
            var3 = TransformService.getInstance(var1, "DOM", this.getProvider());
         } catch (NoSuchAlgorithmException var5) {
            var3 = TransformService.getInstance(var1, "DOM");
         }
      }

      var3.init(var2);
      return new DOMTransform(var3);
   }

   public Transform newTransform(String var1, XMLStructure var2) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
      TransformService var3;
      if (this.getProvider() == null) {
         var3 = TransformService.getInstance(var1, "DOM");
      } else {
         try {
            var3 = TransformService.getInstance(var1, "DOM", this.getProvider());
         } catch (NoSuchAlgorithmException var5) {
            var3 = TransformService.getInstance(var1, "DOM");
         }
      }

      if (var2 == null) {
         var3.init((TransformParameterSpec)null);
      } else {
         var3.init(var2, (XMLCryptoContext)null);
      }

      return new DOMTransform(var3);
   }

   public CanonicalizationMethod newCanonicalizationMethod(String var1, C14NMethodParameterSpec var2) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
      TransformService var3;
      if (this.getProvider() == null) {
         var3 = TransformService.getInstance(var1, "DOM");
      } else {
         try {
            var3 = TransformService.getInstance(var1, "DOM", this.getProvider());
         } catch (NoSuchAlgorithmException var5) {
            var3 = TransformService.getInstance(var1, "DOM");
         }
      }

      var3.init(var2);
      return new DOMCanonicalizationMethod(var3);
   }

   public CanonicalizationMethod newCanonicalizationMethod(String var1, XMLStructure var2) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
      TransformService var3;
      if (this.getProvider() == null) {
         var3 = TransformService.getInstance(var1, "DOM");
      } else {
         try {
            var3 = TransformService.getInstance(var1, "DOM", this.getProvider());
         } catch (NoSuchAlgorithmException var5) {
            var3 = TransformService.getInstance(var1, "DOM");
         }
      }

      if (var2 == null) {
         var3.init((TransformParameterSpec)null);
      } else {
         var3.init(var2, (XMLCryptoContext)null);
      }

      return new DOMCanonicalizationMethod(var3);
   }

   public URIDereferencer getURIDereferencer() {
      return DOMURIDereferencer.INSTANCE;
   }

   private static class UnmarshalContext extends DOMCryptoContext {
      UnmarshalContext() {
      }
   }
}
