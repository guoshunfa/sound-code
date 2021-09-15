package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import javax.xml.ws.WebServiceContext;

public interface AsyncProvider<T> {
   void invoke(@NotNull T var1, @NotNull AsyncProviderCallback<T> var2, @NotNull WebServiceContext var3);
}
