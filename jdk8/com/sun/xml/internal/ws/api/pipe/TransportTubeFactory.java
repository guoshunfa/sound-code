package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.WebServiceException;

public abstract class TransportTubeFactory {
   private static final TransportTubeFactory DEFAULT = new TransportTubeFactory.DefaultTransportTubeFactory();
   private static final Logger logger = Logger.getLogger(TransportTubeFactory.class.getName());

   public abstract Tube doCreate(@NotNull ClientTubeAssemblerContext var1);

   public static Tube create(@Nullable ClassLoader classLoader, @NotNull ClientTubeAssemblerContext context) {
      Iterator var2 = ServiceFinder.find(TransportTubeFactory.class, classLoader, context.getContainer()).iterator();

      TransportTubeFactory factory;
      Tube tube;
      do {
         if (!var2.hasNext()) {
            ClientPipeAssemblerContext ctxt = new ClientPipeAssemblerContext(context.getAddress(), context.getWsdlModel(), context.getService(), context.getBinding(), context.getContainer());
            ctxt.setCodec(context.getCodec());
            Iterator var7 = ServiceFinder.find(TransportPipeFactory.class, classLoader).iterator();

            Pipe pipe;
            TransportPipeFactory factory;
            do {
               if (!var7.hasNext()) {
                  return DEFAULT.createDefault(ctxt);
               }

               factory = (TransportPipeFactory)var7.next();
               pipe = factory.doCreate(ctxt);
            } while(pipe == null);

            if (logger.isLoggable(Level.FINE)) {
               logger.log(Level.FINE, "{0} successfully created {1}", new Object[]{factory.getClass(), pipe});
            }

            return PipeAdapter.adapt(pipe);
         }

         factory = (TransportTubeFactory)var2.next();
         tube = factory.doCreate(context);
      } while(tube == null);

      if (logger.isLoggable(Level.FINE)) {
         logger.log(Level.FINE, "{0} successfully created {1}", new Object[]{factory.getClass(), tube});
      }

      return tube;
   }

   protected Tube createDefault(ClientTubeAssemblerContext context) {
      String scheme = context.getAddress().getURI().getScheme();
      if (scheme == null || !scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
         throw new WebServiceException("Unsupported endpoint address: " + context.getAddress());
      } else {
         return this.createHttpTransport(context);
      }
   }

   protected Tube createHttpTransport(ClientTubeAssemblerContext context) {
      return new HttpTransportPipe(context.getCodec(), context.getBinding());
   }

   private static class DefaultTransportTubeFactory extends TransportTubeFactory {
      private DefaultTransportTubeFactory() {
      }

      public Tube doCreate(ClientTubeAssemblerContext context) {
         return this.createDefault(context);
      }

      // $FF: synthetic method
      DefaultTransportTubeFactory(Object x0) {
         this();
      }
   }
}
