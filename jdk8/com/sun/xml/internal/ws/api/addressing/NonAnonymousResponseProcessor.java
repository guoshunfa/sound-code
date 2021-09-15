package com.sun.xml.internal.ws.api.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.TransportTubeFactory;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.binding.BindingImpl;

public class NonAnonymousResponseProcessor {
   private static final NonAnonymousResponseProcessor DEFAULT = new NonAnonymousResponseProcessor();

   public static NonAnonymousResponseProcessor getDefault() {
      return DEFAULT;
   }

   protected NonAnonymousResponseProcessor() {
   }

   public Packet process(Packet packet) {
      Fiber.CompletionCallback fiberCallback = null;
      Fiber currentFiber = Fiber.getCurrentIfSet();
      if (currentFiber != null) {
         final Fiber.CompletionCallback currentFiberCallback = currentFiber.getCompletionCallback();
         if (currentFiberCallback != null) {
            fiberCallback = new Fiber.CompletionCallback() {
               public void onCompletion(@NotNull Packet response) {
                  currentFiberCallback.onCompletion(response);
               }

               public void onCompletion(@NotNull Throwable error) {
                  currentFiberCallback.onCompletion(error);
               }
            };
            currentFiber.setCompletionCallback((Fiber.CompletionCallback)null);
         }
      }

      WSEndpoint<?> endpoint = packet.endpoint;
      WSBinding binding = endpoint.getBinding();
      Tube transport = TransportTubeFactory.create(Thread.currentThread().getContextClassLoader(), new ClientTubeAssemblerContext(packet.endpointAddress, endpoint.getPort(), (WSService)null, binding, endpoint.getContainer(), ((BindingImpl)binding).createCodec(), (SEIModel)null, (Class)null));
      Fiber fiber = endpoint.getEngine().createFiber();
      fiber.start(transport, packet, fiberCallback);
      Packet copy = packet.copy(false);
      copy.endpointAddress = null;
      return copy;
   }
}
