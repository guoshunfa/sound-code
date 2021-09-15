package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import org.xml.sax.Locator;

public interface WSDLObject {
   @NotNull
   Locator getLocation();
}
