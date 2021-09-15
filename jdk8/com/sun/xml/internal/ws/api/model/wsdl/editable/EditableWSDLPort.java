package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;

public interface EditableWSDLPort extends WSDLPort {
   @NotNull
   EditableWSDLBoundPortType getBinding();

   @NotNull
   EditableWSDLService getOwner();

   void setAddress(EndpointAddress var1);

   void setEPR(@NotNull WSEndpointReference var1);

   void freeze(EditableWSDLModel var1);
}
