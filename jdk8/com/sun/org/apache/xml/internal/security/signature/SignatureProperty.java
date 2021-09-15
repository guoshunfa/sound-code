package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SignatureProperty extends SignatureElementProxy {
   public SignatureProperty(Document var1, String var2) {
      this(var1, var2, (String)null);
   }

   public SignatureProperty(Document var1, String var2, String var3) {
      super(var1);
      this.setTarget(var2);
      this.setId(var3);
   }

   public SignatureProperty(Element var1, String var2) throws XMLSecurityException {
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

   public void setTarget(String var1) {
      if (var1 != null) {
         this.constructionElement.setAttributeNS((String)null, "Target", var1);
      }

   }

   public String getTarget() {
      return this.constructionElement.getAttributeNS((String)null, "Target");
   }

   public Node appendChild(Node var1) {
      return this.constructionElement.appendChild(var1);
   }

   public String getBaseLocalName() {
      return "SignatureProperty";
   }
}
