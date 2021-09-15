package com.sun.xml.internal.ws.server.provider;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.ProviderInvokerTubeFactory;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.server.InvokerTube;
import javax.xml.ws.Provider;

public abstract class ProviderInvokerTube<T> extends InvokerTube<Provider<T>> {
   protected ProviderArgumentsBuilder<T> argsBuilder;

   ProviderInvokerTube(Invoker invoker, ProviderArgumentsBuilder<T> argsBuilder) {
      super(invoker);
      this.argsBuilder = argsBuilder;
   }

   public static <T> ProviderInvokerTube<T> create(Class<T> implType, WSBinding binding, Invoker invoker, Container container) {
      ProviderEndpointModel<T> model = new ProviderEndpointModel(implType, binding);
      ProviderArgumentsBuilder<?> argsBuilder = ProviderArgumentsBuilder.create(model, binding);
      if (binding instanceof SOAPBindingImpl) {
         ((SOAPBindingImpl)binding).setMode(model.mode);
      }

      return ProviderInvokerTubeFactory.create((ClassLoader)null, container, implType, invoker, argsBuilder, model.isAsync);
   }
}
