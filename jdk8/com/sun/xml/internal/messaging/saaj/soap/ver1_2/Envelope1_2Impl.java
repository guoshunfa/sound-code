package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class Envelope1_2Impl extends EnvelopeImpl {
   protected static final Logger log = Logger.getLogger(Envelope1_2Impl.class.getName(), "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");

   public Envelope1_2Impl(SOAPDocumentImpl ownerDoc, String prefix) {
      super(ownerDoc, (Name)NameImpl.createEnvelope1_2Name(prefix));
   }

   public Envelope1_2Impl(SOAPDocumentImpl ownerDoc, String prefix, boolean createHeader, boolean createBody) throws SOAPException {
      super(ownerDoc, NameImpl.createEnvelope1_2Name(prefix), createHeader, createBody);
   }

   protected NameImpl getBodyName(String prefix) {
      return NameImpl.createBody1_2Name(prefix);
   }

   protected NameImpl getHeaderName(String prefix) {
      return NameImpl.createHeader1_2Name(prefix);
   }

   public void setEncodingStyle(String encodingStyle) throws SOAPException {
      log.severe("SAAJ0404.ver1_2.no.encodingStyle.in.envelope");
      throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on Envelope");
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

   public SOAPElement addChildElement(Name name) throws SOAPException {
      if (this.getBody() != null) {
         log.severe("SAAJ0405.ver1_2.body.must.last.in.envelope");
         throw new SOAPExceptionImpl("Body must be the last element in SOAP Envelope");
      } else {
         return super.addChildElement(name);
      }
   }

   public SOAPElement addChildElement(QName name) throws SOAPException {
      if (this.getBody() != null) {
         log.severe("SAAJ0405.ver1_2.body.must.last.in.envelope");
         throw new SOAPExceptionImpl("Body must be the last element in SOAP Envelope");
      } else {
         return super.addChildElement(name);
      }
   }

   public SOAPElement addTextNode(String text) throws SOAPException {
      log.log(Level.SEVERE, (String)"SAAJ0416.ver1_2.adding.text.not.legal", (Object)this.getElementQName());
      throw new SOAPExceptionImpl("Adding text to SOAP 1.2 Envelope is not legal");
   }
}
