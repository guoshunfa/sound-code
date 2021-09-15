package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

abstract class AbstractDOMSignatureMethod extends DOMStructure implements SignatureMethod {
   abstract boolean verify(Key var1, SignedInfo var2, byte[] var3, XMLValidateContext var4) throws InvalidKeyException, SignatureException, XMLSignatureException;

   abstract byte[] sign(Key var1, SignedInfo var2, XMLSignContext var3) throws InvalidKeyException, XMLSignatureException;

   abstract String getJCAAlgorithm();

   abstract AbstractDOMSignatureMethod.Type getAlgorithmType();

   public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
      Document var4 = DOMUtils.getOwnerDocument(var1);
      Element var5 = DOMUtils.createElement(var4, "SignatureMethod", "http://www.w3.org/2000/09/xmldsig#", var2);
      DOMUtils.setAttribute(var5, "Algorithm", this.getAlgorithm());
      if (this.getParameterSpec() != null) {
         this.marshalParams(var5, var2);
      }

      var1.appendChild(var5);
   }

   void marshalParams(Element var1, String var2) throws MarshalException {
      throw new MarshalException("no parameters should be specified for the " + this.getAlgorithm() + " SignatureMethod algorithm");
   }

   SignatureMethodParameterSpec unmarshalParams(Element var1) throws MarshalException {
      throw new MarshalException("no parameters should be specified for the " + this.getAlgorithm() + " SignatureMethod algorithm");
   }

   void checkParams(SignatureMethodParameterSpec var1) throws InvalidAlgorithmParameterException {
      if (var1 != null) {
         throw new InvalidAlgorithmParameterException("no parameters should be specified for the " + this.getAlgorithm() + " SignatureMethod algorithm");
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SignatureMethod)) {
         return false;
      } else {
         SignatureMethod var2 = (SignatureMethod)var1;
         return this.getAlgorithm().equals(var2.getAlgorithm()) && this.paramsEqual(var2.getParameterSpec());
      }
   }

   public int hashCode() {
      byte var1 = 17;
      int var3 = 31 * var1 + this.getAlgorithm().hashCode();
      AlgorithmParameterSpec var2 = this.getParameterSpec();
      if (var2 != null) {
         var3 = 31 * var3 + var2.hashCode();
      }

      return var3;
   }

   boolean paramsEqual(AlgorithmParameterSpec var1) {
      return this.getParameterSpec() == var1;
   }

   static enum Type {
      DSA,
      RSA,
      ECDSA,
      HMAC;
   }
}
