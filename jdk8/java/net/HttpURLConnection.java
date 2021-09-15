package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.security.Permission;
import java.util.Date;

public abstract class HttpURLConnection extends URLConnection {
   protected String method = "GET";
   protected int chunkLength = -1;
   protected int fixedContentLength = -1;
   protected long fixedContentLengthLong = -1L;
   private static final int DEFAULT_CHUNK_SIZE = 4096;
   protected int responseCode = -1;
   protected String responseMessage = null;
   private static boolean followRedirects = true;
   protected boolean instanceFollowRedirects;
   private static final String[] methods = new String[]{"GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE"};
   public static final int HTTP_OK = 200;
   public static final int HTTP_CREATED = 201;
   public static final int HTTP_ACCEPTED = 202;
   public static final int HTTP_NOT_AUTHORITATIVE = 203;
   public static final int HTTP_NO_CONTENT = 204;
   public static final int HTTP_RESET = 205;
   public static final int HTTP_PARTIAL = 206;
   public static final int HTTP_MULT_CHOICE = 300;
   public static final int HTTP_MOVED_PERM = 301;
   public static final int HTTP_MOVED_TEMP = 302;
   public static final int HTTP_SEE_OTHER = 303;
   public static final int HTTP_NOT_MODIFIED = 304;
   public static final int HTTP_USE_PROXY = 305;
   public static final int HTTP_BAD_REQUEST = 400;
   public static final int HTTP_UNAUTHORIZED = 401;
   public static final int HTTP_PAYMENT_REQUIRED = 402;
   public static final int HTTP_FORBIDDEN = 403;
   public static final int HTTP_NOT_FOUND = 404;
   public static final int HTTP_BAD_METHOD = 405;
   public static final int HTTP_NOT_ACCEPTABLE = 406;
   public static final int HTTP_PROXY_AUTH = 407;
   public static final int HTTP_CLIENT_TIMEOUT = 408;
   public static final int HTTP_CONFLICT = 409;
   public static final int HTTP_GONE = 410;
   public static final int HTTP_LENGTH_REQUIRED = 411;
   public static final int HTTP_PRECON_FAILED = 412;
   public static final int HTTP_ENTITY_TOO_LARGE = 413;
   public static final int HTTP_REQ_TOO_LONG = 414;
   public static final int HTTP_UNSUPPORTED_TYPE = 415;
   /** @deprecated */
   @Deprecated
   public static final int HTTP_SERVER_ERROR = 500;
   public static final int HTTP_INTERNAL_ERROR = 500;
   public static final int HTTP_NOT_IMPLEMENTED = 501;
   public static final int HTTP_BAD_GATEWAY = 502;
   public static final int HTTP_UNAVAILABLE = 503;
   public static final int HTTP_GATEWAY_TIMEOUT = 504;
   public static final int HTTP_VERSION = 505;

   public String getHeaderFieldKey(int var1) {
      return null;
   }

   public void setFixedLengthStreamingMode(int var1) {
      if (this.connected) {
         throw new IllegalStateException("Already connected");
      } else if (this.chunkLength != -1) {
         throw new IllegalStateException("Chunked encoding streaming mode set");
      } else if (var1 < 0) {
         throw new IllegalArgumentException("invalid content length");
      } else {
         this.fixedContentLength = var1;
      }
   }

   public void setFixedLengthStreamingMode(long var1) {
      if (this.connected) {
         throw new IllegalStateException("Already connected");
      } else if (this.chunkLength != -1) {
         throw new IllegalStateException("Chunked encoding streaming mode set");
      } else if (var1 < 0L) {
         throw new IllegalArgumentException("invalid content length");
      } else {
         this.fixedContentLengthLong = var1;
      }
   }

   public void setChunkedStreamingMode(int var1) {
      if (this.connected) {
         throw new IllegalStateException("Can't set streaming mode: already connected");
      } else if (this.fixedContentLength == -1 && this.fixedContentLengthLong == -1L) {
         this.chunkLength = var1 <= 0 ? 4096 : var1;
      } else {
         throw new IllegalStateException("Fixed length streaming mode set");
      }
   }

   public String getHeaderField(int var1) {
      return null;
   }

   protected HttpURLConnection(URL var1) {
      super(var1);
      this.instanceFollowRedirects = followRedirects;
   }

   public static void setFollowRedirects(boolean var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkSetFactory();
      }

      followRedirects = var0;
   }

   public static boolean getFollowRedirects() {
      return followRedirects;
   }

   public void setInstanceFollowRedirects(boolean var1) {
      this.instanceFollowRedirects = var1;
   }

   public boolean getInstanceFollowRedirects() {
      return this.instanceFollowRedirects;
   }

   public void setRequestMethod(String var1) throws ProtocolException {
      if (this.connected) {
         throw new ProtocolException("Can't reset method: already connected");
      } else {
         for(int var2 = 0; var2 < methods.length; ++var2) {
            if (methods[var2].equals(var1)) {
               if (var1.equals("TRACE")) {
                  SecurityManager var3 = System.getSecurityManager();
                  if (var3 != null) {
                     var3.checkPermission(new NetPermission("allowHttpTrace"));
                  }
               }

               this.method = var1;
               return;
            }
         }

         throw new ProtocolException("Invalid HTTP method: " + var1);
      }
   }

   public String getRequestMethod() {
      return this.method;
   }

   public int getResponseCode() throws IOException {
      if (this.responseCode != -1) {
         return this.responseCode;
      } else {
         Exception var1 = null;

         try {
            this.getInputStream();
         } catch (Exception var6) {
            var1 = var6;
         }

         String var2 = this.getHeaderField(0);
         if (var2 == null) {
            if (var1 != null) {
               if (var1 instanceof RuntimeException) {
                  throw (RuntimeException)var1;
               } else {
                  throw (IOException)var1;
               }
            } else {
               return -1;
            }
         } else {
            if (var2.startsWith("HTTP/1.")) {
               int var3 = var2.indexOf(32);
               if (var3 > 0) {
                  int var4 = var2.indexOf(32, var3 + 1);
                  if (var4 > 0 && var4 < var2.length()) {
                     this.responseMessage = var2.substring(var4 + 1);
                  }

                  if (var4 < 0) {
                     var4 = var2.length();
                  }

                  try {
                     this.responseCode = Integer.parseInt(var2.substring(var3 + 1, var4));
                     return this.responseCode;
                  } catch (NumberFormatException var7) {
                  }
               }
            }

            return -1;
         }
      }
   }

   public String getResponseMessage() throws IOException {
      this.getResponseCode();
      return this.responseMessage;
   }

   public long getHeaderFieldDate(String var1, long var2) {
      String var4 = this.getHeaderField(var1);

      try {
         if (var4.indexOf("GMT") == -1) {
            var4 = var4 + " GMT";
         }

         return Date.parse(var4);
      } catch (Exception var6) {
         return var2;
      }
   }

   public abstract void disconnect();

   public abstract boolean usingProxy();

   public Permission getPermission() throws IOException {
      int var1 = this.url.getPort();
      var1 = var1 < 0 ? 80 : var1;
      String var2 = this.url.getHost() + ":" + var1;
      SocketPermission var3 = new SocketPermission(var2, "connect");
      return var3;
   }

   public InputStream getErrorStream() {
      return null;
   }
}
