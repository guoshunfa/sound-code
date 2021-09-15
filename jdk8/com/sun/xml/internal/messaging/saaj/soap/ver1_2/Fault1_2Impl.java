package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.DetailImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class Fault1_2Impl extends FaultImpl {
   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_2", "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");
   private static final QName textName = new QName("http://www.w3.org/2003/05/soap-envelope", "Text");
   private final QName valueName = new QName("http://www.w3.org/2003/05/soap-envelope", "Value", this.getPrefix());
   private final QName subcodeName = new QName("http://www.w3.org/2003/05/soap-envelope", "Subcode", this.getPrefix());
   private SOAPElement innermostSubCodeElement = null;

   public Fault1_2Impl(SOAPDocumentImpl ownerDoc, String name, String prefix) {
      super(ownerDoc, NameImpl.createFault1_2Name(name, prefix));
   }

   public Fault1_2Impl(SOAPDocumentImpl ownerDocument, String prefix) {
      super(ownerDocument, NameImpl.createFault1_2Name((String)null, prefix));
   }

   protected NameImpl getDetailName() {
      return NameImpl.createSOAP12Name("Detail", this.getPrefix());
   }

   protected NameImpl getFaultCodeName() {
      return NameImpl.createSOAP12Name("Code", this.getPrefix());
   }

   protected NameImpl getFaultStringName() {
      return this.getFaultReasonName();
   }

   protected NameImpl getFaultActorName() {
      return this.getFaultRoleName();
   }

   private NameImpl getFaultRoleName() {
      return NameImpl.createSOAP12Name("Role", this.getPrefix());
   }

   private NameImpl getFaultReasonName() {
      return NameImpl.createSOAP12Name("Reason", this.getPrefix());
   }

   private NameImpl getFaultReasonTextName() {
      return NameImpl.createSOAP12Name("Text", this.getPrefix());
   }

   private NameImpl getFaultNodeName() {
      return NameImpl.createSOAP12Name("Node", this.getPrefix());
   }

   private static NameImpl getXmlLangName() {
      return NameImpl.createXmlName("lang");
   }

   protected DetailImpl createDetail() {
      return new Detail1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument());
   }

   protected FaultElementImpl createSOAPFaultElement(String localName) {
      return new FaultElement1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), localName);
   }

   protected void checkIfStandardFaultCode(String faultCode, String uri) throws SOAPException {
      QName qname = new QName(uri, faultCode);
      if (!SOAPConstants.SOAP_DATAENCODINGUNKNOWN_FAULT.equals(qname) && !SOAPConstants.SOAP_MUSTUNDERSTAND_FAULT.equals(qname) && !SOAPConstants.SOAP_RECEIVER_FAULT.equals(qname) && !SOAPConstants.SOAP_SENDER_FAULT.equals(qname) && !SOAPConstants.SOAP_VERSIONMISMATCH_FAULT.equals(qname)) {
         log.log(Level.SEVERE, (String)"SAAJ0435.ver1_2.code.not.standard", (Object)qname);
         throw new SOAPExceptionImpl(qname + " is not a standard Code value");
      }
   }

   protected void finallySetFaultCode(String faultcode) throws SOAPException {
      SOAPElement value = this.faultCodeElement.addChildElement(this.valueName);
      value.addTextNode(faultcode);
   }

   private void findReasonElement() {
      this.findFaultStringElement();
   }

   public Iterator getFaultReasonTexts() throws SOAPException {
      if (this.faultStringElement == null) {
         this.findReasonElement();
      }

      Iterator eachTextElement = this.faultStringElement.getChildElements(textName);
      ArrayList texts = new ArrayList();

      while(eachTextElement.hasNext()) {
         SOAPElement textElement = (SOAPElement)eachTextElement.next();
         Locale thisLocale = getLocale(textElement);
         if (thisLocale == null) {
            log.severe("SAAJ0431.ver1_2.xml.lang.missing");
            throw new SOAPExceptionImpl("\"xml:lang\" attribute is not present on the Text element");
         }

         texts.add(textElement.getValue());
      }

      if (texts.isEmpty()) {
         log.severe("SAAJ0434.ver1_2.text.element.not.present");
         throw new SOAPExceptionImpl("env:Text must be present inside env:Reason");
      } else {
         return texts.iterator();
      }
   }

   public void addFaultReasonText(String text, Locale locale) throws SOAPException {
      if (locale == null) {
         log.severe("SAAJ0430.ver1_2.locale.required");
         throw new SOAPException("locale is required and must not be null");
      } else {
         if (this.faultStringElement == null) {
            this.findReasonElement();
         }

         SOAPElement reasonText;
         if (this.faultStringElement == null) {
            this.faultStringElement = this.addSOAPFaultElement("Reason");
            reasonText = this.faultStringElement.addChildElement(this.getFaultReasonTextName());
         } else {
            this.removeDefaultFaultString();
            reasonText = this.getFaultReasonTextElement(locale);
            if (reasonText != null) {
               reasonText.removeContents();
            } else {
               reasonText = this.faultStringElement.addChildElement(this.getFaultReasonTextName());
            }
         }

         String xmlLang = localeToXmlLang(locale);
         reasonText.addAttribute((Name)getXmlLangName(), xmlLang);
         reasonText.addTextNode(text);
      }
   }

   private void removeDefaultFaultString() throws SOAPException {
      SOAPElement reasonText = this.getFaultReasonTextElement(Locale.getDefault());
      if (reasonText != null) {
         String defaultFaultString = "Fault string, and possibly fault code, not set";
         if (defaultFaultString.equals(reasonText.getValue())) {
            reasonText.detachNode();
         }
      }

   }

   public String getFaultReasonText(Locale locale) throws SOAPException {
      if (locale == null) {
         return null;
      } else {
         if (this.faultStringElement == null) {
            this.findReasonElement();
         }

         if (this.faultStringElement != null) {
            SOAPElement textElement = this.getFaultReasonTextElement(locale);
            if (textElement != null) {
               textElement.normalize();
               return textElement.getFirstChild().getNodeValue();
            }
         }

         return null;
      }
   }

   public Iterator getFaultReasonLocales() throws SOAPException {
      if (this.faultStringElement == null) {
         this.findReasonElement();
      }

      Iterator eachTextElement = this.faultStringElement.getChildElements(textName);
      ArrayList localeSet = new ArrayList();

      while(eachTextElement.hasNext()) {
         SOAPElement textElement = (SOAPElement)eachTextElement.next();
         Locale thisLocale = getLocale(textElement);
         if (thisLocale == null) {
            log.severe("SAAJ0431.ver1_2.xml.lang.missing");
            throw new SOAPExceptionImpl("\"xml:lang\" attribute is not present on the Text element");
         }

         localeSet.add(thisLocale);
      }

      if (localeSet.isEmpty()) {
         log.severe("SAAJ0434.ver1_2.text.element.not.present");
         throw new SOAPExceptionImpl("env:Text elements with mandatory xml:lang attributes must be present inside env:Reason");
      } else {
         return localeSet.iterator();
      }
   }

   public Locale getFaultStringLocale() {
      Locale locale = null;

      try {
         locale = (Locale)this.getFaultReasonLocales().next();
      } catch (SOAPException var3) {
      }

      return locale;
   }

   private SOAPElement getFaultReasonTextElement(Locale locale) throws SOAPException {
      Iterator eachTextElement = this.faultStringElement.getChildElements(textName);

      SOAPElement textElement;
      Locale thisLocale;
      do {
         if (!eachTextElement.hasNext()) {
            return null;
         }

         textElement = (SOAPElement)eachTextElement.next();
         thisLocale = getLocale(textElement);
         if (thisLocale == null) {
            log.severe("SAAJ0431.ver1_2.xml.lang.missing");
            throw new SOAPExceptionImpl("\"xml:lang\" attribute is not present on the Text element");
         }
      } while(!thisLocale.equals(locale));

      return textElement;
   }

   public String getFaultNode() {
      SOAPElement faultNode = this.findChild(this.getFaultNodeName());
      return faultNode == null ? null : faultNode.getValue();
   }

   public void setFaultNode(String uri) throws SOAPException {
      SOAPElement faultNode = this.findChild(this.getFaultNodeName());
      if (faultNode != null) {
         faultNode.detachNode();
      }

      SOAPElement faultNode = this.createSOAPFaultElement((Name)this.getFaultNodeName());
      faultNode = faultNode.addTextNode(uri);
      if (this.getFaultRole() != null) {
         this.insertBefore(faultNode, this.faultActorElement);
      } else if (this.hasDetail()) {
         this.insertBefore(faultNode, this.detail);
      } else {
         this.addNode(faultNode);
      }
   }

   public String getFaultRole() {
      return this.getFaultActor();
   }

   public void setFaultRole(String uri) throws SOAPException {
      if (this.faultActorElement == null) {
         this.findFaultActorElement();
      }

      if (this.faultActorElement != null) {
         this.faultActorElement.detachNode();
      }

      this.faultActorElement = this.createSOAPFaultElement((Name)this.getFaultActorName());
      this.faultActorElement.addTextNode(uri);
      if (this.hasDetail()) {
         this.insertBefore(this.faultActorElement, this.detail);
      } else {
         this.addNode(this.faultActorElement);
      }
   }

   public String getFaultCode() {
      if (this.faultCodeElement == null) {
         this.findFaultCodeElement();
      }

      Iterator codeValues = this.faultCodeElement.getChildElements(this.valueName);
      return ((SOAPElement)codeValues.next()).getValue();
   }

   public QName getFaultCodeAsQName() {
      String faultcode = this.getFaultCode();
      if (faultcode == null) {
         return null;
      } else {
         if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
         }

         Iterator valueElements = this.faultCodeElement.getChildElements(this.valueName);
         return convertCodeToQName(faultcode, (SOAPElement)valueElements.next());
      }
   }

   public Name getFaultCodeAsName() {
      String faultcode = this.getFaultCode();
      if (faultcode == null) {
         return null;
      } else {
         if (this.faultCodeElement == null) {
            this.findFaultCodeElement();
         }

         Iterator valueElements = this.faultCodeElement.getChildElements(this.valueName);
         return NameImpl.convertToName(convertCodeToQName(faultcode, (SOAPElement)valueElements.next()));
      }
   }

   public String getFaultString() {
      String reason = null;

      try {
         reason = (String)this.getFaultReasonTexts().next();
      } catch (SOAPException var3) {
      }

      return reason;
   }

   public void setFaultString(String faultString) throws SOAPException {
      this.addFaultReasonText(faultString, Locale.getDefault());
   }

   public void setFaultString(String faultString, Locale locale) throws SOAPException {
      this.addFaultReasonText(faultString, locale);
   }

   public void appendFaultSubcode(QName subcode) throws SOAPException {
      if (subcode != null) {
         if (subcode.getNamespaceURI() != null && !"".equals(subcode.getNamespaceURI())) {
            if (this.innermostSubCodeElement == null) {
               if (this.faultCodeElement == null) {
                  this.findFaultCodeElement();
               }

               this.innermostSubCodeElement = this.faultCodeElement;
            }

            String prefix = null;
            if (subcode.getPrefix() != null && !"".equals(subcode.getPrefix())) {
               prefix = subcode.getPrefix();
            } else {
               prefix = ((ElementImpl)this.innermostSubCodeElement).getNamespacePrefix(subcode.getNamespaceURI());
            }

            if (prefix == null || "".equals(prefix)) {
               prefix = "ns1";
            }

            this.innermostSubCodeElement = this.innermostSubCodeElement.addChildElement(this.subcodeName);
            SOAPElement subcodeValueElement = this.innermostSubCodeElement.addChildElement(this.valueName);
            ((ElementImpl)subcodeValueElement).ensureNamespaceIsDeclared(prefix, subcode.getNamespaceURI());
            subcodeValueElement.addTextNode(prefix + ":" + subcode.getLocalPart());
         } else {
            log.severe("SAAJ0432.ver1_2.subcode.not.ns.qualified");
            throw new SOAPExceptionImpl("A Subcode must be namespace-qualified");
         }
      }
   }

   public void removeAllFaultSubcodes() {
      if (this.faultCodeElement == null) {
         this.findFaultCodeElement();
      }

      Iterator subcodeElements = this.faultCodeElement.getChildElements(this.subcodeName);
      if (subcodeElements.hasNext()) {
         SOAPElement subcode = (SOAPElement)subcodeElements.next();
         subcode.detachNode();
      }

   }

   public Iterator getFaultSubcodes() {
      if (this.faultCodeElement == null) {
         this.findFaultCodeElement();
      }

      final List subcodeList = new ArrayList();
      SOAPElement currentCodeElement = this.faultCodeElement;

      ElementImpl currentCodeElement;
      for(Iterator subcodeElements = currentCodeElement.getChildElements(this.subcodeName); subcodeElements.hasNext(); subcodeElements = currentCodeElement.getChildElements(this.subcodeName)) {
         currentCodeElement = (ElementImpl)subcodeElements.next();
         Iterator valueElements = currentCodeElement.getChildElements(this.valueName);
         SOAPElement valueElement = (SOAPElement)valueElements.next();
         String code = valueElement.getValue();
         subcodeList.add(convertCodeToQName(code, valueElement));
      }

      return new Iterator() {
         Iterator subCodeIter = subcodeList.iterator();

         public boolean hasNext() {
            return this.subCodeIter.hasNext();
         }

         public Object next() {
            return this.subCodeIter.next();
         }

         public void remove() {
            throw new UnsupportedOperationException("Method remove() not supported on SubCodes Iterator");
         }
      };
   }

   private static Locale getLocale(SOAPElement reasonText) {
      return xmlLangToLocale(reasonText.getAttributeValue((Name)getXmlLangName()));
   }

   public void setEncodingStyle(String encodingStyle) throws SOAPException {
      log.severe("SAAJ0407.ver1_2.no.encodingStyle.in.fault");
      throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on Fault");
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

   public SOAPElement addTextNode(String text) throws SOAPException {
      log.log(Level.SEVERE, (String)"SAAJ0416.ver1_2.adding.text.not.legal", (Object)this.getElementQName());
      throw new SOAPExceptionImpl("Adding text to SOAP 1.2 Fault is not legal");
   }

   public SOAPElement addChildElement(SOAPElement element) throws SOAPException {
      String localName = element.getLocalName();
      if ("Detail".equalsIgnoreCase(localName)) {
         if (this.hasDetail()) {
            log.severe("SAAJ0436.ver1_2.detail.exists.error");
            throw new SOAPExceptionImpl("Cannot add Detail, Detail already exists");
         }

         String uri = element.getElementQName().getNamespaceURI();
         if (!uri.equals("http://www.w3.org/2003/05/soap-envelope")) {
            log.severe("SAAJ0437.ver1_2.version.mismatch.error");
            throw new SOAPExceptionImpl("Cannot add Detail, Incorrect SOAP version specified for Detail element");
         }
      }

      if (element instanceof Detail1_2Impl) {
         ElementImpl importedElement = (ElementImpl)this.importElement(element);
         this.addNode(importedElement);
         return this.convertToSoapElement(importedElement);
      } else {
         return super.addChildElement(element);
      }
   }

   protected boolean isStandardFaultElement(String localName) {
      return localName.equalsIgnoreCase("code") || localName.equalsIgnoreCase("reason") || localName.equalsIgnoreCase("node") || localName.equalsIgnoreCase("role") || localName.equalsIgnoreCase("detail");
   }

   protected QName getDefaultFaultCode() {
      return SOAPConstants.SOAP_SENDER_FAULT;
   }

   protected FaultElementImpl createSOAPFaultElement(QName qname) {
      return new FaultElement1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), qname);
   }

   protected FaultElementImpl createSOAPFaultElement(Name qname) {
      return new FaultElement1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), (NameImpl)qname);
   }

   public void setFaultActor(String faultActor) throws SOAPException {
      this.setFaultRole(faultActor);
   }
}
