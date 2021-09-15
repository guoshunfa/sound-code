package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.server.SDDocument;

public interface SDDocumentResolver {
   @Nullable
   SDDocument resolve(String var1);
}
