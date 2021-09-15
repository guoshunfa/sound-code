package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.BodyImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import org.w3c.dom.Node;

public class Body1_2Impl extends BodyImpl {
   protected static final Logger log = Logger.getLogger(Body1_2Impl.class.getName(), "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");

   public Body1_2Impl(SOAPDocumentImpl ownerDocument, String prefix) {
      super(ownerDocument, NameImpl.createBody1_2Name(prefix));
   }

   protected NameImpl getFaultName(String name) {
      return NameImpl.createFault1_2Name(name, (String)null);
   }

   protected SOAPBodyElement createBodyElement(Name name) {
      return new BodyElement1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), name);
   }

   protected SOAPBodyElement createBodyElement(QName name) {
      return new BodyElement1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), name);
   }

   protected QName getDefaultFaultCode() {
      return SOAPConstants.SOAP_RECEIVER_FAULT;
   }

   public SOAPFault addFault() throws SOAPException {
      if (this.hasAnyChildElement()) {
         log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
         throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
      } else {
         return super.addFault();
      }
   }

   public void setEncodingStyle(String encodingStyle) throws SOAPException {
      log.severe("SAAJ0401.ver1_2.no.encodingstyle.in.body");
      throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on Body");
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

   protected boolean isFault(SOAPElement child) {
      return child.getElementName().getURI().equals("http://www.w3.org/2003/05/soap-envelope") && child.getElementName().getLocalName().equals("Fault");
   }

   protected SOAPFault createFaultElement() {
      return new Fault1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), this.getPrefix());
   }

   public SOAPBodyElement addBodyElement(Name name) throws SOAPException {
      if (this.hasFault()) {
         log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
         throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
      } else {
         return super.addBodyElement(name);
      }
   }

   public SOAPBodyElement addBodyElement(QName name) throws SOAPException {
      if (this.hasFault()) {
         log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
         throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
      } else {
         return super.addBodyElement(name);
      }
   }

   protected SOAPElement addElement(Name name) throws SOAPException {
      if (this.hasFault()) {
         log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
         throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
      } else {
         return super.addElement(name);
      }
   }

   protected SOAPElement addElement(QName name) throws SOAPException {
      if (this.hasFault()) {
         log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
         throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
      } else {
         return super.addElement(name);
      }
   }

   public SOAPElement addChildElement(Name name) throws SOAPException {
      if (this.hasFault()) {
         log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
         throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
      } else {
         return super.addChildElement(name);
      }
   }

   public SOAPElement addChildElement(QName name) throws SOAPException {
      if (this.hasFault()) {
         log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
         throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
      } else {
         return super.addChildElement(name);
      }
   }

   private boolean hasAnyChildElement() {
      for(Node currentNode = this.getFirstChild(); currentNode != null; currentNode = currentNode.getNextSibling()) {
         if (currentNode.getNodeType() == 1) {
            return true;
         }
      }

      return false;
   }
}
