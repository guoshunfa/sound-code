package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.DSAKeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.RSAKeyValue;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class KeyValue extends SignatureElementProxy implements KeyInfoContent {
   public KeyValue(Document var1, DSAKeyValue var2) {
      super(var1);
      XMLUtils.addReturnToElement(this.constructionElement);
      this.constructionElement.appendChild(var2.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public KeyValue(Document var1, RSAKeyValue var2) {
      super(var1);
      XMLUtils.addReturnToElement(this.constructionElement);
      this.constructionElement.appendChild(var2.getElement());
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public KeyValue(Document var1, Element var2) {
      super(var1);
      XMLUtils.addReturnToElement(this.constructionElement);
      this.constructionElement.appendChild(var2);
      XMLUtils.addReturnToElement(this.constructionElement);
   }

   public KeyValue(Document var1, PublicKey var2) {
      super(var1);
      XMLUtils.addReturnToElement(this.constructionElement);
      if (var2 instanceof DSAPublicKey) {
         DSAKeyValue var3 = new DSAKeyValue(this.doc, var2);
         this.constructionElement.appendChild(var3.getElement());
         XMLUtils.addReturnToElement(this.constructionElement);
      } else if (var2 instanceof RSAPublicKey) {
         RSAKeyValue var4 = new RSAKeyValue(this.doc, var2);
         this.constructionElement.appendChild(var4.getElement());
         XMLUtils.addReturnToElement(this.constructionElement);
      }

   }

   public KeyValue(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public PublicKey getPublicKey() throws XMLSecurityException {
      Element var1 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "RSAKeyValue", 0);
      if (var1 != null) {
         RSAKeyValue var4 = new RSAKeyValue(var1, this.baseURI);
         return var4.getPublicKey();
      } else {
         Element var2 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "DSAKeyValue", 0);
         if (var2 != null) {
            DSAKeyValue var3 = new DSAKeyValue(var2, this.baseURI);
            return var3.getPublicKey();
         } else {
            return null;
         }
      }
   }

   public String getBaseLocalName() {
      return "KeyValue";
   }
}
