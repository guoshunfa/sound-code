package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import javax.xml.namespace.QName;

public interface EditableWSDLService extends WSDLService {
   @NotNull
   EditableWSDLModel getParent();

   EditableWSDLPort get(QName var1);

   EditableWSDLPort getFirstPort();

   @Nullable
   EditableWSDLPort getMatchingPort(QName var1);

   Iterable<? extends EditableWSDLPort> getPorts();

   void put(QName var1, EditableWSDLPort var2);

   void freeze(EditableWSDLModel var1);
}
