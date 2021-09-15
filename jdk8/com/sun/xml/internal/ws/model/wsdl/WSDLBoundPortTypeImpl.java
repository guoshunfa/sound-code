package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.util.QNameMap;
import com.sun.xml.internal.ws.util.exception.LocatableWebServiceException;
import java.util.Iterator;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;

public final class WSDLBoundPortTypeImpl extends AbstractFeaturedObjectImpl implements EditableWSDLBoundPortType {
   private final QName name;
   private final QName portTypeName;
   private EditableWSDLPortType portType;
   private BindingID bindingId;
   @NotNull
   private final EditableWSDLModel owner;
   private final QNameMap<EditableWSDLBoundOperation> bindingOperations = new QNameMap();
   private QNameMap<EditableWSDLBoundOperation> payloadMap;
   private EditableWSDLBoundOperation emptyPayloadOperation;
   private SOAPBinding.Style style;

   public WSDLBoundPortTypeImpl(XMLStreamReader xsr, @NotNull EditableWSDLModel owner, QName name, QName portTypeName) {
      super(xsr);
      this.style = SOAPBinding.Style.DOCUMENT;
      this.owner = owner;
      this.name = name;
      this.portTypeName = portTypeName;
      owner.addBinding(this);
   }

   public QName getName() {
      return this.name;
   }

   @NotNull
   public EditableWSDLModel getOwner() {
      return this.owner;
   }

   public EditableWSDLBoundOperation get(QName operationName) {
      return (EditableWSDLBoundOperation)this.bindingOperations.get(operationName);
   }

   public void put(QName opName, EditableWSDLBoundOperation ptOp) {
      this.bindingOperations.put(opName, ptOp);
   }

   public QName getPortTypeName() {
      return this.portTypeName;
   }

   public EditableWSDLPortType getPortType() {
      return this.portType;
   }

   public Iterable<EditableWSDLBoundOperation> getBindingOperations() {
      return this.bindingOperations.values();
   }

   public BindingID getBindingId() {
      return (BindingID)(this.bindingId == null ? BindingID.SOAP11_HTTP : this.bindingId);
   }

   public void setBindingId(BindingID bindingId) {
      this.bindingId = bindingId;
   }

   public void setStyle(SOAPBinding.Style style) {
      this.style = style;
   }

   public SOAPBinding.Style getStyle() {
      return this.style;
   }

   public boolean isRpcLit() {
      return SOAPBinding.Style.RPC == this.style;
   }

   public boolean isDoclit() {
      return SOAPBinding.Style.DOCUMENT == this.style;
   }

   public ParameterBinding getBinding(QName operation, String part, WebParam.Mode mode) {
      EditableWSDLBoundOperation op = this.get(operation);
      if (op == null) {
         return null;
      } else {
         return WebParam.Mode.IN != mode && WebParam.Mode.INOUT != mode ? op.getOutputBinding(part) : op.getInputBinding(part);
      }
   }

   public EditableWSDLBoundOperation getOperation(String namespaceUri, String localName) {
      return namespaceUri == null && localName == null ? this.emptyPayloadOperation : (EditableWSDLBoundOperation)this.payloadMap.get(namespaceUri == null ? "" : namespaceUri, localName);
   }

   public void freeze() {
      this.portType = this.owner.getPortType(this.portTypeName);
      if (this.portType == null) {
         throw new LocatableWebServiceException(ClientMessages.UNDEFINED_PORT_TYPE(this.portTypeName), new Locator[]{this.getLocation()});
      } else {
         this.portType.freeze();
         Iterator var1 = this.bindingOperations.values().iterator();

         while(var1.hasNext()) {
            EditableWSDLBoundOperation op = (EditableWSDLBoundOperation)var1.next();
            op.freeze(this.owner);
         }

         this.freezePayloadMap();
         this.owner.finalizeRpcLitBinding(this);
      }
   }

   private void freezePayloadMap() {
      Iterator var1;
      EditableWSDLBoundOperation op;
      if (this.style == SOAPBinding.Style.RPC) {
         this.payloadMap = new QNameMap();
         var1 = this.bindingOperations.values().iterator();

         while(var1.hasNext()) {
            op = (EditableWSDLBoundOperation)var1.next();
            this.payloadMap.put(op.getRequestPayloadName(), op);
         }
      } else {
         this.payloadMap = new QNameMap();
         var1 = this.bindingOperations.values().iterator();

         while(var1.hasNext()) {
            op = (EditableWSDLBoundOperation)var1.next();
            QName name = op.getRequestPayloadName();
            if (name == null) {
               this.emptyPayloadOperation = op;
            } else {
               this.payloadMap.put(name, op);
            }
         }
      }

   }
}
