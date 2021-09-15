package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLPortTypeImpl extends AbstractExtensibleImpl implements EditableWSDLPortType {
   private QName name;
   private final Map<String, EditableWSDLOperation> portTypeOperations;
   private EditableWSDLModel owner;

   public WSDLPortTypeImpl(XMLStreamReader xsr, EditableWSDLModel owner, QName name) {
      super(xsr);
      this.name = name;
      this.owner = owner;
      this.portTypeOperations = new Hashtable();
   }

   public QName getName() {
      return this.name;
   }

   public EditableWSDLOperation get(String operationName) {
      return (EditableWSDLOperation)this.portTypeOperations.get(operationName);
   }

   public Iterable<EditableWSDLOperation> getOperations() {
      return this.portTypeOperations.values();
   }

   public void put(String opName, EditableWSDLOperation ptOp) {
      this.portTypeOperations.put(opName, ptOp);
   }

   EditableWSDLModel getOwner() {
      return this.owner;
   }

   public void freeze() {
      Iterator var1 = this.portTypeOperations.values().iterator();

      while(var1.hasNext()) {
         EditableWSDLOperation op = (EditableWSDLOperation)var1.next();
         op.freeze(this.owner);
      }

   }
}
