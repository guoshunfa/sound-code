package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Element;

public class SPKIData extends SignatureElementProxy implements KeyInfoContent {
   public SPKIData(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public String getBaseLocalName() {
      return "SPKIData";
   }
}
