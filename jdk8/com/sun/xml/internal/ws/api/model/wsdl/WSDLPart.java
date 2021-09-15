package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.xml.internal.ws.api.model.ParameterBinding;

public interface WSDLPart extends WSDLObject {
   String getName();

   ParameterBinding getBinding();

   int getIndex();

   WSDLPartDescriptor getDescriptor();
}
