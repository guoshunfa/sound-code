package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.addressing.W3CAddressingConstants;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

public final class WSDLPatcher extends XMLStreamReaderToXMLStreamWriter {
   private static final String NS_XSD = "http://www.w3.org/2001/XMLSchema";
   private static final QName SCHEMA_INCLUDE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "include");
   private static final QName SCHEMA_IMPORT_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "import");
   private static final QName SCHEMA_REDEFINE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "redefine");
   private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.wsdl.patcher");
   private final DocumentLocationResolver docResolver;
   private final PortAddressResolver portAddressResolver;
   private String targetNamespace;
   private QName serviceName;
   private QName portName;
   private String portAddress;
   private boolean inEpr;
   private boolean inEprAddress;

   public WSDLPatcher(@NotNull PortAddressResolver portAddressResolver, @NotNull DocumentLocationResolver docResolver) {
      this.portAddressResolver = portAddressResolver;
      this.docResolver = docResolver;
   }

   protected void handleAttribute(int i) throws XMLStreamException {
      QName name = this.in.getName();
      String attLocalName = this.in.getAttributeLocalName(i);
      String value;
      if ((!name.equals(SCHEMA_INCLUDE_QNAME) || !attLocalName.equals("schemaLocation")) && (!name.equals(SCHEMA_IMPORT_QNAME) || !attLocalName.equals("schemaLocation")) && (!name.equals(SCHEMA_REDEFINE_QNAME) || !attLocalName.equals("schemaLocation")) && (!name.equals(WSDLConstants.QNAME_IMPORT) || !attLocalName.equals("location"))) {
         if ((name.equals(WSDLConstants.NS_SOAP_BINDING_ADDRESS) || name.equals(WSDLConstants.NS_SOAP12_BINDING_ADDRESS)) && attLocalName.equals("location")) {
            this.portAddress = this.in.getAttributeValue(i);
            value = this.getAddressLocation();
            if (value != null) {
               logger.fine("Service:" + this.serviceName + " port:" + this.portName + " current address " + this.portAddress + " Patching it with " + value);
               this.writeAttribute(i, value);
               return;
            }
         }

         super.handleAttribute(i);
      } else {
         value = this.in.getAttributeValue(i);
         String actualPath = this.getPatchedImportLocation(value);
         if (actualPath != null) {
            logger.fine("Fixing the relative location:" + value + " with absolute location:" + actualPath);
            this.writeAttribute(i, actualPath);
         }
      }
   }

   private void writeAttribute(int i, String value) throws XMLStreamException {
      String nsUri = this.in.getAttributeNamespace(i);
      if (nsUri != null) {
         this.out.writeAttribute(this.in.getAttributePrefix(i), nsUri, this.in.getAttributeLocalName(i), value);
      } else {
         this.out.writeAttribute(this.in.getAttributeLocalName(i), value);
      }

   }

   protected void handleStartElement() throws XMLStreamException {
      QName name = this.in.getName();
      String value;
      if (name.equals(WSDLConstants.QNAME_DEFINITIONS)) {
         value = this.in.getAttributeValue((String)null, "targetNamespace");
         if (value != null) {
            this.targetNamespace = value;
         }
      } else if (name.equals(WSDLConstants.QNAME_SERVICE)) {
         value = this.in.getAttributeValue((String)null, "name");
         if (value != null) {
            this.serviceName = new QName(this.targetNamespace, value);
         }
      } else if (name.equals(WSDLConstants.QNAME_PORT)) {
         value = this.in.getAttributeValue((String)null, "name");
         if (value != null) {
            this.portName = new QName(this.targetNamespace, value);
         }
      } else if (!name.equals(W3CAddressingConstants.WSA_EPR_QNAME) && !name.equals(MemberSubmissionAddressingConstants.WSA_EPR_QNAME)) {
         if ((name.equals(W3CAddressingConstants.WSA_ADDRESS_QNAME) || name.equals(MemberSubmissionAddressingConstants.WSA_ADDRESS_QNAME)) && this.inEpr) {
            this.inEprAddress = true;
         }
      } else if (this.serviceName != null && this.portName != null) {
         this.inEpr = true;
      }

      super.handleStartElement();
   }

   protected void handleEndElement() throws XMLStreamException {
      QName name = this.in.getName();
      if (name.equals(WSDLConstants.QNAME_SERVICE)) {
         this.serviceName = null;
      } else if (name.equals(WSDLConstants.QNAME_PORT)) {
         this.portName = null;
      } else if (!name.equals(W3CAddressingConstants.WSA_EPR_QNAME) && !name.equals(MemberSubmissionAddressingConstants.WSA_EPR_QNAME)) {
         if ((name.equals(W3CAddressingConstants.WSA_ADDRESS_QNAME) || name.equals(MemberSubmissionAddressingConstants.WSA_ADDRESS_QNAME)) && this.inEprAddress) {
            String value = this.getAddressLocation();
            if (value != null) {
               logger.fine("Fixing EPR Address for service:" + this.serviceName + " port:" + this.portName + " address with " + value);
               this.out.writeCharacters(value);
            }

            this.inEprAddress = false;
         }
      } else if (this.inEpr) {
         this.inEpr = false;
      }

      super.handleEndElement();
   }

   protected void handleCharacters() throws XMLStreamException {
      if (this.inEprAddress) {
         String value = this.getAddressLocation();
         if (value != null) {
            return;
         }
      }

      super.handleCharacters();
   }

   @Nullable
   private String getPatchedImportLocation(String relPath) {
      return this.docResolver.getLocationFor((String)null, relPath);
   }

   private String getAddressLocation() {
      return this.portAddressResolver != null && this.portName != null ? this.portAddressResolver.getAddressFor(this.serviceName, this.portName.getLocalPart(), this.portAddress) : null;
   }
}
