package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPPartImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.Body1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.Detail1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.Envelope1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.Fault1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.FaultElement1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.Header1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPPart1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.Body1_2Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.Detail1_2Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.Envelope1_2Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.Fault1_2Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.Header1_2Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.SOAPPart1_2Impl;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFault;

public class ElementFactory {
   public static SOAPElement createElement(SOAPDocumentImpl ownerDocument, Name name) {
      return createElement(ownerDocument, name.getLocalName(), name.getPrefix(), name.getURI());
   }

   public static SOAPElement createElement(SOAPDocumentImpl ownerDocument, QName name) {
      return createElement(ownerDocument, name.getLocalPart(), name.getPrefix(), name.getNamespaceURI());
   }

   public static SOAPElement createElement(SOAPDocumentImpl ownerDocument, String localName, String prefix, String namespaceUri) {
      if (ownerDocument == null) {
         if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
            ownerDocument = (new SOAPPart1_1Impl()).getDocument();
         } else if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceUri)) {
            ownerDocument = (new SOAPPart1_2Impl()).getDocument();
         } else {
            ownerDocument = new SOAPDocumentImpl((SOAPPartImpl)null);
         }
      }

      SOAPElement newElement = createNamedElement(ownerDocument, localName, prefix, namespaceUri);
      return (SOAPElement)(newElement != null ? newElement : new ElementImpl(ownerDocument, namespaceUri, NameImpl.createQName(prefix, localName)));
   }

   public static SOAPElement createNamedElement(SOAPDocumentImpl ownerDocument, String localName, String prefix, String namespaceUri) {
      if (prefix == null) {
         prefix = "SOAP-ENV";
      }

      if (localName.equalsIgnoreCase("Envelope")) {
         if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
            return new Envelope1_1Impl(ownerDocument, prefix);
         }

         if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceUri)) {
            return new Envelope1_2Impl(ownerDocument, prefix);
         }
      }

      if (localName.equalsIgnoreCase("Body")) {
         if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
            return new Body1_1Impl(ownerDocument, prefix);
         }

         if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceUri)) {
            return new Body1_2Impl(ownerDocument, prefix);
         }
      }

      if (localName.equalsIgnoreCase("Header")) {
         if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
            return new Header1_1Impl(ownerDocument, prefix);
         }

         if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceUri)) {
            return new Header1_2Impl(ownerDocument, prefix);
         }
      }

      if (localName.equalsIgnoreCase("Fault")) {
         SOAPFault fault = null;
         if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
            fault = new Fault1_1Impl(ownerDocument, prefix);
         } else if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceUri)) {
            fault = new Fault1_2Impl(ownerDocument, prefix);
         }

         if (fault != null) {
            return (SOAPElement)fault;
         }
      }

      if (localName.equalsIgnoreCase("Detail")) {
         if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
            return new Detail1_1Impl(ownerDocument, prefix);
         }

         if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceUri)) {
            return new Detail1_2Impl(ownerDocument, prefix);
         }
      }

      return (localName.equalsIgnoreCase("faultcode") || localName.equalsIgnoreCase("faultstring") || localName.equalsIgnoreCase("faultactor")) && "http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri) ? new FaultElement1_1Impl(ownerDocument, localName, prefix) : null;
   }

   protected static void invalidCreate(String msg) {
      throw new TreeException(msg);
   }
}
