package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public abstract class BodyElementImpl extends ElementImpl implements SOAPBodyElement {
   public BodyElementImpl(SOAPDocumentImpl ownerDoc, Name qname) {
      super(ownerDoc, qname);
   }

   public BodyElementImpl(SOAPDocumentImpl ownerDoc, QName qname) {
      super(ownerDoc, qname);
   }

   public void setParentElement(SOAPElement element) throws SOAPException {
      if (!(element instanceof SOAPBody)) {
         log.severe("SAAJ0101.impl.parent.of.body.elem.mustbe.body");
         throw new SOAPException("Parent of a SOAPBodyElement has to be a SOAPBody");
      } else {
         super.setParentElement(element);
      }
   }
}
