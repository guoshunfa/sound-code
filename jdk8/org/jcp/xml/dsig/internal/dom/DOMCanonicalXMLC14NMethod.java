package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.Data;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

public final class DOMCanonicalXMLC14NMethod extends ApacheCanonicalizer {
   public void init(TransformParameterSpec var1) throws InvalidAlgorithmParameterException {
      if (var1 != null) {
         throw new InvalidAlgorithmParameterException("no parameters should be specified for Canonical XML C14N algorithm");
      }
   }

   public Data transform(Data var1, XMLCryptoContext var2) throws TransformException {
      if (var1 instanceof DOMSubTreeData) {
         DOMSubTreeData var3 = (DOMSubTreeData)var1;
         if (var3.excludeComments()) {
            try {
               this.apacheCanonicalizer = Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
            } catch (InvalidCanonicalizerException var5) {
               throw new TransformException("Couldn't find Canonicalizer for: http://www.w3.org/TR/2001/REC-xml-c14n-20010315: " + var5.getMessage(), var5);
            }
         }
      }

      return this.canonicalize(var1, var2);
   }
}
