package com.sun.xml.internal.ws.addressing;

import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class WSEPRExtension extends WSEndpointReference.EPRExtension {
   XMLStreamBuffer xsb;
   final QName qname;

   public WSEPRExtension(XMLStreamBuffer xsb, QName qname) {
      this.xsb = xsb;
      this.qname = qname;
   }

   public XMLStreamReader readAsXMLStreamReader() throws XMLStreamException {
      return this.xsb.readAsXMLStreamReader();
   }

   public QName getQName() {
      return this.qname;
   }
}
