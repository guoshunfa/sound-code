package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.server.AbstractInstanceResolver;
import com.sun.xml.internal.ws.api.server.ResourceInjector;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import java.lang.reflect.Method;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public abstract class AbstractMultiInstanceResolver<T> extends AbstractInstanceResolver<T> {
   protected final Class<T> clazz;
   private WSWebServiceContext webServiceContext;
   protected WSEndpoint owner;
   private final Method postConstructMethod;
   private final Method preDestroyMethod;
   private ResourceInjector resourceInjector;

   public AbstractMultiInstanceResolver(Class<T> clazz) {
      this.clazz = clazz;
      this.postConstructMethod = this.findAnnotatedMethod(clazz, PostConstruct.class);
      this.preDestroyMethod = this.findAnnotatedMethod(clazz, PreDestroy.class);
   }

   protected final void prepare(T t) {
      assert this.webServiceContext != null;

      this.resourceInjector.inject(this.webServiceContext, t);
      invokeMethod(this.postConstructMethod, t, new Object[0]);
   }

   protected final T create() {
      T t = createNewInstance(this.clazz);
      this.prepare(t);
      return t;
   }

   public void start(WSWebServiceContext wsc, WSEndpoint endpoint) {
      this.resourceInjector = getResourceInjector(endpoint);
      this.webServiceContext = wsc;
      this.owner = endpoint;
   }

   protected final void dispose(T instance) {
      invokeMethod(this.preDestroyMethod, instance, new Object[0]);
   }
}
