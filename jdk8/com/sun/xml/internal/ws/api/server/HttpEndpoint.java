package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import java.util.concurrent.Executor;

public abstract class HttpEndpoint {
   public static HttpEndpoint create(@NotNull WSEndpoint endpoint) {
      return new com.sun.xml.internal.ws.transport.http.server.HttpEndpoint((Executor)null, HttpAdapter.createAlone(endpoint));
   }

   public abstract void publish(@NotNull String var1);

   public abstract void stop();
}
