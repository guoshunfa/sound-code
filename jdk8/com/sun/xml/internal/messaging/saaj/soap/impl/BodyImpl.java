package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class BodyImpl extends ElementImpl implements SOAPBody {
   private SOAPFault fault;

   protected BodyImpl(SOAPDocumentImpl ownerDoc, NameImpl bodyName) {
      super(ownerDoc, (Name)bodyName);
   }

   protected abstract NameImpl getFaultName(String var1);

   protected abstract boolean isFault(SOAPElement var1);

   protected abstract SOAPBodyElement createBodyElement(Name var1);

   protected abstract SOAPBodyElement createBodyElement(QName var1);

   protected abstract SOAPFault createFaultElement();

   protected abstract QName getDefaultFaultCode();

   public SOAPFault addFault() throws SOAPException {
      if (this.hasFault()) {
         log.severe("SAAJ0110.impl.fault.already.exists");
         throw new SOAPExceptionImpl("Error: Fault already exists");
      } else {
         this.fault = this.createFaultElement();
         this.addNode(this.fault);
         this.fault.setFaultCode(this.getDefaultFaultCode());
         this.fault.setFaultString("Fault string, and possibly fault code, not set");
         return this.fault;
      }
   }

   public SOAPFault addFault(Name faultCode, String faultString, Locale locale) throws SOAPException {
      SOAPFault fault = this.addFault();
      fault.setFaultCode(faultCode);
      fault.setFaultString(faultString, locale);
      return fault;
   }

   public SOAPFault addFault(QName faultCode, String faultString, Locale locale) throws SOAPException {
      SOAPFault fault = this.addFault();
      fault.setFaultCode(faultCode);
      fault.setFaultString(faultString, locale);
      return fault;
   }

   public SOAPFault addFault(Name faultCode, String faultString) throws SOAPException {
      SOAPFault fault = this.addFault();
      fault.setFaultCode(faultCode);
      fault.setFaultString(faultString);
      return fault;
   }

   public SOAPFault addFault(QName faultCode, String faultString) throws SOAPException {
      SOAPFault fault = this.addFault();
      fault.setFaultCode(faultCode);
      fault.setFaultString(faultString);
      return fault;
   }

   void initializeFault() {
      FaultImpl flt = (FaultImpl)this.findFault();
      this.fault = flt;
   }

   protected SOAPElement findFault() {
      Iterator eachChild = this.getChildElementNodes();

      SOAPElement child;
      do {
         if (!eachChild.hasNext()) {
            return null;
         }

         child = (SOAPElement)eachChild.next();
      } while(!this.isFault(child));

      return child;
   }

   public boolean hasFault() {
      this.initializeFault();
      return this.fault != null;
   }

   public SOAPFault getFault() {
      return this.hasFault() ? this.fault : null;
   }

   public SOAPBodyElement addBodyElement(Name name) throws SOAPException {
      SOAPBodyElement newBodyElement = (SOAPBodyElement)ElementFactory.createNamedElement(((SOAPDocument)this.getOwnerDocument()).getDocument(), name.getLocalName(), name.getPrefix(), name.getURI());
      if (newBodyElement == null) {
         newBodyElement = this.createBodyElement(name);
      }

      this.addNode(newBodyElement);
      return newBodyElement;
   }

   public SOAPBodyElement addBodyElement(QName qname) throws SOAPException {
      SOAPBodyElement newBodyElement = (SOAPBodyElement)ElementFactory.createNamedElement(((SOAPDocument)this.getOwnerDocument()).getDocument(), qname.getLocalPart(), qname.getPrefix(), qname.getNamespaceURI());
      if (newBodyElement == null) {
         newBodyElement = this.createBodyElement(qname);
      }

      this.addNode(newBodyElement);
      return newBodyElement;
   }

   public void setParentElement(SOAPElement element) throws SOAPException {
      if (!(element instanceof SOAPEnvelope)) {
         log.severe("SAAJ0111.impl.body.parent.must.be.envelope");
         throw new SOAPException("Parent of SOAPBody has to be a SOAPEnvelope");
      } else {
         super.setParentElement(element);
      }
   }

   protected SOAPElement addElement(Name name) throws SOAPException {
      return this.addBodyElement(name);
   }

   protected SOAPElement addElement(QName name) throws SOAPException {
      return this.addBodyElement(name);
   }

   public SOAPBodyElement addDocument(Document document) throws SOAPException {
      SOAPBodyElement newBodyElement = null;
      DocumentFragment docFrag = document.createDocumentFragment();
      Element rootElement = document.getDocumentElement();
      if (rootElement != null) {
         docFrag.appendChild(rootElement);
         Document ownerDoc = this.getOwnerDocument();
         Node replacingNode = ownerDoc.importNode(docFrag, true);
         this.addNode(replacingNode);

         for(Iterator i = this.getChildElements(NameImpl.copyElementName(rootElement)); i.hasNext(); newBodyElement = (SOAPBodyElement)i.next()) {
         }
      }

      return newBodyElement;
   }

   protected SOAPElement convertToSoapElement(Element element) {
      return element instanceof SOAPBodyElement && !element.getClass().equals(ElementImpl.class) ? (SOAPElement)element : replaceElementWithSOAPElement(element, (ElementImpl)this.createBodyElement(NameImpl.copyElementName(element)));
   }

   public SOAPElement setElementQName(QName newName) throws SOAPException {
      log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[]{this.elementQName.getLocalPart(), newName.getLocalPart()});
      throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + newName.getLocalPart());
   }

   public Document extractContentAsDocument() throws SOAPException {
      Iterator eachChild = this.getChildElements();

      javax.xml.soap.Node firstBodyElement;
      for(firstBodyElement = null; eachChild.hasNext() && !(firstBodyElement instanceof SOAPElement); firstBodyElement = (javax.xml.soap.Node)eachChild.next()) {
      }

      boolean exactlyOneChildElement = true;
      Node node;
      if (firstBodyElement == null) {
         exactlyOneChildElement = false;
      } else {
         for(node = firstBodyElement.getNextSibling(); node != null; node = node.getNextSibling()) {
            if (node instanceof Element) {
               exactlyOneChildElement = false;
               break;
            }
         }
      }

      if (!exactlyOneChildElement) {
         log.log(Level.SEVERE, "SAAJ0250.impl.body.should.have.exactly.one.child");
         throw new SOAPException("Cannot extract Document from body");
      } else {
         node = null;

         Document document;
         try {
            DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();
            Element rootElement = (Element)document.importNode(firstBodyElement, true);
            document.appendChild(rootElement);
         } catch (Exception var8) {
            log.log(Level.SEVERE, "SAAJ0251.impl.cannot.extract.document.from.body");
            throw new SOAPExceptionImpl("Unable to extract Document from body", var8);
         }

         firstBodyElement.detachNode();
         return document;
      }
   }
}
