package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.Locale;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPFaultElement;
import org.w3c.dom.Element;

public abstract class FaultImpl extends ElementImpl implements SOAPFault {
   protected SOAPFaultElement faultStringElement;
   protected SOAPFaultElement faultActorElement;
   protected SOAPFaultElement faultCodeElement;
   protected Detail detail;

   protected FaultImpl(SOAPDocumentImpl ownerDoc, NameImpl name) {
      super(ownerDoc, (Name)name);
   }

   protected abstract NameImpl getDetailName();

   protected abstract NameImpl getFaultCodeName();

   protected abstract NameImpl getFaultStringName();

   protected abstract NameImpl getFaultActorName();

   protected abstract DetailImpl createDetail();

   protected abstract FaultElementImpl createSOAPFaultElement(String var1);

   protected abstract FaultElementImpl createSOAPFaultElement(QName var1);

   protected abstract FaultElementImpl createSOAPFaultElement(Name var1);

   protected abstract void checkIfStandardFaultCode(String var1, String var2) throws SOAPException;

   protected abstract void finallySetFaultCode(String var1) throws SOAPException;

   protected abstract boolean isStandardFaultElement(String var1);

   protected abstract QName getDefaultFaultCode();

   protected void findFaultCodeElement() {
      this.faultCodeElement = (SOAPFaultElement)this.findChild(this.getFaultCodeName());
   }

   protected void findFaultActorElement() {
      this.faultActorElement = (SOAPFaultElement)this.findChild(this.getFaultActorName());
   }

   protected void findFaultStringElement() {
      this.faultStringElement = (SOAPFaultElement)this.findChild(this.getFaultStringName());
   }

   public void setFaultCode(String faultCode) throws SOAPException {
      this.setFaultCode(NameImpl.getLocalNameFromTagName(faultCode), NameImpl.getPrefixFromTagName(faultCode), (String)null);
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

      if (uri == null || "".equals(uri)) {
         uri = this.faultCodeElement.getNamespaceURI(prefix);
      }

      if (uri == null || "".equals(uri)) {
         if (prefix != null && !"".equals(prefix)) {
            log.log(Level.SEVERE, "SAAJ0140.impl.no.ns.URI", new Object[]{prefix + ":" + faultCode});
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

   public void setFaultCode(Name faultCodeQName) throws SOAPException {
      this.setFaultCode(faultCodeQName.getLocalName(), faultCodeQName.getPrefix(), faultCodeQName.getURI());
   }

   public void setFaultCode(QName faultCodeQName) throws SOAPException {
      this.setFaultCode(faultCodeQName.getLocalPart(), faultCodeQName.getPrefix(), faultCodeQName.getNamespaceURI());
   }

   protected static QName convertCodeToQName(String code, SOAPElement codeContainingElement) {
      int prefixIndex = code.indexOf(58);
      if (prefixIndex == -1) {
         return new QName(code);
      } else {
         String prefix = code.substring(0, prefixIndex);
         String nsName = ((ElementImpl)codeContainingElement).lookupNamespaceURI(prefix);
         return new QName(nsName, getLocalPart(code), prefix);
      }
   }

   protected void initializeDetail() {
      NameImpl detailName = this.getDetailName();
      this.detail = (Detail)this.findChild(detailName);
   }

   public Detail getDetail() {
      if (this.detail == null) {
         this.initializeDetail();
      }

      if (this.detail != null && this.detail.getParentNode() == null) {
         this.detail = null;
      }

      return this.detail;
   }

   public Detail addDetail() throws SOAPException {
      if (this.detail == null) {
         this.initializeDetail();
      }

      if (this.detail == null) {
         this.detail = this.createDetail();
         this.addNode(this.detail);
         return this.detail;
      } else {
         throw new SOAPExceptionImpl("Error: Detail already exists");
      }
   }

   public boolean hasDetail() {
      return this.getDetail() != null;
   }

   public abstract void setFaultActor(String var1) throws SOAPException;

   public String getFaultActor() {
      if (this.faultActorElement == null) {
         this.findFaultActorElement();
      }

      return this.faultActorElement != null ? this.faultActorElement.getValue() : null;
   }

   public SOAPElement setElementQName(QName newName) throws SOAPException {
      log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[]{this.elementQName.getLocalPart(), newName.getLocalPart()});
      throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + newName.getLocalPart());
   }

   protected SOAPElement convertToSoapElement(Element element) {
      if (element instanceof SOAPFaultElement) {
         return (SOAPElement)element;
      } else if (element instanceof SOAPElement) {
         SOAPElement soapElement = (SOAPElement)element;
         if (this.getDetailName().equals(soapElement.getElementName())) {
            return replaceElementWithSOAPElement(element, this.createDetail());
         } else {
            String localName = soapElement.getElementName().getLocalName();
            return this.isStandardFaultElement(localName) ? replaceElementWithSOAPElement(element, this.createSOAPFaultElement(soapElement.getElementQName())) : soapElement;
         }
      } else {
         Name elementName = NameImpl.copyElementName(element);
         Object newElement;
         if (this.getDetailName().equals(elementName)) {
            newElement = this.createDetail();
         } else {
            String localName = elementName.getLocalName();
            if (this.isStandardFaultElement(localName)) {
               newElement = this.createSOAPFaultElement(elementName);
            } else {
               newElement = (ElementImpl)this.createElement(elementName);
            }
         }

         return replaceElementWithSOAPElement(element, (ElementImpl)newElement);
      }
   }

   protected SOAPFaultElement addFaultCodeElement() throws SOAPException {
      if (this.faultCodeElement == null) {
         this.findFaultCodeElement();
      }

      if (this.faultCodeElement == null) {
         this.faultCodeElement = this.addSOAPFaultElement(this.getFaultCodeName().getLocalName());
         return this.faultCodeElement;
      } else {
         throw new SOAPExceptionImpl("Error: Faultcode already exists");
      }
   }

   private SOAPFaultElement addFaultStringElement() throws SOAPException {
      if (this.faultStringElement == null) {
         this.findFaultStringElement();
      }

      if (this.faultStringElement == null) {
         this.faultStringElement = this.addSOAPFaultElement(this.getFaultStringName().getLocalName());
         return this.faultStringElement;
      } else {
         throw new SOAPExceptionImpl("Error: Faultstring already exists");
      }
   }

   private SOAPFaultElement addFaultActorElement() throws SOAPException {
      if (this.faultActorElement == null) {
         this.findFaultActorElement();
      }

      if (this.faultActorElement == null) {
         this.faultActorElement = this.addSOAPFaultElement(this.getFaultActorName().getLocalName());
         return this.faultActorElement;
      } else {
         throw new SOAPExceptionImpl("Error: Faultactor already exists");
      }
   }

   protected SOAPElement addElement(Name name) throws SOAPException {
      if (this.getDetailName().equals(name)) {
         return this.addDetail();
      } else if (this.getFaultCodeName().equals(name)) {
         return this.addFaultCodeElement();
      } else if (this.getFaultStringName().equals(name)) {
         return this.addFaultStringElement();
      } else {
         return (SOAPElement)(this.getFaultActorName().equals(name) ? this.addFaultActorElement() : super.addElement(name));
      }
   }

   protected SOAPElement addElement(QName name) throws SOAPException {
      return this.addElement(NameImpl.convertToName(name));
   }

   protected FaultElementImpl addSOAPFaultElement(String localName) throws SOAPException {
      FaultElementImpl faultElem = this.createSOAPFaultElement(localName);
      this.addNode(faultElem);
      return faultElem;
   }

   protected static Locale xmlLangToLocale(String xmlLang) {
      if (xmlLang == null) {
         return null;
      } else {
         int index = xmlLang.indexOf("-");
         if (index == -1) {
            index = xmlLang.indexOf("_");
         }

         if (index == -1) {
            return new Locale(xmlLang, "");
         } else {
            String language = xmlLang.substring(0, index);
            String country = xmlLang.substring(index + 1);
            return new Locale(language, country);
         }
      }
   }

   protected static String localeToXmlLang(Locale locale) {
      String xmlLang = locale.getLanguage();
      String country = locale.getCountry();
      if (!"".equals(country)) {
         xmlLang = xmlLang + "-" + country;
      }

      return xmlLang;
   }
}
