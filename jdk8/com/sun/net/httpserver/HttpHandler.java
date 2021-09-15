package com.sun.net.httpserver;

import java.io.IOException;
import jdk.Exported;

@Exported
public interface HttpHandler {
   void handle(HttpExchange var1) throws IOException;
}
