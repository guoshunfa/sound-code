package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.handler.HandlerChainsModel;
import com.sun.xml.internal.ws.util.HandlerAnnotationInfo;
import com.sun.xml.internal.ws.util.HandlerAnnotationProcessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.soap.SOAPBinding;

abstract class HandlerConfigurator {
   abstract void configureHandlers(@NotNull WSPortInfo var1, @NotNull BindingImpl var2);

   abstract HandlerResolver getResolver();

   static final class AnnotationConfigurator extends HandlerConfigurator {
      private final HandlerChainsModel handlerModel;
      private final Map<WSPortInfo, HandlerAnnotationInfo> chainMap = new HashMap();
      private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.handler");

      AnnotationConfigurator(WSServiceDelegate delegate) {
         this.handlerModel = HandlerAnnotationProcessor.buildHandlerChainsModel(delegate.getServiceClass());

         assert this.handlerModel != null;

      }

      void configureHandlers(WSPortInfo port, BindingImpl binding) {
         HandlerAnnotationInfo chain = (HandlerAnnotationInfo)this.chainMap.get(port);
         if (chain == null) {
            this.logGetChain(port);
            chain = this.handlerModel.getHandlersForPortInfo(port);
            this.chainMap.put(port, chain);
         }

         if (binding instanceof SOAPBinding) {
            ((SOAPBinding)binding).setRoles(chain.getRoles());
         }

         this.logSetChain(port, chain);
         binding.setHandlerChain(chain.getHandlers());
      }

      HandlerResolver getResolver() {
         return new HandlerResolver() {
            public List<Handler> getHandlerChain(javax.xml.ws.handler.PortInfo portInfo) {
               return new ArrayList(AnnotationConfigurator.this.handlerModel.getHandlersForPortInfo(portInfo).getHandlers());
            }
         };
      }

      private void logSetChain(WSPortInfo info, HandlerAnnotationInfo chain) {
         logger.finer("Setting chain of length " + chain.getHandlers().size() + " for port info");
         this.logPortInfo(info, Level.FINER);
      }

      private void logGetChain(WSPortInfo info) {
         logger.fine("No handler chain found for port info:");
         this.logPortInfo(info, Level.FINE);
         logger.fine("Existing handler chains:");
         if (this.chainMap.isEmpty()) {
            logger.fine("none");
         } else {
            Iterator var2 = this.chainMap.keySet().iterator();

            while(var2.hasNext()) {
               WSPortInfo key = (WSPortInfo)var2.next();
               logger.fine(((HandlerAnnotationInfo)this.chainMap.get(key)).getHandlers().size() + " handlers for port info ");
               this.logPortInfo(key, Level.FINE);
            }
         }

      }

      private void logPortInfo(WSPortInfo info, Level level) {
         logger.log(level, "binding: " + info.getBindingID() + "\nservice: " + info.getServiceName() + "\nport: " + info.getPortName());
      }
   }

   static final class HandlerResolverImpl extends HandlerConfigurator {
      @Nullable
      private final HandlerResolver resolver;

      public HandlerResolverImpl(HandlerResolver resolver) {
         this.resolver = resolver;
      }

      void configureHandlers(@NotNull WSPortInfo port, @NotNull BindingImpl binding) {
         if (this.resolver != null) {
            binding.setHandlerChain(this.resolver.getHandlerChain(port));
         }

      }

      HandlerResolver getResolver() {
         return this.resolver;
      }
   }
}
