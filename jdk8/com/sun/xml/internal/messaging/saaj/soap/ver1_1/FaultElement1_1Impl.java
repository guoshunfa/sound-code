package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class FaultElement1_1Impl extends FaultElementImpl {
   public FaultElement1_1Impl(SOAPDocumentImpl ownerDoc, NameImpl qname) {
      super(ownerDoc, qname);
   }

   public FaultElement1_1Impl(SOAPDocumentImpl ownerDoc, QName qname) {
      super(ownerDoc, qname);
   }

   public FaultElement1_1Impl(SOAPDocumentImpl ownerDoc, String localName) {
      super(ownerDoc, NameImpl.createFaultElement1_1Name(localName));
   }

   public FaultElement1_1Impl(SOAPDocumentImpl ownerDoc, String localName, String prefix) {
      super(ownerDoc, NameImpl.createFaultElement1_1Name(localName, prefix));
   }

   protected boolean isStandardFaultElement() {
      String localName = this.elementQName.getLocalPart();
      return localName.equalsIgnoreCase("faultcode") || localName.equalsIgnoreCase("faultstring") || localName.equalsIgnoreCase("faultactor");
   }

   public SOAPElement setElementQName(QName newName) throws SOAPException {
      if (!this.isStandardFaultElement()) {
         FaultElement1_1Impl copy = new FaultElement1_1Impl((SOAPDocumentImpl)this.getOwnerDocument(), newName);
         return replaceElementWithSOAPElement(this, copy);
      } else {
         return super.setElementQName(newName);
      }
   }
}
