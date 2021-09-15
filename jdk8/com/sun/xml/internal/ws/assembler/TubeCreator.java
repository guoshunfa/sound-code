package com.sun.xml.internal.ws.assembler;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.TubeFactory;
import com.sun.xml.internal.ws.assembler.dev.TubelineAssemblyContextUpdater;
import com.sun.xml.internal.ws.resources.TubelineassemblyMessages;
import com.sun.xml.internal.ws.runtime.config.TubeFactoryConfig;

final class TubeCreator {
   private static final Logger LOGGER = Logger.getLogger(TubeCreator.class);
   private final TubeFactory factory;
   private final String msgDumpPropertyBase;

   TubeCreator(TubeFactoryConfig config, ClassLoader tubeFactoryClassLoader) {
      String className = config.getClassName();

      try {
         Class factoryClass;
         if (this.isJDKInternal(className)) {
            factoryClass = Class.forName(className, true, (ClassLoader)null);
         } else {
            factoryClass = Class.forName(className, true, tubeFactoryClassLoader);
         }

         if (TubeFactory.class.isAssignableFrom(factoryClass)) {
            this.factory = (TubeFactory)factoryClass.newInstance();
            this.msgDumpPropertyBase = this.factory.getClass().getName() + ".dump";
         } else {
            throw new RuntimeException(TubelineassemblyMessages.MASM_0015_CLASS_DOES_NOT_IMPLEMENT_INTERFACE(factoryClass.getName(), TubeFactory.class.getName()));
         }
      } catch (InstantiationException var6) {
         throw (RuntimeException)LOGGER.logSevereException(new RuntimeException(TubelineassemblyMessages.MASM_0016_UNABLE_TO_INSTANTIATE_TUBE_FACTORY(className), var6), true);
      } catch (IllegalAccessException var7) {
         throw (RuntimeException)LOGGER.logSevereException(new RuntimeException(TubelineassemblyMessages.MASM_0016_UNABLE_TO_INSTANTIATE_TUBE_FACTORY(className), var7), true);
      } catch (ClassNotFoundException var8) {
         throw (RuntimeException)LOGGER.logSevereException(new RuntimeException(TubelineassemblyMessages.MASM_0017_UNABLE_TO_LOAD_TUBE_FACTORY_CLASS(className), var8), true);
      }
   }

   Tube createTube(DefaultClientTubelineAssemblyContext context) {
      return this.factory.createTube((ClientTubelineAssemblyContext)context);
   }

   Tube createTube(DefaultServerTubelineAssemblyContext context) {
      return this.factory.createTube((ServerTubelineAssemblyContext)context);
   }

   void updateContext(ClientTubelineAssemblyContext context) {
      if (this.factory instanceof TubelineAssemblyContextUpdater) {
         ((TubelineAssemblyContextUpdater)this.factory).prepareContext(context);
      }

   }

   void updateContext(DefaultServerTubelineAssemblyContext context) {
      if (this.factory instanceof TubelineAssemblyContextUpdater) {
         ((TubelineAssemblyContextUpdater)this.factory).prepareContext((ServerTubelineAssemblyContext)context);
      }

   }

   String getMessageDumpPropertyBase() {
      return this.msgDumpPropertyBase;
   }

   private boolean isJDKInternal(String className) {
      return className.startsWith("com.sun.xml.internal.ws");
   }
}
