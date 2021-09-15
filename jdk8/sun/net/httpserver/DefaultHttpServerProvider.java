package sun.net.httpserver;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import java.io.IOException;
import java.net.InetSocketAddress;

public class DefaultHttpServerProvider extends HttpServerProvider {
   public HttpServer createHttpServer(InetSocketAddress var1, int var2) throws IOException {
      return new HttpServerImpl(var1, var2);
   }

   public HttpsServer createHttpsServer(InetSocketAddress var1, int var2) throws IOException {
      return new HttpsServerImpl(var1, var2);
   }
}
