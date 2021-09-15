package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;

public interface WSDLOperation extends WSDLObject, WSDLExtensible {
   @NotNull
   QName getName();

   @NotNull
   WSDLInput getInput();

   @Nullable
   WSDLOutput getOutput();

   boolean isOneWay();

   Iterable<? extends WSDLFault> getFaults();

   @Nullable
   WSDLFault getFault(QName var1);

   @NotNull
   QName getPortTypeName();

   String getParameterOrder();
}
