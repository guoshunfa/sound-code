package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLBoundOperationImpl extends AbstractExtensibleImpl implements EditableWSDLBoundOperation {
   private final QName name;
   private final Map<String, ParameterBinding> inputParts;
   private final Map<String, ParameterBinding> outputParts;
   private final Map<String, ParameterBinding> faultParts;
   private final Map<String, String> inputMimeTypes;
   private final Map<String, String> outputMimeTypes;
   private final Map<String, String> faultMimeTypes;
   private boolean explicitInputSOAPBodyParts = false;
   private boolean explicitOutputSOAPBodyParts = false;
   private boolean explicitFaultSOAPBodyParts = false;
   private Boolean emptyInputBody;
   private Boolean emptyOutputBody;
   private Boolean emptyFaultBody;
   private final Map<String, EditableWSDLPart> inParts;
   private final Map<String, EditableWSDLPart> outParts;
   private final List<EditableWSDLBoundFault> wsdlBoundFaults;
   private EditableWSDLOperation operation;
   private String soapAction;
   private WSDLBoundOperation.ANONYMOUS anonymous;
   private final EditableWSDLBoundPortType owner;
   private SOAPBinding.Style style;
   private String reqNamespace;
   private String respNamespace;
   private QName requestPayloadName;
   private QName responsePayloadName;
   private boolean emptyRequestPayload;
   private boolean emptyResponsePayload;
   private Map<QName, ? extends EditableWSDLMessage> messages;

   public WSDLBoundOperationImpl(XMLStreamReader xsr, EditableWSDLBoundPortType owner, QName name) {
      super(xsr);
      this.style = SOAPBinding.Style.DOCUMENT;
      this.name = name;
      this.inputParts = new HashMap();
      this.outputParts = new HashMap();
      this.faultParts = new HashMap();
      this.inputMimeTypes = new HashMap();
      this.outputMimeTypes = new HashMap();
      this.faultMimeTypes = new HashMap();
      this.inParts = new HashMap();
      this.outParts = new HashMap();
      this.wsdlBoundFaults = new ArrayList();
      this.owner = owner;
   }

   public QName getName() {
      return this.name;
   }

   public String getSOAPAction() {
      return this.soapAction;
   }

   public void setSoapAction(String soapAction) {
      this.soapAction = soapAction != null ? soapAction : "";
   }

   public EditableWSDLPart getPart(String partName, WebParam.Mode mode) {
      if (mode == WebParam.Mode.IN) {
         return (EditableWSDLPart)this.inParts.get(partName);
      } else {
         return mode == WebParam.Mode.OUT ? (EditableWSDLPart)this.outParts.get(partName) : null;
      }
   }

   public void addPart(EditableWSDLPart part, WebParam.Mode mode) {
      if (mode == WebParam.Mode.IN) {
         this.inParts.put(part.getName(), part);
      } else if (mode == WebParam.Mode.OUT) {
         this.outParts.put(part.getName(), part);
      }

   }

   public Map<String, ParameterBinding> getInputParts() {
      return this.inputParts;
   }

   public Map<String, ParameterBinding> getOutputParts() {
      return this.outputParts;
   }

   public Map<String, ParameterBinding> getFaultParts() {
      return this.faultParts;
   }

   public Map<String, ? extends EditableWSDLPart> getInParts() {
      return Collections.unmodifiableMap(this.inParts);
   }

   public Map<String, ? extends EditableWSDLPart> getOutParts() {
      return Collections.unmodifiableMap(this.outParts);
   }

   @NotNull
   public List<? extends EditableWSDLBoundFault> getFaults() {
      return this.wsdlBoundFaults;
   }

   public void addFault(@NotNull EditableWSDLBoundFault fault) {
      this.wsdlBoundFaults.add(fault);
   }

   public ParameterBinding getInputBinding(String part) {
      if (this.emptyInputBody == null) {
         if (this.inputParts.get(" ") != null) {
            this.emptyInputBody = true;
         } else {
            this.emptyInputBody = false;
         }
      }

      ParameterBinding block = (ParameterBinding)this.inputParts.get(part);
      if (block == null) {
         return !this.explicitInputSOAPBodyParts && !this.emptyInputBody ? ParameterBinding.BODY : ParameterBinding.UNBOUND;
      } else {
         return block;
      }
   }

   public ParameterBinding getOutputBinding(String part) {
      if (this.emptyOutputBody == null) {
         if (this.outputParts.get(" ") != null) {
            this.emptyOutputBody = true;
         } else {
            this.emptyOutputBody = false;
         }
      }

      ParameterBinding block = (ParameterBinding)this.outputParts.get(part);
      if (block == null) {
         return !this.explicitOutputSOAPBodyParts && !this.emptyOutputBody ? ParameterBinding.BODY : ParameterBinding.UNBOUND;
      } else {
         return block;
      }
   }

   public ParameterBinding getFaultBinding(String part) {
      if (this.emptyFaultBody == null) {
         if (this.faultParts.get(" ") != null) {
            this.emptyFaultBody = true;
         } else {
            this.emptyFaultBody = false;
         }
      }

      ParameterBinding block = (ParameterBinding)this.faultParts.get(part);
      if (block == null) {
         return !this.explicitFaultSOAPBodyParts && !this.emptyFaultBody ? ParameterBinding.BODY : ParameterBinding.UNBOUND;
      } else {
         return block;
      }
   }

   public String getMimeTypeForInputPart(String part) {
      return (String)this.inputMimeTypes.get(part);
   }

   public String getMimeTypeForOutputPart(String part) {
      return (String)this.outputMimeTypes.get(part);
   }

   public String getMimeTypeForFaultPart(String part) {
      return (String)this.faultMimeTypes.get(part);
   }

   public EditableWSDLOperation getOperation() {
      return this.operation;
   }

   public EditableWSDLBoundPortType getBoundPortType() {
      return this.owner;
   }

   public void setInputExplicitBodyParts(boolean b) {
      this.explicitInputSOAPBodyParts = b;
   }

   public void setOutputExplicitBodyParts(boolean b) {
      this.explicitOutputSOAPBodyParts = b;
   }

   public void setFaultExplicitBodyParts(boolean b) {
      this.explicitFaultSOAPBodyParts = b;
   }

   public void setStyle(SOAPBinding.Style style) {
      this.style = style;
   }

   @Nullable
   public QName getRequestPayloadName() {
      if (this.emptyRequestPayload) {
         return null;
      } else if (this.requestPayloadName != null) {
         return this.requestPayloadName;
      } else if (this.style.equals(SOAPBinding.Style.RPC)) {
         String ns = this.getRequestNamespace() != null ? this.getRequestNamespace() : this.name.getNamespaceURI();
         this.requestPayloadName = new QName(ns, this.name.getLocalPart());
         return this.requestPayloadName;
      } else {
         QName inMsgName = this.operation.getInput().getMessage().getName();
         EditableWSDLMessage message = (EditableWSDLMessage)this.messages.get(inMsgName);
         Iterator var3 = message.parts().iterator();

         EditableWSDLPart part;
         ParameterBinding binding;
         do {
            if (!var3.hasNext()) {
               this.emptyRequestPayload = true;
               return null;
            }

            part = (EditableWSDLPart)var3.next();
            binding = this.getInputBinding(part.getName());
         } while(!binding.isBody());

         this.requestPayloadName = part.getDescriptor().name();
         return this.requestPayloadName;
      }
   }

   @Nullable
   public QName getResponsePayloadName() {
      if (this.emptyResponsePayload) {
         return null;
      } else if (this.responsePayloadName != null) {
         return this.responsePayloadName;
      } else if (this.style.equals(SOAPBinding.Style.RPC)) {
         String ns = this.getResponseNamespace() != null ? this.getResponseNamespace() : this.name.getNamespaceURI();
         this.responsePayloadName = new QName(ns, this.name.getLocalPart() + "Response");
         return this.responsePayloadName;
      } else {
         QName outMsgName = this.operation.getOutput().getMessage().getName();
         EditableWSDLMessage message = (EditableWSDLMessage)this.messages.get(outMsgName);
         Iterator var3 = message.parts().iterator();

         EditableWSDLPart part;
         ParameterBinding binding;
         do {
            if (!var3.hasNext()) {
               this.emptyResponsePayload = true;
               return null;
            }

            part = (EditableWSDLPart)var3.next();
            binding = this.getOutputBinding(part.getName());
         } while(!binding.isBody());

         this.responsePayloadName = part.getDescriptor().name();
         return this.responsePayloadName;
      }
   }

   public String getRequestNamespace() {
      return this.reqNamespace != null ? this.reqNamespace : this.name.getNamespaceURI();
   }

   public void setRequestNamespace(String ns) {
      this.reqNamespace = ns;
   }

   public String getResponseNamespace() {
      return this.respNamespace != null ? this.respNamespace : this.name.getNamespaceURI();
   }

   public void setResponseNamespace(String ns) {
      this.respNamespace = ns;
   }

   EditableWSDLBoundPortType getOwner() {
      return this.owner;
   }

   public void freeze(EditableWSDLModel parent) {
      this.messages = parent.getMessages();
      this.operation = this.owner.getPortType().get(this.name.getLocalPart());
      Iterator var2 = this.wsdlBoundFaults.iterator();

      while(var2.hasNext()) {
         EditableWSDLBoundFault bf = (EditableWSDLBoundFault)var2.next();
         bf.freeze(this);
      }

   }

   public void setAnonymous(WSDLBoundOperation.ANONYMOUS anonymous) {
      this.anonymous = anonymous;
   }

   public WSDLBoundOperation.ANONYMOUS getAnonymous() {
      return this.anonymous;
   }
}
