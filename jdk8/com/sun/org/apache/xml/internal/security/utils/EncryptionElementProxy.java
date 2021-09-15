package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class EncryptionElementProxy extends ElementProxy {
   public EncryptionElementProxy(Document var1) {
      super(var1);
   }

   public EncryptionElementProxy(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public final String getBaseNamespace() {
      return "http://www.w3.org/2001/04/xmlenc#";
   }
}
