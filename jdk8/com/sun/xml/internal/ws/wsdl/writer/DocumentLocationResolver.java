package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.istack.internal.Nullable;

public interface DocumentLocationResolver {
   @Nullable
   String getLocationFor(String var1, String var2);
}
