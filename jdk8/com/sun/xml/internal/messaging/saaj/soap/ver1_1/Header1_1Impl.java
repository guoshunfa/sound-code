package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.HeaderImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;

public class Header1_1Impl extends HeaderImpl {
   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_1", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.LocalStrings");

   public Header1_1Impl(SOAPDocumentImpl ownerDocument, String prefix) {
      super(ownerDocument, NameImpl.createHeader1_1Name(prefix));
   }

   protected NameImpl getNotUnderstoodName() {
      log.log(Level.SEVERE, (String)"SAAJ0301.ver1_1.hdr.op.unsupported.in.SOAP1.1", (Object[])(new String[]{"getNotUnderstoodName"}));
      throw new UnsupportedOperationException("Not supported by SOAP 1.1");
   }

   protected NameImpl getUpgradeName() {
      return NameImpl.create("Upgrade", this.getPrefix(), "http://schemas.xmlsoap.org/soap/envelope/");
   }

   protected NameImpl getSupportedEnvelopeName() {
      return NameImpl.create("SupportedEnvelope", this.getPrefix(), "http://schemas.xmlsoap.org/soap/envelope/");
   }

   public SOAPHeaderElement addNotUnderstoodHeaderElement(QName name) throws SOAPException {
      log.log(Level.SEVERE, (String)"SAAJ0301.ver1_1.hdr.op.unsupported.in.SOAP1.1", (Object[])(new String[]{"addNotUnderstoodHeaderElement"}));
      throw new UnsupportedOperationException("Not supported by SOAP 1.1");
   }

   protected SOAPHeaderElement createHeaderElement(Name name) {
      return new HeaderElement1_1Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), name);
   }

   protected SOAPHeaderElement createHeaderElement(QName name) {
      return new HeaderElement1_1Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), name);
   }
}
