package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public interface AsyncProviderCallback<T> {
   void send(@Nullable T var1);

   void sendError(@NotNull Throwable var1);
}
