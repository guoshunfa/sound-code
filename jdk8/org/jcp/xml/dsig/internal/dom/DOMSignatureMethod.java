package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureECDSA;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.DSAKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.jcp.xml.dsig.internal.SignerOutputStream;
import org.w3c.dom.Element;
import sun.security.util.KeyUtil;

public abstract class DOMSignatureMethod extends AbstractDOMSignatureMethod {
   private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
   private SignatureMethodParameterSpec params;
   private Signature signature;
   static final String RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
   static final String RSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
   static final String RSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
   static final String ECDSA_SHA1 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
   static final String ECDSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
   static final String ECDSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
   static final String ECDSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
   static final String DSA_SHA256 = "http://www.w3.org/2009/xmldsig11#dsa-sha256";

   DOMSignatureMethod(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
      if (var1 != null && !(var1 instanceof SignatureMethodParameterSpec)) {
         throw new InvalidAlgorithmParameterException("params must be of type SignatureMethodParameterSpec");
      } else {
         this.checkParams((SignatureMethodParameterSpec)var1);
         this.params = (SignatureMethodParameterSpec)var1;
      }
   }

   DOMSignatureMethod(Element var1) throws MarshalException {
      Element var2 = DOMUtils.getFirstChildElement(var1);
      if (var2 != null) {
         this.params = this.unmarshalParams(var2);
      }

      try {
         this.checkParams(this.params);
      } catch (InvalidAlgorithmParameterException var4) {
         throw new MarshalException(var4);
      }
   }

   static SignatureMethod unmarshal(Element var0) throws MarshalException {
      String var1 = DOMUtils.getAttributeValue(var0, "Algorithm");
      if (var1.equals("http://www.w3.org/2000/09/xmldsig#rsa-sha1")) {
         return new DOMSignatureMethod.SHA1withRSA(var0);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256")) {
         return new DOMSignatureMethod.SHA256withRSA(var0);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384")) {
         return new DOMSignatureMethod.SHA384withRSA(var0);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512")) {
         return new DOMSignatureMethod.SHA512withRSA(var0);
      } else if (var1.equals("http://www.w3.org/2000/09/xmldsig#dsa-sha1")) {
         return new DOMSignatureMethod.SHA1withDSA(var0);
      } else if (var1.equals("http://www.w3.org/2009/xmldsig11#dsa-sha256")) {
         return new DOMSignatureMethod.SHA256withDSA(var0);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1")) {
         return new DOMSignatureMethod.SHA1withECDSA(var0);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256")) {
         return new DOMSignatureMethod.SHA256withECDSA(var0);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384")) {
         return new DOMSignatureMethod.SHA384withECDSA(var0);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512")) {
         return new DOMSignatureMethod.SHA512withECDSA(var0);
      } else if (var1.equals("http://www.w3.org/2000/09/xmldsig#hmac-sha1")) {
         return new DOMHMACSignatureMethod.SHA1(var0);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256")) {
         return new DOMHMACSignatureMethod.SHA256(var0);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384")) {
         return new DOMHMACSignatureMethod.SHA384(var0);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512")) {
         return new DOMHMACSignatureMethod.SHA512(var0);
      } else {
         throw new MarshalException("unsupported SignatureMethod algorithm: " + var1);
      }
   }

   public final AlgorithmParameterSpec getParameterSpec() {
      return this.params;
   }

   boolean verify(Key var1, SignedInfo var2, byte[] var3, XMLValidateContext var4) throws InvalidKeyException, SignatureException, XMLSignatureException {
      if (var1 != null && var2 != null && var3 != null) {
         if (!(var1 instanceof PublicKey)) {
            throw new InvalidKeyException("key must be PublicKey");
         } else {
            checkKeySize(var4, var1);
            if (this.signature == null) {
               try {
                  Provider var5 = (Provider)var4.getProperty("org.jcp.xml.dsig.internal.dom.SignatureProvider");
                  this.signature = var5 == null ? Signature.getInstance(this.getJCAAlgorithm()) : Signature.getInstance(this.getJCAAlgorithm(), var5);
               } catch (NoSuchAlgorithmException var8) {
                  throw new XMLSignatureException(var8);
               }
            }

            this.signature.initVerify((PublicKey)var1);
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "Signature provider:" + this.signature.getProvider());
               log.log(Level.FINE, "verifying with key: " + var1);
            }

            ((DOMSignedInfo)var2).canonicalize(var4, new SignerOutputStream(this.signature));

            try {
               AbstractDOMSignatureMethod.Type var9 = this.getAlgorithmType();
               if (var9 == AbstractDOMSignatureMethod.Type.DSA) {
                  int var6 = ((DSAKey)var1).getParams().getQ().bitLength();
                  return this.signature.verify(JavaUtils.convertDsaXMLDSIGtoASN1(var3, var6 / 8));
               } else {
                  return var9 == AbstractDOMSignatureMethod.Type.ECDSA ? this.signature.verify(SignatureECDSA.convertXMLDSIGtoASN1(var3)) : this.signature.verify(var3);
               }
            } catch (IOException var7) {
               throw new XMLSignatureException(var7);
            }
         }
      } else {
         throw new NullPointerException();
      }
   }

   private static void checkKeySize(XMLCryptoContext var0, Key var1) throws XMLSignatureException {
      if (Utils.secureValidation(var0)) {
         int var2 = KeyUtil.getKeySize(var1);
         if (var2 == -1) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "Size for " + var1.getAlgorithm() + " key cannot be determined");
            }

            return;
         }

         if (Policy.restrictKey(var1.getAlgorithm(), var2)) {
            throw new XMLSignatureException(var1.getAlgorithm() + " keys less than " + Policy.minKeySize(var1.getAlgorithm()) + " bits are forbidden when secure validation is enabled");
         }
      }

   }

   byte[] sign(Key var1, SignedInfo var2, XMLSignContext var3) throws InvalidKeyException, XMLSignatureException {
      if (var1 != null && var2 != null) {
         if (!(var1 instanceof PrivateKey)) {
            throw new InvalidKeyException("key must be PrivateKey");
         } else {
            checkKeySize(var3, var1);
            if (this.signature == null) {
               try {
                  Provider var4 = (Provider)var3.getProperty("org.jcp.xml.dsig.internal.dom.SignatureProvider");
                  this.signature = var4 == null ? Signature.getInstance(this.getJCAAlgorithm()) : Signature.getInstance(this.getJCAAlgorithm(), var4);
               } catch (NoSuchAlgorithmException var8) {
                  throw new XMLSignatureException(var8);
               }
            }

            this.signature.initSign((PrivateKey)var1);
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "Signature provider:" + this.signature.getProvider());
               log.log(Level.FINE, "Signing with key: " + var1);
            }

            ((DOMSignedInfo)var2).canonicalize(var3, new SignerOutputStream(this.signature));

            try {
               AbstractDOMSignatureMethod.Type var9 = this.getAlgorithmType();
               if (var9 == AbstractDOMSignatureMethod.Type.DSA) {
                  int var5 = ((DSAKey)var1).getParams().getQ().bitLength();
                  return JavaUtils.convertDsaASN1toXMLDSIG(this.signature.sign(), var5 / 8);
               } else {
                  return var9 == AbstractDOMSignatureMethod.Type.ECDSA ? SignatureECDSA.convertASN1toXMLDSIG(this.signature.sign()) : this.signature.sign();
               }
            } catch (SignatureException var6) {
               throw new XMLSignatureException(var6);
            } catch (IOException var7) {
               throw new XMLSignatureException(var7);
            }
         }
      } else {
         throw new NullPointerException();
      }
   }

   static final class SHA512withECDSA extends DOMSignatureMethod {
      SHA512withECDSA(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         super(var1);
      }

      SHA512withECDSA(Element var1) throws MarshalException {
         super(var1);
      }

      public String getAlgorithm() {
         return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
      }

      String getJCAAlgorithm() {
         return "SHA512withECDSA";
      }

      AbstractDOMSignatureMethod.Type getAlgorithmType() {
         return AbstractDOMSignatureMethod.Type.ECDSA;
      }
   }

   static final class SHA384withECDSA extends DOMSignatureMethod {
      SHA384withECDSA(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         super(var1);
      }

      SHA384withECDSA(Element var1) throws MarshalException {
         super(var1);
      }

      public String getAlgorithm() {
         return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
      }

      String getJCAAlgorithm() {
         return "SHA384withECDSA";
      }

      AbstractDOMSignatureMethod.Type getAlgorithmType() {
         return AbstractDOMSignatureMethod.Type.ECDSA;
      }
   }

   static final class SHA256withECDSA extends DOMSignatureMethod {
      SHA256withECDSA(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         super(var1);
      }

      SHA256withECDSA(Element var1) throws MarshalException {
         super(var1);
      }

      public String getAlgorithm() {
         return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
      }

      String getJCAAlgorithm() {
         return "SHA256withECDSA";
      }

      AbstractDOMSignatureMethod.Type getAlgorithmType() {
         return AbstractDOMSignatureMethod.Type.ECDSA;
      }
   }

   static final class SHA1withECDSA extends DOMSignatureMethod {
      SHA1withECDSA(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         super(var1);
      }

      SHA1withECDSA(Element var1) throws MarshalException {
         super(var1);
      }

      public String getAlgorithm() {
         return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
      }

      String getJCAAlgorithm() {
         return "SHA1withECDSA";
      }

      AbstractDOMSignatureMethod.Type getAlgorithmType() {
         return AbstractDOMSignatureMethod.Type.ECDSA;
      }
   }

   static final class SHA256withDSA extends DOMSignatureMethod {
      SHA256withDSA(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         super(var1);
      }

      SHA256withDSA(Element var1) throws MarshalException {
         super(var1);
      }

      public String getAlgorithm() {
         return "http://www.w3.org/2009/xmldsig11#dsa-sha256";
      }

      String getJCAAlgorithm() {
         return "SHA256withDSA";
      }

      AbstractDOMSignatureMethod.Type getAlgorithmType() {
         return AbstractDOMSignatureMethod.Type.DSA;
      }
   }

   static final class SHA1withDSA extends DOMSignatureMethod {
      SHA1withDSA(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         super(var1);
      }

      SHA1withDSA(Element var1) throws MarshalException {
         super(var1);
      }

      public String getAlgorithm() {
         return "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
      }

      String getJCAAlgorithm() {
         return "SHA1withDSA";
      }

      AbstractDOMSignatureMethod.Type getAlgorithmType() {
         return AbstractDOMSignatureMethod.Type.DSA;
      }
   }

   static final class SHA512withRSA extends DOMSignatureMethod {
      SHA512withRSA(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         super(var1);
      }

      SHA512withRSA(Element var1) throws MarshalException {
         super(var1);
      }

      public String getAlgorithm() {
         return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
      }

      String getJCAAlgorithm() {
         return "SHA512withRSA";
      }

      AbstractDOMSignatureMethod.Type getAlgorithmType() {
         return AbstractDOMSignatureMethod.Type.RSA;
      }
   }

   static final class SHA384withRSA extends DOMSignatureMethod {
      SHA384withRSA(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         super(var1);
      }

      SHA384withRSA(Element var1) throws MarshalException {
         super(var1);
      }

      public String getAlgorithm() {
         return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
      }

      String getJCAAlgorithm() {
         return "SHA384withRSA";
      }

      AbstractDOMSignatureMethod.Type getAlgorithmType() {
         return AbstractDOMSignatureMethod.Type.RSA;
      }
   }

   static final class SHA256withRSA extends DOMSignatureMethod {
      SHA256withRSA(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         super(var1);
      }

      SHA256withRSA(Element var1) throws MarshalException {
         super(var1);
      }

      public String getAlgorithm() {
         return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
      }

      String getJCAAlgorithm() {
         return "SHA256withRSA";
      }

      AbstractDOMSignatureMethod.Type getAlgorithmType() {
         return AbstractDOMSignatureMethod.Type.RSA;
      }
   }

   static final class SHA1withRSA extends DOMSignatureMethod {
      SHA1withRSA(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         super(var1);
      }

      SHA1withRSA(Element var1) throws MarshalException {
         super(var1);
      }

      public String getAlgorithm() {
         return "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
      }

      String getJCAAlgorithm() {
         return "SHA1withRSA";
      }

      AbstractDOMSignatureMethod.Type getAlgorithmType() {
         return AbstractDOMSignatureMethod.Type.RSA;
      }
   }
}
