package com.sun.xml.internal.ws.transport.http;

import com.sun.istack.internal.NotNull;
import java.io.IOException;

public abstract class HttpMetadataPublisher {
   public abstract boolean handleMetadataRequest(@NotNull HttpAdapter var1, @NotNull WSHTTPConnection var2) throws IOException;
}
