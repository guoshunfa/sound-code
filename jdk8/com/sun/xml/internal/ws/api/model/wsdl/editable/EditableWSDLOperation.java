package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import javax.xml.namespace.QName;

public interface EditableWSDLOperation extends WSDLOperation {
   @NotNull
   EditableWSDLInput getInput();

   void setInput(EditableWSDLInput var1);

   @Nullable
   EditableWSDLOutput getOutput();

   void setOutput(EditableWSDLOutput var1);

   Iterable<? extends EditableWSDLFault> getFaults();

   void addFault(EditableWSDLFault var1);

   @Nullable
   EditableWSDLFault getFault(QName var1);

   void setParameterOrder(String var1);

   void freeze(EditableWSDLModel var1);
}
