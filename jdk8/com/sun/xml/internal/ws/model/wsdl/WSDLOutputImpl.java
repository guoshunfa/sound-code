package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLOutputImpl extends AbstractExtensibleImpl implements EditableWSDLOutput {
   private String name;
   private QName messageName;
   private EditableWSDLOperation operation;
   private EditableWSDLMessage message;
   private String action;
   private boolean defaultAction = true;

   public WSDLOutputImpl(XMLStreamReader xsr, String name, QName messageName, EditableWSDLOperation operation) {
      super(xsr);
      this.name = name;
      this.messageName = messageName;
      this.operation = operation;
   }

   public String getName() {
      return this.name == null ? this.operation.getName().getLocalPart() + "Response" : this.name;
   }

   public EditableWSDLMessage getMessage() {
      return this.message;
   }

   public String getAction() {
      return this.action;
   }

   public boolean isDefaultAction() {
      return this.defaultAction;
   }

   public void setDefaultAction(boolean defaultAction) {
      this.defaultAction = defaultAction;
   }

   @NotNull
   public EditableWSDLOperation getOperation() {
      return this.operation;
   }

   @NotNull
   public QName getQName() {
      return new QName(this.operation.getName().getNamespaceURI(), this.getName());
   }

   public void setAction(String action) {
      this.action = action;
   }

   public void freeze(EditableWSDLModel root) {
      this.message = root.getMessage(this.messageName);
   }
}
