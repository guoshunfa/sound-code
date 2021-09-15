package com.sun.xml.internal.ws.transport.http.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.client.ClientTransportException;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.transport.Headers;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;

public class HttpClientTransport {
   private static final byte[] THROW_AWAY_BUFFER = new byte[8192];
   int statusCode;
   String statusMessage;
   int contentLength;
   private final Map<String, List<String>> reqHeaders;
   private Map<String, List<String>> respHeaders = null;
   private OutputStream outputStream;
   private boolean https;
   private HttpURLConnection httpConnection = null;
   private final EndpointAddress endpoint;
   private final Packet context;
   private final Integer chunkSize;

   public HttpClientTransport(@NotNull Packet packet, @NotNull Map<String, List<String>> reqHeaders) {
      this.endpoint = packet.endpointAddress;
      this.context = packet;
      this.reqHeaders = reqHeaders;
      this.chunkSize = (Integer)this.context.invocationProperties.get("com.sun.xml.internal.ws.transport.http.client.streaming.chunk.size");
   }

   OutputStream getOutput() {
      try {
         this.createHttpConnection();
         if (this.requiresOutputStream()) {
            this.outputStream = this.httpConnection.getOutputStream();
            if (this.chunkSize != null) {
               this.outputStream = new HttpClientTransport.WSChunkedOuputStream(this.outputStream, this.chunkSize);
            }

            List<String> contentEncoding = (List)this.reqHeaders.get("Content-Encoding");
            if (contentEncoding != null && ((String)contentEncoding.get(0)).contains("gzip")) {
               this.outputStream = new GZIPOutputStream(this.outputStream);
            }
         }

         this.httpConnection.connect();
      } catch (Exception var2) {
         throw new ClientTransportException(ClientMessages.localizableHTTP_CLIENT_FAILED(var2), var2);
      }

      return this.outputStream;
   }

   void closeOutput() throws IOException {
      if (this.outputStream != null) {
         this.outputStream.close();
         this.outputStream = null;
      }

   }

   @Nullable
   InputStream getInput() {
      try {
         InputStream in = this.readResponse();
         if (in != null) {
            String contentEncoding = this.httpConnection.getContentEncoding();
            if (contentEncoding != null && contentEncoding.contains("gzip")) {
               in = new GZIPInputStream((InputStream)in);
            }
         }

         return (InputStream)in;
      } catch (IOException var3) {
         throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(this.statusCode, this.statusMessage), var3);
      }
   }

   public Map<String, List<String>> getHeaders() {
      if (this.respHeaders != null) {
         return this.respHeaders;
      } else {
         this.respHeaders = new Headers();
         this.respHeaders.putAll(this.httpConnection.getHeaderFields());
         return this.respHeaders;
      }
   }

   @Nullable
   protected InputStream readResponse() {
      final InputStream is;
      try {
         is = this.httpConnection.getInputStream();
      } catch (IOException var3) {
         is = this.httpConnection.getErrorStream();
      }

      return (InputStream)(is == null ? is : new FilterInputStream(is) {
         boolean closed;

         public void close() throws IOException {
            if (!this.closed) {
               this.closed = true;

               while(true) {
                  if (is.read(HttpClientTransport.THROW_AWAY_BUFFER) == -1) {
                     super.close();
                     break;
                  }
               }
            }

         }
      });
   }

   protected void readResponseCodeAndMessage() {
      try {
         this.statusCode = this.httpConnection.getResponseCode();
         this.statusMessage = this.httpConnection.getResponseMessage();
         this.contentLength = this.httpConnection.getContentLength();
      } catch (IOException var2) {
         throw new WebServiceException(var2);
      }
   }

   protected HttpURLConnection openConnection(Packet packet) {
      return null;
   }

   protected boolean checkHTTPS(HttpURLConnection connection) {
      if (connection instanceof HttpsURLConnection) {
         String verificationProperty = (String)this.context.invocationProperties.get("com.sun.xml.internal.ws.client.http.HostnameVerificationProperty");
         if (verificationProperty != null && verificationProperty.equalsIgnoreCase("true")) {
            ((HttpsURLConnection)connection).setHostnameVerifier(new HttpClientTransport.HttpClientVerifier());
         }

         HostnameVerifier verifier = (HostnameVerifier)this.context.invocationProperties.get("com.sun.xml.internal.ws.transport.https.client.hostname.verifier");
         if (verifier != null) {
            ((HttpsURLConnection)connection).setHostnameVerifier(verifier);
         }

         SSLSocketFactory sslSocketFactory = (SSLSocketFactory)this.context.invocationProperties.get("com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory");
         if (sslSocketFactory != null) {
            ((HttpsURLConnection)connection).setSSLSocketFactory(sslSocketFactory);
         }

         return true;
      } else {
         return false;
      }
   }

   private void createHttpConnection() throws IOException {
      this.httpConnection = this.openConnection(this.context);
      if (this.httpConnection == null) {
         this.httpConnection = (HttpURLConnection)this.endpoint.openConnection();
      }

      String scheme = this.endpoint.getURI().getScheme();
      if (scheme.equals("https")) {
         this.https = true;
      }

      if (this.checkHTTPS(this.httpConnection)) {
         this.https = true;
      }

      this.httpConnection.setAllowUserInteraction(true);
      this.httpConnection.setDoOutput(true);
      this.httpConnection.setDoInput(true);
      String requestMethod = (String)this.context.invocationProperties.get("javax.xml.ws.http.request.method");
      String method = requestMethod != null ? requestMethod : "POST";
      this.httpConnection.setRequestMethod(method);
      Integer reqTimeout = (Integer)this.context.invocationProperties.get("com.sun.xml.internal.ws.request.timeout");
      if (reqTimeout != null) {
         this.httpConnection.setReadTimeout(reqTimeout);
      }

      Integer connectTimeout = (Integer)this.context.invocationProperties.get("com.sun.xml.internal.ws.connect.timeout");
      if (connectTimeout != null) {
         this.httpConnection.setConnectTimeout(connectTimeout);
      }

      Integer chunkSize = (Integer)this.context.invocationProperties.get("com.sun.xml.internal.ws.transport.http.client.streaming.chunk.size");
      if (chunkSize != null) {
         this.httpConnection.setChunkedStreamingMode(chunkSize);
      }

      Iterator var7 = this.reqHeaders.entrySet().iterator();

      while(true) {
         Map.Entry entry;
         do {
            if (!var7.hasNext()) {
               return;
            }

            entry = (Map.Entry)var7.next();
         } while("Content-Length".equals(entry.getKey()));

         Iterator var9 = ((List)entry.getValue()).iterator();

         while(var9.hasNext()) {
            String value = (String)var9.next();
            this.httpConnection.addRequestProperty((String)entry.getKey(), value);
         }
      }
   }

   boolean isSecure() {
      return this.https;
   }

   protected void setStatusCode(int statusCode) {
      this.statusCode = statusCode;
   }

   private boolean requiresOutputStream() {
      return !this.httpConnection.getRequestMethod().equalsIgnoreCase("GET") && !this.httpConnection.getRequestMethod().equalsIgnoreCase("HEAD") && !this.httpConnection.getRequestMethod().equalsIgnoreCase("DELETE");
   }

   @Nullable
   String getContentType() {
      return this.httpConnection.getContentType();
   }

   public int getContentLength() {
      return this.httpConnection.getContentLength();
   }

   static {
      try {
         JAXBContext.newInstance().createUnmarshaller();
      } catch (JAXBException var1) {
      }

   }

   private static final class WSChunkedOuputStream extends FilterOutputStream {
      final int chunkSize;

      WSChunkedOuputStream(OutputStream actual, int chunkSize) {
         super(actual);
         this.chunkSize = chunkSize;
      }

      public void write(byte[] b, int off, int len) throws IOException {
         while(len > 0) {
            int sent = len > this.chunkSize ? this.chunkSize : len;
            this.out.write(b, off, sent);
            len -= sent;
            off += sent;
         }

      }
   }

   private static class LocalhostHttpClientVerifier implements HostnameVerifier {
      public boolean verify(String s, SSLSession sslSession) {
         return "localhost".equalsIgnoreCase(s) || "127.0.0.1".equals(s);
      }
   }

   private static class HttpClientVerifier implements HostnameVerifier {
      private HttpClientVerifier() {
      }

      public boolean verify(String s, SSLSession sslSession) {
         return true;
      }

      // $FF: synthetic method
      HttpClientVerifier(Object x0) {
         this();
      }
   }
}
