package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.AbstractInstanceResolver;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public final class SingletonResolver<T> extends AbstractInstanceResolver<T> {
   @NotNull
   private final T singleton;

   public SingletonResolver(@NotNull T singleton) {
      this.singleton = singleton;
   }

   @NotNull
   public T resolve(Packet request) {
      return this.singleton;
   }

   public void start(WSWebServiceContext wsc, WSEndpoint endpoint) {
      getResourceInjector(endpoint).inject(wsc, this.singleton);
      invokeMethod(this.findAnnotatedMethod(this.singleton.getClass(), PostConstruct.class), this.singleton, new Object[0]);
   }

   public void dispose() {
      invokeMethod(this.findAnnotatedMethod(this.singleton.getClass(), PreDestroy.class), this.singleton, new Object[0]);
   }
}
