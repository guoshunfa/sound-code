package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.DetailEntryImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class DetailEntry1_1Impl extends DetailEntryImpl {
   public DetailEntry1_1Impl(SOAPDocumentImpl ownerDoc, Name qname) {
      super(ownerDoc, qname);
   }

   public DetailEntry1_1Impl(SOAPDocumentImpl ownerDoc, QName qname) {
      super(ownerDoc, qname);
   }

   public SOAPElement setElementQName(QName newName) throws SOAPException {
      DetailEntryImpl copy = new DetailEntry1_1Impl((SOAPDocumentImpl)this.getOwnerDocument(), newName);
      return replaceElementWithSOAPElement(this, copy);
   }
}
