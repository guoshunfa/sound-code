package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.addressing.EPRSDDocumentFilter;
import com.sun.xml.internal.ws.addressing.WSEPRExtension;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentFeature;
import com.sun.xml.internal.ws.api.ComponentsFeature;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.Engine;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.internal.ws.api.pipe.ServerPipeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.SyncStartForAsyncFeature;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.TubelineAssembler;
import com.sun.xml.internal.ws.api.pipe.TubelineAssemblerFactory;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.server.EndpointAwareCodec;
import com.sun.xml.internal.ws.api.server.EndpointComponent;
import com.sun.xml.internal.ws.api.server.EndpointReferenceExtensionContributor;
import com.sun.xml.internal.ws.api.server.LazyMOMProvider;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLDirectProperties;
import com.sun.xml.internal.ws.model.wsdl.WSDLPortProperties;
import com.sun.xml.internal.ws.model.wsdl.WSDLProperties;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.resources.HandlerMessages;
import com.sun.xml.internal.ws.util.Pool;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.management.ObjectName;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.Handler;
import org.w3c.dom.Element;

public class WSEndpointImpl<T> extends WSEndpoint<T> implements LazyMOMProvider.WSEndpointScopeChangeListener {
   private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server.endpoint");
   @NotNull
   private final QName serviceName;
   @NotNull
   private final QName portName;
   protected final WSBinding binding;
   private final SEIModel seiModel;
   @NotNull
   private final Container container;
   private final WSDLPort port;
   protected final Tube masterTubeline;
   private final ServiceDefinitionImpl serviceDef;
   private final SOAPVersion soapVersion;
   private final Engine engine;
   @NotNull
   private final Codec masterCodec;
   @NotNull
   private final PolicyMap endpointPolicy;
   private final Pool<Tube> tubePool;
   private final OperationDispatcher operationDispatcher;
   @NotNull
   private ManagedObjectManager managedObjectManager;
   private boolean managedObjectManagerClosed = false;
   private final Object managedObjectManagerLock = new Object();
   private LazyMOMProvider.Scope lazyMOMProviderScope;
   @NotNull
   private final ServerTubeAssemblerContext context;
   private Map<QName, WSEndpointReference.EPRExtension> endpointReferenceExtensions;
   private boolean disposed;
   private final Class<T> implementationClass;
   @NotNull
   private final WSDLProperties wsdlProperties;
   private final Set<Component> componentRegistry;
   private static final Logger monitoringLogger = Logger.getLogger("com.sun.xml.internal.ws.monitoring");

   protected WSEndpointImpl(@NotNull QName serviceName, @NotNull QName portName, WSBinding binding, Container container, SEIModel seiModel, WSDLPort port, Class<T> implementationClass, @Nullable ServiceDefinitionImpl serviceDef, EndpointAwareTube terminalTube, boolean isSynchronous, PolicyMap endpointPolicy) {
      this.lazyMOMProviderScope = LazyMOMProvider.Scope.STANDALONE;
      this.endpointReferenceExtensions = new HashMap();
      this.componentRegistry = new CopyOnWriteArraySet();
      this.serviceName = serviceName;
      this.portName = portName;
      this.binding = binding;
      this.soapVersion = binding.getSOAPVersion();
      this.container = container;
      this.port = port;
      this.implementationClass = implementationClass;
      this.serviceDef = serviceDef;
      this.seiModel = seiModel;
      this.endpointPolicy = endpointPolicy;
      LazyMOMProvider.INSTANCE.registerEndpoint(this);
      this.initManagedObjectManager();
      if (serviceDef != null) {
         serviceDef.setOwner(this);
      }

      ComponentFeature cf = (ComponentFeature)binding.getFeature(ComponentFeature.class);
      if (cf != null) {
         switch(cf.getTarget()) {
         case ENDPOINT:
            this.componentRegistry.add(cf.getComponent());
            break;
         case CONTAINER:
            container.getComponents().add(cf.getComponent());
            break;
         default:
            throw new IllegalArgumentException();
         }
      }

      ComponentsFeature csf = (ComponentsFeature)binding.getFeature(ComponentsFeature.class);
      if (csf != null) {
         Iterator var14 = csf.getComponentFeatures().iterator();

         while(var14.hasNext()) {
            ComponentFeature cfi = (ComponentFeature)var14.next();
            switch(cfi.getTarget()) {
            case ENDPOINT:
               this.componentRegistry.add(cfi.getComponent());
               break;
            case CONTAINER:
               container.getComponents().add(cfi.getComponent());
               break;
            default:
               throw new IllegalArgumentException();
            }
         }
      }

      TubelineAssembler assembler = TubelineAssemblerFactory.create(Thread.currentThread().getContextClassLoader(), binding.getBindingId(), container);

      assert assembler != null;

      this.operationDispatcher = port == null ? null : new OperationDispatcher(port, binding, seiModel);
      this.context = this.createServerTubeAssemblerContext(terminalTube, isSynchronous);
      this.masterTubeline = assembler.createServer(this.context);
      Codec c = this.context.getCodec();
      if (c instanceof EndpointAwareCodec) {
         c = c.copy();
         ((EndpointAwareCodec)c).setEndpoint(this);
      }

      this.masterCodec = c;
      this.tubePool = new Pool.TubePool(this.masterTubeline);
      terminalTube.setEndpoint(this);
      this.engine = new Engine(this.toString(), container);
      this.wsdlProperties = (WSDLProperties)(port == null ? new WSDLDirectProperties(serviceName, portName, seiModel) : new WSDLPortProperties(port, seiModel));
      HashMap eprExtensions = new HashMap();

      try {
         Iterator var18;
         WSEndpointReference.EPRExtension extn;
         if (port != null) {
            WSEndpointReference wsdlEpr = port.getEPR();
            if (wsdlEpr != null) {
               var18 = wsdlEpr.getEPRExtensions().iterator();

               while(var18.hasNext()) {
                  extn = (WSEndpointReference.EPRExtension)var18.next();
                  eprExtensions.put(extn.getQName(), extn);
               }
            }
         }

         EndpointReferenceExtensionContributor[] eprExtnContributors = (EndpointReferenceExtensionContributor[])ServiceFinder.find(EndpointReferenceExtensionContributor.class).toArray();
         EndpointReferenceExtensionContributor[] var28 = eprExtnContributors;
         int var29 = eprExtnContributors.length;

         for(int var20 = 0; var20 < var29; ++var20) {
            EndpointReferenceExtensionContributor eprExtnContributor = var28[var20];
            WSEndpointReference.EPRExtension wsdlEPRExtn = (WSEndpointReference.EPRExtension)eprExtensions.remove(eprExtnContributor.getQName());
            WSEndpointReference.EPRExtension endpointEprExtn = eprExtnContributor.getEPRExtension(this, wsdlEPRExtn);
            if (endpointEprExtn != null) {
               eprExtensions.put(endpointEprExtn.getQName(), endpointEprExtn);
            }
         }

         var18 = eprExtensions.values().iterator();

         while(var18.hasNext()) {
            extn = (WSEndpointReference.EPRExtension)var18.next();
            this.endpointReferenceExtensions.put(extn.getQName(), new WSEPRExtension(XMLStreamBuffer.createNewBufferFromXMLStreamReader(extn.readAsXMLStreamReader()), extn.getQName()));
         }
      } catch (XMLStreamException var24) {
         throw new WebServiceException(var24);
      }

      if (!eprExtensions.isEmpty()) {
         serviceDef.addFilter(new EPRSDDocumentFilter(this));
      }

   }

   protected ServerTubeAssemblerContext createServerTubeAssemblerContext(EndpointAwareTube terminalTube, boolean isSynchronous) {
      ServerTubeAssemblerContext ctx = new ServerPipeAssemblerContext(this.seiModel, this.port, this, terminalTube, isSynchronous);
      return ctx;
   }

   protected WSEndpointImpl(@NotNull QName serviceName, @NotNull QName portName, WSBinding binding, Container container, SEIModel seiModel, WSDLPort port, Tube masterTubeline) {
      this.lazyMOMProviderScope = LazyMOMProvider.Scope.STANDALONE;
      this.endpointReferenceExtensions = new HashMap();
      this.componentRegistry = new CopyOnWriteArraySet();
      this.serviceName = serviceName;
      this.portName = portName;
      this.binding = binding;
      this.soapVersion = binding.getSOAPVersion();
      this.container = container;
      this.endpointPolicy = null;
      this.port = port;
      this.seiModel = seiModel;
      this.serviceDef = null;
      this.implementationClass = null;
      this.masterTubeline = masterTubeline;
      this.masterCodec = ((BindingImpl)this.binding).createCodec();
      LazyMOMProvider.INSTANCE.registerEndpoint(this);
      this.initManagedObjectManager();
      this.operationDispatcher = port == null ? null : new OperationDispatcher(port, binding, seiModel);
      this.context = new ServerPipeAssemblerContext(seiModel, port, this, (Tube)null, false);
      this.tubePool = new Pool.TubePool(masterTubeline);
      this.engine = new Engine(this.toString(), container);
      this.wsdlProperties = (WSDLProperties)(port == null ? new WSDLDirectProperties(serviceName, portName, seiModel) : new WSDLPortProperties(port, seiModel));
   }

   public Collection<WSEndpointReference.EPRExtension> getEndpointReferenceExtensions() {
      return this.endpointReferenceExtensions.values();
   }

   @Nullable
   public OperationDispatcher getOperationDispatcher() {
      return this.operationDispatcher;
   }

   public PolicyMap getPolicyMap() {
      return this.endpointPolicy;
   }

   @NotNull
   public Class<T> getImplementationClass() {
      return this.implementationClass;
   }

   @NotNull
   public WSBinding getBinding() {
      return this.binding;
   }

   @NotNull
   public Container getContainer() {
      return this.container;
   }

   public WSDLPort getPort() {
      return this.port;
   }

   @Nullable
   public SEIModel getSEIModel() {
      return this.seiModel;
   }

   public void setExecutor(Executor exec) {
      this.engine.setExecutor(exec);
   }

   public Engine getEngine() {
      return this.engine;
   }

   public void schedule(Packet request, WSEndpoint.CompletionCallback callback, FiberContextSwitchInterceptor interceptor) {
      this.processAsync(request, callback, interceptor, true);
   }

   private void processAsync(final Packet request, final WSEndpoint.CompletionCallback callback, FiberContextSwitchInterceptor interceptor, boolean schedule) {
      Container old = ContainerResolver.getDefault().enterContainer(this.container);

      try {
         request.endpoint = this;
         request.addSatellite(this.wsdlProperties);
         Fiber fiber = this.engine.createFiber();
         fiber.setDeliverThrowableInPacket(true);
         if (interceptor != null) {
            fiber.addInterceptor(interceptor);
         }

         final Tube tube = (Tube)this.tubePool.take();
         Fiber.CompletionCallback cbak = new Fiber.CompletionCallback() {
            public void onCompletion(@NotNull Packet response) {
               ThrowableContainerPropertySet tc = (ThrowableContainerPropertySet)response.getSatellite(ThrowableContainerPropertySet.class);
               if (tc == null) {
                  WSEndpointImpl.this.tubePool.recycle(tube);
               }

               if (callback != null) {
                  if (tc != null) {
                     response = WSEndpointImpl.this.createServiceResponseForException(tc, response, WSEndpointImpl.this.soapVersion, request.endpoint.getPort(), (SEIModel)null, request.endpoint.getBinding());
                  }

                  callback.onCompletion(response);
               }

            }

            public void onCompletion(@NotNull Throwable error) {
               throw new IllegalStateException();
            }
         };
         fiber.start(tube, request, cbak, this.binding.isFeatureEnabled(SyncStartForAsyncFeature.class) || !schedule);
      } finally {
         ContainerResolver.getDefault().exitContainer(old);
      }

   }

   public Packet createServiceResponseForException(ThrowableContainerPropertySet tc, Packet responsePacket, SOAPVersion soapVersion, WSDLPort wsdlPort, SEIModel seiModel, WSBinding binding) {
      if (tc.isFaultCreated()) {
         return responsePacket;
      } else {
         Message faultMessage = SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, (CheckedExceptionImpl)null, (Throwable)tc.getThrowable());
         Packet result = responsePacket.createServerResponse(faultMessage, wsdlPort, seiModel, binding);
         tc.setFaultMessage(faultMessage);
         tc.setResponsePacket(responsePacket);
         tc.setFaultCreated(true);
         return result;
      }
   }

   public void process(Packet request, WSEndpoint.CompletionCallback callback, FiberContextSwitchInterceptor interceptor) {
      this.processAsync(request, callback, interceptor, false);
   }

   @NotNull
   public WSEndpoint.PipeHead createPipeHead() {
      return new WSEndpoint.PipeHead() {
         private final Tube tube;

         {
            this.tube = TubeCloner.clone(WSEndpointImpl.this.masterTubeline);
         }

         @NotNull
         public Packet process(Packet request, WebServiceContextDelegate wscd, TransportBackChannel tbc) {
            Container old = ContainerResolver.getDefault().enterContainer(WSEndpointImpl.this.container);

            Packet var7;
            try {
               request.webServiceContextDelegate = wscd;
               request.transportBackChannel = tbc;
               request.endpoint = WSEndpointImpl.this;
               request.addSatellite(WSEndpointImpl.this.wsdlProperties);
               Fiber fiber = WSEndpointImpl.this.engine.createFiber();

               Packet response;
               try {
                  response = fiber.runSync(this.tube, request);
               } catch (RuntimeException var12) {
                  Message faultMsg = SOAPFaultBuilder.createSOAPFaultMessage(WSEndpointImpl.this.soapVersion, (CheckedExceptionImpl)null, (Throwable)var12);
                  response = request.createServerResponse(faultMsg, (WSDLPort)request.endpoint.getPort(), (SEIModel)null, (WSBinding)request.endpoint.getBinding());
               }

               var7 = response;
            } finally {
               ContainerResolver.getDefault().exitContainer(old);
            }

            return var7;
         }
      };
   }

   public synchronized void dispose() {
      if (!this.disposed) {
         this.disposed = true;
         this.masterTubeline.preDestroy();
         Iterator var1 = this.binding.getHandlerChain().iterator();

         while(true) {
            while(var1.hasNext()) {
               Handler handler = (Handler)var1.next();
               Method[] var3 = handler.getClass().getMethods();
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  Method method = var3[var5];
                  if (method.getAnnotation(PreDestroy.class) != null) {
                     try {
                        method.invoke(handler);
                     } catch (Exception var8) {
                        logger.log(Level.WARNING, (String)HandlerMessages.HANDLER_PREDESTROY_IGNORE(var8.getMessage()), (Throwable)var8);
                     }
                     break;
                  }
               }
            }

            this.closeManagedObjectManager();
            LazyMOMProvider.INSTANCE.unregisterEndpoint(this);
            return;
         }
      }
   }

   public ServiceDefinitionImpl getServiceDefinition() {
      return this.serviceDef;
   }

   public Set<EndpointComponent> getComponentRegistry() {
      Set<EndpointComponent> sec = new WSEndpointImpl.EndpointComponentSet();
      Iterator var2 = this.componentRegistry.iterator();

      while(var2.hasNext()) {
         Component c = (Component)var2.next();
         sec.add(c instanceof WSEndpointImpl.EndpointComponentWrapper ? ((WSEndpointImpl.EndpointComponentWrapper)c).component : new WSEndpointImpl.ComponentWrapper(c));
      }

      return sec;
   }

   @NotNull
   public Set<Component> getComponents() {
      return this.componentRegistry;
   }

   public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, String address, String wsdlAddress, Element... referenceParameters) {
      List<Element> refParams = null;
      if (referenceParameters != null) {
         refParams = Arrays.asList(referenceParameters);
      }

      return this.getEndpointReference(clazz, address, wsdlAddress, (List)null, refParams);
   }

   public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, String address, String wsdlAddress, List<Element> metadata, List<Element> referenceParameters) {
      QName portType = null;
      if (this.port != null) {
         portType = this.port.getBinding().getPortTypeName();
      }

      AddressingVersion av = AddressingVersion.fromSpecClass(clazz);
      return (new WSEndpointReference(av, address, this.serviceName, this.portName, portType, metadata, wsdlAddress, referenceParameters, this.endpointReferenceExtensions.values(), (Map)null)).toSpec(clazz);
   }

   @NotNull
   public QName getPortName() {
      return this.portName;
   }

   @NotNull
   public Codec createCodec() {
      return this.masterCodec.copy();
   }

   @NotNull
   public QName getServiceName() {
      return this.serviceName;
   }

   private void initManagedObjectManager() {
      synchronized(this.managedObjectManagerLock) {
         if (this.managedObjectManager == null) {
            switch(this.lazyMOMProviderScope) {
            case GLASSFISH_NO_JMX:
               this.managedObjectManager = new WSEndpointMOMProxy(this);
               break;
            default:
               this.managedObjectManager = this.obtainManagedObjectManager();
            }
         }

      }
   }

   @NotNull
   public ManagedObjectManager getManagedObjectManager() {
      return this.managedObjectManager;
   }

   @NotNull
   ManagedObjectManager obtainManagedObjectManager() {
      MonitorRootService monitorRootService = new MonitorRootService(this);
      ManagedObjectManager mOM = monitorRootService.createManagedObjectManager(this);
      mOM.resumeJMXRegistration();
      return mOM;
   }

   public void scopeChanged(LazyMOMProvider.Scope scope) {
      synchronized(this.managedObjectManagerLock) {
         if (!this.managedObjectManagerClosed) {
            this.lazyMOMProviderScope = scope;
            if (this.managedObjectManager == null) {
               if (scope != LazyMOMProvider.Scope.GLASSFISH_NO_JMX) {
                  this.managedObjectManager = this.obtainManagedObjectManager();
               } else {
                  this.managedObjectManager = new WSEndpointMOMProxy(this);
               }
            } else if (this.managedObjectManager instanceof WSEndpointMOMProxy && !((WSEndpointMOMProxy)this.managedObjectManager).isInitialized()) {
               ((WSEndpointMOMProxy)this.managedObjectManager).setManagedObjectManager(this.obtainManagedObjectManager());
            }

         }
      }
   }

   public void closeManagedObjectManager() {
      synchronized(this.managedObjectManagerLock) {
         if (!this.managedObjectManagerClosed) {
            if (this.managedObjectManager != null) {
               boolean close = true;
               if (this.managedObjectManager instanceof WSEndpointMOMProxy && !((WSEndpointMOMProxy)this.managedObjectManager).isInitialized()) {
                  close = false;
               }

               if (close) {
                  try {
                     ObjectName name = this.managedObjectManager.getObjectName(this.managedObjectManager.getRoot());
                     if (name != null) {
                        monitoringLogger.log(Level.INFO, (String)"Closing Metro monitoring root: {0}", (Object)name);
                     }

                     this.managedObjectManager.close();
                  } catch (IOException var5) {
                     monitoringLogger.log(Level.WARNING, (String)"Ignoring error when closing Managed Object Manager", (Throwable)var5);
                  }
               }
            }

            this.managedObjectManagerClosed = true;
         }
      }
   }

   @NotNull
   public ServerTubeAssemblerContext getAssemblerContext() {
      return this.context;
   }

   private static class EndpointComponentWrapper implements Component {
      private final EndpointComponent component;

      public EndpointComponentWrapper(EndpointComponent component) {
         this.component = component;
      }

      public <S> S getSPI(Class<S> spiType) {
         return this.component.getSPI(spiType);
      }

      public int hashCode() {
         return this.component.hashCode();
      }

      public boolean equals(Object obj) {
         return this.component.equals(obj);
      }
   }

   private static class ComponentWrapper implements EndpointComponent {
      private final Component component;

      public ComponentWrapper(Component component) {
         this.component = component;
      }

      public <S> S getSPI(Class<S> spiType) {
         return this.component.getSPI(spiType);
      }

      public int hashCode() {
         return this.component.hashCode();
      }

      public boolean equals(Object obj) {
         return this.component.equals(obj);
      }
   }

   private class EndpointComponentSet extends HashSet<EndpointComponent> {
      private EndpointComponentSet() {
      }

      public Iterator<EndpointComponent> iterator() {
         final Iterator<EndpointComponent> it = super.iterator();
         return new Iterator<EndpointComponent>() {
            private EndpointComponent last = null;

            public boolean hasNext() {
               return it.hasNext();
            }

            public EndpointComponent next() {
               this.last = (EndpointComponent)it.next();
               return this.last;
            }

            public void remove() {
               it.remove();
               if (this.last != null) {
                  WSEndpointImpl.this.componentRegistry.remove(this.last instanceof WSEndpointImpl.ComponentWrapper ? ((WSEndpointImpl.ComponentWrapper)this.last).component : new WSEndpointImpl.EndpointComponentWrapper(this.last));
               }

               this.last = null;
            }
         };
      }

      public boolean add(EndpointComponent e) {
         boolean result = super.add(e);
         if (result) {
            WSEndpointImpl.this.componentRegistry.add(new WSEndpointImpl.EndpointComponentWrapper(e));
         }

         return result;
      }

      public boolean remove(Object o) {
         boolean result = super.remove(o);
         if (result) {
            WSEndpointImpl.this.componentRegistry.remove(o instanceof WSEndpointImpl.ComponentWrapper ? ((WSEndpointImpl.ComponentWrapper)o).component : new WSEndpointImpl.EndpointComponentWrapper((EndpointComponent)o));
         }

         return result;
      }

      // $FF: synthetic method
      EndpointComponentSet(Object x1) {
         this();
      }
   }
}
