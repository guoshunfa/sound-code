package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.soap.impl.ElementFactory;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class SOAPFactoryImpl extends SOAPFactory {
   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");

   protected abstract SOAPDocumentImpl createDocument();

   public SOAPElement createElement(String tagName) throws SOAPException {
      if (tagName == null) {
         log.log(Level.SEVERE, "SAAJ0567.soap.null.input", new Object[]{"tagName", "SOAPFactory.createElement"});
         throw new SOAPException("Null tagName argument passed to createElement");
      } else {
         return ElementFactory.createElement(this.createDocument(), NameImpl.createFromTagName(tagName));
      }
   }

   public SOAPElement createElement(Name name) throws SOAPException {
      if (name == null) {
         log.log(Level.SEVERE, "SAAJ0567.soap.null.input", new Object[]{"name", "SOAPFactory.createElement"});
         throw new SOAPException("Null name argument passed to createElement");
      } else {
         return ElementFactory.createElement(this.createDocument(), name);
      }
   }

   public SOAPElement createElement(QName qname) throws SOAPException {
      if (qname == null) {
         log.log(Level.SEVERE, "SAAJ0567.soap.null.input", new Object[]{"qname", "SOAPFactory.createElement"});
         throw new SOAPException("Null qname argument passed to createElement");
      } else {
         return ElementFactory.createElement(this.createDocument(), qname);
      }
   }

   public SOAPElement createElement(String localName, String prefix, String uri) throws SOAPException {
      if (localName == null) {
         log.log(Level.SEVERE, "SAAJ0567.soap.null.input", new Object[]{"localName", "SOAPFactory.createElement"});
         throw new SOAPException("Null localName argument passed to createElement");
      } else {
         return ElementFactory.createElement(this.createDocument(), localName, prefix, uri);
      }
   }

   public Name createName(String localName, String prefix, String uri) throws SOAPException {
      if (localName == null) {
         log.log(Level.SEVERE, "SAAJ0567.soap.null.input", new Object[]{"localName", "SOAPFactory.createName"});
         throw new SOAPException("Null localName argument passed to createName");
      } else {
         return NameImpl.create(localName, prefix, uri);
      }
   }

   public Name createName(String localName) throws SOAPException {
      if (localName == null) {
         log.log(Level.SEVERE, "SAAJ0567.soap.null.input", new Object[]{"localName", "SOAPFactory.createName"});
         throw new SOAPException("Null localName argument passed to createName");
      } else {
         return NameImpl.createFromUnqualifiedName(localName);
      }
   }

   public SOAPElement createElement(Element domElement) throws SOAPException {
      return domElement == null ? null : this.convertToSoapElement(domElement);
   }

   private SOAPElement convertToSoapElement(Element element) throws SOAPException {
      if (element instanceof SOAPElement) {
         return (SOAPElement)element;
      } else {
         SOAPElement copy = this.createElement(element.getLocalName(), element.getPrefix(), element.getNamespaceURI());
         Document ownerDoc = copy.getOwnerDocument();
         NamedNodeMap attrMap = element.getAttributes();

         for(int i = 0; i < attrMap.getLength(); ++i) {
            Attr nextAttr = (Attr)attrMap.item(i);
            Attr importedAttr = (Attr)ownerDoc.importNode(nextAttr, true);
            copy.setAttributeNodeNS(importedAttr);
         }

         NodeList nl = element.getChildNodes();

         for(int i = 0; i < nl.getLength(); ++i) {
            Node next = nl.item(i);
            Node imported = ownerDoc.importNode(next, true);
            copy.appendChild(imported);
         }

         return copy;
      }
   }

   public Detail createDetail() throws SOAPException {
      throw new UnsupportedOperationException();
   }

   public SOAPFault createFault(String reasonText, QName faultCode) throws SOAPException {
      throw new UnsupportedOperationException();
   }

   public SOAPFault createFault() throws SOAPException {
      throw new UnsupportedOperationException();
   }
}
