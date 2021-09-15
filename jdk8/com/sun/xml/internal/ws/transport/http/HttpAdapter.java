package com.sun.xml.internal.ws.transport.http;

import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.NonAnonymousResponseProcessor;
import com.sun.xml.internal.ws.api.ha.HaInfo;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.internal.ws.api.server.AbstractServerAsyncTransport;
import com.sun.xml.internal.ws.api.server.Adapter;
import com.sun.xml.internal.ws.api.server.BoundEndpoint;
import com.sun.xml.internal.ws.api.server.DocumentAddressResolver;
import com.sun.xml.internal.ws.api.server.Module;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.ServiceDefinition;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.server.UnsupportedMediaException;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.xml.internal.ws.util.Pool;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.http.HTTPBinding;

public class HttpAdapter extends Adapter<HttpAdapter.HttpToolkit> {
   private static final Logger LOGGER = Logger.getLogger(HttpAdapter.class.getName());
   protected Map<String, SDDocument> wsdls;
   private Map<SDDocument, String> revWsdls;
   private ServiceDefinition serviceDefinition;
   public final HttpAdapterList<? extends HttpAdapter> owner;
   public final String urlPattern;
   protected boolean stickyCookie;
   protected boolean disableJreplicaCookie;
   public static final HttpAdapter.CompletionCallback NO_OP_COMPLETION_CALLBACK = new HttpAdapter.CompletionCallback() {
      public void onCompletion() {
      }
   };
   public static volatile boolean dump = false;
   public static volatile int dump_threshold = 4096;
   public static volatile boolean publishStatusPage = true;

   public static HttpAdapter createAlone(WSEndpoint endpoint) {
      return (new HttpAdapter.DummyList()).createAdapter("", "", endpoint);
   }

   /** @deprecated */
   protected HttpAdapter(WSEndpoint endpoint, HttpAdapterList<? extends HttpAdapter> owner) {
      this(endpoint, owner, (String)null);
   }

   protected HttpAdapter(WSEndpoint endpoint, HttpAdapterList<? extends HttpAdapter> owner, String urlPattern) {
      super(endpoint);
      this.serviceDefinition = null;
      this.disableJreplicaCookie = false;
      this.owner = owner;
      this.urlPattern = urlPattern;
      this.initWSDLMap(endpoint.getServiceDefinition());
   }

   public ServiceDefinition getServiceDefinition() {
      return this.serviceDefinition;
   }

   public final void initWSDLMap(ServiceDefinition sdef) {
      this.serviceDefinition = sdef;
      if (sdef == null) {
         this.wsdls = Collections.emptyMap();
         this.revWsdls = Collections.emptyMap();
      } else {
         this.wsdls = new HashMap();
         Map<String, SDDocument> systemIds = new TreeMap();
         Iterator var3 = sdef.iterator();

         while(var3.hasNext()) {
            SDDocument sdd = (SDDocument)var3.next();
            if (sdd == sdef.getPrimary()) {
               this.wsdls.put("wsdl", sdd);
               this.wsdls.put("WSDL", sdd);
            } else {
               systemIds.put(sdd.getURL().toString(), sdd);
            }
         }

         int wsdlnum = 1;
         int xsdnum = 1;
         Iterator var5 = systemIds.entrySet().iterator();

         Map.Entry e;
         while(var5.hasNext()) {
            e = (Map.Entry)var5.next();
            SDDocument sdd = (SDDocument)e.getValue();
            if (sdd.isWSDL()) {
               this.wsdls.put("wsdl=" + wsdlnum++, sdd);
            }

            if (sdd.isSchema()) {
               this.wsdls.put("xsd=" + xsdnum++, sdd);
            }
         }

         this.revWsdls = new HashMap();
         var5 = this.wsdls.entrySet().iterator();

         while(var5.hasNext()) {
            e = (Map.Entry)var5.next();
            if (!((String)e.getKey()).equals("WSDL")) {
               this.revWsdls.put(e.getValue(), e.getKey());
            }
         }
      }

   }

   public String getValidPath() {
      return this.urlPattern.endsWith("/*") ? this.urlPattern.substring(0, this.urlPattern.length() - 2) : this.urlPattern;
   }

   protected HttpAdapter.HttpToolkit createToolkit() {
      return new HttpAdapter.HttpToolkit();
   }

   public void handle(@NotNull WSHTTPConnection connection) throws IOException {
      if (!this.handleGet(connection)) {
         Pool<HttpAdapter.HttpToolkit> currentPool = this.getPool();
         HttpAdapter.HttpToolkit tk = (HttpAdapter.HttpToolkit)currentPool.take();

         try {
            tk.handle(connection);
         } finally {
            currentPool.recycle(tk);
         }

      }
   }

   public boolean handleGet(@NotNull WSHTTPConnection connection) throws IOException {
      WSBinding binding;
      if (connection.getRequestMethod().equals("GET")) {
         Iterator var2 = this.endpoint.getComponents().iterator();

         while(var2.hasNext()) {
            Component c = (Component)var2.next();
            HttpMetadataPublisher spi = (HttpMetadataPublisher)c.getSPI(HttpMetadataPublisher.class);
            if (spi != null && spi.handleMetadataRequest(this, connection)) {
               return true;
            }
         }

         if (this.isMetadataQuery(connection.getQueryString())) {
            this.publishWSDL(connection);
            return true;
         }

         binding = this.getEndpoint().getBinding();
         if (!(binding instanceof HTTPBinding)) {
            this.writeWebServicesHtmlPage(connection);
            return true;
         }
      } else if (connection.getRequestMethod().equals("HEAD")) {
         connection.getInput().close();
         binding = this.getEndpoint().getBinding();
         if (this.isMetadataQuery(connection.getQueryString())) {
            SDDocument doc = (SDDocument)this.wsdls.get(connection.getQueryString());
            connection.setStatus(doc != null ? 200 : 404);
            connection.getOutput().close();
            connection.close();
            return true;
         }

         if (!(binding instanceof HTTPBinding)) {
            connection.setStatus(404);
            connection.getOutput().close();
            connection.close();
            return true;
         }
      }

      return false;
   }

   private Packet decodePacket(@NotNull WSHTTPConnection con, @NotNull Codec codec) throws IOException {
      String ct = con.getRequestHeader("Content-Type");
      InputStream in = con.getInput();
      Packet packet = new Packet();
      packet.soapAction = fixQuotesAroundSoapAction(con.getRequestHeader("SOAPAction"));
      packet.wasTransportSecure = con.isSecure();
      packet.acceptableMimeTypes = con.getRequestHeader("Accept");
      packet.addSatellite(con);
      this.addSatellites(packet);
      packet.isAdapterDeliversNonAnonymousResponse = true;
      packet.component = this;
      packet.transportBackChannel = new HttpAdapter.Oneway(con);
      packet.webServiceContextDelegate = con.getWebServiceContextDelegate();
      packet.setState(Packet.State.ServerRequest);
      if (dump || LOGGER.isLoggable(Level.FINER)) {
         ByteArrayBuffer buf = new ByteArrayBuffer();
         buf.write(in);
         in.close();
         dump(buf, "HTTP request", con.getRequestHeaders());
         in = buf.newInputStream();
      }

      codec.decode(in, ct, packet);
      return packet;
   }

   protected void addSatellites(Packet packet) {
   }

   public static String fixQuotesAroundSoapAction(String soapAction) {
      if (soapAction == null || soapAction.startsWith("\"") && soapAction.endsWith("\"")) {
         return soapAction;
      } else {
         if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, (String)"Received WS-I BP non-conformant Unquoted SoapAction HTTP header: {0}", (Object)soapAction);
         }

         String fixedSoapAction = soapAction;
         if (!soapAction.startsWith("\"")) {
            fixedSoapAction = "\"" + soapAction;
         }

         if (!soapAction.endsWith("\"")) {
            fixedSoapAction = fixedSoapAction + "\"";
         }

         return fixedSoapAction;
      }
   }

   protected NonAnonymousResponseProcessor getNonAnonymousResponseProcessor() {
      return NonAnonymousResponseProcessor.getDefault();
   }

   protected void writeClientError(int connStatus, @NotNull OutputStream os, @NotNull Packet packet) throws IOException {
   }

   private boolean isClientErrorStatus(int connStatus) {
      return connStatus == 403;
   }

   private boolean isNonAnonymousUri(EndpointAddress addr) {
      return addr != null && !addr.toString().equals(AddressingVersion.W3C.anonymousUri) && !addr.toString().equals(AddressingVersion.MEMBER.anonymousUri);
   }

   private void encodePacket(@NotNull Packet packet, @NotNull WSHTTPConnection con, @NotNull Codec codec) throws IOException {
      if (this.isNonAnonymousUri(packet.endpointAddress) && packet.getMessage() != null) {
         try {
            packet = this.getNonAnonymousResponseProcessor().process(packet);
         } catch (RuntimeException var9) {
            SOAPVersion soapVersion = packet.getBinding().getSOAPVersion();
            Message faultMsg = SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, (CheckedExceptionImpl)null, (Throwable)var9);
            packet = packet.createServerResponse(faultMsg, (WSDLPort)packet.endpoint.getPort(), (SEIModel)null, (WSBinding)packet.endpoint.getBinding());
         }
      }

      if (!con.isClosed()) {
         Message responseMessage = packet.getMessage();
         this.addStickyCookie(con);
         this.addReplicaCookie(con, packet);
         ByteArrayBuffer buf;
         if (responseMessage == null) {
            if (!con.isClosed()) {
               if (con.getStatus() == 0) {
                  con.setStatus(202);
               }

               OutputStream os = con.getProtocol().contains("1.1") ? con.getOutput() : new HttpAdapter.Http10OutputStream(con);
               if (!dump && !LOGGER.isLoggable(Level.FINER)) {
                  codec.encode(packet, (OutputStream)os);
               } else {
                  buf = new ByteArrayBuffer();
                  codec.encode(packet, (OutputStream)buf);
                  dump(buf, "HTTP response " + con.getStatus(), con.getResponseHeaders());
                  buf.writeTo((OutputStream)os);
               }

               try {
                  ((OutputStream)os).close();
               } catch (IOException var8) {
                  throw new WebServiceException(var8);
               }
            }
         } else {
            if (con.getStatus() == 0) {
               con.setStatus(responseMessage.isFault() ? 500 : 200);
            }

            if (this.isClientErrorStatus(con.getStatus())) {
               OutputStream os = con.getOutput();
               if (!dump && !LOGGER.isLoggable(Level.FINER)) {
                  this.writeClientError(con.getStatus(), os, packet);
               } else {
                  buf = new ByteArrayBuffer();
                  this.writeClientError(con.getStatus(), buf, packet);
                  dump(buf, "HTTP response " + con.getStatus(), con.getResponseHeaders());
                  buf.writeTo(os);
               }

               os.close();
               return;
            }

            ContentType contentType = codec.getStaticContentType(packet);
            if (contentType != null) {
               con.setContentTypeResponseHeader(contentType.getContentType());
               OutputStream os = con.getProtocol().contains("1.1") ? con.getOutput() : new HttpAdapter.Http10OutputStream(con);
               if (!dump && !LOGGER.isLoggable(Level.FINER)) {
                  codec.encode(packet, (OutputStream)os);
               } else {
                  ByteArrayBuffer buf = new ByteArrayBuffer();
                  codec.encode(packet, (OutputStream)buf);
                  dump(buf, "HTTP response " + con.getStatus(), con.getResponseHeaders());
                  buf.writeTo((OutputStream)os);
               }

               ((OutputStream)os).close();
            } else {
               buf = new ByteArrayBuffer();
               contentType = codec.encode(packet, (OutputStream)buf);
               con.setContentTypeResponseHeader(contentType.getContentType());
               if (dump || LOGGER.isLoggable(Level.FINER)) {
                  dump(buf, "HTTP response " + con.getStatus(), con.getResponseHeaders());
               }

               OutputStream os = con.getOutput();
               buf.writeTo(os);
               os.close();
            }
         }

      }
   }

   private void addStickyCookie(WSHTTPConnection con) {
      if (this.stickyCookie) {
         String proxyJroute = con.getRequestHeader("proxy-jroute");
         if (proxyJroute == null) {
            return;
         }

         String jrouteId = con.getCookie("JROUTE");
         if (jrouteId == null || !jrouteId.equals(proxyJroute)) {
            con.setCookie("JROUTE", proxyJroute);
         }
      }

   }

   private void addReplicaCookie(WSHTTPConnection con, Packet packet) {
      if (this.stickyCookie) {
         HaInfo haInfo = null;
         if (packet.supports("com.sun.xml.internal.ws.api.message.packet.hainfo")) {
            haInfo = (HaInfo)packet.get("com.sun.xml.internal.ws.api.message.packet.hainfo");
         }

         if (haInfo != null) {
            con.setCookie("METRO_KEY", haInfo.getKey());
            if (!this.disableJreplicaCookie) {
               con.setCookie("JREPLICA", haInfo.getReplicaInstance());
            }
         }
      }

   }

   public void invokeAsync(WSHTTPConnection con) throws IOException {
      this.invokeAsync(con, NO_OP_COMPLETION_CALLBACK);
   }

   public void invokeAsync(final WSHTTPConnection con, final HttpAdapter.CompletionCallback callback) throws IOException {
      if (this.handleGet(con)) {
         callback.onCompletion();
      } else {
         final Pool<HttpAdapter.HttpToolkit> currentPool = this.getPool();
         final HttpAdapter.HttpToolkit tk = (HttpAdapter.HttpToolkit)currentPool.take();

         Packet request;
         Packet response;
         try {
            request = this.decodePacket(con, tk.codec);
         } catch (ExceptionHasMessage var8) {
            LOGGER.log(Level.SEVERE, (String)var8.getMessage(), (Throwable)var8);
            response = new Packet();
            response.setMessage(var8.getFaultMessage());
            this.encodePacket(response, con, tk.codec);
            currentPool.recycle(tk);
            con.close();
            callback.onCompletion();
            return;
         } catch (UnsupportedMediaException var9) {
            LOGGER.log(Level.SEVERE, (String)var9.getMessage(), (Throwable)var9);
            response = new Packet();
            con.setStatus(415);
            this.encodePacket(response, con, tk.codec);
            currentPool.recycle(tk);
            con.close();
            callback.onCompletion();
            return;
         }

         this.endpoint.process(request, new WSEndpoint.CompletionCallback() {
            public void onCompletion(@NotNull Packet response) {
               try {
                  try {
                     HttpAdapter.this.encodePacket(response, con, tk.codec);
                  } catch (IOException var6) {
                     HttpAdapter.LOGGER.log(Level.SEVERE, (String)var6.getMessage(), (Throwable)var6);
                  }

                  currentPool.recycle(tk);
               } finally {
                  con.close();
                  callback.onCompletion();
               }

            }
         }, (FiberContextSwitchInterceptor)null);
      }
   }

   private boolean isMetadataQuery(String query) {
      return query != null && (query.equals("WSDL") || query.startsWith("wsdl") || query.startsWith("xsd="));
   }

   public void publishWSDL(@NotNull WSHTTPConnection con) throws IOException {
      con.getInput().close();
      SDDocument doc = (SDDocument)this.wsdls.get(con.getQueryString());
      if (doc == null) {
         this.writeNotFoundErrorPage(con, "Invalid Request");
      } else {
         con.setStatus(200);
         con.setContentTypeResponseHeader("text/xml;charset=utf-8");
         OutputStream os = con.getProtocol().contains("1.1") ? con.getOutput() : new HttpAdapter.Http10OutputStream(con);
         PortAddressResolver portAddressResolver = this.getPortAddressResolver(con.getBaseAddress());
         DocumentAddressResolver resolver = this.getDocumentAddressResolver(portAddressResolver);
         doc.writeTo(portAddressResolver, resolver, (OutputStream)os);
         ((OutputStream)os).close();
      }
   }

   public PortAddressResolver getPortAddressResolver(String baseAddress) {
      return this.owner.createPortAddressResolver(baseAddress, this.endpoint.getImplementationClass());
   }

   public DocumentAddressResolver getDocumentAddressResolver(PortAddressResolver portAddressResolver) {
      final String address = portAddressResolver.getAddressFor(this.endpoint.getServiceName(), this.endpoint.getPortName().getLocalPart());

      assert address != null;

      return new DocumentAddressResolver() {
         public String getRelativeAddressFor(@NotNull SDDocument current, @NotNull SDDocument referenced) {
            assert HttpAdapter.this.revWsdls.containsKey(referenced);

            return address + '?' + (String)HttpAdapter.this.revWsdls.get(referenced);
         }
      };
   }

   private void writeNotFoundErrorPage(WSHTTPConnection con, String message) throws IOException {
      con.setStatus(404);
      con.setContentTypeResponseHeader("text/html; charset=utf-8");
      PrintWriter out = new PrintWriter(new OutputStreamWriter(con.getOutput(), "UTF-8"));
      out.println("<html>");
      out.println("<head><title>");
      out.println(WsservletMessages.SERVLET_HTML_TITLE());
      out.println("</title></head>");
      out.println("<body>");
      out.println(WsservletMessages.SERVLET_HTML_NOT_FOUND(message));
      out.println("</body>");
      out.println("</html>");
      out.close();
   }

   private void writeInternalServerError(WSHTTPConnection con) throws IOException {
      con.setStatus(500);
      con.getOutput().close();
   }

   private static void dump(ByteArrayBuffer buf, String caption, Map<String, List<String>> headers) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(baos, true);
      pw.println("---[" + caption + "]---");
      if (headers != null) {
         Iterator var5 = headers.entrySet().iterator();

         label39:
         while(true) {
            while(true) {
               if (!var5.hasNext()) {
                  break label39;
               }

               Map.Entry<String, List<String>> header = (Map.Entry)var5.next();
               if (((List)header.getValue()).isEmpty()) {
                  pw.println(header.getValue());
               } else {
                  Iterator var7 = ((List)header.getValue()).iterator();

                  while(var7.hasNext()) {
                     String value = (String)var7.next();
                     pw.println((String)header.getKey() + ": " + value);
                  }
               }
            }
         }
      }

      if (buf.size() > dump_threshold) {
         byte[] b = buf.getRawData();
         baos.write(b, 0, dump_threshold);
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

   }

   private void writeWebServicesHtmlPage(WSHTTPConnection con) throws IOException {
      if (publishStatusPage) {
         con.getInput().close();
         con.setStatus(200);
         con.setContentTypeResponseHeader("text/html; charset=utf-8");
         PrintWriter out = new PrintWriter(new OutputStreamWriter(con.getOutput(), "UTF-8"));
         out.println("<html>");
         out.println("<head><title>");
         out.println(WsservletMessages.SERVLET_HTML_TITLE());
         out.println("</title></head>");
         out.println("<body>");
         out.println(WsservletMessages.SERVLET_HTML_TITLE_2());
         Module module = (Module)this.getEndpoint().getContainer().getSPI(Module.class);
         List<BoundEndpoint> endpoints = Collections.emptyList();
         if (module != null) {
            endpoints = module.getBoundEndpoints();
         }

         if (endpoints.isEmpty()) {
            out.println(WsservletMessages.SERVLET_HTML_NO_INFO_AVAILABLE());
         } else {
            out.println("<table width='100%' border='1'>");
            out.println("<tr>");
            out.println("<td>");
            out.println(WsservletMessages.SERVLET_HTML_COLUMN_HEADER_PORT_NAME());
            out.println("</td>");
            out.println("<td>");
            out.println(WsservletMessages.SERVLET_HTML_COLUMN_HEADER_INFORMATION());
            out.println("</td>");
            out.println("</tr>");
            Iterator var5 = endpoints.iterator();

            while(var5.hasNext()) {
               BoundEndpoint a = (BoundEndpoint)var5.next();
               String endpointAddress = a.getAddress(con.getBaseAddress()).toString();
               out.println("<tr>");
               out.println("<td>");
               out.println(WsservletMessages.SERVLET_HTML_ENDPOINT_TABLE(a.getEndpoint().getServiceName(), a.getEndpoint().getPortName()));
               out.println("</td>");
               out.println("<td>");
               out.println(WsservletMessages.SERVLET_HTML_INFORMATION_TABLE(endpointAddress, a.getEndpoint().getImplementationClass().getName()));
               out.println("</td>");
               out.println("</tr>");
            }

            out.println("</table>");
         }

         out.println("</body>");
         out.println("</html>");
         out.close();
      }
   }

   public static synchronized void setPublishStatus(boolean publish) {
      publishStatusPage = publish;
   }

   public static void setDump(boolean dumpMessages) {
      dump = dumpMessages;
   }

   static {
      try {
         dump = Boolean.getBoolean(HttpAdapter.class.getName() + ".dump");
      } catch (SecurityException var3) {
         if (LOGGER.isLoggable(Level.CONFIG)) {
            LOGGER.log(Level.CONFIG, "Cannot read ''{0}'' property, using defaults.", new Object[]{HttpAdapter.class.getName() + ".dump"});
         }
      }

      try {
         dump_threshold = Integer.getInteger(HttpAdapter.class.getName() + ".dumpTreshold", 4096);
      } catch (SecurityException var2) {
         if (LOGGER.isLoggable(Level.CONFIG)) {
            LOGGER.log(Level.CONFIG, "Cannot read ''{0}'' property, using defaults.", new Object[]{HttpAdapter.class.getName() + ".dumpTreshold"});
         }
      }

      try {
         setPublishStatus(Boolean.getBoolean(HttpAdapter.class.getName() + ".publishStatusPage"));
      } catch (SecurityException var1) {
         if (LOGGER.isLoggable(Level.CONFIG)) {
            LOGGER.log(Level.CONFIG, "Cannot read ''{0}'' property, using defaults.", new Object[]{HttpAdapter.class.getName() + ".publishStatusPage"});
         }
      }

   }

   private static final class DummyList extends HttpAdapterList<HttpAdapter> {
      private DummyList() {
      }

      protected HttpAdapter createHttpAdapter(String name, String urlPattern, WSEndpoint<?> endpoint) {
         return new HttpAdapter(endpoint, this, urlPattern);
      }

      // $FF: synthetic method
      DummyList(Object x0) {
         this();
      }
   }

   private static final class Http10OutputStream extends ByteArrayBuffer {
      private final WSHTTPConnection con;

      Http10OutputStream(WSHTTPConnection con) {
         this.con = con;
      }

      public void close() throws IOException {
         super.close();
         this.con.setContentLengthResponseHeader(this.size());
         OutputStream os = this.con.getOutput();
         this.writeTo(os);
         os.close();
      }
   }

   final class HttpToolkit extends Adapter.Toolkit {
      HttpToolkit() {
         super();
      }

      public void handle(WSHTTPConnection con) throws IOException {
         try {
            boolean invoke = false;

            Packet packet;
            try {
               packet = HttpAdapter.this.decodePacket(con, this.codec);
               invoke = true;
            } catch (Exception var9) {
               packet = new Packet();
               if (var9 instanceof ExceptionHasMessage) {
                  HttpAdapter.LOGGER.log(Level.SEVERE, (String)var9.getMessage(), (Throwable)var9);
                  packet.setMessage(((ExceptionHasMessage)var9).getFaultMessage());
               } else if (var9 instanceof UnsupportedMediaException) {
                  HttpAdapter.LOGGER.log(Level.SEVERE, (String)var9.getMessage(), (Throwable)var9);
                  con.setStatus(415);
               } else {
                  HttpAdapter.LOGGER.log(Level.SEVERE, (String)var9.getMessage(), (Throwable)var9);
                  con.setStatus(500);
               }
            }

            if (invoke) {
               try {
                  packet = this.head.process(packet, con.getWebServiceContextDelegate(), packet.transportBackChannel);
               } catch (Throwable var10) {
                  HttpAdapter.LOGGER.log(Level.SEVERE, var10.getMessage(), var10);
                  if (!con.isClosed()) {
                     HttpAdapter.this.writeInternalServerError(con);
                  }

                  return;
               }
            }

            HttpAdapter.this.encodePacket(packet, con, this.codec);
         } finally {
            if (!con.isClosed()) {
               if (HttpAdapter.LOGGER.isLoggable(Level.FINE)) {
                  HttpAdapter.LOGGER.log(Level.FINE, (String)"Closing HTTP Connection with status: {0}", (Object)con.getStatus());
               }

               con.close();
            }

         }
      }
   }

   static final class Oneway implements TransportBackChannel {
      WSHTTPConnection con;
      boolean closed;

      Oneway(WSHTTPConnection con) {
         this.con = con;
      }

      public void close() {
         if (!this.closed) {
            this.closed = true;
            if (this.con.getStatus() == 0) {
               this.con.setStatus(202);
            }

            OutputStream output = null;

            try {
               output = this.con.getOutput();
            } catch (IOException var5) {
            }

            if (HttpAdapter.dump || HttpAdapter.LOGGER.isLoggable(Level.FINER)) {
               try {
                  ByteArrayBuffer buf = new ByteArrayBuffer();
                  HttpAdapter.dump(buf, "HTTP response " + this.con.getStatus(), this.con.getResponseHeaders());
               } catch (Exception var4) {
                  throw new WebServiceException(var4.toString(), var4);
               }
            }

            if (output != null) {
               try {
                  output.close();
               } catch (IOException var3) {
                  throw new WebServiceException(var3);
               }
            }

            this.con.close();
         }

      }
   }

   final class AsyncTransport extends AbstractServerAsyncTransport<WSHTTPConnection> {
      public AsyncTransport() {
         super(HttpAdapter.this.endpoint);
      }

      public void handleAsync(WSHTTPConnection con) throws IOException {
         super.handle(con);
      }

      protected void encodePacket(WSHTTPConnection con, @NotNull Packet packet, @NotNull Codec codec) throws IOException {
         HttpAdapter.this.encodePacket(packet, con, codec);
      }

      @Nullable
      protected String getAcceptableMimeTypes(WSHTTPConnection con) {
         return null;
      }

      @Nullable
      protected TransportBackChannel getTransportBackChannel(WSHTTPConnection con) {
         return new HttpAdapter.Oneway(con);
      }

      @NotNull
      protected PropertySet getPropertySet(WSHTTPConnection con) {
         return con;
      }

      @NotNull
      protected WebServiceContextDelegate getWebServiceContextDelegate(WSHTTPConnection con) {
         return con.getWebServiceContextDelegate();
      }
   }

   public interface CompletionCallback {
      void onCompletion();
   }
}
