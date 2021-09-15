package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.Component;
import java.util.List;

public abstract class Module implements Component {
   @NotNull
   public abstract List<BoundEndpoint> getBoundEndpoints();

   @Nullable
   public <S> S getSPI(@NotNull Class<S> spiType) {
      return null;
   }
}
