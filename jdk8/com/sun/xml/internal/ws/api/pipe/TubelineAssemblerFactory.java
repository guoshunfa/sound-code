package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.assembler.MetroTubelineAssembler;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class TubelineAssemblerFactory {
   private static final Logger logger = Logger.getLogger(TubelineAssemblerFactory.class.getName());

   public abstract TubelineAssembler doCreate(BindingID var1);

   /** @deprecated */
   public static TubelineAssembler create(ClassLoader classLoader, BindingID bindingId) {
      return create(classLoader, bindingId, (Container)null);
   }

   public static TubelineAssembler create(ClassLoader classLoader, BindingID bindingId, @Nullable Container container) {
      if (container != null) {
         TubelineAssemblerFactory taf = (TubelineAssemblerFactory)container.getSPI(TubelineAssemblerFactory.class);
         if (taf != null) {
            TubelineAssembler a = taf.doCreate(bindingId);
            if (a != null) {
               return a;
            }
         }
      }

      Iterator var6 = ServiceFinder.find(TubelineAssemblerFactory.class, classLoader).iterator();

      TubelineAssembler assembler;
      TubelineAssemblerFactory factory;
      do {
         if (!var6.hasNext()) {
            var6 = ServiceFinder.find(PipelineAssemblerFactory.class, classLoader).iterator();

            PipelineAssemblerFactory factory;
            PipelineAssembler assembler;
            do {
               if (!var6.hasNext()) {
                  return new MetroTubelineAssembler(bindingId, MetroTubelineAssembler.JAXWS_TUBES_CONFIG_NAMES);
               }

               factory = (PipelineAssemblerFactory)var6.next();
               assembler = factory.doCreate(bindingId);
            } while(assembler == null);

            logger.log(Level.FINE, "{0} successfully created {1}", new Object[]{factory.getClass(), assembler});
            return new TubelineAssemblerFactory.TubelineAssemblerAdapter(assembler);
         }

         factory = (TubelineAssemblerFactory)var6.next();
         assembler = factory.doCreate(bindingId);
      } while(assembler == null);

      logger.log(Level.FINE, "{0} successfully created {1}", new Object[]{factory.getClass(), assembler});
      return assembler;
   }

   private static class TubelineAssemblerAdapter implements TubelineAssembler {
      private PipelineAssembler assembler;

      TubelineAssemblerAdapter(PipelineAssembler assembler) {
         this.assembler = assembler;
      }

      @NotNull
      public Tube createClient(@NotNull ClientTubeAssemblerContext context) {
         ClientPipeAssemblerContext ctxt = new ClientPipeAssemblerContext(context.getAddress(), context.getWsdlModel(), context.getService(), context.getBinding(), context.getContainer());
         return PipeAdapter.adapt(this.assembler.createClient(ctxt));
      }

      @NotNull
      public Tube createServer(@NotNull ServerTubeAssemblerContext context) {
         if (!(context instanceof ServerPipeAssemblerContext)) {
            throw new IllegalArgumentException("{0} is not instance of ServerPipeAssemblerContext");
         } else {
            return PipeAdapter.adapt(this.assembler.createServer((ServerPipeAssemblerContext)context));
         }
      }
   }
}
