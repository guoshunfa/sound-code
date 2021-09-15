package com.sun.xml.internal.ws.client.dispatch;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.AsyncInvoker;
import com.sun.xml.internal.ws.client.AsyncResponseImpl;
import com.sun.xml.internal.ws.client.RequestContext;
import com.sun.xml.internal.ws.client.ResponseContext;
import com.sun.xml.internal.ws.client.ResponseContextReceiver;
import com.sun.xml.internal.ws.client.Stub;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.encoding.soap.DeserializationException;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import com.sun.xml.internal.ws.resources.DispatchMessages;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

public abstract class DispatchImpl<T> extends Stub implements Dispatch<T> {
   private static final Logger LOGGER = Logger.getLogger(DispatchImpl.class.getName());
   final Service.Mode mode;
   final SOAPVersion soapVersion;
   final boolean allowFaultResponseMsg;
   static final long AWAIT_TERMINATION_TIME = 800L;
   static final String HTTP_REQUEST_METHOD_GET = "GET";
   static final String HTTP_REQUEST_METHOD_POST = "POST";
   static final String HTTP_REQUEST_METHOD_PUT = "PUT";

   /** @deprecated */
   @Deprecated
   protected DispatchImpl(QName port, Service.Mode mode, WSServiceDelegate owner, Tube pipe, BindingImpl binding, @Nullable WSEndpointReference epr) {
      super(port, owner, pipe, binding, owner.getWsdlService() != null ? owner.getWsdlService().get(port) : null, owner.getEndpointAddress(port), epr);
      this.mode = mode;
      this.soapVersion = binding.getSOAPVersion();
      this.allowFaultResponseMsg = false;
   }

   protected DispatchImpl(WSPortInfo portInfo, Service.Mode mode, BindingImpl binding, @Nullable WSEndpointReference epr) {
      this(portInfo, mode, binding, epr, false);
   }

   protected DispatchImpl(WSPortInfo portInfo, Service.Mode mode, BindingImpl binding, @Nullable WSEndpointReference epr, boolean allowFaultResponseMsg) {
      this(portInfo, mode, (BindingImpl)binding, (Tube)null, epr, allowFaultResponseMsg);
   }

   protected DispatchImpl(WSPortInfo portInfo, Service.Mode mode, BindingImpl binding, Tube pipe, @Nullable WSEndpointReference epr, boolean allowFaultResponseMsg) {
      super(portInfo, binding, pipe, portInfo.getEndpointAddress(), epr);
      this.mode = mode;
      this.soapVersion = binding.getSOAPVersion();
      this.allowFaultResponseMsg = allowFaultResponseMsg;
   }

   protected DispatchImpl(WSPortInfo portInfo, Service.Mode mode, Tube pipe, BindingImpl binding, @Nullable WSEndpointReference epr, boolean allowFaultResponseMsg) {
      super(portInfo, binding, pipe, portInfo.getEndpointAddress(), epr);
      this.mode = mode;
      this.soapVersion = binding.getSOAPVersion();
      this.allowFaultResponseMsg = allowFaultResponseMsg;
   }

   abstract Packet createPacket(T var1);

   abstract T toReturnValue(Packet var1);

   public final Response<T> invokeAsync(T param) {
      Container old = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());

      AsyncResponseImpl var5;
      try {
         if (LOGGER.isLoggable(Level.FINE)) {
            this.dumpParam(param, "invokeAsync(T)");
         }

         AsyncInvoker invoker = new DispatchImpl.DispatchAsyncInvoker(param);
         AsyncResponseImpl<T> ft = new AsyncResponseImpl(invoker, (AsyncHandler)null);
         invoker.setReceiver(ft);
         ft.run();
         var5 = ft;
      } finally {
         ContainerResolver.getDefault().exitContainer(old);
      }

      return var5;
   }

   private void dumpParam(T param, String method) {
      if (param instanceof Packet) {
         Packet message = (Packet)param;
         if (LOGGER.isLoggable(Level.FINE)) {
            AddressingVersion av = this.getBinding().getAddressingVersion();
            SOAPVersion sv = this.getBinding().getSOAPVersion();
            String action = av != null && message.getMessage() != null ? AddressingUtils.getAction(message.getMessage().getHeaders(), av, sv) : null;
            String msgId = av != null && message.getMessage() != null ? AddressingUtils.getMessageID(message.getMessage().getHeaders(), av, sv) : null;
            LOGGER.fine("In DispatchImpl." + method + " for message with action: " + action + " and msg ID: " + msgId + " msg: " + message.getMessage());
            if (message.getMessage() == null) {
               LOGGER.fine("Dispatching null message for action: " + action + " and msg ID: " + msgId);
            }
         }
      }

   }

   public final Future<?> invokeAsync(T param, AsyncHandler<T> asyncHandler) {
      Container old = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());

      AsyncResponseImpl var6;
      try {
         if (LOGGER.isLoggable(Level.FINE)) {
            this.dumpParam(param, "invokeAsync(T, AsyncHandler<T>)");
         }

         AsyncInvoker invoker = new DispatchImpl.DispatchAsyncInvoker(param);
         AsyncResponseImpl<T> ft = new AsyncResponseImpl(invoker, asyncHandler);
         invoker.setReceiver(ft);
         invoker.setNonNullAsyncHandlerGiven(asyncHandler != null);
         ft.run();
         var6 = ft;
      } finally {
         ContainerResolver.getDefault().exitContainer(old);
      }

      return var6;
   }

   public final T doInvoke(T in, RequestContext rc, ResponseContextReceiver receiver) {
      Packet response = null;

      Object var17;
      try {
         try {
            checkNullAllowed(in, rc, this.binding, this.mode);
            Packet message = this.createPacket(in);
            message.setState(Packet.State.ClientRequest);
            this.resolveEndpointAddress(message, rc);
            this.setProperties(message, true);
            response = this.process(message, rc, receiver);
            Message msg = response.getMessage();
            if (msg != null && msg.isFault() && !this.allowFaultResponseMsg) {
               SOAPFaultBuilder faultBuilder = SOAPFaultBuilder.create(msg);
               throw (SOAPFaultException)faultBuilder.createException((Map)null);
            }
         } catch (JAXBException var13) {
            throw new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), new Object[]{var13});
         } catch (WebServiceException var14) {
            throw var14;
         } catch (Throwable var15) {
            throw new WebServiceException(var15);
         }

         var17 = this.toReturnValue(response);
      } finally {
         if (response != null && response.transportBackChannel != null) {
            response.transportBackChannel.close();
         }

      }

      return var17;
   }

   public final T invoke(T in) {
      Container old = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());

      Object var3;
      try {
         if (LOGGER.isLoggable(Level.FINE)) {
            this.dumpParam(in, "invoke(T)");
         }

         var3 = this.doInvoke(in, this.requestContext, this);
      } finally {
         ContainerResolver.getDefault().exitContainer(old);
      }

      return var3;
   }

   public final void invokeOneWay(T in) {
      Container old = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());

      try {
         if (LOGGER.isLoggable(Level.FINE)) {
            this.dumpParam(in, "invokeOneWay(T)");
         }

         try {
            checkNullAllowed(in, this.requestContext, this.binding, this.mode);
            Packet request = this.createPacket(in);
            request.setState(Packet.State.ClientRequest);
            this.setProperties(request, false);
            this.process(request, this.requestContext, this);
         } catch (WebServiceException var8) {
            throw var8;
         } catch (Throwable var9) {
            throw new WebServiceException(var9);
         }
      } finally {
         ContainerResolver.getDefault().exitContainer(old);
      }

   }

   void setProperties(Packet packet, boolean expectReply) {
      packet.expectReply = expectReply;
   }

   static boolean isXMLHttp(@NotNull WSBinding binding) {
      return binding.getBindingId().equals(BindingID.XML_HTTP);
   }

   static boolean isPAYLOADMode(@NotNull Service.Mode mode) {
      return mode == Service.Mode.PAYLOAD;
   }

   static void checkNullAllowed(@Nullable Object in, RequestContext rc, WSBinding binding, Service.Mode mode) {
      if (in == null) {
         if (isXMLHttp(binding)) {
            if (methodNotOk(rc)) {
               throw new WebServiceException(DispatchMessages.INVALID_NULLARG_XMLHTTP_REQUEST_METHOD("POST", "GET"));
            }
         } else if (mode == Service.Mode.MESSAGE) {
            throw new WebServiceException(DispatchMessages.INVALID_NULLARG_SOAP_MSGMODE(mode.name(), Service.Mode.PAYLOAD.toString()));
         }

      }
   }

   static boolean methodNotOk(@NotNull RequestContext rc) {
      String requestMethod = (String)rc.get("javax.xml.ws.http.request.method");
      String request = requestMethod == null ? "POST" : requestMethod;
      return "POST".equalsIgnoreCase(request) || "PUT".equalsIgnoreCase(request);
   }

   public static void checkValidSOAPMessageDispatch(WSBinding binding, Service.Mode mode) {
      if (isXMLHttp(binding)) {
         throw new WebServiceException(DispatchMessages.INVALID_SOAPMESSAGE_DISPATCH_BINDING("http://www.w3.org/2004/08/wsdl/http", "http://schemas.xmlsoap.org/wsdl/soap/http or http://www.w3.org/2003/05/soap/bindings/HTTP/"));
      } else if (isPAYLOADMode(mode)) {
         throw new WebServiceException(DispatchMessages.INVALID_SOAPMESSAGE_DISPATCH_MSGMODE(mode.name(), Service.Mode.MESSAGE.toString()));
      }
   }

   public static void checkValidDataSourceDispatch(WSBinding binding, Service.Mode mode) {
      if (!isXMLHttp(binding)) {
         throw new WebServiceException(DispatchMessages.INVALID_DATASOURCE_DISPATCH_BINDING("SOAP/HTTP", "http://www.w3.org/2004/08/wsdl/http"));
      } else if (isPAYLOADMode(mode)) {
         throw new WebServiceException(DispatchMessages.INVALID_DATASOURCE_DISPATCH_MSGMODE(mode.name(), Service.Mode.MESSAGE.toString()));
      }
   }

   @NotNull
   public final QName getPortName() {
      return this.portname;
   }

   void resolveEndpointAddress(@NotNull Packet message, @NotNull RequestContext requestContext) {
      boolean p = message.packetTakesPriorityOverRequestContext;
      String endpoint;
      if (p && message.endpointAddress != null) {
         endpoint = message.endpointAddress.toString();
      } else {
         endpoint = (String)requestContext.get("javax.xml.ws.service.endpoint.address");
      }

      if (endpoint == null) {
         if (message.endpointAddress == null) {
            throw new WebServiceException(DispatchMessages.INVALID_NULLARG_URI());
         }

         endpoint = message.endpointAddress.toString();
      }

      String pathInfo = null;
      String queryString = null;
      if (p && message.invocationProperties.get("javax.xml.ws.http.request.pathinfo") != null) {
         pathInfo = (String)message.invocationProperties.get("javax.xml.ws.http.request.pathinfo");
      } else if (requestContext.get("javax.xml.ws.http.request.pathinfo") != null) {
         pathInfo = (String)requestContext.get("javax.xml.ws.http.request.pathinfo");
      }

      if (p && message.invocationProperties.get("javax.xml.ws.http.request.querystring") != null) {
         queryString = (String)message.invocationProperties.get("javax.xml.ws.http.request.querystring");
      } else if (requestContext.get("javax.xml.ws.http.request.querystring") != null) {
         queryString = (String)requestContext.get("javax.xml.ws.http.request.querystring");
      }

      if (pathInfo != null || queryString != null) {
         pathInfo = checkPath(pathInfo);
         queryString = checkQuery(queryString);
         if (endpoint != null) {
            try {
               URI endpointURI = new URI(endpoint);
               endpoint = this.resolveURI(endpointURI, pathInfo, queryString);
            } catch (URISyntaxException var8) {
               throw new WebServiceException(DispatchMessages.INVALID_URI(endpoint));
            }
         }
      }

      requestContext.put("javax.xml.ws.service.endpoint.address", endpoint);
   }

   @NotNull
   protected String resolveURI(@NotNull URI endpointURI, @Nullable String pathInfo, @Nullable String queryString) {
      String query = null;
      String fragment = null;
      if (queryString != null) {
         URI result;
         try {
            URI tp = new URI((String)null, (String)null, endpointURI.getPath(), queryString, (String)null);
            result = endpointURI.resolve(tp);
         } catch (URISyntaxException var9) {
            throw new WebServiceException(DispatchMessages.INVALID_QUERY_STRING(queryString));
         }

         query = result.getQuery();
         fragment = result.getFragment();
      }

      String path = pathInfo != null ? pathInfo : endpointURI.getPath();

      try {
         StringBuilder spec = new StringBuilder();
         if (path != null) {
            spec.append(path);
         }

         if (query != null) {
            spec.append("?");
            spec.append(query);
         }

         if (fragment != null) {
            spec.append("#");
            spec.append(fragment);
         }

         return (new URL(endpointURI.toURL(), spec.toString())).toExternalForm();
      } catch (MalformedURLException var8) {
         throw new WebServiceException(DispatchMessages.INVALID_URI_RESOLUTION(path));
      }
   }

   private static String checkPath(@Nullable String path) {
      return path != null && !path.startsWith("/") ? "/" + path : path;
   }

   private static String checkQuery(@Nullable String query) {
      if (query == null) {
         return null;
      } else if (query.indexOf(63) == 0) {
         throw new WebServiceException(DispatchMessages.INVALID_QUERY_LEADING_CHAR(query));
      } else {
         return query;
      }
   }

   protected AttachmentSet setOutboundAttachments() {
      HashMap<String, DataHandler> attachments = (HashMap)this.getRequestContext().get("javax.xml.ws.binding.attachments.outbound");
      if (attachments == null) {
         return new AttachmentSetImpl();
      } else {
         List<Attachment> alist = new ArrayList();
         Iterator var3 = attachments.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry<String, DataHandler> att = (Map.Entry)var3.next();
            DataHandlerAttachment dha = new DataHandlerAttachment((String)att.getKey(), (DataHandler)att.getValue());
            alist.add(dha);
         }

         return new AttachmentSetImpl(alist);
      }
   }

   public void setOutboundHeaders(Object... headers) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static Dispatch<Source> createSourceDispatch(QName port, Service.Mode mode, WSServiceDelegate owner, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
      return (Dispatch)(isXMLHttp(binding) ? new RESTSourceDispatch(port, mode, owner, pipe, binding, epr) : new SOAPSourceDispatch(port, mode, owner, pipe, binding, epr));
   }

   public static Dispatch<Source> createSourceDispatch(WSPortInfo portInfo, Service.Mode mode, BindingImpl binding, WSEndpointReference epr) {
      return (Dispatch)(isXMLHttp(binding) ? new RESTSourceDispatch(portInfo, mode, binding, epr) : new SOAPSourceDispatch(portInfo, mode, binding, epr));
   }

   private class DispatchAsyncInvoker extends AsyncInvoker {
      private final T param;
      private final RequestContext rc;

      DispatchAsyncInvoker(T param) {
         this.rc = DispatchImpl.this.requestContext.copy();
         this.param = param;
      }

      public void do_run() {
         DispatchImpl.checkNullAllowed(this.param, this.rc, DispatchImpl.this.binding, DispatchImpl.this.mode);
         Packet message = DispatchImpl.this.createPacket(this.param);
         message.setState(Packet.State.ClientRequest);
         message.nonNullAsyncHandlerGiven = this.nonNullAsyncHandlerGiven;
         DispatchImpl.this.resolveEndpointAddress(message, this.rc);
         DispatchImpl.this.setProperties(message, true);
         final String action = null;
         final String msgId = null;
         if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
            AddressingVersion av = DispatchImpl.this.getBinding().getAddressingVersion();
            SOAPVersion sv = DispatchImpl.this.getBinding().getSOAPVersion();
            action = av != null && message.getMessage() != null ? AddressingUtils.getAction(message.getMessage().getHeaders(), av, sv) : null;
            msgId = av != null && message.getMessage() != null ? AddressingUtils.getMessageID(message.getMessage().getHeaders(), av, sv) : null;
            DispatchImpl.LOGGER.fine("In DispatchAsyncInvoker.do_run for async message with action: " + action + " and msg ID: " + msgId);
         }

         Fiber.CompletionCallback callback = new Fiber.CompletionCallback() {
            public void onCompletion(@NotNull Packet response) {
               if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
                  DispatchImpl.LOGGER.fine("Done with processAsync in DispatchAsyncInvoker.do_run, and setting response for async message with action: " + action + " and msg ID: " + msgId);
               }

               Message msg = response.getMessage();
               if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
                  DispatchImpl.LOGGER.fine("Done with processAsync in DispatchAsyncInvoker.do_run, and setting response for async message with action: " + action + " and msg ID: " + msgId + " msg: " + msg);
               }

               try {
                  if (msg != null && msg.isFault() && !DispatchImpl.this.allowFaultResponseMsg) {
                     SOAPFaultBuilder faultBuilder = SOAPFaultBuilder.create(msg);
                     throw (SOAPFaultException)faultBuilder.createException((Map)null);
                  }

                  DispatchAsyncInvoker.this.responseImpl.setResponseContext(new ResponseContext(response));
                  DispatchAsyncInvoker.this.responseImpl.set(DispatchImpl.this.toReturnValue(response), (Throwable)null);
               } catch (JAXBException var4) {
                  DispatchAsyncInvoker.this.responseImpl.set((Object)null, new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), new Object[]{var4}));
               } catch (WebServiceException var5) {
                  DispatchAsyncInvoker.this.responseImpl.set((Object)null, var5);
               } catch (Throwable var6) {
                  DispatchAsyncInvoker.this.responseImpl.set((Object)null, new WebServiceException(var6));
               }

            }

            public void onCompletion(@NotNull Throwable error) {
               if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
                  DispatchImpl.LOGGER.fine("Done with processAsync in DispatchAsyncInvoker.do_run, and setting response for async message with action: " + action + " and msg ID: " + msgId + " Throwable: " + error.toString());
               }

               if (error instanceof WebServiceException) {
                  DispatchAsyncInvoker.this.responseImpl.set((Object)null, error);
               } else {
                  DispatchAsyncInvoker.this.responseImpl.set((Object)null, new WebServiceException(error));
               }

            }
         };
         DispatchImpl.this.processAsync(this.responseImpl, message, this.rc, callback);
      }
   }

   private class Invoker implements Callable {
      private final T param;
      private final RequestContext rc;
      private ResponseContextReceiver receiver;

      Invoker(T param) {
         this.rc = DispatchImpl.this.requestContext.copy();
         this.param = param;
      }

      public T call() throws Exception {
         if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
            DispatchImpl.this.dumpParam(this.param, "call()");
         }

         return DispatchImpl.this.doInvoke(this.param, this.rc, this.receiver);
      }

      void setReceiver(ResponseContextReceiver receiver) {
         this.receiver = receiver;
      }
   }
}
