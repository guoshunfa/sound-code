package com.sun.xml.internal.ws.api.model.wsdl;

import javax.xml.namespace.QName;

public interface WSDLMessage extends WSDLObject, WSDLExtensible {
   QName getName();

   Iterable<? extends WSDLPart> parts();
}
