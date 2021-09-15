package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class KeyName extends SignatureElementProxy implements KeyInfoContent {
   public KeyName(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public KeyName(Document var1, String var2) {
      super(var1);
      this.addText(var2);
   }

   public String getKeyName() {
      return this.getTextFromTextChild();
   }

   public String getBaseLocalName() {
      return "KeyName";
   }
}
