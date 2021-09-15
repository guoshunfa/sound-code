package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtensionContext;
import javax.xml.stream.XMLStreamReader;

class DelegatingParserExtension extends WSDLParserExtension {
   protected final WSDLParserExtension core;

   public DelegatingParserExtension(WSDLParserExtension core) {
      this.core = core;
   }

   public void start(WSDLParserExtensionContext context) {
      this.core.start(context);
   }

   public void serviceAttributes(EditableWSDLService service, XMLStreamReader reader) {
      this.core.serviceAttributes(service, reader);
   }

   public boolean serviceElements(EditableWSDLService service, XMLStreamReader reader) {
      return this.core.serviceElements(service, reader);
   }

   public void portAttributes(EditableWSDLPort port, XMLStreamReader reader) {
      this.core.portAttributes(port, reader);
   }

   public boolean portElements(EditableWSDLPort port, XMLStreamReader reader) {
      return this.core.portElements(port, reader);
   }

   public boolean portTypeOperationInput(EditableWSDLOperation op, XMLStreamReader reader) {
      return this.core.portTypeOperationInput(op, reader);
   }

   public boolean portTypeOperationOutput(EditableWSDLOperation op, XMLStreamReader reader) {
      return this.core.portTypeOperationOutput(op, reader);
   }

   public boolean portTypeOperationFault(EditableWSDLOperation op, XMLStreamReader reader) {
      return this.core.portTypeOperationFault(op, reader);
   }

   public boolean definitionsElements(XMLStreamReader reader) {
      return this.core.definitionsElements(reader);
   }

   public boolean bindingElements(EditableWSDLBoundPortType binding, XMLStreamReader reader) {
      return this.core.bindingElements(binding, reader);
   }

   public void bindingAttributes(EditableWSDLBoundPortType binding, XMLStreamReader reader) {
      this.core.bindingAttributes(binding, reader);
   }

   public boolean portTypeElements(EditableWSDLPortType portType, XMLStreamReader reader) {
      return this.core.portTypeElements(portType, reader);
   }

   public void portTypeAttributes(EditableWSDLPortType portType, XMLStreamReader reader) {
      this.core.portTypeAttributes(portType, reader);
   }

   public boolean portTypeOperationElements(EditableWSDLOperation operation, XMLStreamReader reader) {
      return this.core.portTypeOperationElements(operation, reader);
   }

   public void portTypeOperationAttributes(EditableWSDLOperation operation, XMLStreamReader reader) {
      this.core.portTypeOperationAttributes(operation, reader);
   }

   public boolean bindingOperationElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      return this.core.bindingOperationElements(operation, reader);
   }

   public void bindingOperationAttributes(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      this.core.bindingOperationAttributes(operation, reader);
   }

   public boolean messageElements(EditableWSDLMessage msg, XMLStreamReader reader) {
      return this.core.messageElements(msg, reader);
   }

   public void messageAttributes(EditableWSDLMessage msg, XMLStreamReader reader) {
      this.core.messageAttributes(msg, reader);
   }

   public boolean portTypeOperationInputElements(EditableWSDLInput input, XMLStreamReader reader) {
      return this.core.portTypeOperationInputElements(input, reader);
   }

   public void portTypeOperationInputAttributes(EditableWSDLInput input, XMLStreamReader reader) {
      this.core.portTypeOperationInputAttributes(input, reader);
   }

   public boolean portTypeOperationOutputElements(EditableWSDLOutput output, XMLStreamReader reader) {
      return this.core.portTypeOperationOutputElements(output, reader);
   }

   public void portTypeOperationOutputAttributes(EditableWSDLOutput output, XMLStreamReader reader) {
      this.core.portTypeOperationOutputAttributes(output, reader);
   }

   public boolean portTypeOperationFaultElements(EditableWSDLFault fault, XMLStreamReader reader) {
      return this.core.portTypeOperationFaultElements(fault, reader);
   }

   public void portTypeOperationFaultAttributes(EditableWSDLFault fault, XMLStreamReader reader) {
      this.core.portTypeOperationFaultAttributes(fault, reader);
   }

   public boolean bindingOperationInputElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      return this.core.bindingOperationInputElements(operation, reader);
   }

   public void bindingOperationInputAttributes(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      this.core.bindingOperationInputAttributes(operation, reader);
   }

   public boolean bindingOperationOutputElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      return this.core.bindingOperationOutputElements(operation, reader);
   }

   public void bindingOperationOutputAttributes(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      this.core.bindingOperationOutputAttributes(operation, reader);
   }

   public boolean bindingOperationFaultElements(EditableWSDLBoundFault fault, XMLStreamReader reader) {
      return this.core.bindingOperationFaultElements(fault, reader);
   }

   public void bindingOperationFaultAttributes(EditableWSDLBoundFault fault, XMLStreamReader reader) {
      this.core.bindingOperationFaultAttributes(fault, reader);
   }

   public void finished(WSDLParserExtensionContext context) {
      this.core.finished(context);
   }

   public void postFinished(WSDLParserExtensionContext context) {
      this.core.postFinished(context);
   }
}
