package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.w3c.dom.Element;

public final class DOMExcC14NMethod extends ApacheCanonicalizer {
   public void init(TransformParameterSpec var1) throws InvalidAlgorithmParameterException {
      if (var1 != null) {
         if (!(var1 instanceof ExcC14NParameterSpec)) {
            throw new InvalidAlgorithmParameterException("params must be of type ExcC14NParameterSpec");
         }

         this.params = (C14NMethodParameterSpec)var1;
      }

   }

   public void init(XMLStructure var1, XMLCryptoContext var2) throws InvalidAlgorithmParameterException {
      super.init(var1, var2);
      Element var3 = DOMUtils.getFirstChildElement(this.transformElem);
      if (var3 == null) {
         this.params = null;
         this.inclusiveNamespaces = null;
      } else {
         this.unmarshalParams(var3);
      }
   }

   private void unmarshalParams(Element var1) {
      String var2 = var1.getAttributeNS((String)null, "PrefixList");
      this.inclusiveNamespaces = var2;
      int var3 = 0;
      int var4 = var2.indexOf(32);

      ArrayList var5;
      for(var5 = new ArrayList(); var4 != -1; var4 = var2.indexOf(32, var3)) {
         var5.add(var2.substring(var3, var4));
         var3 = var4 + 1;
      }

      if (var3 <= var2.length()) {
         var5.add(var2.substring(var3));
      }

      this.params = new ExcC14NParameterSpec(var5);
   }

   public void marshalParams(XMLStructure var1, XMLCryptoContext var2) throws MarshalException {
      super.marshalParams(var1, var2);
      AlgorithmParameterSpec var3 = this.getParameterSpec();
      if (var3 != null) {
         String var4 = DOMUtils.getNSPrefix(var2, "http://www.w3.org/2001/10/xml-exc-c14n#");
         Element var5 = DOMUtils.createElement(this.ownerDoc, "InclusiveNamespaces", "http://www.w3.org/2001/10/xml-exc-c14n#", var4);
         if (var4 != null && var4.length() != 0) {
            var5.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + var4, "http://www.w3.org/2001/10/xml-exc-c14n#");
         } else {
            var5.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2001/10/xml-exc-c14n#");
         }

         ExcC14NParameterSpec var6 = (ExcC14NParameterSpec)var3;
         StringBuffer var7 = new StringBuffer("");
         List var8 = var6.getPrefixList();
         int var9 = 0;

         for(int var10 = var8.size(); var9 < var10; ++var9) {
            var7.append((String)var8.get(var9));
            if (var9 < var10 - 1) {
               var7.append(" ");
            }
         }

         DOMUtils.setAttribute(var5, "PrefixList", var7.toString());
         this.inclusiveNamespaces = var7.toString();
         this.transformElem.appendChild(var5);
      }
   }

   public String getParamsNSURI() {
      return "http://www.w3.org/2001/10/xml-exc-c14n#";
   }

   public Data transform(Data var1, XMLCryptoContext var2) throws TransformException {
      if (var1 instanceof DOMSubTreeData) {
         DOMSubTreeData var3 = (DOMSubTreeData)var1;
         if (var3.excludeComments()) {
            try {
               this.apacheCanonicalizer = Canonicalizer.getInstance("http://www.w3.org/2001/10/xml-exc-c14n#");
            } catch (InvalidCanonicalizerException var5) {
               throw new TransformException("Couldn't find Canonicalizer for: http://www.w3.org/2001/10/xml-exc-c14n#: " + var5.getMessage(), var5);
            }
         }
      }

      return this.canonicalize(var1, var2);
   }
}
