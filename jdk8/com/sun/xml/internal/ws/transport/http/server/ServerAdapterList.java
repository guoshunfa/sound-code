package com.sun.xml.internal.ws.transport.http.server;

import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.transport.http.HttpAdapterList;

public class ServerAdapterList extends HttpAdapterList<ServerAdapter> {
   protected ServerAdapter createHttpAdapter(String name, String urlPattern, WSEndpoint<?> endpoint) {
      return new ServerAdapter(name, urlPattern, endpoint, this);
   }
}
