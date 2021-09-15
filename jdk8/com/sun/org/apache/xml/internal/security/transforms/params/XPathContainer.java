package com.sun.org.apache.xml.internal.security.transforms.params;

import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XPathContainer extends SignatureElementProxy implements TransformParam {
   public XPathContainer(Document var1) {
      super(var1);
   }

   public void setXPath(String var1) {
      if (this.constructionElement.getChildNodes() != null) {
         NodeList var2 = this.constructionElement.getChildNodes();

         for(int var3 = 0; var3 < var2.getLength(); ++var3) {
            this.constructionElement.removeChild(var2.item(var3));
         }
      }

      Text var4 = this.doc.createTextNode(var1);
      this.constructionElement.appendChild(var4);
   }

   public String getXPath() {
      return this.getTextFromTextChild();
   }

   public String getBaseLocalName() {
      return "XPath";
   }
}
