package com.sun.org.apache.xml.internal.security.keys.content.x509;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLX509CRL extends SignatureElementProxy implements XMLX509DataContent {
   public XMLX509CRL(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public XMLX509CRL(Document var1, byte[] var2) {
      super(var1);
      this.addBase64Text(var2);
   }

   public byte[] getCRLBytes() throws XMLSecurityException {
      return this.getBytesFromTextChild();
   }

   public String getBaseLocalName() {
      return "X509CRL";
   }
}
