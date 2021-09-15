package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SignatureProperties extends SignatureElementProxy {
   public SignatureProperties(Document var1) {
      super(var1);
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public SignatureProperties(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
      Attr var3 = var1.getAttributeNodeNS((String)null, "Id");
      if (var3 != null) {
         var1.setIdAttributeNode(var3, true);
      }

      int var4 = this.getLength();

      for(int var5 = 0; var5 < var4; ++var5) {
         Element var6 = XMLUtils.selectDsNode(this.constructionElement, "SignatureProperty", var5);
         Attr var7 = var6.getAttributeNodeNS((String)null, "Id");
         if (var7 != null) {
            var6.setIdAttributeNode(var7, true);
         }
      }

   }

   public int getLength() {
      Element[] var1 = XMLUtils.selectDsNodes(this.constructionElement, "SignatureProperty");
      return var1.length;
   }

   public SignatureProperty item(int var1) throws XMLSignatureException {
      try {
         Element var2 = XMLUtils.selectDsNode(this.constructionElement, "SignatureProperty", var1);
         return var2 == null ? null : new SignatureProperty(var2, this.baseURI);
      } catch (XMLSecurityException var3) {
         throw new XMLSignatureException("empty", var3);
      }
   }

   public void setId(String var1) {
      if (var1 != null) {
         this.constructionElement.setAttributeNS((String)null, "Id", var1);
         this.constructionElement.setIdAttributeNS((String)null, "Id", true);
      }

   }

   public String getId() {
      return this.constructionElement.getAttributeNS((String)null, "Id");
   }

   public void addSignatureProperty(SignatureProperty var1) {
      this.constructionElement.appendChild(var1.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public String getBaseLocalName() {
      return "SignatureProperties";
   }
}
