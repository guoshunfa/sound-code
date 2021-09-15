package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.addressing.WSEPRExtension;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentFeature;
import com.sun.xml.internal.ws.api.ComponentRegistry;
import com.sun.xml.internal.ws.api.ComponentsFeature;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Engine;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptorFactory;
import com.sun.xml.internal.ws.api.pipe.SyncStartForAsyncFeature;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubelineAssembler;
import com.sun.xml.internal.ws.api.pipe.TubelineAssemblerFactory;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.developer.WSBindingProvider;
import com.sun.xml.internal.ws.model.wsdl.WSDLDirectProperties;
import com.sun.xml.internal.ws.model.wsdl.WSDLPortProperties;
import com.sun.xml.internal.ws.model.wsdl.WSDLProperties;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.util.Pool;
import com.sun.xml.internal.ws.util.RuntimeVersion;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.ObjectName;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.RespectBindingFeature;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

public abstract class Stub implements WSBindingProvider, ResponseContextReceiver, ComponentRegistry {
   public static final String PREVENT_SYNC_START_FOR_ASYNC_INVOKE = "com.sun.xml.internal.ws.client.StubRequestSyncStartForAsyncInvoke";
   private Pool<Tube> tubes;
   private final Engine engine;
   protected final WSServiceDelegate owner;
   @Nullable
   protected WSEndpointReference endpointReference;
   protected final BindingImpl binding;
   protected final WSPortInfo portInfo;
   protected AddressingVersion addrVersion;
   public RequestContext requestContext;
   private final RequestContext cleanRequestContext;
   private ResponseContext responseContext;
   @Nullable
   protected final WSDLPort wsdlPort;
   protected QName portname;
   @Nullable
   private volatile Header[] userOutboundHeaders;
   @NotNull
   private final WSDLProperties wsdlProperties;
   protected OperationDispatcher operationDispatcher;
   @NotNull
   private final ManagedObjectManager managedObjectManager;
   private boolean managedObjectManagerClosed;
   private final Set<Component> components;
   private static final Logger monitoringLogger = Logger.getLogger("com.sun.xml.internal.ws.monitoring");

   /** @deprecated */
   @Deprecated
   protected Stub(WSServiceDelegate owner, Tube master, BindingImpl binding, WSDLPort wsdlPort, EndpointAddress defaultEndPointAddress, @Nullable WSEndpointReference epr) {
      this(owner, master, (WSPortInfo)null, (QName)null, binding, wsdlPort, defaultEndPointAddress, epr);
   }

   /** @deprecated */
   @Deprecated
   protected Stub(QName portname, WSServiceDelegate owner, Tube master, BindingImpl binding, WSDLPort wsdlPort, EndpointAddress defaultEndPointAddress, @Nullable WSEndpointReference epr) {
      this(owner, master, (WSPortInfo)null, portname, binding, wsdlPort, defaultEndPointAddress, epr);
   }

   protected Stub(WSPortInfo portInfo, BindingImpl binding, Tube master, EndpointAddress defaultEndPointAddress, @Nullable WSEndpointReference epr) {
      this((WSServiceDelegate)portInfo.getOwner(), master, portInfo, (QName)null, binding, portInfo.getPort(), defaultEndPointAddress, epr);
   }

   protected Stub(WSPortInfo portInfo, BindingImpl binding, EndpointAddress defaultEndPointAddress, @Nullable WSEndpointReference epr) {
      this(portInfo, binding, (Tube)null, defaultEndPointAddress, epr);
   }

   private Stub(WSServiceDelegate owner, @Nullable Tube master, @Nullable WSPortInfo portInfo, QName portname, BindingImpl binding, @Nullable WSDLPort wsdlPort, EndpointAddress defaultEndPointAddress, @Nullable WSEndpointReference epr) {
      this.requestContext = new RequestContext();
      this.operationDispatcher = null;
      this.managedObjectManagerClosed = false;
      this.components = new CopyOnWriteArraySet();
      Container old = ContainerResolver.getDefault().enterContainer(owner.getContainer());

      try {
         this.owner = owner;
         this.portInfo = portInfo;
         this.wsdlPort = wsdlPort != null ? wsdlPort : (portInfo != null ? portInfo.getPort() : null);
         this.portname = portname;
         if (portname == null) {
            if (portInfo != null) {
               this.portname = portInfo.getPortName();
            } else if (wsdlPort != null) {
               this.portname = wsdlPort.getName();
            }
         }

         this.binding = binding;
         ComponentFeature cf = (ComponentFeature)binding.getFeature(ComponentFeature.class);
         if (cf != null && ComponentFeature.Target.STUB.equals(cf.getTarget())) {
            this.components.add(cf.getComponent());
         }

         ComponentsFeature csf = (ComponentsFeature)binding.getFeature(ComponentsFeature.class);
         if (csf != null) {
            Iterator var12 = csf.getComponentFeatures().iterator();

            while(var12.hasNext()) {
               ComponentFeature cfi = (ComponentFeature)var12.next();
               if (ComponentFeature.Target.STUB.equals(cfi.getTarget())) {
                  this.components.add(cfi.getComponent());
               }
            }
         }

         if (epr != null) {
            this.requestContext.setEndPointAddressString(epr.getAddress());
         } else {
            this.requestContext.setEndpointAddress(defaultEndPointAddress);
         }

         this.engine = new Engine(this.getStringId(), owner.getContainer(), owner.getExecutor());
         this.endpointReference = epr;
         this.wsdlProperties = (WSDLProperties)(wsdlPort == null ? new WSDLDirectProperties(owner.getServiceName(), portname) : new WSDLPortProperties(wsdlPort));
         this.cleanRequestContext = this.requestContext.copy();
         this.managedObjectManager = (new MonitorRootClient(this)).createManagedObjectManager(this);
         if (master != null) {
            this.tubes = new Pool.TubePool(master);
         } else {
            this.tubes = new Pool.TubePool(this.createPipeline(portInfo, binding));
         }

         this.addrVersion = binding.getAddressingVersion();
         this.managedObjectManager.resumeJMXRegistration();
      } finally {
         ContainerResolver.getDefault().exitContainer(old);
      }

   }

   private Tube createPipeline(WSPortInfo portInfo, WSBinding binding) {
      checkAllWSDLExtensionsUnderstood(portInfo, binding);
      SEIModel seiModel = null;
      Class sei = null;
      if (portInfo instanceof SEIPortInfo) {
         SEIPortInfo sp = (SEIPortInfo)portInfo;
         seiModel = sp.model;
         sei = sp.sei;
      }

      BindingID bindingId = portInfo.getBindingId();
      TubelineAssembler assembler = TubelineAssemblerFactory.create(Thread.currentThread().getContextClassLoader(), bindingId, this.owner.getContainer());
      if (assembler == null) {
         throw new WebServiceException("Unable to process bindingID=" + bindingId);
      } else {
         return assembler.createClient(new ClientTubeAssemblerContext(portInfo.getEndpointAddress(), portInfo.getPort(), this, binding, this.owner.getContainer(), ((BindingImpl)binding).createCodec(), seiModel, sei));
      }
   }

   public WSDLPort getWSDLPort() {
      return this.wsdlPort;
   }

   public WSService getService() {
      return this.owner;
   }

   public Pool<Tube> getTubes() {
      return this.tubes;
   }

   private static void checkAllWSDLExtensionsUnderstood(WSPortInfo port, WSBinding binding) {
      if (port.getPort() != null && binding.isFeatureEnabled(RespectBindingFeature.class)) {
         port.getPort().areRequiredExtensionsUnderstood();
      }

   }

   public WSPortInfo getPortInfo() {
      return this.portInfo;
   }

   @Nullable
   public OperationDispatcher getOperationDispatcher() {
      if (this.operationDispatcher == null && this.wsdlPort != null) {
         this.operationDispatcher = new OperationDispatcher(this.wsdlPort, this.binding, (SEIModel)null);
      }

      return this.operationDispatcher;
   }

   @NotNull
   protected abstract QName getPortName();

   @NotNull
   protected final QName getServiceName() {
      return this.owner.getServiceName();
   }

   public final Executor getExecutor() {
      return this.owner.getExecutor();
   }

   protected final Packet process(Packet packet, RequestContext requestContext, ResponseContextReceiver receiver) {
      packet.isSynchronousMEP = true;
      packet.component = this;
      this.configureRequestPacket(packet, requestContext);
      Pool<Tube> pool = this.tubes;
      if (pool == null) {
         throw new WebServiceException("close method has already been invoked");
      } else {
         Fiber fiber = this.engine.createFiber();
         this.configureFiber(fiber);
         Tube tube = (Tube)pool.take();
         boolean var12 = false;

         Packet var7;
         try {
            var12 = true;
            var7 = fiber.runSync(tube, packet);
            var12 = false;
         } finally {
            if (var12) {
               Packet reply = fiber.getPacket() == null ? packet : fiber.getPacket();
               receiver.setResponseContext(new ResponseContext(reply));
               pool.recycle(tube);
            }
         }

         Packet reply = fiber.getPacket() == null ? packet : fiber.getPacket();
         receiver.setResponseContext(new ResponseContext(reply));
         pool.recycle(tube);
         return var7;
      }
   }

   private void configureRequestPacket(Packet packet, RequestContext requestContext) {
      packet.proxy = this;
      packet.handlerConfig = this.binding.getHandlerConfig();
      Header[] hl = this.userOutboundHeaders;
      MessageHeaders mh;
      if (hl != null) {
         mh = packet.getMessage().getHeaders();
         Header[] var5 = hl;
         int var6 = hl.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Header h = var5[var7];
            mh.add(h);
         }
      }

      requestContext.fill(packet, this.binding.getAddressingVersion() != null);
      packet.addSatellite(this.wsdlProperties);
      if (this.addrVersion != null) {
         mh = packet.getMessage().getHeaders();
         AddressingUtils.fillRequestAddressingHeaders(mh, this.wsdlPort, this.binding, packet);
         if (this.endpointReference != null) {
            this.endpointReference.addReferenceParametersToList(packet.getMessage().getHeaders());
         }
      }

   }

   protected final void processAsync(AsyncResponseImpl<?> receiver, Packet request, RequestContext requestContext, final Fiber.CompletionCallback completionCallback) {
      request.component = this;
      this.configureRequestPacket(request, requestContext);
      final Pool<Tube> pool = this.tubes;
      if (pool == null) {
         throw new WebServiceException("close method has already been invoked");
      } else {
         Fiber fiber = this.engine.createFiber();
         this.configureFiber(fiber);
         receiver.setCancelable(fiber);
         if (!receiver.isCancelled()) {
            FiberContextSwitchInterceptorFactory fcsif = (FiberContextSwitchInterceptorFactory)this.owner.getSPI(FiberContextSwitchInterceptorFactory.class);
            if (fcsif != null) {
               fiber.addInterceptor(fcsif.create());
            }

            final Tube tube = (Tube)pool.take();
            Fiber.CompletionCallback fiberCallback = new Fiber.CompletionCallback() {
               public void onCompletion(@NotNull Packet response) {
                  pool.recycle(tube);
                  completionCallback.onCompletion(response);
               }

               public void onCompletion(@NotNull Throwable error) {
                  completionCallback.onCompletion(error);
               }
            };
            fiber.start(tube, request, fiberCallback, this.getBinding().isFeatureEnabled(SyncStartForAsyncFeature.class) && !requestContext.containsKey("com.sun.xml.internal.ws.client.StubRequestSyncStartForAsyncInvoke"));
         }
      }
   }

   protected void configureFiber(Fiber fiber) {
   }

   public void close() {
      Pool.TubePool tp = (Pool.TubePool)this.tubes;
      if (tp != null) {
         Tube p = tp.takeMaster();
         p.preDestroy();
         this.tubes = null;
      }

      if (!this.managedObjectManagerClosed) {
         try {
            ObjectName name = this.managedObjectManager.getObjectName(this.managedObjectManager.getRoot());
            if (name != null) {
               monitoringLogger.log(Level.INFO, (String)"Closing Metro monitoring root: {0}", (Object)name);
            }

            this.managedObjectManager.close();
         } catch (IOException var3) {
            monitoringLogger.log(Level.WARNING, (String)"Ignoring error when closing Managed Object Manager", (Throwable)var3);
         }

         this.managedObjectManagerClosed = true;
      }

   }

   public final WSBinding getBinding() {
      return this.binding;
   }

   public final Map<String, Object> getRequestContext() {
      return this.requestContext.asMap();
   }

   public void resetRequestContext() {
      this.requestContext = this.cleanRequestContext.copy();
   }

   public final ResponseContext getResponseContext() {
      return this.responseContext;
   }

   public void setResponseContext(ResponseContext rc) {
      this.responseContext = rc;
   }

   private String getStringId() {
      return RuntimeVersion.VERSION + ": Stub for " + this.getRequestContext().get("javax.xml.ws.service.endpoint.address");
   }

   public String toString() {
      return this.getStringId();
   }

   public final WSEndpointReference getWSEndpointReference() {
      if (this.binding.getBindingID().equals("http://www.w3.org/2004/08/wsdl/http")) {
         throw new UnsupportedOperationException(ClientMessages.UNSUPPORTED_OPERATION("BindingProvider.getEndpointReference(Class<T> class)", "XML/HTTP Binding", "SOAP11 or SOAP12 Binding"));
      } else if (this.endpointReference != null) {
         return this.endpointReference;
      } else {
         String eprAddress = this.requestContext.getEndpointAddress().toString();
         QName portTypeName = null;
         String wsdlAddress = null;
         List<WSEndpointReference.EPRExtension> wsdlEPRExtensions = new ArrayList();
         if (this.wsdlPort != null) {
            portTypeName = this.wsdlPort.getBinding().getPortTypeName();
            wsdlAddress = eprAddress + "?wsdl";

            try {
               WSEndpointReference wsdlEpr = this.wsdlPort.getEPR();
               if (wsdlEpr != null) {
                  Iterator var6 = wsdlEpr.getEPRExtensions().iterator();

                  while(var6.hasNext()) {
                     WSEndpointReference.EPRExtension extnEl = (WSEndpointReference.EPRExtension)var6.next();
                     wsdlEPRExtensions.add(new WSEPRExtension(XMLStreamBuffer.createNewBufferFromXMLStreamReader(extnEl.readAsXMLStreamReader()), extnEl.getQName()));
                  }
               }
            } catch (XMLStreamException var8) {
               throw new WebServiceException(var8);
            }
         }

         AddressingVersion av = AddressingVersion.W3C;
         this.endpointReference = new WSEndpointReference(av, eprAddress, this.getServiceName(), this.getPortName(), portTypeName, (List)null, wsdlAddress, (List)null, wsdlEPRExtensions, (Map)null);
         return this.endpointReference;
      }
   }

   public final W3CEndpointReference getEndpointReference() {
      if (this.binding.getBindingID().equals("http://www.w3.org/2004/08/wsdl/http")) {
         throw new UnsupportedOperationException(ClientMessages.UNSUPPORTED_OPERATION("BindingProvider.getEndpointReference()", "XML/HTTP Binding", "SOAP11 or SOAP12 Binding"));
      } else {
         return (W3CEndpointReference)this.getEndpointReference(W3CEndpointReference.class);
      }
   }

   public final <T extends EndpointReference> T getEndpointReference(Class<T> clazz) {
      return this.getWSEndpointReference().toSpec(clazz);
   }

   @NotNull
   public ManagedObjectManager getManagedObjectManager() {
      return this.managedObjectManager;
   }

   public final void setOutboundHeaders(List<Header> headers) {
      if (headers == null) {
         this.userOutboundHeaders = null;
      } else {
         Iterator var2 = headers.iterator();

         while(var2.hasNext()) {
            Header h = (Header)var2.next();
            if (h == null) {
               throw new IllegalArgumentException();
            }
         }

         this.userOutboundHeaders = (Header[])headers.toArray(new Header[headers.size()]);
      }

   }

   public final void setOutboundHeaders(Header... headers) {
      if (headers == null) {
         this.userOutboundHeaders = null;
      } else {
         Header[] hl = headers;
         int var3 = headers.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Header h = hl[var4];
            if (h == null) {
               throw new IllegalArgumentException();
            }
         }

         hl = new Header[headers.length];
         System.arraycopy(headers, 0, hl, 0, headers.length);
         this.userOutboundHeaders = hl;
      }

   }

   public final List<Header> getInboundHeaders() {
      return Collections.unmodifiableList(((MessageHeaders)this.responseContext.get("com.sun.xml.internal.ws.api.message.HeaderList")).asList());
   }

   public final void setAddress(String address) {
      this.requestContext.put("javax.xml.ws.service.endpoint.address", address);
   }

   public <S> S getSPI(Class<S> spiType) {
      Iterator var2 = this.components.iterator();

      Object s;
      do {
         if (!var2.hasNext()) {
            return this.owner.getSPI(spiType);
         }

         Component c = (Component)var2.next();
         s = c.getSPI(spiType);
      } while(s == null);

      return s;
   }

   public Set<Component> getComponents() {
      return this.components;
   }
}
