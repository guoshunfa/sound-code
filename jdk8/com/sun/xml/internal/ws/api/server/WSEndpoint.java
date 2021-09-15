package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentRegistry;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.config.management.EndpointCreationAttributes;
import com.sun.xml.internal.ws.api.config.management.ManagedEndpointFactory;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.Engine;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.server.EndpointAwareTube;
import com.sun.xml.internal.ws.server.EndpointFactory;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.xml.namespace.QName;
import javax.xml.ws.EndpointReference;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;

public abstract class WSEndpoint<T> implements ComponentRegistry {
   @NotNull
   public abstract Codec createCodec();

   @NotNull
   public abstract QName getServiceName();

   @NotNull
   public abstract QName getPortName();

   @NotNull
   public abstract Class<T> getImplementationClass();

   @NotNull
   public abstract WSBinding getBinding();

   @NotNull
   public abstract Container getContainer();

   @Nullable
   public abstract WSDLPort getPort();

   public abstract void setExecutor(@NotNull Executor var1);

   public final void schedule(@NotNull Packet request, @NotNull WSEndpoint.CompletionCallback callback) {
      this.schedule(request, callback, (FiberContextSwitchInterceptor)null);
   }

   public abstract void schedule(@NotNull Packet var1, @NotNull WSEndpoint.CompletionCallback var2, @Nullable FiberContextSwitchInterceptor var3);

   public void process(@NotNull Packet request, @NotNull WSEndpoint.CompletionCallback callback, @Nullable FiberContextSwitchInterceptor interceptor) {
      this.schedule(request, callback, interceptor);
   }

   public Engine getEngine() {
      throw new UnsupportedOperationException();
   }

   @NotNull
   public abstract WSEndpoint.PipeHead createPipeHead();

   public abstract void dispose();

   @Nullable
   public abstract ServiceDefinition getServiceDefinition();

   public List<BoundEndpoint> getBoundEndpoints() {
      Module m = (Module)this.getContainer().getSPI(Module.class);
      return m != null ? m.getBoundEndpoints() : null;
   }

   /** @deprecated */
   @NotNull
   public abstract Set<EndpointComponent> getComponentRegistry();

   @NotNull
   public Set<Component> getComponents() {
      return Collections.emptySet();
   }

   @Nullable
   public <S> S getSPI(@NotNull Class<S> spiType) {
      Set<Component> componentRegistry = this.getComponents();
      if (componentRegistry != null) {
         Iterator var3 = componentRegistry.iterator();

         while(var3.hasNext()) {
            Component c = (Component)var3.next();
            S s = c.getSPI(spiType);
            if (s != null) {
               return s;
            }
         }
      }

      return this.getContainer().getSPI(spiType);
   }

   @Nullable
   public abstract SEIModel getSEIModel();

   /** @deprecated */
   public abstract PolicyMap getPolicyMap();

   @NotNull
   public abstract ManagedObjectManager getManagedObjectManager();

   public abstract void closeManagedObjectManager();

   @NotNull
   public abstract ServerTubeAssemblerContext getAssemblerContext();

   public static <T> WSEndpoint<T> create(@NotNull Class<T> implType, boolean processHandlerAnnotation, @Nullable Invoker invoker, @Nullable QName serviceName, @Nullable QName portName, @Nullable Container container, @Nullable WSBinding binding, @Nullable SDDocumentSource primaryWsdl, @Nullable Collection<? extends SDDocumentSource> metadata, @Nullable EntityResolver resolver, boolean isTransportSynchronous) {
      return create(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, resolver, isTransportSynchronous, true);
   }

   public static <T> WSEndpoint<T> create(@NotNull Class<T> implType, boolean processHandlerAnnotation, @Nullable Invoker invoker, @Nullable QName serviceName, @Nullable QName portName, @Nullable Container container, @Nullable WSBinding binding, @Nullable SDDocumentSource primaryWsdl, @Nullable Collection<? extends SDDocumentSource> metadata, @Nullable EntityResolver resolver, boolean isTransportSynchronous, boolean isStandard) {
      WSEndpoint<T> endpoint = EndpointFactory.createEndpoint(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, resolver, isTransportSynchronous, isStandard);
      Iterator<ManagedEndpointFactory> managementFactories = ServiceFinder.find(ManagedEndpointFactory.class).iterator();
      if (managementFactories.hasNext()) {
         ManagedEndpointFactory managementFactory = (ManagedEndpointFactory)managementFactories.next();
         EndpointCreationAttributes attributes = new EndpointCreationAttributes(processHandlerAnnotation, invoker, resolver, isTransportSynchronous);
         WSEndpoint<T> managedEndpoint = managementFactory.createEndpoint(endpoint, attributes);
         if (endpoint.getAssemblerContext().getTerminalTube() instanceof EndpointAwareTube) {
            ((EndpointAwareTube)endpoint.getAssemblerContext().getTerminalTube()).setEndpoint(managedEndpoint);
         }

         return managedEndpoint;
      } else {
         return endpoint;
      }
   }

   /** @deprecated */
   @Deprecated
   public static <T> WSEndpoint<T> create(@NotNull Class<T> implType, boolean processHandlerAnnotation, @Nullable Invoker invoker, @Nullable QName serviceName, @Nullable QName portName, @Nullable Container container, @Nullable WSBinding binding, @Nullable SDDocumentSource primaryWsdl, @Nullable Collection<? extends SDDocumentSource> metadata, @Nullable EntityResolver resolver) {
      return create(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, resolver, false);
   }

   public static <T> WSEndpoint<T> create(@NotNull Class<T> implType, boolean processHandlerAnnotation, @Nullable Invoker invoker, @Nullable QName serviceName, @Nullable QName portName, @Nullable Container container, @Nullable WSBinding binding, @Nullable SDDocumentSource primaryWsdl, @Nullable Collection<? extends SDDocumentSource> metadata, @Nullable URL catalogUrl) {
      return create(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, XmlUtil.createEntityResolver(catalogUrl), false);
   }

   @NotNull
   public static QName getDefaultServiceName(Class endpointClass) {
      return getDefaultServiceName(endpointClass, true, (MetadataReader)null);
   }

   @NotNull
   public static QName getDefaultServiceName(Class endpointClass, MetadataReader metadataReader) {
      return getDefaultServiceName(endpointClass, true, metadataReader);
   }

   @NotNull
   public static QName getDefaultServiceName(Class endpointClass, boolean isStandard) {
      return getDefaultServiceName(endpointClass, isStandard, (MetadataReader)null);
   }

   @NotNull
   public static QName getDefaultServiceName(Class endpointClass, boolean isStandard, MetadataReader metadataReader) {
      return EndpointFactory.getDefaultServiceName(endpointClass, isStandard, metadataReader);
   }

   @NotNull
   public static QName getDefaultPortName(@NotNull QName serviceName, Class endpointClass) {
      return getDefaultPortName(serviceName, endpointClass, (MetadataReader)null);
   }

   @NotNull
   public static QName getDefaultPortName(@NotNull QName serviceName, Class endpointClass, MetadataReader metadataReader) {
      return getDefaultPortName(serviceName, endpointClass, true, metadataReader);
   }

   @NotNull
   public static QName getDefaultPortName(@NotNull QName serviceName, Class endpointClass, boolean isStandard) {
      return getDefaultPortName(serviceName, endpointClass, isStandard, (MetadataReader)null);
   }

   @NotNull
   public static QName getDefaultPortName(@NotNull QName serviceName, Class endpointClass, boolean isStandard, MetadataReader metadataReader) {
      return EndpointFactory.getDefaultPortName(serviceName, endpointClass, isStandard, metadataReader);
   }

   public abstract <T extends EndpointReference> T getEndpointReference(Class<T> var1, String var2, String var3, Element... var4);

   public abstract <T extends EndpointReference> T getEndpointReference(Class<T> var1, String var2, String var3, List<Element> var4, List<Element> var5);

   public boolean equalsProxiedInstance(WSEndpoint endpoint) {
      return endpoint == null ? false : this.equals(endpoint);
   }

   @Nullable
   public abstract OperationDispatcher getOperationDispatcher();

   public abstract Packet createServiceResponseForException(ThrowableContainerPropertySet var1, Packet var2, SOAPVersion var3, WSDLPort var4, SEIModel var5, WSBinding var6);

   public interface PipeHead {
      @NotNull
      Packet process(@NotNull Packet var1, @Nullable WebServiceContextDelegate var2, @Nullable TransportBackChannel var3);
   }

   public interface CompletionCallback {
      void onCompletion(@NotNull Packet var1);
   }
}
