package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.pipe.StandalonePipeAssembler;
import java.util.Iterator;
import java.util.logging.Logger;

/** @deprecated */
public abstract class PipelineAssemblerFactory {
   private static final Logger logger = Logger.getLogger(PipelineAssemblerFactory.class.getName());

   public abstract PipelineAssembler doCreate(BindingID var1);

   public static PipelineAssembler create(ClassLoader classLoader, BindingID bindingId) {
      Iterator var2 = ServiceFinder.find(PipelineAssemblerFactory.class, classLoader).iterator();

      PipelineAssemblerFactory factory;
      PipelineAssembler assembler;
      do {
         if (!var2.hasNext()) {
            return new StandalonePipeAssembler();
         }

         factory = (PipelineAssemblerFactory)var2.next();
         assembler = factory.doCreate(bindingId);
      } while(assembler == null);

      logger.fine(factory.getClass() + " successfully created " + assembler);
      return assembler;
   }
}
