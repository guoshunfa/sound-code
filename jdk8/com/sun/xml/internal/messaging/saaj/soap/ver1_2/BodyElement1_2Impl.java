package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.BodyElementImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class BodyElement1_2Impl extends BodyElementImpl {
   public BodyElement1_2Impl(SOAPDocumentImpl ownerDoc, Name qname) {
      super(ownerDoc, qname);
   }

   public BodyElement1_2Impl(SOAPDocumentImpl ownerDoc, QName qname) {
      super(ownerDoc, qname);
   }

   public SOAPElement setElementQName(QName newName) throws SOAPException {
      BodyElementImpl copy = new BodyElement1_2Impl((SOAPDocumentImpl)this.getOwnerDocument(), newName);
      return replaceElementWithSOAPElement(this, copy);
   }
}
