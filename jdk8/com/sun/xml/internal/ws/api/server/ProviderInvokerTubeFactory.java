package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.server.provider.AsyncProviderInvokerTube;
import com.sun.xml.internal.ws.server.provider.ProviderArgumentsBuilder;
import com.sun.xml.internal.ws.server.provider.ProviderInvokerTube;
import com.sun.xml.internal.ws.server.provider.SyncProviderInvokerTube;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ProviderInvokerTubeFactory<T> {
   private static final ProviderInvokerTubeFactory DEFAULT = new ProviderInvokerTubeFactory.DefaultProviderInvokerTubeFactory();
   private static final Logger logger = Logger.getLogger(ProviderInvokerTubeFactory.class.getName());

   protected abstract ProviderInvokerTube<T> doCreate(@NotNull Class<T> var1, @NotNull Invoker var2, @NotNull ProviderArgumentsBuilder<?> var3, boolean var4);

   public static <T> ProviderInvokerTube<T> create(@Nullable ClassLoader classLoader, @NotNull Container container, @NotNull Class<T> implType, @NotNull Invoker invoker, @NotNull ProviderArgumentsBuilder<?> argsBuilder, boolean isAsync) {
      Iterator var6 = ServiceFinder.find(ProviderInvokerTubeFactory.class, classLoader, container).iterator();

      ProviderInvokerTubeFactory factory;
      ProviderInvokerTube tube;
      do {
         if (!var6.hasNext()) {
            return DEFAULT.createDefault(implType, invoker, argsBuilder, isAsync);
         }

         factory = (ProviderInvokerTubeFactory)var6.next();
         tube = factory.doCreate(implType, invoker, argsBuilder, isAsync);
      } while(tube == null);

      if (logger.isLoggable(Level.FINE)) {
         logger.log(Level.FINE, "{0} successfully created {1}", new Object[]{factory.getClass(), tube});
      }

      return tube;
   }

   protected ProviderInvokerTube<T> createDefault(@NotNull Class<T> implType, @NotNull Invoker invoker, @NotNull ProviderArgumentsBuilder<?> argsBuilder, boolean isAsync) {
      return (ProviderInvokerTube)(isAsync ? new AsyncProviderInvokerTube(invoker, argsBuilder) : new SyncProviderInvokerTube(invoker, argsBuilder));
   }

   private static class DefaultProviderInvokerTubeFactory<T> extends ProviderInvokerTubeFactory<T> {
      private DefaultProviderInvokerTubeFactory() {
      }

      public ProviderInvokerTube<T> doCreate(@NotNull Class<T> implType, @NotNull Invoker invoker, @NotNull ProviderArgumentsBuilder<?> argsBuilder, boolean isAsync) {
         return this.createDefault(implType, invoker, argsBuilder, isAsync);
      }

      // $FF: synthetic method
      DefaultProviderInvokerTubeFactory(Object x0) {
         this();
      }
   }
}
