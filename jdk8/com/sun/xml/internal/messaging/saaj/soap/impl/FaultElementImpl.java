package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFaultElement;

public abstract class FaultElementImpl extends ElementImpl implements SOAPFaultElement {
   protected FaultElementImpl(SOAPDocumentImpl ownerDoc, NameImpl qname) {
      super(ownerDoc, (Name)qname);
   }

   protected FaultElementImpl(SOAPDocumentImpl ownerDoc, QName qname) {
      super(ownerDoc, qname);
   }

   protected abstract boolean isStandardFaultElement();

   public SOAPElement setElementQName(QName newName) throws SOAPException {
      log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[]{this.elementQName.getLocalPart(), newName.getLocalPart()});
      throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + newName.getLocalPart());
   }
}
