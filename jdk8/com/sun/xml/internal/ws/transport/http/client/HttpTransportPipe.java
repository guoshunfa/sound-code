package com.sun.xml.internal.ws.transport.http.client;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.client.ClientTransportException;
import com.sun.xml.internal.ws.developer.HttpConfigFeature;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.transport.Headers;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.xml.internal.ws.util.RuntimeVersion;
import com.sun.xml.internal.ws.util.StreamUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.SOAPBinding;

public class HttpTransportPipe extends AbstractTubeImpl {
   private static final List<String> USER_AGENT;
   private static final Logger LOGGER;
   public static boolean dump;
   private final Codec codec;
   private final WSBinding binding;
   private final CookieHandler cookieJar;
   private final boolean sticky;

   public HttpTransportPipe(Codec codec, WSBinding binding) {
      this.codec = codec;
      this.binding = binding;
      this.sticky = isSticky(binding);
      HttpConfigFeature configFeature = (HttpConfigFeature)binding.getFeature(HttpConfigFeature.class);
      if (configFeature == null) {
         configFeature = new HttpConfigFeature();
      }

      this.cookieJar = configFeature.getCookieHandler();
   }

   private static boolean isSticky(WSBinding binding) {
      boolean tSticky = false;
      WebServiceFeature[] features = binding.getFeatures().toArray();
      WebServiceFeature[] var3 = features;
      int var4 = features.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         WebServiceFeature f = var3[var5];
         if (f instanceof StickyFeature) {
            tSticky = true;
            break;
         }
      }

      return tSticky;
   }

   private HttpTransportPipe(HttpTransportPipe that, TubeCloner cloner) {
      this(that.codec.copy(), that.binding);
      cloner.add(that, this);
   }

   public NextAction processException(@NotNull Throwable t) {
      return this.doThrow(t);
   }

   public NextAction processRequest(@NotNull Packet request) {
      return this.doReturnWith(this.process(request));
   }

   public NextAction processResponse(@NotNull Packet response) {
      return this.doReturnWith(response);
   }

   protected HttpClientTransport getTransport(Packet request, Map<String, List<String>> reqHeaders) {
      return new HttpClientTransport(request, reqHeaders);
   }

   public Packet process(Packet request) {
      try {
         Map<String, List<String>> reqHeaders = new Headers();
         Map<String, List<String>> userHeaders = (Map)request.invocationProperties.get("javax.xml.ws.http.request.headers");
         boolean addUserAgent = true;
         if (userHeaders != null) {
            reqHeaders.putAll(userHeaders);
            if (userHeaders.get("User-Agent") != null) {
               addUserAgent = false;
            }
         }

         if (addUserAgent) {
            reqHeaders.put("User-Agent", USER_AGENT);
         }

         this.addBasicAuth(request, reqHeaders);
         this.addCookies(request, reqHeaders);
         HttpClientTransport con = this.getTransport(request, reqHeaders);
         request.addSatellite(new HttpResponseProperties(con));
         ContentType ct = this.codec.getStaticContentType(request);
         ByteArrayBuffer buf;
         if (ct == null) {
            buf = new ByteArrayBuffer();
            ct = this.codec.encode(request, (OutputStream)buf);
            reqHeaders.put("Content-Length", Collections.singletonList(Integer.toString(buf.size())));
            reqHeaders.put("Content-Type", Collections.singletonList(ct.getContentType()));
            if (ct.getAcceptHeader() != null) {
               reqHeaders.put("Accept", Collections.singletonList(ct.getAcceptHeader()));
            }

            if (this.binding instanceof SOAPBinding) {
               this.writeSOAPAction(reqHeaders, ct.getSOAPActionHeader());
            }

            if (dump || LOGGER.isLoggable(Level.FINER)) {
               this.dump(buf, "HTTP request", reqHeaders);
            }

            buf.writeTo(con.getOutput());
         } else {
            reqHeaders.put("Content-Type", Collections.singletonList(ct.getContentType()));
            if (ct.getAcceptHeader() != null) {
               reqHeaders.put("Accept", Collections.singletonList(ct.getAcceptHeader()));
            }

            if (this.binding instanceof SOAPBinding) {
               this.writeSOAPAction(reqHeaders, ct.getSOAPActionHeader());
            }

            if (!dump && !LOGGER.isLoggable(Level.FINER)) {
               OutputStream os = con.getOutput();
               if (os != null) {
                  this.codec.encode(request, os);
               }
            } else {
               buf = new ByteArrayBuffer();
               this.codec.encode(request, (OutputStream)buf);
               this.dump(buf, "HTTP request - " + request.endpointAddress, reqHeaders);
               OutputStream out = con.getOutput();
               if (out != null) {
                  buf.writeTo(out);
               }
            }
         }

         con.closeOutput();
         return this.createResponsePacket(request, con);
      } catch (WebServiceException var9) {
         throw var9;
      } catch (Exception var10) {
         throw new WebServiceException(var10);
      }
   }

   private Packet createResponsePacket(Packet request, HttpClientTransport con) throws IOException {
      con.readResponseCodeAndMessage();
      this.recordCookies(request, con);
      InputStream responseStream = con.getInput();
      if (dump || LOGGER.isLoggable(Level.FINER)) {
         ByteArrayBuffer buf = new ByteArrayBuffer();
         if (responseStream != null) {
            buf.write(responseStream);
            responseStream.close();
         }

         this.dump(buf, "HTTP response - " + request.endpointAddress + " - " + con.statusCode, con.getHeaders());
         responseStream = buf.newInputStream();
      }

      int cl = con.contentLength;
      InputStream tempIn = null;
      if (cl == -1) {
         tempIn = StreamUtils.hasSomeData(responseStream);
         if (tempIn != null) {
            responseStream = tempIn;
         }
      }

      if ((cl == 0 || cl == -1 && tempIn == null) && responseStream != null) {
         responseStream.close();
         responseStream = null;
      }

      this.checkStatusCode(responseStream, con);
      Packet reply = request.createClientResponse((Message)null);
      reply.wasTransportSecure = con.isSecure();
      if (responseStream != null) {
         String contentType = con.getContentType();
         if (contentType != null && contentType.contains("text/html") && this.binding instanceof SOAPBinding) {
            throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(con.statusCode, con.statusMessage));
         }

         this.codec.decode(responseStream, contentType, reply);
      }

      return reply;
   }

   private void checkStatusCode(InputStream in, HttpClientTransport con) throws IOException {
      int statusCode = con.statusCode;
      String statusMessage = con.statusMessage;
      if (this.binding instanceof SOAPBinding) {
         if (this.binding.getSOAPVersion() == SOAPVersion.SOAP_12) {
            if (statusCode == 200 || statusCode == 202 || this.isErrorCode(statusCode)) {
               if (this.isErrorCode(statusCode) && in == null) {
                  throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(statusCode, statusMessage));
               } else {
                  return;
               }
            }
         } else if (statusCode == 200 || statusCode == 202 || statusCode == 500) {
            if (statusCode == 500 && in == null) {
               throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(statusCode, statusMessage));
            }

            return;
         }

         if (in != null) {
            in.close();
         }

         throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(statusCode, statusMessage));
      }
   }

   private boolean isErrorCode(int code) {
      return code == 500 || code == 400;
   }

   private void addCookies(Packet context, Map<String, List<String>> reqHeaders) throws IOException {
      Boolean shouldMaintainSessionProperty = (Boolean)context.invocationProperties.get("javax.xml.ws.session.maintain");
      if (shouldMaintainSessionProperty == null || shouldMaintainSessionProperty) {
         if (this.sticky || shouldMaintainSessionProperty != null && shouldMaintainSessionProperty) {
            Map<String, List<String>> rememberedCookies = this.cookieJar.get(context.endpointAddress.getURI(), reqHeaders);
            this.processCookieHeaders(reqHeaders, rememberedCookies, "Cookie");
            this.processCookieHeaders(reqHeaders, rememberedCookies, "Cookie2");
         }

      }
   }

   private void processCookieHeaders(Map<String, List<String>> requestHeaders, Map<String, List<String>> rememberedCookies, String cookieHeader) {
      List<String> jarCookies = (List)rememberedCookies.get(cookieHeader);
      if (jarCookies != null && !jarCookies.isEmpty()) {
         List<String> resultCookies = this.mergeUserCookies(jarCookies, (List)requestHeaders.get(cookieHeader));
         requestHeaders.put(cookieHeader, resultCookies);
      }

   }

   private List<String> mergeUserCookies(List<String> rememberedCookies, List<String> userCookies) {
      if (userCookies != null && !userCookies.isEmpty()) {
         Map<String, String> map = new HashMap();
         this.cookieListToMap(rememberedCookies, map);
         this.cookieListToMap(userCookies, map);
         return new ArrayList(map.values());
      } else {
         return rememberedCookies;
      }
   }

   private void cookieListToMap(List<String> cookieList, Map<String, String> targetMap) {
      Iterator var3 = cookieList.iterator();

      while(var3.hasNext()) {
         String cookie = (String)var3.next();
         int index = cookie.indexOf("=");
         String cookieName = cookie.substring(0, index);
         targetMap.put(cookieName, cookie);
      }

   }

   private void recordCookies(Packet context, HttpClientTransport con) throws IOException {
      Boolean shouldMaintainSessionProperty = (Boolean)context.invocationProperties.get("javax.xml.ws.session.maintain");
      if (shouldMaintainSessionProperty == null || shouldMaintainSessionProperty) {
         if (this.sticky || shouldMaintainSessionProperty != null && shouldMaintainSessionProperty) {
            this.cookieJar.put(context.endpointAddress.getURI(), con.getHeaders());
         }

      }
   }

   private void addBasicAuth(Packet context, Map<String, List<String>> reqHeaders) {
      String user = (String)context.invocationProperties.get("javax.xml.ws.security.auth.username");
      if (user != null) {
         String pw = (String)context.invocationProperties.get("javax.xml.ws.security.auth.password");
         if (pw != null) {
            StringBuilder buf = new StringBuilder(user);
            buf.append(":");
            buf.append(pw);
            String creds = DatatypeConverter.printBase64Binary(buf.toString().getBytes());
            reqHeaders.put("Authorization", Collections.singletonList("Basic " + creds));
         }
      }

   }

   private void writeSOAPAction(Map<String, List<String>> reqHeaders, String soapAction) {
      if (!SOAPVersion.SOAP_12.equals(this.binding.getSOAPVersion())) {
         if (soapAction != null) {
            reqHeaders.put("SOAPAction", Collections.singletonList(soapAction));
         } else {
            reqHeaders.put("SOAPAction", Collections.singletonList("\"\""));
         }

      }
   }

   public void preDestroy() {
   }

   public HttpTransportPipe copy(TubeCloner cloner) {
      return new HttpTransportPipe(this, cloner);
   }

   private void dump(ByteArrayBuffer buf, String caption, Map<String, List<String>> headers) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(baos, true);
      pw.println("---[" + caption + "]---");
      Iterator var6 = headers.entrySet().iterator();

      while(true) {
         while(var6.hasNext()) {
            Map.Entry<String, List<String>> header = (Map.Entry)var6.next();
            if (((List)header.getValue()).isEmpty()) {
               pw.println(header.getValue());
            } else {
               Iterator var8 = ((List)header.getValue()).iterator();

               while(var8.hasNext()) {
                  String value = (String)var8.next();
                  pw.println((String)header.getKey() + ": " + value);
               }
            }
         }

         if (buf.size() > HttpAdapter.dump_threshold) {
            byte[] b = buf.getRawData();
            baos.write(b, 0, HttpAdapter.dump_threshold);
            pw.println();
            pw.println(WsservletMessages.MESSAGE_TOO_LONG(HttpAdapter.class.getName() + ".dumpTreshold"));
         } else {
            buf.writeTo(baos);
         }

         pw.println("--------------------");
         String msg = baos.toString();
         if (dump) {
            System.out.println(msg);
         }

         if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.log(Level.FINER, msg);
         }

         return;
      }
   }

   static {
      USER_AGENT = Collections.singletonList(RuntimeVersion.VERSION.toString());
      LOGGER = Logger.getLogger(HttpTransportPipe.class.getName());

      boolean b;
      try {
         b = Boolean.getBoolean(HttpTransportPipe.class.getName() + ".dump");
      } catch (Throwable var2) {
         b = false;
      }

      dump = b;
   }
}
