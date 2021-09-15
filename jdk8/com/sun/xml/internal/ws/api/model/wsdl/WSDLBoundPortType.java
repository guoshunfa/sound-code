package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;

public interface WSDLBoundPortType extends WSDLFeaturedObject, WSDLExtensible {
   QName getName();

   @NotNull
   WSDLModel getOwner();

   WSDLBoundOperation get(QName var1);

   QName getPortTypeName();

   WSDLPortType getPortType();

   Iterable<? extends WSDLBoundOperation> getBindingOperations();

   @NotNull
   SOAPBinding.Style getStyle();

   BindingID getBindingId();

   @Nullable
   WSDLBoundOperation getOperation(String var1, String var2);

   ParameterBinding getBinding(QName var1, String var2, WebParam.Mode var3);
}
