package com.sun.xml.internal.ws.transport.http.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.net.httpserver.HttpContext;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.InstanceResolver;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.server.EndpointFactory;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.xml.internal.ws.transport.http.HttpAdapterList;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.ws.Binding;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointContext;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.WebServicePermission;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

public class EndpointImpl extends Endpoint {
   private static final WebServicePermission ENDPOINT_PUBLISH_PERMISSION = new WebServicePermission("publishEndpoint");
   private Object actualEndpoint;
   private final WSBinding binding;
   @Nullable
   private final Object implementor;
   private List<Source> metadata;
   private Executor executor;
   private Map<String, Object> properties;
   private boolean stopped;
   @Nullable
   private EndpointContext endpointContext;
   @NotNull
   private final Class<?> implClass;
   private final Invoker invoker;
   private Container container;

   public EndpointImpl(@NotNull BindingID bindingId, @NotNull Object impl, WebServiceFeature... features) {
      this(bindingId, impl, impl.getClass(), InstanceResolver.createSingleton(impl).createInvoker(), features);
   }

   public EndpointImpl(@NotNull BindingID bindingId, @NotNull Class implClass, javax.xml.ws.spi.Invoker invoker, WebServiceFeature... features) {
      this(bindingId, (Object)null, implClass, new EndpointImpl.InvokerImpl(invoker), features);
   }

   private EndpointImpl(@NotNull BindingID bindingId, Object impl, @NotNull Class implClass, Invoker invoker, WebServiceFeature... features) {
      this.properties = Collections.emptyMap();
      this.binding = BindingImpl.create(bindingId, features);
      this.implClass = implClass;
      this.invoker = invoker;
      this.implementor = impl;
   }

   /** @deprecated */
   public EndpointImpl(WSEndpoint wse, Object serverContext) {
      this((WSEndpoint)wse, (Object)serverContext, (EndpointContext)null);
   }

   /** @deprecated */
   public EndpointImpl(WSEndpoint wse, Object serverContext, EndpointContext ctxt) {
      this.properties = Collections.emptyMap();
      this.endpointContext = ctxt;
      this.actualEndpoint = new HttpEndpoint((Executor)null, this.getAdapter(wse, ""));
      ((HttpEndpoint)this.actualEndpoint).publish(serverContext);
      this.binding = wse.getBinding();
      this.implementor = null;
      this.implClass = null;
      this.invoker = null;
   }

   /** @deprecated */
   public EndpointImpl(WSEndpoint wse, String address) {
      this((WSEndpoint)wse, (String)address, (EndpointContext)null);
   }

   /** @deprecated */
   public EndpointImpl(WSEndpoint wse, String address, EndpointContext ctxt) {
      this.properties = Collections.emptyMap();

      URL url;
      try {
         url = new URL(address);
      } catch (MalformedURLException var6) {
         throw new IllegalArgumentException("Cannot create URL for this address " + address);
      }

      if (!url.getProtocol().equals("http")) {
         throw new IllegalArgumentException(url.getProtocol() + " protocol based address is not supported");
      } else if (!url.getPath().startsWith("/")) {
         throw new IllegalArgumentException("Incorrect WebService address=" + address + ". The address's path should start with /");
      } else {
         this.endpointContext = ctxt;
         this.actualEndpoint = new HttpEndpoint((Executor)null, this.getAdapter(wse, url.getPath()));
         ((HttpEndpoint)this.actualEndpoint).publish(address);
         this.binding = wse.getBinding();
         this.implementor = null;
         this.implClass = null;
         this.invoker = null;
      }
   }

   public Binding getBinding() {
      return this.binding;
   }

   public Object getImplementor() {
      return this.implementor;
   }

   public void publish(String address) {
      this.canPublish();

      URL url;
      try {
         url = new URL(address);
      } catch (MalformedURLException var4) {
         throw new IllegalArgumentException("Cannot create URL for this address " + address);
      }

      if (!url.getProtocol().equals("http")) {
         throw new IllegalArgumentException(url.getProtocol() + " protocol based address is not supported");
      } else if (!url.getPath().startsWith("/")) {
         throw new IllegalArgumentException("Incorrect WebService address=" + address + ". The address's path should start with /");
      } else {
         this.createEndpoint(url.getPath());
         ((HttpEndpoint)this.actualEndpoint).publish(address);
      }
   }

   public void publish(Object serverContext) {
      this.canPublish();
      if (!HttpContext.class.isAssignableFrom(serverContext.getClass())) {
         throw new IllegalArgumentException(serverContext.getClass() + " is not a supported context.");
      } else {
         this.createEndpoint(((HttpContext)serverContext).getPath());
         ((HttpEndpoint)this.actualEndpoint).publish(serverContext);
      }
   }

   public void publish(javax.xml.ws.spi.http.HttpContext serverContext) {
      this.canPublish();
      this.createEndpoint(serverContext.getPath());
      ((HttpEndpoint)this.actualEndpoint).publish((Object)serverContext);
   }

   public void stop() {
      if (this.isPublished()) {
         ((HttpEndpoint)this.actualEndpoint).stop();
         this.actualEndpoint = null;
         this.stopped = true;
      }

   }

   public boolean isPublished() {
      return this.actualEndpoint != null;
   }

   public List<Source> getMetadata() {
      return this.metadata;
   }

   public void setMetadata(List<Source> metadata) {
      if (this.isPublished()) {
         throw new IllegalStateException("Cannot set Metadata. Endpoint is already published");
      } else {
         this.metadata = metadata;
      }
   }

   public Executor getExecutor() {
      return this.executor;
   }

   public void setExecutor(Executor executor) {
      this.executor = executor;
   }

   public Map<String, Object> getProperties() {
      return new HashMap(this.properties);
   }

   public void setProperties(Map<String, Object> map) {
      this.properties = new HashMap(map);
   }

   private void createEndpoint(String urlPattern) {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(ENDPOINT_PUBLISH_PERMISSION);
      }

      try {
         Class.forName("com.sun.net.httpserver.HttpServer");
      } catch (Exception var5) {
         throw new UnsupportedOperationException("Couldn't load light weight http server", var5);
      }

      this.container = this.getContainer();
      MetadataReader metadataReader = EndpointFactory.getExternalMetadatReader(this.implClass, this.binding);
      WSEndpoint wse = WSEndpoint.create(this.implClass, true, this.invoker, (QName)this.getProperty(QName.class, "javax.xml.ws.wsdl.service"), (QName)this.getProperty(QName.class, "javax.xml.ws.wsdl.port"), this.container, this.binding, this.getPrimaryWsdl(metadataReader), this.buildDocList(), (EntityResolver)null, false);
      this.actualEndpoint = new HttpEndpoint(this.executor, this.getAdapter(wse, urlPattern));
   }

   private <T> T getProperty(Class<T> type, String key) {
      Object o = this.properties.get(key);
      if (o == null) {
         return null;
      } else if (type.isInstance(o)) {
         return type.cast(o);
      } else {
         throw new IllegalArgumentException("Property " + key + " has to be of type " + type);
      }
   }

   private List<SDDocumentSource> buildDocList() {
      List<SDDocumentSource> r = new ArrayList();
      if (this.metadata != null) {
         Iterator var2 = this.metadata.iterator();

         while(var2.hasNext()) {
            Source source = (Source)var2.next();

            try {
               XMLStreamBufferResult xsbr = (XMLStreamBufferResult)XmlUtil.identityTransform(source, new XMLStreamBufferResult());
               String systemId = source.getSystemId();
               r.add(SDDocumentSource.create(new URL(systemId), xsbr.getXMLStreamBuffer()));
            } catch (TransformerException var6) {
               throw new ServerRtException("server.rt.err", new Object[]{var6});
            } catch (IOException var7) {
               throw new ServerRtException("server.rt.err", new Object[]{var7});
            } catch (SAXException var8) {
               throw new ServerRtException("server.rt.err", new Object[]{var8});
            } catch (ParserConfigurationException var9) {
               throw new ServerRtException("server.rt.err", new Object[]{var9});
            }
         }
      }

      return r;
   }

   @Nullable
   private SDDocumentSource getPrimaryWsdl(MetadataReader metadataReader) {
      EndpointFactory.verifyImplementorClass(this.implClass, metadataReader);
      String wsdlLocation = EndpointFactory.getWsdlLocation(this.implClass, metadataReader);
      if (wsdlLocation != null) {
         ClassLoader cl = this.implClass.getClassLoader();
         URL url = cl.getResource(wsdlLocation);
         if (url != null) {
            return SDDocumentSource.create(url);
         } else {
            throw new ServerRtException("cannot.load.wsdl", new Object[]{wsdlLocation});
         }
      } else {
         return null;
      }
   }

   private void canPublish() {
      if (this.isPublished()) {
         throw new IllegalStateException("Cannot publish this endpoint. Endpoint has been already published.");
      } else if (this.stopped) {
         throw new IllegalStateException("Cannot publish this endpoint. Endpoint has been already stopped.");
      }
   }

   public EndpointReference getEndpointReference(Element... referenceParameters) {
      return this.getEndpointReference(W3CEndpointReference.class, referenceParameters);
   }

   public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, Element... referenceParameters) {
      if (!this.isPublished()) {
         throw new WebServiceException("Endpoint is not published yet");
      } else {
         return ((HttpEndpoint)this.actualEndpoint).getEndpointReference(clazz, referenceParameters);
      }
   }

   public void setEndpointContext(EndpointContext ctxt) {
      this.endpointContext = ctxt;
   }

   private HttpAdapter getAdapter(WSEndpoint endpoint, String urlPattern) {
      HttpAdapterList adapterList = null;
      if (this.endpointContext != null) {
         if (this.endpointContext instanceof Component) {
            adapterList = (HttpAdapterList)((Component)this.endpointContext).getSPI(HttpAdapterList.class);
         }

         if (adapterList == null) {
            Iterator var4 = this.endpointContext.getEndpoints().iterator();

            while(var4.hasNext()) {
               Endpoint e = (Endpoint)var4.next();
               if (e.isPublished() && e != this) {
                  adapterList = ((HttpEndpoint)((HttpEndpoint)((EndpointImpl)e).actualEndpoint)).getAdapterOwner();

                  assert adapterList != null;
                  break;
               }
            }
         }
      }

      if (adapterList == null) {
         adapterList = new ServerAdapterList();
      }

      return ((HttpAdapterList)adapterList).createAdapter("", urlPattern, endpoint);
   }

   private Container getContainer() {
      if (this.endpointContext != null) {
         if (this.endpointContext instanceof Component) {
            Container c = (Container)((Component)this.endpointContext).getSPI(Container.class);
            if (c != null) {
               return c;
            }
         }

         Iterator var3 = this.endpointContext.getEndpoints().iterator();

         while(var3.hasNext()) {
            Endpoint e = (Endpoint)var3.next();
            if (e.isPublished() && e != this) {
               return ((EndpointImpl)e).container;
            }
         }
      }

      return new ServerContainer();
   }

   private static class InvokerImpl extends Invoker {
      private javax.xml.ws.spi.Invoker spiInvoker;

      InvokerImpl(javax.xml.ws.spi.Invoker spiInvoker) {
         this.spiInvoker = spiInvoker;
      }

      public void start(@NotNull WSWebServiceContext wsc, @NotNull WSEndpoint endpoint) {
         try {
            this.spiInvoker.inject(wsc);
         } catch (IllegalAccessException var4) {
            throw new WebServiceException(var4);
         } catch (InvocationTargetException var5) {
            throw new WebServiceException(var5);
         }
      }

      public Object invoke(@NotNull Packet p, @NotNull Method m, @NotNull Object... args) throws InvocationTargetException, IllegalAccessException {
         return this.spiInvoker.invoke(m, args);
      }
   }
}
