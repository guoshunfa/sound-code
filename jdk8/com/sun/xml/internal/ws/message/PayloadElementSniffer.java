package com.sun.xml.internal.ws.message;

import com.sun.xml.internal.ws.encoding.soap.SOAP12Constants;
import com.sun.xml.internal.ws.encoding.soap.SOAPConstants;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PayloadElementSniffer extends DefaultHandler {
   private boolean bodyStarted;
   private QName payloadQName;

   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (this.bodyStarted) {
         this.payloadQName = new QName(uri, localName);
         throw new SAXException("Payload element found, interrupting the parsing process.");
      } else {
         if (this.equalsQName(uri, localName, SOAPConstants.QNAME_SOAP_BODY) || this.equalsQName(uri, localName, SOAP12Constants.QNAME_SOAP_BODY)) {
            this.bodyStarted = true;
         }

      }
   }

   private boolean equalsQName(String uri, String localName, QName qname) {
      return qname.getLocalPart().equals(localName) && qname.getNamespaceURI().equals(uri);
   }

   public QName getPayloadQName() {
      return this.payloadQName;
   }
}
