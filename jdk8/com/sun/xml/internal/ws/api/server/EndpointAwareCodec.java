package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.Codec;

public interface EndpointAwareCodec extends Codec {
   void setEndpoint(@NotNull WSEndpoint var1);
}
