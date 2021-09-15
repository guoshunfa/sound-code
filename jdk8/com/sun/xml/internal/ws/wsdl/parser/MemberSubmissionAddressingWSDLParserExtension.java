package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFeaturedObject;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public class MemberSubmissionAddressingWSDLParserExtension extends W3CAddressingWSDLParserExtension {
   public boolean bindingElements(EditableWSDLBoundPortType binding, XMLStreamReader reader) {
      return this.addressibleElement(reader, binding);
   }

   public boolean portElements(EditableWSDLPort port, XMLStreamReader reader) {
      return this.addressibleElement(reader, port);
   }

   private boolean addressibleElement(XMLStreamReader reader, WSDLFeaturedObject binding) {
      QName ua = reader.getName();
      if (ua.equals(AddressingVersion.MEMBER.wsdlExtensionTag)) {
         String required = reader.getAttributeValue("http://schemas.xmlsoap.org/wsdl/", "required");
         binding.addFeature(new MemberSubmissionAddressingFeature(Boolean.parseBoolean(required)));
         XMLStreamReaderUtil.skipElement(reader);
         return true;
      } else {
         return false;
      }
   }

   public boolean bindingOperationElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      return false;
   }

   protected void patchAnonymousDefault(EditableWSDLBoundPortType binding) {
   }

   protected String getNamespaceURI() {
      return AddressingVersion.MEMBER.wsdlNsUri;
   }

   protected QName getWsdlActionTag() {
      return AddressingVersion.MEMBER.wsdlActionTag;
   }
}
