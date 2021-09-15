package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;

public interface WSDLBoundFault extends WSDLObject, WSDLExtensible {
   @NotNull
   String getName();

   @Nullable
   QName getQName();

   @Nullable
   WSDLFault getFault();

   @NotNull
   WSDLBoundOperation getBoundOperation();
}
