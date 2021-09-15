package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;

public interface EditableWSDLPortType extends WSDLPortType {
   EditableWSDLOperation get(String var1);

   Iterable<? extends EditableWSDLOperation> getOperations();

   void put(String var1, EditableWSDLOperation var2);

   void freeze();
}
