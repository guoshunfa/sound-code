package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;

public interface ServiceDefinition extends Iterable<SDDocument> {
   @NotNull
   SDDocument getPrimary();

   void addFilter(@NotNull SDDocumentFilter var1);
}
