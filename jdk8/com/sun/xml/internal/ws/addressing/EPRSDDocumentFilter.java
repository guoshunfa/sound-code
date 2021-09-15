package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.server.BoundEndpoint;
import com.sun.xml.internal.ws.api.server.Module;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.SDDocumentFilter;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.server.WSEndpointImpl;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class EPRSDDocumentFilter implements SDDocumentFilter {
   private final WSEndpointImpl<?> endpoint;
   List<BoundEndpoint> beList;

   public EPRSDDocumentFilter(@NotNull WSEndpointImpl<?> endpoint) {
      this.endpoint = endpoint;
   }

   @Nullable
   private WSEndpointImpl<?> getEndpoint(String serviceName, String portName) {
      if (serviceName != null && portName != null) {
         if (this.endpoint.getServiceName().getLocalPart().equals(serviceName) && this.endpoint.getPortName().getLocalPart().equals(portName)) {
            return this.endpoint;
         } else {
            if (this.beList == null) {
               Module module = (Module)this.endpoint.getContainer().getSPI(Module.class);
               if (module != null) {
                  this.beList = module.getBoundEndpoints();
               } else {
                  this.beList = Collections.emptyList();
               }
            }

            Iterator var6 = this.beList.iterator();

            WSEndpoint wse;
            do {
               if (!var6.hasNext()) {
                  return null;
               }

               BoundEndpoint be = (BoundEndpoint)var6.next();
               wse = be.getEndpoint();
            } while(!wse.getServiceName().getLocalPart().equals(serviceName) || !wse.getPortName().getLocalPart().equals(portName));

            return (WSEndpointImpl)wse;
         }
      } else {
         return null;
      }
   }

   public XMLStreamWriter filter(SDDocument doc, XMLStreamWriter w) throws XMLStreamException, IOException {
      return (XMLStreamWriter)(!doc.isWSDL() ? w : new XMLStreamWriterFilter(w) {
         private boolean eprExtnFilterON = false;
         private boolean portHasEPR = false;
         private int eprDepth = -1;
         private String serviceName = null;
         private boolean onService = false;
         private int serviceDepth = -1;
         private String portName = null;
         private boolean onPort = false;
         private int portDepth = -1;
         private String portAddress;
         private boolean onPortAddress = false;

         private void handleStartElement(String localName, String namespaceURI) throws XMLStreamException {
            this.resetOnElementFlags();
            if (this.serviceDepth >= 0) {
               ++this.serviceDepth;
            }

            if (this.portDepth >= 0) {
               ++this.portDepth;
            }

            if (this.eprDepth >= 0) {
               ++this.eprDepth;
            }

            if (namespaceURI.equals(WSDLConstants.QNAME_SERVICE.getNamespaceURI()) && localName.equals(WSDLConstants.QNAME_SERVICE.getLocalPart())) {
               this.onService = true;
               this.serviceDepth = 0;
            } else if (namespaceURI.equals(WSDLConstants.QNAME_PORT.getNamespaceURI()) && localName.equals(WSDLConstants.QNAME_PORT.getLocalPart())) {
               if (this.serviceDepth >= 1) {
                  this.onPort = true;
                  this.portDepth = 0;
               }
            } else if (namespaceURI.equals("http://www.w3.org/2005/08/addressing") && localName.equals("EndpointReference")) {
               if (this.serviceDepth >= 1 && this.portDepth >= 1) {
                  this.portHasEPR = true;
                  this.eprDepth = 0;
               }
            } else if ((namespaceURI.equals(WSDLConstants.NS_SOAP_BINDING_ADDRESS.getNamespaceURI()) || namespaceURI.equals(WSDLConstants.NS_SOAP12_BINDING_ADDRESS.getNamespaceURI())) && localName.equals("address") && this.portDepth == 1) {
               this.onPortAddress = true;
            }

            WSEndpoint endpoint = EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName);
            if (endpoint != null && this.eprDepth == 1 && !namespaceURI.equals("http://www.w3.org/2005/08/addressing")) {
               this.eprExtnFilterON = true;
            }

         }

         private void resetOnElementFlags() {
            if (this.onService) {
               this.onService = false;
            }

            if (this.onPort) {
               this.onPort = false;
            }

            if (this.onPortAddress) {
               this.onPortAddress = false;
            }

         }

         private void writeEPRExtensions(Collection<WSEndpointReference.EPRExtension> eprExtns) throws XMLStreamException {
            if (eprExtns != null) {
               Iterator var2 = eprExtns.iterator();

               while(var2.hasNext()) {
                  WSEndpointReference.EPRExtension e = (WSEndpointReference.EPRExtension)var2.next();
                  XMLStreamReaderToXMLStreamWriter c = new XMLStreamReaderToXMLStreamWriter();
                  XMLStreamReader r = e.readAsXMLStreamReader();
                  c.bridge(r, this.writer);
                  XMLStreamReaderFactory.recycle(r);
               }
            }

         }

         public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
            this.handleStartElement(localName, namespaceURI);
            if (!this.eprExtnFilterON) {
               super.writeStartElement(prefix, localName, namespaceURI);
            }

         }

         public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
            this.handleStartElement(localName, namespaceURI);
            if (!this.eprExtnFilterON) {
               super.writeStartElement(namespaceURI, localName);
            }

         }

         public void writeStartElement(String localName) throws XMLStreamException {
            if (!this.eprExtnFilterON) {
               super.writeStartElement(localName);
            }

         }

         private void handleEndElement() throws XMLStreamException {
            this.resetOnElementFlags();
            if (this.portDepth == 0 && !this.portHasEPR && EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName) != null) {
               this.writer.writeStartElement(AddressingVersion.W3C.getPrefix(), "EndpointReference", AddressingVersion.W3C.nsUri);
               this.writer.writeNamespace(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.nsUri);
               this.writer.writeStartElement(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.eprType.address, AddressingVersion.W3C.nsUri);
               this.writer.writeCharacters(this.portAddress);
               this.writer.writeEndElement();
               this.writeEPRExtensions(EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName).getEndpointReferenceExtensions());
               this.writer.writeEndElement();
            }

            if (this.eprDepth == 0) {
               if (this.portHasEPR && EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName) != null) {
                  this.writeEPRExtensions(EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName).getEndpointReferenceExtensions());
               }

               this.eprExtnFilterON = false;
            }

            if (this.serviceDepth >= 0) {
               --this.serviceDepth;
            }

            if (this.portDepth >= 0) {
               --this.portDepth;
            }

            if (this.eprDepth >= 0) {
               --this.eprDepth;
            }

            if (this.serviceDepth == -1) {
               this.serviceName = null;
            }

            if (this.portDepth == -1) {
               this.portHasEPR = false;
               this.portAddress = null;
               this.portName = null;
            }

         }

         public void writeEndElement() throws XMLStreamException {
            this.handleEndElement();
            if (!this.eprExtnFilterON) {
               super.writeEndElement();
            }

         }

         private void handleAttribute(String localName, String value) {
            if (localName.equals("name")) {
               if (this.onService) {
                  this.serviceName = value;
                  this.onService = false;
               } else if (this.onPort) {
                  this.portName = value;
                  this.onPort = false;
               }
            }

            if (localName.equals("location") && this.onPortAddress) {
               this.portAddress = value;
            }

         }

         public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
            this.handleAttribute(localName, value);
            if (!this.eprExtnFilterON) {
               super.writeAttribute(prefix, namespaceURI, localName, value);
            }

         }

         public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
            this.handleAttribute(localName, value);
            if (!this.eprExtnFilterON) {
               super.writeAttribute(namespaceURI, localName, value);
            }

         }

         public void writeAttribute(String localName, String value) throws XMLStreamException {
            this.handleAttribute(localName, value);
            if (!this.eprExtnFilterON) {
               super.writeAttribute(localName, value);
            }

         }

         public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
            if (!this.eprExtnFilterON) {
               super.writeEmptyElement(namespaceURI, localName);
            }

         }

         public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
            if (!this.eprExtnFilterON) {
               super.writeNamespace(prefix, namespaceURI);
            }

         }

         public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
            if (!this.eprExtnFilterON) {
               super.setNamespaceContext(context);
            }

         }

         public void setDefaultNamespace(String uri) throws XMLStreamException {
            if (!this.eprExtnFilterON) {
               super.setDefaultNamespace(uri);
            }

         }

         public void setPrefix(String prefix, String uri) throws XMLStreamException {
            if (!this.eprExtnFilterON) {
               super.setPrefix(prefix, uri);
            }

         }

         public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
            if (!this.eprExtnFilterON) {
               super.writeProcessingInstruction(target, data);
            }

         }

         public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
            if (!this.eprExtnFilterON) {
               super.writeEmptyElement(prefix, localName, namespaceURI);
            }

         }

         public void writeCData(String data) throws XMLStreamException {
            if (!this.eprExtnFilterON) {
               super.writeCData(data);
            }

         }

         public void writeCharacters(String text) throws XMLStreamException {
            if (!this.eprExtnFilterON) {
               super.writeCharacters(text);
            }

         }

         public void writeComment(String data) throws XMLStreamException {
            if (!this.eprExtnFilterON) {
               super.writeComment(data);
            }

         }

         public void writeDTD(String dtd) throws XMLStreamException {
            if (!this.eprExtnFilterON) {
               super.writeDTD(dtd);
            }

         }

         public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
            if (!this.eprExtnFilterON) {
               super.writeDefaultNamespace(namespaceURI);
            }

         }

         public void writeEmptyElement(String localName) throws XMLStreamException {
            if (!this.eprExtnFilterON) {
               super.writeEmptyElement(localName);
            }

         }

         public void writeEntityRef(String name) throws XMLStreamException {
            if (!this.eprExtnFilterON) {
               super.writeEntityRef(name);
            }

         }

         public void writeProcessingInstruction(String target) throws XMLStreamException {
            if (!this.eprExtnFilterON) {
               super.writeProcessingInstruction(target);
            }

         }

         public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
            if (!this.eprExtnFilterON) {
               super.writeCharacters(text, start, len);
            }

         }
      });
   }
}
