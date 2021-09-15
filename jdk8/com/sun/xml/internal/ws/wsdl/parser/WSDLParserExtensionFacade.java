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
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

final class WSDLParserExtensionFacade extends WSDLParserExtension {
   private final WSDLParserExtension[] extensions;

   WSDLParserExtensionFacade(WSDLParserExtension... extensions) {
      assert extensions != null;

      this.extensions = extensions;
   }

   public void start(WSDLParserExtensionContext context) {
      WSDLParserExtension[] var2 = this.extensions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WSDLParserExtension e = var2[var4];
         e.start(context);
      }

   }

   public boolean serviceElements(EditableWSDLService service, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         if (e.serviceElements(service, reader)) {
            return true;
         }
      }

      XMLStreamReaderUtil.skipElement(reader);
      return true;
   }

   public void serviceAttributes(EditableWSDLService service, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         e.serviceAttributes(service, reader);
      }

   }

   public boolean portElements(EditableWSDLPort port, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         if (e.portElements(port, reader)) {
            return true;
         }
      }

      if (this.isRequiredExtension(reader)) {
         port.addNotUnderstoodExtension(reader.getName(), this.getLocator(reader));
      }

      XMLStreamReaderUtil.skipElement(reader);
      return true;
   }

   public boolean portTypeOperationInput(EditableWSDLOperation op, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         e.portTypeOperationInput(op, reader);
      }

      return false;
   }

   public boolean portTypeOperationOutput(EditableWSDLOperation op, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         e.portTypeOperationOutput(op, reader);
      }

      return false;
   }

   public boolean portTypeOperationFault(EditableWSDLOperation op, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         e.portTypeOperationFault(op, reader);
      }

      return false;
   }

   public void portAttributes(EditableWSDLPort port, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         e.portAttributes(port, reader);
      }

   }

   public boolean definitionsElements(XMLStreamReader reader) {
      WSDLParserExtension[] var2 = this.extensions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WSDLParserExtension e = var2[var4];
         if (e.definitionsElements(reader)) {
            return true;
         }
      }

      XMLStreamReaderUtil.skipElement(reader);
      return true;
   }

   public boolean bindingElements(EditableWSDLBoundPortType binding, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         if (e.bindingElements(binding, reader)) {
            return true;
         }
      }

      if (this.isRequiredExtension(reader)) {
         binding.addNotUnderstoodExtension(reader.getName(), this.getLocator(reader));
      }

      XMLStreamReaderUtil.skipElement(reader);
      return true;
   }

   public void bindingAttributes(EditableWSDLBoundPortType binding, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         e.bindingAttributes(binding, reader);
      }

   }

   public boolean portTypeElements(EditableWSDLPortType portType, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         if (e.portTypeElements(portType, reader)) {
            return true;
         }
      }

      XMLStreamReaderUtil.skipElement(reader);
      return true;
   }

   public void portTypeAttributes(EditableWSDLPortType portType, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         e.portTypeAttributes(portType, reader);
      }

   }

   public boolean portTypeOperationElements(EditableWSDLOperation operation, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         if (e.portTypeOperationElements(operation, reader)) {
            return true;
         }
      }

      XMLStreamReaderUtil.skipElement(reader);
      return true;
   }

   public void portTypeOperationAttributes(EditableWSDLOperation operation, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         e.portTypeOperationAttributes(operation, reader);
      }

   }

   public boolean bindingOperationElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         if (e.bindingOperationElements(operation, reader)) {
            return true;
         }
      }

      XMLStreamReaderUtil.skipElement(reader);
      return true;
   }

   public void bindingOperationAttributes(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         e.bindingOperationAttributes(operation, reader);
      }

   }

   public boolean messageElements(EditableWSDLMessage msg, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         if (e.messageElements(msg, reader)) {
            return true;
         }
      }

      XMLStreamReaderUtil.skipElement(reader);
      return true;
   }

   public void messageAttributes(EditableWSDLMessage msg, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         e.messageAttributes(msg, reader);
      }

   }

   public boolean portTypeOperationInputElements(EditableWSDLInput input, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         if (e.portTypeOperationInputElements(input, reader)) {
            return true;
         }
      }

      XMLStreamReaderUtil.skipElement(reader);
      return true;
   }

   public void portTypeOperationInputAttributes(EditableWSDLInput input, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         e.portTypeOperationInputAttributes(input, reader);
      }

   }

   public boolean portTypeOperationOutputElements(EditableWSDLOutput output, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         if (e.portTypeOperationOutputElements(output, reader)) {
            return true;
         }
      }

      XMLStreamReaderUtil.skipElement(reader);
      return true;
   }

   public void portTypeOperationOutputAttributes(EditableWSDLOutput output, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         e.portTypeOperationOutputAttributes(output, reader);
      }

   }

   public boolean portTypeOperationFaultElements(EditableWSDLFault fault, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         if (e.portTypeOperationFaultElements(fault, reader)) {
            return true;
         }
      }

      XMLStreamReaderUtil.skipElement(reader);
      return true;
   }

   public void portTypeOperationFaultAttributes(EditableWSDLFault fault, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         e.portTypeOperationFaultAttributes(fault, reader);
      }

   }

   public boolean bindingOperationInputElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         if (e.bindingOperationInputElements(operation, reader)) {
            return true;
         }
      }

      XMLStreamReaderUtil.skipElement(reader);
      return true;
   }

   public void bindingOperationInputAttributes(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         e.bindingOperationInputAttributes(operation, reader);
      }

   }

   public boolean bindingOperationOutputElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         if (e.bindingOperationOutputElements(operation, reader)) {
            return true;
         }
      }

      XMLStreamReaderUtil.skipElement(reader);
      return true;
   }

   public void bindingOperationOutputAttributes(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         e.bindingOperationOutputAttributes(operation, reader);
      }

   }

   public boolean bindingOperationFaultElements(EditableWSDLBoundFault fault, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         if (e.bindingOperationFaultElements(fault, reader)) {
            return true;
         }
      }

      XMLStreamReaderUtil.skipElement(reader);
      return true;
   }

   public void bindingOperationFaultAttributes(EditableWSDLBoundFault fault, XMLStreamReader reader) {
      WSDLParserExtension[] var3 = this.extensions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WSDLParserExtension e = var3[var5];
         e.bindingOperationFaultAttributes(fault, reader);
      }

   }

   public void finished(WSDLParserExtensionContext context) {
      WSDLParserExtension[] var2 = this.extensions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WSDLParserExtension e = var2[var4];
         e.finished(context);
      }

   }

   public void postFinished(WSDLParserExtensionContext context) {
      WSDLParserExtension[] var2 = this.extensions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WSDLParserExtension e = var2[var4];
         e.postFinished(context);
      }

   }

   private boolean isRequiredExtension(XMLStreamReader reader) {
      String required = reader.getAttributeValue("http://schemas.xmlsoap.org/wsdl/", "required");
      return required != null ? Boolean.parseBoolean(required) : false;
   }

   private Locator getLocator(XMLStreamReader reader) {
      Location location = reader.getLocation();
      LocatorImpl loc = new LocatorImpl();
      loc.setSystemId(location.getSystemId());
      loc.setLineNumber(location.getLineNumber());
      return loc;
   }
}
