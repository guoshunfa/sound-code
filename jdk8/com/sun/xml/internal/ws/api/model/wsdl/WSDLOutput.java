package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;

public interface WSDLOutput extends WSDLObject, WSDLExtensible {
   String getName();

   WSDLMessage getMessage();

   String getAction();

   @NotNull
   WSDLOperation getOperation();

   @NotNull
   QName getQName();

   boolean isDefaultAction();
}
