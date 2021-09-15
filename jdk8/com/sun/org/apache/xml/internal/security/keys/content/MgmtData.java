package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MgmtData extends SignatureElementProxy implements KeyInfoContent {
   public MgmtData(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public MgmtData(Document var1, String var2) {
      super(var1);
      this.addText(var2);
   }

   public String getMgmtData() {
      return this.getTextFromTextChild();
   }

   public String getBaseLocalName() {
      return "MgmtData";
   }
}
