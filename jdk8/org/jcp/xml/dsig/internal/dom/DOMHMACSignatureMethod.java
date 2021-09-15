package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.spec.HMACParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.jcp.xml.dsig.internal.MacOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class DOMHMACSignatureMethod extends AbstractDOMSignatureMethod {
   private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
   static final String HMAC_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
   static final String HMAC_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
   static final String HMAC_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
   private Mac hmac;
   private int outputLength;
   private boolean outputLengthSet;
   private SignatureMethodParameterSpec params;

   DOMHMACSignatureMethod(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
      this.checkParams((SignatureMethodParameterSpec)var1);
      this.params = (SignatureMethodParameterSpec)var1;
   }

   DOMHMACSignatureMethod(Element var1) throws MarshalException {
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

   void checkParams(SignatureMethodParameterSpec var1) throws InvalidAlgorithmParameterException {
      if (var1 != null) {
         if (!(var1 instanceof HMACParameterSpec)) {
            throw new InvalidAlgorithmParameterException("params must be of type HMACParameterSpec");
         }

         this.outputLength = ((HMACParameterSpec)var1).getOutputLength();
         this.outputLengthSet = true;
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Setting outputLength from HMACParameterSpec to: " + this.outputLength);
         }
      }

   }

   public final AlgorithmParameterSpec getParameterSpec() {
      return this.params;
   }

   SignatureMethodParameterSpec unmarshalParams(Element var1) throws MarshalException {
      this.outputLength = Integer.valueOf(var1.getFirstChild().getNodeValue());
      this.outputLengthSet = true;
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "unmarshalled outputLength: " + this.outputLength);
      }

      return new HMACParameterSpec(this.outputLength);
   }

   void marshalParams(Element var1, String var2) throws MarshalException {
      Document var3 = DOMUtils.getOwnerDocument(var1);
      Element var4 = DOMUtils.createElement(var3, "HMACOutputLength", "http://www.w3.org/2000/09/xmldsig#", var2);
      var4.appendChild(var3.createTextNode(String.valueOf(this.outputLength)));
      var1.appendChild(var4);
   }

   boolean verify(Key var1, SignedInfo var2, byte[] var3, XMLValidateContext var4) throws InvalidKeyException, SignatureException, XMLSignatureException {
      if (var1 != null && var2 != null && var3 != null) {
         if (!(var1 instanceof SecretKey)) {
            throw new InvalidKeyException("key must be SecretKey");
         } else {
            if (this.hmac == null) {
               try {
                  this.hmac = Mac.getInstance(this.getJCAAlgorithm());
               } catch (NoSuchAlgorithmException var6) {
                  throw new XMLSignatureException(var6);
               }
            }

            if (this.outputLengthSet && this.outputLength < this.getDigestLength()) {
               throw new XMLSignatureException("HMACOutputLength must not be less than " + this.getDigestLength());
            } else {
               this.hmac.init((SecretKey)var1);
               ((DOMSignedInfo)var2).canonicalize(var4, new MacOutputStream(this.hmac));
               byte[] var5 = this.hmac.doFinal();
               return MessageDigest.isEqual(var3, var5);
            }
         }
      } else {
         throw new NullPointerException();
      }
   }

   byte[] sign(Key var1, SignedInfo var2, XMLSignContext var3) throws InvalidKeyException, XMLSignatureException {
      if (var1 != null && var2 != null) {
         if (!(var1 instanceof SecretKey)) {
            throw new InvalidKeyException("key must be SecretKey");
         } else {
            if (this.hmac == null) {
               try {
                  this.hmac = Mac.getInstance(this.getJCAAlgorithm());
               } catch (NoSuchAlgorithmException var5) {
                  throw new XMLSignatureException(var5);
               }
            }

            if (this.outputLengthSet && this.outputLength < this.getDigestLength()) {
               throw new XMLSignatureException("HMACOutputLength must not be less than " + this.getDigestLength());
            } else {
               this.hmac.init((SecretKey)var1);
               ((DOMSignedInfo)var2).canonicalize(var3, new MacOutputStream(this.hmac));
               return this.hmac.doFinal();
            }
         }
      } else {
         throw new NullPointerException();
      }
   }

   boolean paramsEqual(AlgorithmParameterSpec var1) {
      if (this.getParameterSpec() == var1) {
         return true;
      } else if (!(var1 instanceof HMACParameterSpec)) {
         return false;
      } else {
         HMACParameterSpec var2 = (HMACParameterSpec)var1;
         return this.outputLength == var2.getOutputLength();
      }
   }

   AbstractDOMSignatureMethod.Type getAlgorithmType() {
      return AbstractDOMSignatureMethod.Type.HMAC;
   }

   abstract int getDigestLength();

   static final class SHA512 extends DOMHMACSignatureMethod {
      SHA512(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         super(var1);
      }

      SHA512(Element var1) throws MarshalException {
         super(var1);
      }

      public String getAlgorithm() {
         return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
      }

      String getJCAAlgorithm() {
         return "HmacSHA512";
      }

      int getDigestLength() {
         return 512;
      }
   }

   static final class SHA384 extends DOMHMACSignatureMethod {
      SHA384(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         super(var1);
      }

      SHA384(Element var1) throws MarshalException {
         super(var1);
      }

      public String getAlgorithm() {
         return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
      }

      String getJCAAlgorithm() {
         return "HmacSHA384";
      }

      int getDigestLength() {
         return 384;
      }
   }

   static final class SHA256 extends DOMHMACSignatureMethod {
      SHA256(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         super(var1);
      }

      SHA256(Element var1) throws MarshalException {
         super(var1);
      }

      public String getAlgorithm() {
         return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
      }

      String getJCAAlgorithm() {
         return "HmacSHA256";
      }

      int getDigestLength() {
         return 256;
      }
   }

   static final class SHA1 extends DOMHMACSignatureMethod {
      SHA1(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         super(var1);
      }

      SHA1(Element var1) throws MarshalException {
         super(var1);
      }

      public String getAlgorithm() {
         return "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
      }

      String getJCAAlgorithm() {
         return "HmacSHA1";
      }

      int getDigestLength() {
         return 160;
      }
   }
}
