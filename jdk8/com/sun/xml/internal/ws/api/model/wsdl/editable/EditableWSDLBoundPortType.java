package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;

public interface EditableWSDLBoundPortType extends WSDLBoundPortType {
   @NotNull
   EditableWSDLModel getOwner();

   EditableWSDLBoundOperation get(QName var1);

   EditableWSDLPortType getPortType();

   Iterable<? extends EditableWSDLBoundOperation> getBindingOperations();

   @Nullable
   EditableWSDLBoundOperation getOperation(String var1, String var2);

   void put(QName var1, EditableWSDLBoundOperation var2);

   void setBindingId(BindingID var1);

   void setStyle(SOAPBinding.Style var1);

   void freeze();
}
