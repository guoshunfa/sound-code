package sun.net.httpserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

class ExchangeImpl {
   Headers reqHdrs;
   Headers rspHdrs;
   Request req;
   String method;
   boolean writefinished;
   URI uri;
   HttpConnection connection;
   long reqContentLen;
   long rspContentLen;
   InputStream ris;
   OutputStream ros;
   Thread thread;
   boolean close;
   boolean closed;
   boolean http10 = false;
   private static final String pattern = "EEE, dd MMM yyyy HH:mm:ss zzz";
   private static final TimeZone gmtTZ = TimeZone.getTimeZone("GMT");
   private static final ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
      protected DateFormat initialValue() {
         SimpleDateFormat var1 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
         var1.setTimeZone(ExchangeImpl.gmtTZ);
         return var1;
      }
   };
   private static final String HEAD = "HEAD";
   InputStream uis;
   OutputStream uos;
   LeftOverInputStream uis_orig;
   PlaceholderOutputStream uos_orig;
   boolean sentHeaders;
   Map<String, Object> attributes;
   int rcode = -1;
   HttpPrincipal principal;
   ServerImpl server;
   private byte[] rspbuf = new byte[128];

   ExchangeImpl(String var1, URI var2, Request var3, long var4, HttpConnection var6) throws IOException {
      this.req = var3;
      this.reqHdrs = var3.headers();
      this.rspHdrs = new Headers();
      this.method = var1;
      this.uri = var2;
      this.connection = var6;
      this.reqContentLen = var4;
      this.ros = var3.outputStream();
      this.ris = var3.inputStream();
      this.server = this.getServerImpl();
      this.server.startExchange();
   }

   public Headers getRequestHeaders() {
      return new UnmodifiableHeaders(this.reqHdrs);
   }

   public Headers getResponseHeaders() {
      return this.rspHdrs;
   }

   public URI getRequestURI() {
      return this.uri;
   }

   public String getRequestMethod() {
      return this.method;
   }

   public HttpContextImpl getHttpContext() {
      return this.connection.getHttpContext();
   }

   private boolean isHeadRequest() {
      return "HEAD".equals(this.getRequestMethod());
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;

         try {
            if (this.uis_orig == null || this.uos == null) {
               this.connection.close();
               return;
            }

            if (!this.uos_orig.isWrapped()) {
               this.connection.close();
               return;
            }

            if (!this.uis_orig.isClosed()) {
               this.uis_orig.close();
            }

            this.uos.close();
         } catch (IOException var2) {
            this.connection.close();
         }

      }
   }

   public InputStream getRequestBody() {
      if (this.uis != null) {
         return this.uis;
      } else {
         if (this.reqContentLen == -1L) {
            this.uis_orig = new ChunkedInputStream(this, this.ris);
            this.uis = this.uis_orig;
         } else {
            this.uis_orig = new FixedLengthInputStream(this, this.ris, this.reqContentLen);
            this.uis = this.uis_orig;
         }

         return this.uis;
      }
   }

   LeftOverInputStream getOriginalInputStream() {
      return this.uis_orig;
   }

   public int getResponseCode() {
      return this.rcode;
   }

   public OutputStream getResponseBody() {
      if (this.uos == null) {
         this.uos_orig = new PlaceholderOutputStream((OutputStream)null);
         this.uos = this.uos_orig;
      }

      return this.uos;
   }

   PlaceholderOutputStream getPlaceholderResponseBody() {
      this.getResponseBody();
      return this.uos_orig;
   }

   public void sendResponseHeaders(int var1, long var2) throws IOException {
      if (this.sentHeaders) {
         throw new IOException("headers already sent");
      } else {
         this.rcode = var1;
         String var4 = "HTTP/1.1 " + var1 + Code.msg(var1) + "\r\n";
         BufferedOutputStream var5 = new BufferedOutputStream(this.ros);
         PlaceholderOutputStream var6 = this.getPlaceholderResponseBody();
         var5.write(this.bytes(var4, 0), 0, var4.length());
         boolean var7 = false;
         this.rspHdrs.set("Date", ((DateFormat)dateFormat.get()).format(new Date()));
         Logger var8;
         String var9;
         if (var1 >= 100 && var1 < 200 || var1 == 204 || var1 == 304) {
            if (var2 != -1L) {
               var8 = this.server.getLogger();
               var9 = "sendResponseHeaders: rCode = " + var1 + ": forcing contentLen = -1";
               var8.warning(var9);
            }

            var2 = -1L;
         }

         if (this.isHeadRequest()) {
            if (var2 >= 0L) {
               var8 = this.server.getLogger();
               var9 = "sendResponseHeaders: being invoked with a content length for a HEAD request";
               var8.warning(var9);
            }

            var7 = true;
            var2 = 0L;
         } else if (var2 == 0L) {
            if (this.http10) {
               var6.setWrappedStream(new UndefLengthOutputStream(this, this.ros));
               this.close = true;
            } else {
               this.rspHdrs.set("Transfer-encoding", "chunked");
               var6.setWrappedStream(new ChunkedOutputStream(this, this.ros));
            }
         } else {
            if (var2 == -1L) {
               var7 = true;
               var2 = 0L;
            }

            this.rspHdrs.set("Content-length", Long.toString(var2));
            var6.setWrappedStream(new FixedLengthOutputStream(this, this.ros, var2));
         }

         this.write(this.rspHdrs, var5);
         this.rspContentLen = var2;
         var5.flush();
         var5 = null;
         this.sentHeaders = true;
         if (var7) {
            WriteFinishedEvent var10 = new WriteFinishedEvent(this);
            this.server.addEvent(var10);
            this.closed = true;
         }

         this.server.logReply(var1, this.req.requestLine(), (String)null);
      }
   }

   void write(Headers var1, OutputStream var2) throws IOException {
      Set var3 = var1.entrySet();
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         Map.Entry var5 = (Map.Entry)var4.next();
         String var6 = (String)var5.getKey();
         List var8 = (List)var5.getValue();
         Iterator var9 = var8.iterator();

         while(var9.hasNext()) {
            String var10 = (String)var9.next();
            int var11 = var6.length();
            byte[] var7 = this.bytes(var6, 2);
            var7[var11++] = 58;
            var7[var11++] = 32;
            var2.write(var7, 0, var11);
            var7 = this.bytes(var10, 2);
            var11 = var10.length();
            var7[var11++] = 13;
            var7[var11++] = 10;
            var2.write(var7, 0, var11);
         }
      }

      var2.write(13);
      var2.write(10);
   }

   private byte[] bytes(String var1, int var2) {
      int var3 = var1.length();
      if (var3 + var2 > this.rspbuf.length) {
         int var4 = var3 + var2 - this.rspbuf.length;
         this.rspbuf = new byte[2 * (this.rspbuf.length + var4)];
      }

      char[] var6 = var1.toCharArray();

      for(int var5 = 0; var5 < var6.length; ++var5) {
         this.rspbuf[var5] = (byte)var6[var5];
      }

      return this.rspbuf;
   }

   public InetSocketAddress getRemoteAddress() {
      Socket var1 = this.connection.getChannel().socket();
      InetAddress var2 = var1.getInetAddress();
      int var3 = var1.getPort();
      return new InetSocketAddress(var2, var3);
   }

   public InetSocketAddress getLocalAddress() {
      Socket var1 = this.connection.getChannel().socket();
      InetAddress var2 = var1.getLocalAddress();
      int var3 = var1.getLocalPort();
      return new InetSocketAddress(var2, var3);
   }

   public String getProtocol() {
      String var1 = this.req.requestLine();
      int var2 = var1.lastIndexOf(32);
      return var1.substring(var2 + 1);
   }

   public SSLSession getSSLSession() {
      SSLEngine var1 = this.connection.getSSLEngine();
      return var1 == null ? null : var1.getSession();
   }

   public Object getAttribute(String var1) {
      if (var1 == null) {
         throw new NullPointerException("null name parameter");
      } else {
         if (this.attributes == null) {
            this.attributes = this.getHttpContext().getAttributes();
         }

         return this.attributes.get(var1);
      }
   }

   public void setAttribute(String var1, Object var2) {
      if (var1 == null) {
         throw new NullPointerException("null name parameter");
      } else {
         if (this.attributes == null) {
            this.attributes = this.getHttpContext().getAttributes();
         }

         this.attributes.put(var1, var2);
      }
   }

   public void setStreams(InputStream var1, OutputStream var2) {
      assert this.uis != null;

      if (var1 != null) {
         this.uis = var1;
      }

      if (var2 != null) {
         this.uos = var2;
      }

   }

   HttpConnection getConnection() {
      return this.connection;
   }

   ServerImpl getServerImpl() {
      return this.getHttpContext().getServerImpl();
   }

   public HttpPrincipal getPrincipal() {
      return this.principal;
   }

   void setPrincipal(HttpPrincipal var1) {
      this.principal = var1;
   }

   static ExchangeImpl get(HttpExchange var0) {
      if (var0 instanceof HttpExchangeImpl) {
         return ((HttpExchangeImpl)var0).getExchangeImpl();
      } else {
         assert var0 instanceof HttpsExchangeImpl;

         return ((HttpsExchangeImpl)var0).getExchangeImpl();
      }
   }
}
