package org.jcp.xml.dsig.internal.dom;

import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import org.w3c.dom.Element;

public class DOMCanonicalizationMethod extends DOMTransform implements CanonicalizationMethod {
   public DOMCanonicalizationMethod(TransformService var1) throws InvalidAlgorithmParameterException {
      super(var1);
      if (!(var1 instanceof ApacheCanonicalizer) && !isC14Nalg(var1.getAlgorithm())) {
         throw new InvalidAlgorithmParameterException("Illegal CanonicalizationMethod");
      }
   }

   public DOMCanonicalizationMethod(Element var1, XMLCryptoContext var2, Provider var3) throws MarshalException {
      super(var1, var2, var3);
      if (!(this.spi instanceof ApacheCanonicalizer) && !isC14Nalg(this.spi.getAlgorithm())) {
         throw new MarshalException("Illegal CanonicalizationMethod");
      }
   }

   public Data canonicalize(Data var1, XMLCryptoContext var2) throws TransformException {
      return this.transform(var1, var2);
   }

   public Data canonicalize(Data var1, XMLCryptoContext var2, OutputStream var3) throws TransformException {
      return this.transform(var1, var2, var3);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CanonicalizationMethod)) {
         return false;
      } else {
         CanonicalizationMethod var2 = (CanonicalizationMethod)var1;
         return this.getAlgorithm().equals(var2.getAlgorithm()) && DOMUtils.paramsEqual(this.getParameterSpec(), var2.getParameterSpec());
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

   private static boolean isC14Nalg(String var0) {
      return var0.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315") || var0.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments") || var0.equals("http://www.w3.org/2001/10/xml-exc-c14n#") || var0.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments") || var0.equals("http://www.w3.org/2006/12/xml-c14n11") || var0.equals("http://www.w3.org/2006/12/xml-c14n11#WithComments");
   }
}
