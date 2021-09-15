package com.sun.xml.internal.ws.api.config.management;

import com.sun.xml.internal.ws.api.server.WSEndpoint;

public interface ManagedEndpointFactory {
   <T> WSEndpoint<T> createEndpoint(WSEndpoint<T> var1, EndpointCreationAttributes var2);
}
