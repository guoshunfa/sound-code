package com.sun.xml.internal.ws.api.wsdl.parser;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.server.Container;

public interface WSDLParserExtensionContext {
   boolean isClientSide();

   EditableWSDLModel getWSDLModel();

   @NotNull
   Container getContainer();

   @NotNull
   PolicyResolver getPolicyResolver();
}
