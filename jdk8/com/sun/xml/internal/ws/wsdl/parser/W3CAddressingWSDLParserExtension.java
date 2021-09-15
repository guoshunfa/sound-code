package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFeaturedObject;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtensionContext;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.AddressingFeature;

public class W3CAddressingWSDLParserExtension extends WSDLParserExtension {
   protected static final String COLON_DELIMITER = ":";
   protected static final String SLASH_DELIMITER = "/";

   public boolean bindingElements(EditableWSDLBoundPortType binding, XMLStreamReader reader) {
      return this.addressibleElement(reader, binding);
   }

   public boolean portElements(EditableWSDLPort port, XMLStreamReader reader) {
      return this.addressibleElement(reader, port);
   }

   private boolean addressibleElement(XMLStreamReader reader, WSDLFeaturedObject binding) {
      QName ua = reader.getName();
      if (ua.equals(AddressingVersion.W3C.wsdlExtensionTag)) {
         String required = reader.getAttributeValue("http://schemas.xmlsoap.org/wsdl/", "required");
         binding.addFeature(new AddressingFeature(true, Boolean.parseBoolean(required)));
         XMLStreamReaderUtil.skipElement(reader);
         return true;
      } else {
         return false;
      }
   }

   public boolean bindingOperationElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      EditableWSDLBoundOperation edit = operation;
      QName anon = reader.getName();
      if (anon.equals(AddressingVersion.W3C.wsdlAnonymousTag)) {
         try {
            String value = reader.getElementText();
            if (value != null && !value.trim().equals("")) {
               if (value.equals("optional")) {
                  edit.setAnonymous(WSDLBoundOperation.ANONYMOUS.optional);
               } else if (value.equals("required")) {
                  edit.setAnonymous(WSDLBoundOperation.ANONYMOUS.required);
               } else {
                  if (!value.equals("prohibited")) {
                     throw new WebServiceException("wsaw:Anonymous value \"" + value + "\" not understood.");
                  }

                  edit.setAnonymous(WSDLBoundOperation.ANONYMOUS.prohibited);
               }

               return true;
            } else {
               throw new WebServiceException("Null values not permitted in wsaw:Anonymous.");
            }
         } catch (XMLStreamException var6) {
            throw new WebServiceException(var6);
         }
      } else {
         return false;
      }
   }

   public void portTypeOperationInputAttributes(EditableWSDLInput input, XMLStreamReader reader) {
      String action = ParserUtil.getAttribute(reader, this.getWsdlActionTag());
      if (action != null) {
         input.setAction(action);
         input.setDefaultAction(false);
      }

   }

   public void portTypeOperationOutputAttributes(EditableWSDLOutput output, XMLStreamReader reader) {
      String action = ParserUtil.getAttribute(reader, this.getWsdlActionTag());
      if (action != null) {
         output.setAction(action);
         output.setDefaultAction(false);
      }

   }

   public void portTypeOperationFaultAttributes(EditableWSDLFault fault, XMLStreamReader reader) {
      String action = ParserUtil.getAttribute(reader, this.getWsdlActionTag());
      if (action != null) {
         fault.setAction(action);
         fault.setDefaultAction(false);
      }

   }

   public void finished(WSDLParserExtensionContext context) {
      EditableWSDLModel model = context.getWSDLModel();
      Iterator var3 = model.getServices().values().iterator();

      while(var3.hasNext()) {
         EditableWSDLService service = (EditableWSDLService)var3.next();
         Iterator var5 = service.getPorts().iterator();

         while(var5.hasNext()) {
            EditableWSDLPort port = (EditableWSDLPort)var5.next();
            EditableWSDLBoundPortType binding = port.getBinding();
            this.populateActions(binding);
            this.patchAnonymousDefault(binding);
         }
      }

   }

   protected String getNamespaceURI() {
      return AddressingVersion.W3C.wsdlNsUri;
   }

   protected QName getWsdlActionTag() {
      return AddressingVersion.W3C.wsdlActionTag;
   }

   private void populateActions(EditableWSDLBoundPortType binding) {
      EditableWSDLPortType porttype = binding.getPortType();
      Iterator var3 = porttype.getOperations().iterator();

      while(true) {
         label65:
         while(var3.hasNext()) {
            EditableWSDLOperation o = (EditableWSDLOperation)var3.next();
            EditableWSDLBoundOperation wboi = binding.get(o.getName());
            if (wboi == null) {
               o.getInput().setAction(this.defaultInputAction(o));
            } else {
               String soapAction = wboi.getSOAPAction();
               if (o.getInput().getAction() == null || o.getInput().getAction().equals("")) {
                  if (soapAction != null && !soapAction.equals("")) {
                     o.getInput().setAction(soapAction);
                  } else {
                     o.getInput().setAction(this.defaultInputAction(o));
                  }
               }

               if (o.getOutput() != null) {
                  if (o.getOutput().getAction() == null || o.getOutput().getAction().equals("")) {
                     o.getOutput().setAction(this.defaultOutputAction(o));
                  }

                  if (o.getFaults() != null && o.getFaults().iterator().hasNext()) {
                     Iterator var7 = o.getFaults().iterator();

                     while(true) {
                        EditableWSDLFault f;
                        do {
                           if (!var7.hasNext()) {
                              continue label65;
                           }

                           f = (EditableWSDLFault)var7.next();
                        } while(f.getAction() != null && !f.getAction().equals(""));

                        f.setAction(this.defaultFaultAction(f.getName(), o));
                     }
                  }
               }
            }
         }

         return;
      }
   }

   protected void patchAnonymousDefault(EditableWSDLBoundPortType binding) {
      Iterator var2 = binding.getBindingOperations().iterator();

      while(var2.hasNext()) {
         EditableWSDLBoundOperation wbo = (EditableWSDLBoundOperation)var2.next();
         if (wbo.getAnonymous() == null) {
            wbo.setAnonymous(WSDLBoundOperation.ANONYMOUS.optional);
         }
      }

   }

   private String defaultInputAction(EditableWSDLOperation o) {
      return buildAction(o.getInput().getName(), o, false);
   }

   private String defaultOutputAction(EditableWSDLOperation o) {
      return buildAction(o.getOutput().getName(), o, false);
   }

   private String defaultFaultAction(String name, EditableWSDLOperation o) {
      return buildAction(name, o, true);
   }

   protected static final String buildAction(String name, EditableWSDLOperation o, boolean isFault) {
      String tns = o.getName().getNamespaceURI();
      String delim = "/";
      if (!tns.startsWith("http")) {
         delim = ":";
      }

      if (tns.endsWith(delim)) {
         tns = tns.substring(0, tns.length() - 1);
      }

      if (o.getPortTypeName() == null) {
         throw new WebServiceException("\"" + o.getName() + "\" operation's owning portType name is null.");
      } else {
         return tns + delim + o.getPortTypeName().getLocalPart() + delim + (isFault ? o.getName().getLocalPart() + delim + "Fault" + delim : "") + name;
      }
   }
}
