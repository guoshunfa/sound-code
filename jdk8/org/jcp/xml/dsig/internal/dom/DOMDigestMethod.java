package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class DOMDigestMethod extends DOMStructure implements DigestMethod {
   static final String SHA384 = "http://www.w3.org/2001/04/xmldsig-more#sha384";
   private DigestMethodParameterSpec params;

   DOMDigestMethod(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
      if (var1 != null && !(var1 instanceof DigestMethodParameterSpec)) {
         throw new InvalidAlgorithmParameterException("params must be of type DigestMethodParameterSpec");
      } else {
         this.checkParams((DigestMethodParameterSpec)var1);
         this.params = (DigestMethodParameterSpec)var1;
      }
   }

   DOMDigestMethod(Element var1) throws MarshalException {
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

   static DigestMethod unmarshal(Element var0) throws MarshalException {
      String var1 = DOMUtils.getAttributeValue(var0, "Algorithm");
      if (var1.equals("http://www.w3.org/2000/09/xmldsig#sha1")) {
         return new DOMDigestMethod.SHA1(var0);
      } else if (var1.equals("http://www.w3.org/2001/04/xmlenc#sha256")) {
         return new DOMDigestMethod.SHA256(var0);
      } else if (var1.equals("http://www.w3.org/2001/04/xmldsig-more#sha384")) {
         return new DOMDigestMethod.SHA384(var0);
      } else if (var1.equals("http://www.w3.org/2001/04/xmlenc#sha512")) {
         return new DOMDigestMethod.SHA512(var0);
      } else {
         throw new MarshalException("unsupported DigestMethod algorithm: " + var1);
      }
   }

   void checkParams(DigestMethodParameterSpec var1) throws InvalidAlgorithmParameterException {
      if (var1 != null) {
         throw new InvalidAlgorithmParameterException("no parameters should be specified for the " + this.getMessageDigestAlgorithm() + " DigestMethod algorithm");
      }
   }

   public final AlgorithmParameterSpec getParameterSpec() {
      return this.params;
   }

   DigestMethodParameterSpec unmarshalParams(Element var1) throws MarshalException {
      throw new MarshalException("no parameters should be specified for the " + this.getMessageDigestAlgorithm() + " DigestMethod algorithm");
   }

   public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
      Document var4 = DOMUtils.getOwnerDocument(var1);
      Element var5 = DOMUtils.createElement(var4, "DigestMethod", "http://www.w3.org/2000/09/xmldsig#", var2);
      DOMUtils.setAttribute(var5, "Algorithm", this.getAlgorithm());
      if (this.params != null) {
         this.marshalParams(var5, var2);
      }

      var1.appendChild(var5);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof DigestMethod)) {
         return false;
      } else {
         DigestMethod var2 = (DigestMethod)var1;
         boolean var3 = this.params == null ? var2.getParameterSpec() == null : this.params.equals(var2.getParameterSpec());
         return this.getAlgorithm().equals(var2.getAlgorithm()) && var3;
      }
   }

   public int hashCode() {
      int var1 = 17;
      if (this.params != null) {
         var1 = 31 * var1 + this.params.hashCode();
      }

      var1 = 31 * var1 + this.getAlgorithm().hashCode();
      return var1;
   }

   void marshalParams(Element var1, String var2) throws MarshalException {
      throw new MarshalException("no parameters should be specified for the " + this.getMessageDigestAlgorithm() + " DigestMethod algorithm");
   }

   abstract String getMessageDigestAlgorithm();

   static final class SHA512 extends DOMDigestMethod {
      SHA512(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         super(var1);
      }

      SHA512(Element var1) throws MarshalException {
         super(var1);
      }

      public String getAlgorithm() {
         return "http://www.w3.org/2001/04/xmlenc#sha512";
      }

      String getMessageDigestAlgorithm() {
         return "SHA-512";
      }
   }

   static final class SHA384 extends DOMDigestMethod {
      SHA384(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         super(var1);
      }

      SHA384(Element var1) throws MarshalException {
         super(var1);
      }

      public String getAlgorithm() {
         return "http://www.w3.org/2001/04/xmldsig-more#sha384";
      }

      String getMessageDigestAlgorithm() {
         return "SHA-384";
      }
   }

   static final class SHA256 extends DOMDigestMethod {
      SHA256(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         super(var1);
      }

      SHA256(Element var1) throws MarshalException {
         super(var1);
      }

      public String getAlgorithm() {
         return "http://www.w3.org/2001/04/xmlenc#sha256";
      }

      String getMessageDigestAlgorithm() {
         return "SHA-256";
      }
   }

   static final class SHA1 extends DOMDigestMethod {
      SHA1(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         super(var1);
      }

      SHA1(Element var1) throws MarshalException {
         super(var1);
      }

      public String getAlgorithm() {
         return "http://www.w3.org/2000/09/xmldsig#sha1";
      }

      String getMessageDigestAlgorithm() {
         return "SHA-1";
      }
   }
}
