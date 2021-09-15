package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundFault;

public interface EditableWSDLBoundFault extends WSDLBoundFault {
   @Nullable
   EditableWSDLFault getFault();

   @NotNull
   EditableWSDLBoundOperation getBoundOperation();

   void freeze(EditableWSDLBoundOperation var1);
}
