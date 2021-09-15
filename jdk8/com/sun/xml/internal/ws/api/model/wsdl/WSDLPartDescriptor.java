package com.sun.xml.internal.ws.api.model.wsdl;

import javax.xml.namespace.QName;

public interface WSDLPartDescriptor extends WSDLObject {
   QName name();

   WSDLDescriptorKind type();
}
