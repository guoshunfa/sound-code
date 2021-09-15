package com.oracle.webservices.internal.api.databinding;

import javax.xml.transform.Result;
import javax.xml.ws.Holder;

public interface WSDLResolver {
   Result getWSDL(String var1);

   Result getAbstractWSDL(Holder<String> var1);

   Result getSchemaOutput(String var1, Holder<String> var2);
}
