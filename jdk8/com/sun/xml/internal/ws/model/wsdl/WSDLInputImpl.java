package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLInputImpl extends AbstractExtensibleImpl implements EditableWSDLInput {
   private String name;
   private QName messageName;
   private EditableWSDLOperation operation;
   private EditableWSDLMessage message;
   private String action;
   private boolean defaultAction = true;

   public WSDLInputImpl(XMLStreamReader xsr, String name, QName messageName, EditableWSDLOperation operation) {
      super(xsr);
      this.name = name;
      this.messageName = messageName;
      this.operation = operation;
   }

   public String getName() {
      if (this.name != null) {
         return this.name;
      } else {
         return this.operation.isOneWay() ? this.operation.getName().getLocalPart() : this.operation.getName().getLocalPart() + "Request";
      }
   }

   public EditableWSDLMessage getMessage() {
      return this.message;
   }

   public String getAction() {
      return this.action;
   }

   @NotNull
   public EditableWSDLOperation getOperation() {
      return this.operation;
   }

   public QName getQName() {
      return new QName(this.operation.getName().getNamespaceURI(), this.getName());
   }

   public void setAction(String action) {
      this.action = action;
   }

   public boolean isDefaultAction() {
      return this.defaultAction;
   }

   public void setDefaultAction(boolean defaultAction) {
      this.defaultAction = defaultAction;
   }

   public void freeze(EditableWSDLModel parent) {
      this.message = parent.getMessage(this.messageName);
   }
}
