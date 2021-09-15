package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class FaultElement1_2Impl extends FaultElementImpl {
   public FaultElement1_2Impl(SOAPDocumentImpl ownerDoc, NameImpl qname) {
      super(ownerDoc, qname);
   }

   public FaultElement1_2Impl(SOAPDocumentImpl ownerDoc, QName qname) {
      super(ownerDoc, qname);
   }

   public FaultElement1_2Impl(SOAPDocumentImpl ownerDoc, String localName) {
      super(ownerDoc, NameImpl.createSOAP12Name(localName));
   }

   protected boolean isStandardFaultElement() {
      String localName = this.elementQName.getLocalPart();
      return localName.equalsIgnoreCase("code") || localName.equalsIgnoreCase("reason") || localName.equalsIgnoreCase("node") || localName.equalsIgnoreCase("role");
   }

   public SOAPElement setElementQName(QName newName) throws SOAPException {
      if (!this.isStandardFaultElement()) {
         FaultElement1_2Impl copy = new FaultElement1_2Impl((SOAPDocumentImpl)this.getOwnerDocument(), newName);
         return replaceElementWithSOAPElement(this, copy);
      } else {
         return super.setElementQName(newName);
      }
   }

   public void setEncodingStyle(String encodingStyle) throws SOAPException {
      log.severe("SAAJ0408.ver1_2.no.encodingStyle.in.fault.child");
      throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on a Fault child element");
   }

   public SOAPElement addAttribute(Name name, String value) throws SOAPException {
      if (name.getLocalName().equals("encodingStyle") && name.getURI().equals("http://www.w3.org/2003/05/soap-envelope")) {
         this.setEncodingStyle(value);
      }

      return super.addAttribute(name, value);
   }

   public SOAPElement addAttribute(QName name, String value) throws SOAPException {
      if (name.getLocalPart().equals("encodingStyle") && name.getNamespaceURI().equals("http://www.w3.org/2003/05/soap-envelope")) {
         this.setEncodingStyle(value);
      }

      return super.addAttribute(name, value);
   }
}
