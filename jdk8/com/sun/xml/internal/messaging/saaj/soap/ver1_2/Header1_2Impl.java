package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.HeaderImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;

public class Header1_2Impl extends HeaderImpl {
   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_2", "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");

   public Header1_2Impl(SOAPDocumentImpl ownerDocument, String prefix) {
      super(ownerDocument, NameImpl.createHeader1_2Name(prefix));
   }

   protected NameImpl getNotUnderstoodName() {
      return NameImpl.createNotUnderstood1_2Name((String)null);
   }

   protected NameImpl getUpgradeName() {
      return NameImpl.createUpgrade1_2Name((String)null);
   }

   protected NameImpl getSupportedEnvelopeName() {
      return NameImpl.createSupportedEnvelope1_2Name((String)null);
   }

   public SOAPHeaderElement addNotUnderstoodHeaderElement(QName sourceName) throws SOAPException {
      if (sourceName == null) {
         log.severe("SAAJ0410.ver1_2.no.null.to.addNotUnderstoodHeader");
         throw new SOAPException("Cannot pass NULL to addNotUnderstoodHeaderElement");
      } else if ("".equals(sourceName.getNamespaceURI())) {
         log.severe("SAAJ0417.ver1_2.qname.not.ns.qualified");
         throw new SOAPException("The qname passed to addNotUnderstoodHeaderElement must be namespace-qualified");
      } else {
         String prefix = sourceName.getPrefix();
         if ("".equals(prefix)) {
            prefix = "ns1";
         }

         Name notunderstoodName = this.getNotUnderstoodName();
         SOAPHeaderElement notunderstoodHeaderElement = (SOAPHeaderElement)this.addChildElement(notunderstoodName);
         notunderstoodHeaderElement.addAttribute(NameImpl.createFromUnqualifiedName("qname"), getQualifiedName(new QName(sourceName.getNamespaceURI(), sourceName.getLocalPart(), prefix)));
         notunderstoodHeaderElement.addNamespaceDeclaration(prefix, sourceName.getNamespaceURI());
         return notunderstoodHeaderElement;
      }
   }

   public SOAPElement addTextNode(String text) throws SOAPException {
      log.log(Level.SEVERE, (String)"SAAJ0416.ver1_2.adding.text.not.legal", (Object)this.getElementQName());
      throw new SOAPExceptionImpl("Adding text to SOAP 1.2 Header is not legal");
   }

   protected SOAPHeaderElement createHeaderElement(Name name) throws SOAPException {
      String uri = name.getURI();
      if (uri != null && !uri.equals("")) {
         return new HeaderElement1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), name);
      } else {
         log.severe("SAAJ0413.ver1_2.header.elems.must.be.ns.qualified");
         throw new SOAPExceptionImpl("SOAP 1.2 header elements must be namespace qualified");
      }
   }

   protected SOAPHeaderElement createHeaderElement(QName name) throws SOAPException {
      String uri = name.getNamespaceURI();
      if (uri != null && !uri.equals("")) {
         return new HeaderElement1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), name);
      } else {
         log.severe("SAAJ0413.ver1_2.header.elems.must.be.ns.qualified");
         throw new SOAPExceptionImpl("SOAP 1.2 header elements must be namespace qualified");
      }
   }

   public void setEncodingStyle(String encodingStyle) throws SOAPException {
      log.severe("SAAJ0409.ver1_2.no.encodingstyle.in.header");
      throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on Header");
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
