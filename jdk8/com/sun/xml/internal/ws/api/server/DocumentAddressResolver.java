package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public interface DocumentAddressResolver {
   @Nullable
   String getRelativeAddressFor(@NotNull SDDocument var1, @NotNull SDDocument var2);
}
