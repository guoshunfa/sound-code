package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.BodyImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.Locale;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFault;

public class Body1_1Impl extends BodyImpl {
   public Body1_1Impl(SOAPDocumentImpl ownerDocument, String prefix) {
      super(ownerDocument, NameImpl.createBody1_1Name(prefix));
   }

   public SOAPFault addSOAP12Fault(QName faultCode, String faultReason, Locale locale) {
      throw new UnsupportedOperationException("Not supported in SOAP 1.1");
   }

   protected NameImpl getFaultName(String name) {
      return NameImpl.createFault1_1Name((String)null);
   }

   protected SOAPBodyElement createBodyElement(Name name) {
      return new BodyElement1_1Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), name);
   }

   protected SOAPBodyElement createBodyElement(QName name) {
      return new BodyElement1_1Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), name);
   }

   protected QName getDefaultFaultCode() {
      return new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server");
   }

   protected boolean isFault(SOAPElement child) {
      return child.getElementName().equals(this.getFaultName((String)null));
   }

   protected SOAPFault createFaultElement() {
      return new Fault1_1Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), this.getPrefix());
   }
}
