package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPart;

public interface EditableWSDLPart extends WSDLPart {
   void setBinding(ParameterBinding var1);

   void setIndex(int var1);
}
