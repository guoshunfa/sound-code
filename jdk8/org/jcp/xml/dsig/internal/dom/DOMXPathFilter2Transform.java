package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilter2ParameterSpec;
import javax.xml.crypto.dsig.spec.XPathType;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public final class DOMXPathFilter2Transform extends ApacheTransform {
   public void init(TransformParameterSpec var1) throws InvalidAlgorithmParameterException {
      if (var1 == null) {
         throw new InvalidAlgorithmParameterException("params are required");
      } else if (!(var1 instanceof XPathFilter2ParameterSpec)) {
         throw new InvalidAlgorithmParameterException("params must be of type XPathFilter2ParameterSpec");
      } else {
         this.params = var1;
      }
   }

   public void init(XMLStructure var1, XMLCryptoContext var2) throws InvalidAlgorithmParameterException {
      super.init(var1, var2);

      try {
         this.unmarshalParams(DOMUtils.getFirstChildElement(this.transformElem));
      } catch (MarshalException var4) {
         throw new InvalidAlgorithmParameterException(var4);
      }
   }

   private void unmarshalParams(Element var1) throws MarshalException {
      ArrayList var2;
      for(var2 = new ArrayList(); var1 != null; var1 = DOMUtils.getNextSiblingElement(var1)) {
         String var3 = var1.getFirstChild().getNodeValue();
         String var4 = DOMUtils.getAttributeValue(var1, "Filter");
         if (var4 == null) {
            throw new MarshalException("filter cannot be null");
         }

         XPathType.Filter var5 = null;
         if (var4.equals("intersect")) {
            var5 = XPathType.Filter.INTERSECT;
         } else if (var4.equals("subtract")) {
            var5 = XPathType.Filter.SUBTRACT;
         } else {
            if (!var4.equals("union")) {
               throw new MarshalException("Unknown XPathType filter type" + var4);
            }

            var5 = XPathType.Filter.UNION;
         }

         NamedNodeMap var6 = var1.getAttributes();
         if (var6 == null) {
            var2.add(new XPathType(var3, var5));
         } else {
            int var7 = var6.getLength();
            HashMap var8 = new HashMap(var7);

            for(int var9 = 0; var9 < var7; ++var9) {
               Attr var10 = (Attr)var6.item(var9);
               String var11 = var10.getPrefix();
               if (var11 != null && var11.equals("xmlns")) {
                  var8.put(var10.getLocalName(), var10.getValue());
               }
            }

            var2.add(new XPathType(var3, var5, var8));
         }
      }

      this.params = new XPathFilter2ParameterSpec(var2);
   }

   public void marshalParams(XMLStructure var1, XMLCryptoContext var2) throws MarshalException {
      super.marshalParams(var1, var2);
      XPathFilter2ParameterSpec var3 = (XPathFilter2ParameterSpec)this.getParameterSpec();
      String var4 = DOMUtils.getNSPrefix(var2, "http://www.w3.org/2002/06/xmldsig-filter2");
      String var5 = var4 != null && var4.length() != 0 ? "xmlns:" + var4 : "xmlns";
      List var6 = var3.getXPathList();
      Iterator var7 = var6.iterator();

      while(var7.hasNext()) {
         XPathType var8 = (XPathType)var7.next();
         Element var9 = DOMUtils.createElement(this.ownerDoc, "XPath", "http://www.w3.org/2002/06/xmldsig-filter2", var4);
         var9.appendChild(this.ownerDoc.createTextNode(var8.getExpression()));
         DOMUtils.setAttribute(var9, "Filter", var8.getFilter().toString());
         var9.setAttributeNS("http://www.w3.org/2000/xmlns/", var5, "http://www.w3.org/2002/06/xmldsig-filter2");
         Set var10 = var8.getNamespaceMap().entrySet();
         Iterator var11 = var10.iterator();

         while(var11.hasNext()) {
            Map.Entry var12 = (Map.Entry)var11.next();
            var9.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + (String)var12.getKey(), (String)var12.getValue());
         }

         this.transformElem.appendChild(var9);
      }

   }
}
