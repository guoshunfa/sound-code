package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilterParameterSpec;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public final class DOMXPathTransform extends ApacheTransform {
   public void init(TransformParameterSpec var1) throws InvalidAlgorithmParameterException {
      if (var1 == null) {
         throw new InvalidAlgorithmParameterException("params are required");
      } else if (!(var1 instanceof XPathFilterParameterSpec)) {
         throw new InvalidAlgorithmParameterException("params must be of type XPathFilterParameterSpec");
      } else {
         this.params = var1;
      }
   }

   public void init(XMLStructure var1, XMLCryptoContext var2) throws InvalidAlgorithmParameterException {
      super.init(var1, var2);
      this.unmarshalParams(DOMUtils.getFirstChildElement(this.transformElem));
   }

   private void unmarshalParams(Element var1) {
      String var2 = var1.getFirstChild().getNodeValue();
      NamedNodeMap var3 = var1.getAttributes();
      if (var3 != null) {
         int var4 = var3.getLength();
         HashMap var5 = new HashMap(var4);

         for(int var6 = 0; var6 < var4; ++var6) {
            Attr var7 = (Attr)var3.item(var6);
            String var8 = var7.getPrefix();
            if (var8 != null && var8.equals("xmlns")) {
               var5.put(var7.getLocalName(), var7.getValue());
            }
         }

         this.params = new XPathFilterParameterSpec(var2, var5);
      } else {
         this.params = new XPathFilterParameterSpec(var2);
      }

   }

   public void marshalParams(XMLStructure var1, XMLCryptoContext var2) throws MarshalException {
      super.marshalParams(var1, var2);
      XPathFilterParameterSpec var3 = (XPathFilterParameterSpec)this.getParameterSpec();
      Element var4 = DOMUtils.createElement(this.ownerDoc, "XPath", "http://www.w3.org/2000/09/xmldsig#", DOMUtils.getSignaturePrefix(var2));
      var4.appendChild(this.ownerDoc.createTextNode(var3.getXPath()));
      Set var5 = var3.getNamespaceMap().entrySet();
      Iterator var6 = var5.iterator();

      while(var6.hasNext()) {
         Map.Entry var7 = (Map.Entry)var6.next();
         var4.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + (String)var7.getKey(), (String)var7.getValue());
      }

      this.transformElem.appendChild(var4);
   }
}
