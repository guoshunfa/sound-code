package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;

public interface EditableWSDLOutput extends WSDLOutput {
   EditableWSDLMessage getMessage();

   @NotNull
   EditableWSDLOperation getOperation();

   void setAction(String var1);

   void setDefaultAction(boolean var1);

   void freeze(EditableWSDLModel var1);
}
