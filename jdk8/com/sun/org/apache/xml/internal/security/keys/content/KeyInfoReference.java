package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Signature11ElementProxy;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class KeyInfoReference extends Signature11ElementProxy implements KeyInfoContent {
   public KeyInfoReference(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public KeyInfoReference(Document var1, String var2) {
      super(var1);
      this.constructionElement.setAttributeNS((String)null, "URI", var2);
   }

   public Attr getURIAttr() {
      return this.constructionElement.getAttributeNodeNS((String)null, "URI");
   }

   public String getURI() {
      return this.getURIAttr().getNodeValue();
   }

   public void setId(String var1) {
      if (var1 != null) {
         this.constructionElement.setAttributeNS((String)null, "Id", var1);
         this.constructionElement.setIdAttributeNS((String)null, "Id", true);
      } else {
         this.constructionElement.removeAttributeNS((String)null, "Id");
      }

   }

   public String getId() {
      return this.constructionElement.getAttributeNS((String)null, "Id");
   }

   public String getBaseLocalName() {
      return "KeyInfoReference";
   }
}
