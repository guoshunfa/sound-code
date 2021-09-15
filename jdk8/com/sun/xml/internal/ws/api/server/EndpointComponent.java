package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

/** @deprecated */
public interface EndpointComponent {
   @Nullable
   <T> T getSPI(@NotNull Class<T> var1);
}
