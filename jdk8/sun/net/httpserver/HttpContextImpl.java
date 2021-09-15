package sun.net.httpserver;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

class HttpContextImpl extends HttpContext {
   private String path;
   private String protocol;
   private HttpHandler handler;
   private Map<String, Object> attributes = new HashMap();
   private ServerImpl server;
   private LinkedList<Filter> sfilters = new LinkedList();
   private LinkedList<Filter> ufilters = new LinkedList();
   private Authenticator authenticator;
   private AuthFilter authfilter;

   HttpContextImpl(String var1, String var2, HttpHandler var3, ServerImpl var4) {
      if (var2 != null && var1 != null && var2.length() >= 1 && var2.charAt(0) == '/') {
         this.protocol = var1.toLowerCase();
         this.path = var2;
         if (!this.protocol.equals("http") && !this.protocol.equals("https")) {
            throw new IllegalArgumentException("Illegal value for protocol");
         } else {
            this.handler = var3;
            this.server = var4;
            this.authfilter = new AuthFilter((Authenticator)null);
            this.sfilters.add(this.authfilter);
         }
      } else {
         throw new IllegalArgumentException("Illegal value for path or protocol");
      }
   }

   public HttpHandler getHandler() {
      return this.handler;
   }

   public void setHandler(HttpHandler var1) {
      if (var1 == null) {
         throw new NullPointerException("Null handler parameter");
      } else if (this.handler != null) {
         throw new IllegalArgumentException("handler already set");
      } else {
         this.handler = var1;
      }
   }

   public String getPath() {
      return this.path;
   }

   public HttpServer getServer() {
      return this.server.getWrapper();
   }

   ServerImpl getServerImpl() {
      return this.server;
   }

   public String getProtocol() {
      return this.protocol;
   }

   public Map<String, Object> getAttributes() {
      return this.attributes;
   }

   public List<Filter> getFilters() {
      return this.ufilters;
   }

   List<Filter> getSystemFilters() {
      return this.sfilters;
   }

   public Authenticator setAuthenticator(Authenticator var1) {
      Authenticator var2 = this.authenticator;
      this.authenticator = var1;
      this.authfilter.setAuthenticator(var1);
      return var2;
   }

   public Authenticator getAuthenticator() {
      return this.authenticator;
   }

   Logger getLogger() {
      return this.server.getLogger();
   }
}
