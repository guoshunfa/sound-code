package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.server.WSEndpoint;

public interface EndpointAwareTube extends Tube {
   void setEndpoint(WSEndpoint<?> var1);
}
