package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;

public interface WSDLFault extends WSDLObject, WSDLExtensible {
   String getName();

   WSDLMessage getMessage();

   @NotNull
   WSDLOperation getOperation();

   @NotNull
   QName getQName();

   String getAction();

   boolean isDefaultAction();
}
