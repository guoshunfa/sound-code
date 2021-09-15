package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class SignatureElementProxy extends ElementProxy {
   protected SignatureElementProxy() {
   }

   public SignatureElementProxy(Document var1) {
      if (var1 == null) {
         throw new RuntimeException("Document is null");
      } else {
         this.doc = var1;
         this.constructionElement = XMLUtils.createElementInSignatureSpace(this.doc, this.getBaseLocalName());
      }
   }

   public SignatureElementProxy(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public String getBaseNamespace() {
      return "http://www.w3.org/2000/09/xmldsig#";
   }
}
