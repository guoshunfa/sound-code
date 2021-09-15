package com.sun.net.httpserver;

import java.util.List;
import java.util.Map;
import jdk.Exported;

@Exported
public abstract class HttpContext {
   protected HttpContext() {
   }

   public abstract HttpHandler getHandler();

   public abstract void setHandler(HttpHandler var1);

   public abstract String getPath();

   public abstract HttpServer getServer();

   public abstract Map<String, Object> getAttributes();

   public abstract List<Filter> getFilters();

   public abstract Authenticator setAuthenticator(Authenticator var1);

   public abstract Authenticator getAuthenticator();
}
