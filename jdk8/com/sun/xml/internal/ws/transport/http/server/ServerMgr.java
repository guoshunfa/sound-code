package com.sun.xml.internal.ws.transport.http.server;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import com.sun.xml.internal.ws.server.ServerRtException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

final class ServerMgr {
   private static final ServerMgr serverMgr = new ServerMgr();
   private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server.http");
   private final Map<InetSocketAddress, ServerMgr.ServerState> servers = new HashMap();

   private ServerMgr() {
   }

   static ServerMgr getInstance() {
      return serverMgr;
   }

   HttpContext createContext(String address) {
      try {
         URL url = new URL(address);
         int port = url.getPort();
         if (port == -1) {
            port = url.getDefaultPort();
         }

         InetSocketAddress inetAddress = new InetSocketAddress(url.getHost(), port);
         HttpServer server;
         ServerMgr.ServerState state;
         synchronized(this.servers) {
            state = (ServerMgr.ServerState)this.servers.get(inetAddress);
            if (state == null) {
               int finalPortNum = port;
               Iterator var9 = this.servers.values().iterator();

               while(true) {
                  if (var9.hasNext()) {
                     ServerMgr.ServerState s = (ServerMgr.ServerState)var9.next();
                     if (s.getServer().getAddress().getPort() != finalPortNum) {
                        continue;
                     }

                     state = s;
                  }

                  if (!inetAddress.getAddress().isAnyLocalAddress() || state == null) {
                     logger.fine("Creating new HTTP Server at " + inetAddress);
                     server = HttpServer.create(inetAddress, 0);
                     server.setExecutor(Executors.newCachedThreadPool());
                     String path = url.toURI().getPath();
                     logger.fine("Creating HTTP Context at = " + path);
                     HttpContext context = server.createContext(path);
                     server.start();
                     inetAddress = server.getAddress();
                     logger.fine("HTTP server started = " + inetAddress);
                     state = new ServerMgr.ServerState(server, path);
                     this.servers.put(inetAddress, state);
                     return context;
                  }
                  break;
               }
            }
         }

         server = state.getServer();
         if (state.getPaths().contains(url.getPath())) {
            String err = "Context with URL path " + url.getPath() + " already exists on the server " + server.getAddress();
            logger.fine(err);
            throw new IllegalArgumentException(err);
         } else {
            logger.fine("Creating HTTP Context at = " + url.getPath());
            HttpContext context = server.createContext(url.getPath());
            state.oneMoreContext(url.getPath());
            return context;
         }
      } catch (Exception var13) {
         throw new ServerRtException("server.rt.err", new Object[]{var13});
      }
   }

   void removeContext(HttpContext context) {
      InetSocketAddress inetAddress = context.getServer().getAddress();
      synchronized(this.servers) {
         ServerMgr.ServerState state = (ServerMgr.ServerState)this.servers.get(inetAddress);
         int instances = state.noOfContexts();
         if (instances < 2) {
            ((ExecutorService)state.getServer().getExecutor()).shutdown();
            state.getServer().stop(0);
            this.servers.remove(inetAddress);
         } else {
            state.getServer().removeContext(context);
            state.oneLessContext(context.getPath());
         }

      }
   }

   private static final class ServerState {
      private final HttpServer server;
      private int instances;
      private Set<String> paths = new HashSet();

      ServerState(HttpServer server, String path) {
         this.server = server;
         this.instances = 1;
         this.paths.add(path);
      }

      public HttpServer getServer() {
         return this.server;
      }

      public void oneMoreContext(String path) {
         ++this.instances;
         this.paths.add(path);
      }

      public void oneLessContext(String path) {
         --this.instances;
         this.paths.remove(path);
      }

      public int noOfContexts() {
         return this.instances;
      }

      public Set<String> getPaths() {
         return this.paths;
      }
   }
}
