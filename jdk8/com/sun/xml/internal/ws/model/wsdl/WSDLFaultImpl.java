package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLFaultImpl extends AbstractExtensibleImpl implements EditableWSDLFault {
   private final String name;
   private final QName messageName;
   private EditableWSDLMessage message;
   private EditableWSDLOperation operation;
   private String action = "";
   private boolean defaultAction = true;

   public WSDLFaultImpl(XMLStreamReader xsr, String name, QName messageName, EditableWSDLOperation operation) {
      super(xsr);
      this.name = name;
      this.messageName = messageName;
      this.operation = operation;
   }

   public String getName() {
      return this.name;
   }

   public EditableWSDLMessage getMessage() {
      return this.message;
   }

   @NotNull
   public EditableWSDLOperation getOperation() {
      return this.operation;
   }

   @NotNull
   public QName getQName() {
      return new QName(this.operation.getName().getNamespaceURI(), this.name);
   }

   @NotNull
   public String getAction() {
      return this.action;
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

   public void freeze(EditableWSDLModel root) {
      this.message = root.getMessage(this.messageName);
   }
}
