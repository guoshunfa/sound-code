package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMXSLTTransform extends ApacheTransform {
   public void init(TransformParameterSpec var1) throws InvalidAlgorithmParameterException {
      if (var1 == null) {
         throw new InvalidAlgorithmParameterException("params are required");
      } else if (!(var1 instanceof XSLTTransformParameterSpec)) {
         throw new InvalidAlgorithmParameterException("unrecognized params");
      } else {
         this.params = var1;
      }
   }

   public void init(XMLStructure var1, XMLCryptoContext var2) throws InvalidAlgorithmParameterException {
      super.init(var1, var2);
      this.unmarshalParams(DOMUtils.getFirstChildElement(this.transformElem));
   }

   private void unmarshalParams(Element var1) {
      this.params = new XSLTTransformParameterSpec(new javax.xml.crypto.dom.DOMStructure(var1));
   }

   public void marshalParams(XMLStructure var1, XMLCryptoContext var2) throws MarshalException {
      super.marshalParams(var1, var2);
      XSLTTransformParameterSpec var3 = (XSLTTransformParameterSpec)this.getParameterSpec();
      Node var4 = ((javax.xml.crypto.dom.DOMStructure)var3.getStylesheet()).getNode();
      DOMUtils.appendChild(this.transformElem, var4);
   }
}
