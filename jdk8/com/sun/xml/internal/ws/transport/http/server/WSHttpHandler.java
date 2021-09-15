package com.sun.xml.internal.ws.transport.http.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.xml.internal.ws.resources.HttpserverMessages;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

final class WSHttpHandler implements HttpHandler {
   private static final String GET_METHOD = "GET";
   private static final String POST_METHOD = "POST";
   private static final String HEAD_METHOD = "HEAD";
   private static final String PUT_METHOD = "PUT";
   private static final String DELETE_METHOD = "DELETE";
   private static final Logger LOGGER = Logger.getLogger("com.sun.xml.internal.ws.server.http");
   private static final boolean fineTraceEnabled;
   private final HttpAdapter adapter;
   private final Executor executor;

   public WSHttpHandler(@NotNull HttpAdapter adapter, @Nullable Executor executor) {
      assert adapter != null;

      this.adapter = adapter;
      this.executor = executor;
   }

   public void handle(HttpExchange msg) {
      try {
         if (fineTraceEnabled) {
            LOGGER.log(Level.FINE, (String)"Received HTTP request:{0}", (Object)msg.getRequestURI());
         }

         if (this.executor != null) {
            this.executor.execute(new WSHttpHandler.HttpHandlerRunnable(msg));
         } else {
            this.handleExchange(msg);
         }
      } catch (Throwable var3) {
      }

   }

   private void handleExchange(HttpExchange msg) throws IOException {
      ServerConnectionImpl con = new ServerConnectionImpl(this.adapter, msg);

      try {
         if (fineTraceEnabled) {
            LOGGER.log(Level.FINE, (String)"Received HTTP request:{0}", (Object)msg.getRequestURI());
         }

         String method = msg.getRequestMethod();
         if (!method.equals("GET") && !method.equals("POST") && !method.equals("HEAD") && !method.equals("PUT") && !method.equals("DELETE")) {
            if (LOGGER.isLoggable(Level.WARNING)) {
               LOGGER.warning(HttpserverMessages.UNEXPECTED_HTTP_METHOD(method));
            }
         } else {
            this.adapter.handle(con);
         }
      } finally {
         msg.close();
      }

   }

   static {
      fineTraceEnabled = LOGGER.isLoggable(Level.FINE);
   }

   class HttpHandlerRunnable implements Runnable {
      final HttpExchange msg;

      HttpHandlerRunnable(HttpExchange msg) {
         this.msg = msg;
      }

      public void run() {
         try {
            WSHttpHandler.this.handleExchange(this.msg);
         } catch (Throwable var2) {
            var2.printStackTrace();
         }

      }
   }
}
