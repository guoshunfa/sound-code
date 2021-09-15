package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.DetailImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class Fault1_1Impl extends FaultImpl {
   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_1", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.LocalStrings");

   public Fault1_1Impl(SOAPDocumentImpl ownerDocument, String prefix) {
      super(ownerDocument, NameImpl.createFault1_1Name(prefix));
   }

   protected NameImpl getDetailName() {
      return NameImpl.createDetail1_1Name();
   }

   protected NameImpl getFaultCodeName() {
      return NameImpl.createFromUnqualifiedName("faultcode");
   }

   protected NameImpl getFaultStringName() {
      return NameImpl.createFromUnqualifiedName("faultstring");
   }

   protected NameImpl getFaultActorName() {
      return NameImpl.createFromUnqualifiedName("faultactor");
   }

   protected DetailImpl createDetail() {
      return new Detail1_1Impl(((SOAPDocument)this.getOwnerDocument()).getDocument());
   }

   protected FaultElementImpl createSOAPFaultElement(String localName) {
      return new FaultElement1_1Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), localName);
   }

   protected void checkIfStandardFaultCode(String faultCode, String uri) throws SOAPException {
   }

   protected void finallySetFaultCode(String faultcode) throws SOAPException {
      this.faultCodeElement.addTextNode(faultcode);
   }

   public String getFaultCode() {
      if (this.faultCodeElement == null) {
         this.findFaultCodeElement();
      }

      return this.faultCodeElement.getValue();
   }

   public Name getFaultCodeAsName() {
      String faultcodeString = this.getFaultCode();
      if (faultcodeString == null) {
         return null;
      } else {
         int prefixIndex = faultcodeString.indexOf(58);
         if (prefixIndex == -1) {
            return NameImpl.createFromUnqualifiedName(faultcodeString);
         } else {
            String prefix = faultcodeString.substring(0, prefixIndex);
            if (this.faultCodeElement == null) {
               this.findFaultCodeElement();
            }

            String nsName = this.faultCodeElement.getNamespaceURI(prefix);
            return NameImpl.createFromQualifiedName(faultcodeString, nsName);
         }
      }
   }

   public QName getFaultCodeAsQName() {
      String faultcodeString = this.getFaultCode();
      if (faultcodeString == null) {
         return null;
      } else {
         if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
         }

         return convertCodeToQName(faultcodeString, this.faultCodeElement);
      }
   }

   public void setFaultString(String faultString) throws SOAPException {
      if (this.faultStringElement == null) {
         this.findFaultStringElement();
      }

      if (this.faultStringElement == null) {
         this.faultStringElement = this.addSOAPFaultElement("faultstring");
      } else {
         this.faultStringElement.removeContents();
         this.faultStringElement.removeAttribute("xml:lang");
      }

      this.faultStringElement.addTextNode(faultString);
   }

   public String getFaultString() {
      if (this.faultStringElement == null) {
         this.findFaultStringElement();
      }

      return this.faultStringElement.getValue();
   }

   public Locale getFaultStringLocale() {
      if (this.faultStringElement == null) {
         this.findFaultStringElement();
      }

      if (this.faultStringElement != null) {
         String xmlLangAttr = this.faultStringElement.getAttributeValue(NameImpl.createFromUnqualifiedName("xml:lang"));
         if (xmlLangAttr != null) {
            return xmlLangToLocale(xmlLangAttr);
         }
      }

      return null;
   }

   public void setFaultString(String faultString, Locale locale) throws SOAPException {
      this.setFaultString(faultString);
      this.faultStringElement.addAttribute(NameImpl.createFromTagName("xml:lang"), localeToXmlLang(locale));
   }

   protected boolean isStandardFaultElement(String localName) {
      return localName.equalsIgnoreCase("detail") || localName.equalsIgnoreCase("faultcode") || localName.equalsIgnoreCase("faultstring") || localName.equalsIgnoreCase("faultactor");
   }

   public void appendFaultSubcode(QName subcode) {
      log.log(Level.SEVERE, (String)"SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", (Object)"appendFaultSubcode");
      throw new UnsupportedOperationException("Not supported in SOAP 1.1");
   }

   public void removeAllFaultSubcodes() {
      log.log(Level.SEVERE, (String)"SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", (Object)"removeAllFaultSubcodes");
      throw new UnsupportedOperationException("Not supported in SOAP 1.1");
   }

   public Iterator getFaultSubcodes() {
      log.log(Level.SEVERE, (String)"SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", (Object)"getFaultSubcodes");
      throw new UnsupportedOperationException("Not supported in SOAP 1.1");
   }

   public String getFaultReasonText(Locale locale) {
      log.log(Level.SEVERE, (String)"SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", (Object)"getFaultReasonText");
      throw new UnsupportedOperationException("Not supported in SOAP 1.1");
   }

   public Iterator getFaultReasonTexts() {
      log.log(Level.SEVERE, (String)"SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", (Object)"getFaultReasonTexts");
      throw new UnsupportedOperationException("Not supported in SOAP 1.1");
   }

   public Iterator getFaultReasonLocales() {
      log.log(Level.SEVERE, (String)"SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", (Object)"getFaultReasonLocales");
      throw new UnsupportedOperationException("Not supported in SOAP 1.1");
   }

   public void addFaultReasonText(String text, Locale locale) throws SOAPException {
      log.log(Level.SEVERE, (String)"SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", (Object)"addFaultReasonText");
      throw new UnsupportedOperationException("Not supported in SOAP 1.1");
   }

   public String getFaultRole() {
      log.log(Level.SEVERE, (String)"SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", (Object)"getFaultRole");
      throw new UnsupportedOperationException("Not supported in SOAP 1.1");
   }

   public void setFaultRole(String uri) {
      log.log(Level.SEVERE, (String)"SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", (Object)"setFaultRole");
      throw new UnsupportedOperationException("Not supported in SOAP 1.1");
   }

   public String getFaultNode() {
      log.log(Level.SEVERE, (String)"SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", (Object)"getFaultNode");
      throw new UnsupportedOperationException("Not supported in SOAP 1.1");
   }

   public void setFaultNode(String uri) {
      log.log(Level.SEVERE, (String)"SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", (Object)"setFaultNode");
      throw new UnsupportedOperationException("Not supported in SOAP 1.1");
   }

   protected QName getDefaultFaultCode() {
      return new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server");
   }

   public SOAPElement addChildElement(SOAPElement element) throws SOAPException {
      String localName = element.getLocalName();
      if ("Detail".equalsIgnoreCase(localName) && this.hasDetail()) {
         log.severe("SAAJ0305.ver1_2.detail.exists.error");
         throw new SOAPExceptionImpl("Cannot add Detail, Detail already exists");
      } else {
         return super.addChildElement(element);
      }
   }

   protected FaultElementImpl createSOAPFaultElement(QName qname) {
      return new FaultElement1_1Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), qname);
   }

   protected FaultElementImpl createSOAPFaultElement(Name qname) {
      return new FaultElement1_1Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), (NameImpl)qname);
   }

   public void setFaultCode(String faultCode, String prefix, String uri) throws SOAPException {
      if ((prefix == null || "".equals(prefix)) && uri != null && !"".equals(uri)) {
         prefix = this.getNamespacePrefix(uri);
         if (prefix == null || "".equals(prefix)) {
            prefix = "ns0";
         }
      }

      if (this.faultCodeElement == null) {
         this.findFaultCodeElement();
      }

      if (this.faultCodeElement == null) {
         this.faultCodeElement = this.addFaultCodeElement();
      } else {
         this.faultCodeElement.removeContents();
      }

      if ((uri == null || "".equals(uri)) && prefix != null && !"".equals("prefix")) {
         uri = this.faultCodeElement.getNamespaceURI(prefix);
      }

      if (uri == null || "".equals(uri)) {
         if (prefix != null && !"".equals(prefix)) {
            log.log(Level.SEVERE, "SAAJ0307.impl.no.ns.URI", new Object[]{prefix + ":" + faultCode});
            throw new SOAPExceptionImpl("Empty/Null NamespaceURI specified for faultCode \"" + prefix + ":" + faultCode + "\"");
         }

         uri = "";
      }

      this.checkIfStandardFaultCode(faultCode, uri);
      ((FaultElementImpl)this.faultCodeElement).ensureNamespaceIsDeclared(prefix, uri);
      if (prefix != null && !"".equals(prefix)) {
         this.finallySetFaultCode(prefix + ":" + faultCode);
      } else {
         this.finallySetFaultCode(faultCode);
      }

   }

   private boolean standardFaultCode(String faultCode) {
      if (!faultCode.equals("VersionMismatch") && !faultCode.equals("MustUnderstand") && !faultCode.equals("Client") && !faultCode.equals("Server")) {
         return faultCode.startsWith("VersionMismatch.") || faultCode.startsWith("MustUnderstand.") || faultCode.startsWith("Client.") || faultCode.startsWith("Server.");
      } else {
         return true;
      }
   }

   public void setFaultActor(String faultActor) throws SOAPException {
      if (this.faultActorElement == null) {
         this.findFaultActorElement();
      }

      if (this.faultActorElement != null) {
         this.faultActorElement.detachNode();
      }

      if (faultActor != null) {
         this.faultActorElement = this.createSOAPFaultElement((Name)this.getFaultActorName());
         this.faultActorElement.addTextNode(faultActor);
         if (this.hasDetail()) {
            this.insertBefore(this.faultActorElement, this.detail);
         } else {
            this.addNode(this.faultActorElement);
         }
      }
   }
}
