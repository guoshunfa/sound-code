package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.util.QNameMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLOperationImpl extends AbstractExtensibleImpl implements EditableWSDLOperation {
   private final QName name;
   private String parameterOrder;
   private EditableWSDLInput input;
   private EditableWSDLOutput output;
   private final List<EditableWSDLFault> faults;
   private final QNameMap<EditableWSDLFault> faultMap;
   protected Iterable<EditableWSDLMessage> messages;
   private final EditableWSDLPortType owner;

   public WSDLOperationImpl(XMLStreamReader xsr, EditableWSDLPortType owner, QName name) {
      super(xsr);
      this.name = name;
      this.faults = new ArrayList();
      this.faultMap = new QNameMap();
      this.owner = owner;
   }

   public QName getName() {
      return this.name;
   }

   public String getParameterOrder() {
      return this.parameterOrder;
   }

   public void setParameterOrder(String parameterOrder) {
      this.parameterOrder = parameterOrder;
   }

   public EditableWSDLInput getInput() {
      return this.input;
   }

   public void setInput(EditableWSDLInput input) {
      this.input = input;
   }

   public EditableWSDLOutput getOutput() {
      return this.output;
   }

   public boolean isOneWay() {
      return this.output == null;
   }

   public void setOutput(EditableWSDLOutput output) {
      this.output = output;
   }

   public Iterable<EditableWSDLFault> getFaults() {
      return this.faults;
   }

   public EditableWSDLFault getFault(QName faultDetailName) {
      EditableWSDLFault fault = (EditableWSDLFault)this.faultMap.get(faultDetailName);
      if (fault != null) {
         return fault;
      } else {
         Iterator var3 = this.faults.iterator();

         EditableWSDLFault fi;
         EditableWSDLPart part;
         do {
            if (!var3.hasNext()) {
               return null;
            }

            fi = (EditableWSDLFault)var3.next();

            assert fi.getMessage().parts().iterator().hasNext();

            part = (EditableWSDLPart)fi.getMessage().parts().iterator().next();
         } while(!part.getDescriptor().name().equals(faultDetailName));

         this.faultMap.put(faultDetailName, fi);
         return fi;
      }
   }

   @NotNull
   public QName getPortTypeName() {
      return this.owner.getName();
   }

   public void addFault(EditableWSDLFault fault) {
      this.faults.add(fault);
   }

   public void freeze(EditableWSDLModel root) {
      assert this.input != null;

      this.input.freeze(root);
      if (this.output != null) {
         this.output.freeze(root);
      }

      Iterator var2 = this.faults.iterator();

      while(var2.hasNext()) {
         EditableWSDLFault fault = (EditableWSDLFault)var2.next();
         fault.freeze(root);
      }

   }
}
