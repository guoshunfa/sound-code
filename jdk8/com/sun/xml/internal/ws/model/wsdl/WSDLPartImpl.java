package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPartDescriptor;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import javax.xml.stream.XMLStreamReader;

public final class WSDLPartImpl extends AbstractObjectImpl implements EditableWSDLPart {
   private final String name;
   private ParameterBinding binding;
   private int index;
   private final WSDLPartDescriptor descriptor;

   public WSDLPartImpl(XMLStreamReader xsr, String partName, int index, WSDLPartDescriptor descriptor) {
      super(xsr);
      this.name = partName;
      this.binding = ParameterBinding.UNBOUND;
      this.index = index;
      this.descriptor = descriptor;
   }

   public String getName() {
      return this.name;
   }

   public ParameterBinding getBinding() {
      return this.binding;
   }

   public void setBinding(ParameterBinding binding) {
      this.binding = binding;
   }

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public WSDLPartDescriptor getDescriptor() {
      return this.descriptor;
   }
}
