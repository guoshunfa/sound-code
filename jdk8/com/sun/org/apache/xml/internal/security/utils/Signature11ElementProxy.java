package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Signature11ElementProxy extends ElementProxy {
   protected Signature11ElementProxy() {
   }

   public Signature11ElementProxy(Document var1) {
      if (var1 == null) {
         throw new RuntimeException("Document is null");
      } else {
         this.doc = var1;
         this.constructionElement = XMLUtils.createElementInSignature11Space(this.doc, this.getBaseLocalName());
      }
   }

   public Signature11ElementProxy(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public String getBaseNamespace() {
      return "http://www.w3.org/2009/xmldsig11#";
   }
}
