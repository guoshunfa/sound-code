package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public class WSDLBoundFaultImpl extends AbstractExtensibleImpl implements EditableWSDLBoundFault {
   private final String name;
   private EditableWSDLFault fault;
   private EditableWSDLBoundOperation owner;

   public WSDLBoundFaultImpl(XMLStreamReader xsr, String name, EditableWSDLBoundOperation owner) {
      super(xsr);
      this.name = name;
      this.owner = owner;
   }

   @NotNull
   public String getName() {
      return this.name;
   }

   public QName getQName() {
      return this.owner.getOperation() != null ? new QName(this.owner.getOperation().getName().getNamespaceURI(), this.name) : null;
   }

   public EditableWSDLFault getFault() {
      return this.fault;
   }

   @NotNull
   public EditableWSDLBoundOperation getBoundOperation() {
      return this.owner;
   }

   public void freeze(EditableWSDLBoundOperation root) {
      assert root != null;

      EditableWSDLOperation op = root.getOperation();
      if (op != null) {
         Iterator var3 = op.getFaults().iterator();

         while(var3.hasNext()) {
            EditableWSDLFault f = (EditableWSDLFault)var3.next();
            if (f.getName().equals(this.name)) {
               this.fault = f;
               break;
            }
         }
      }

   }
}
