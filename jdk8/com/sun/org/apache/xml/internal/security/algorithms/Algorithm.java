package com.sun.org.apache.xml.internal.security.algorithms;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Algorithm extends SignatureElementProxy {
   public Algorithm(Document var1, String var2) {
      super(var1);
      this.setAlgorithmURI(var2);
   }

   public Algorithm(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public String getAlgorithmURI() {
      return this.constructionElement.getAttributeNS((String)null, "Algorithm");
   }

   protected void setAlgorithmURI(String var1) {
      if (var1 != null) {
         this.constructionElement.setAttributeNS((String)null, "Algorithm", var1);
      }

   }
}
