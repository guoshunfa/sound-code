package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Element;

public class PGPData extends SignatureElementProxy implements KeyInfoContent {
   public PGPData(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public String getBaseLocalName() {
      return "PGPData";
   }
}
