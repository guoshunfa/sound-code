package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;

public interface WSDLService extends WSDLObject, WSDLExtensible {
   @NotNull
   WSDLModel getParent();

   @NotNull
   QName getName();

   WSDLPort get(QName var1);

   WSDLPort getFirstPort();

   @Nullable
   WSDLPort getMatchingPort(QName var1);

   Iterable<? extends WSDLPort> getPorts();
}
