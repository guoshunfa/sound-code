package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public class W3CAddressingMetadataWSDLParserExtension extends W3CAddressingWSDLParserExtension {
   String METADATA_WSDL_EXTN_NS = "http://www.w3.org/2007/05/addressing/metadata";
   QName METADATA_WSDL_ACTION_TAG;

   public W3CAddressingMetadataWSDLParserExtension() {
      this.METADATA_WSDL_ACTION_TAG = new QName(this.METADATA_WSDL_EXTN_NS, "Action", "wsam");
   }

   public boolean bindingElements(EditableWSDLBoundPortType binding, XMLStreamReader reader) {
      return false;
   }

   public boolean portElements(EditableWSDLPort port, XMLStreamReader reader) {
      return false;
   }

   public boolean bindingOperationElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      return false;
   }

   protected void patchAnonymousDefault(EditableWSDLBoundPortType binding) {
   }

   protected String getNamespaceURI() {
      return this.METADATA_WSDL_EXTN_NS;
   }

   protected QName getWsdlActionTag() {
      return this.METADATA_WSDL_ACTION_TAG;
   }
}
