package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ObjectContainer extends SignatureElementProxy {
   public ObjectContainer(Document var1) {
      super(var1);
   }

   public ObjectContainer(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
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

   public void setMimeType(String var1) {
      if (var1 != null) {
         this.constructionElement.setAttributeNS((String)null, "MimeType", var1);
      }

   }

   public String getMimeType() {
      return this.constructionElement.getAttributeNS((String)null, "MimeType");
   }

   public void setEncoding(String var1) {
      if (var1 != null) {
         this.constructionElement.setAttributeNS((String)null, "Encoding", var1);
      }

   }

   public String getEncoding() {
      return this.constructionElement.getAttributeNS((String)null, "Encoding");
   }

   public Node appendChild(Node var1) {
      return this.constructionElement.appendChild(var1);
   }

   public String getBaseLocalName() {
      return "Object";
   }
}
