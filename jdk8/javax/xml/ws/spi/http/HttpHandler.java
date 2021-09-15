package javax.xml.ws.spi.http;

import java.io.IOException;

public abstract class HttpHandler {
   public abstract void handle(HttpExchange var1) throws IOException;
}
