package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public interface Component {
   @Nullable
   <S> S getSPI(@NotNull Class<S> var1);
}
