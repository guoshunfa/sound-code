package sun.net.httpserver;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

public class HttpsServerImpl extends HttpsServer {
   ServerImpl server;

   HttpsServerImpl() throws IOException {
      this(new InetSocketAddress(443), 0);
   }

   HttpsServerImpl(InetSocketAddress var1, int var2) throws IOException {
      this.server = new ServerImpl(this, "https", var1, var2);
   }

   public void setHttpsConfigurator(HttpsConfigurator var1) {
      this.server.setHttpsConfigurator(var1);
   }

   public HttpsConfigurator getHttpsConfigurator() {
      return this.server.getHttpsConfigurator();
   }

   public void bind(InetSocketAddress var1, int var2) throws IOException {
      this.server.bind(var1, var2);
   }

   public void start() {
      this.server.start();
   }

   public void setExecutor(Executor var1) {
      this.server.setExecutor(var1);
   }

   public Executor getExecutor() {
      return this.server.getExecutor();
   }

   public void stop(int var1) {
      this.server.stop(var1);
   }

   public HttpContextImpl createContext(String var1, HttpHandler var2) {
      return this.server.createContext(var1, var2);
   }

   public HttpContextImpl createContext(String var1) {
      return this.server.createContext(var1);
   }

   public void removeContext(String var1) throws IllegalArgumentException {
      this.server.removeContext(var1);
   }

   public void removeContext(HttpContext var1) throws IllegalArgumentException {
      this.server.removeContext(var1);
   }

   public InetSocketAddress getAddress() {
      return this.server.getAddress();
   }
}
