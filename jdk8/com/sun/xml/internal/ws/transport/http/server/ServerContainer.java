package com.sun.xml.internal.ws.transport.http.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.BoundEndpoint;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.Module;
import java.util.ArrayList;
import java.util.List;

class ServerContainer extends Container {
   private final Module module = new Module() {
      private final List<BoundEndpoint> endpoints = new ArrayList();

      @NotNull
      public List<BoundEndpoint> getBoundEndpoints() {
         return this.endpoints;
      }
   };

   public <T> T getSPI(Class<T> spiType) {
      T t = super.getSPI(spiType);
      if (t != null) {
         return t;
      } else {
         return spiType == Module.class ? spiType.cast(this.module) : null;
      }
   }
}
