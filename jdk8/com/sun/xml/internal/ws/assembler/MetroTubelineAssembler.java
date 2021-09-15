package com.sun.xml.internal.ws.assembler;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubelineAssembler;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.TubelineAssemblyDecorator;
import com.sun.xml.internal.ws.dump.LoggingDumpTube;
import com.sun.xml.internal.ws.resources.TubelineassemblyMessages;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

public class MetroTubelineAssembler implements TubelineAssembler {
   private static final String COMMON_MESSAGE_DUMP_SYSTEM_PROPERTY_BASE = "com.sun.metro.soap.dump";
   public static final MetroConfigNameImpl JAXWS_TUBES_CONFIG_NAMES = new MetroConfigNameImpl("jaxws-tubes-default.xml", "jaxws-tubes.xml");
   private static final Logger LOGGER = Logger.getLogger(MetroTubelineAssembler.class);
   private final BindingID bindingId;
   private final TubelineAssemblyController tubelineAssemblyController;

   public MetroTubelineAssembler(BindingID bindingId, MetroConfigName metroConfigName) {
      this.bindingId = bindingId;
      this.tubelineAssemblyController = new TubelineAssemblyController(metroConfigName);
   }

   TubelineAssemblyController getTubelineAssemblyController() {
      return this.tubelineAssemblyController;
   }

   @NotNull
   public Tube createClient(@NotNull ClientTubeAssemblerContext jaxwsContext) {
      if (LOGGER.isLoggable(Level.FINER)) {
         LOGGER.finer("Assembling client-side tubeline for WS endpoint: " + jaxwsContext.getAddress().getURI().toString());
      }

      DefaultClientTubelineAssemblyContext context = this.createClientContext(jaxwsContext);
      Collection<TubeCreator> tubeCreators = this.tubelineAssemblyController.getTubeCreators((ClientTubelineAssemblyContext)context);
      Iterator var4 = tubeCreators.iterator();

      while(var4.hasNext()) {
         TubeCreator tubeCreator = (TubeCreator)var4.next();
         tubeCreator.updateContext((ClientTubelineAssemblyContext)context);
      }

      TubelineAssemblyDecorator decorator = TubelineAssemblyDecorator.composite(ServiceFinder.find(TubelineAssemblyDecorator.class, (Component)context.getContainer()));
      boolean first = true;
      Iterator var6 = tubeCreators.iterator();

      while(var6.hasNext()) {
         TubeCreator tubeCreator = (TubeCreator)var6.next();
         MetroTubelineAssembler.MessageDumpingInfo msgDumpInfo = this.setupMessageDumping(tubeCreator.getMessageDumpPropertyBase(), MetroTubelineAssembler.Side.Client);
         Tube oldTubelineHead = context.getTubelineHead();
         LoggingDumpTube afterDumpTube = null;
         if (msgDumpInfo.dumpAfter) {
            afterDumpTube = new LoggingDumpTube(msgDumpInfo.logLevel, LoggingDumpTube.Position.After, context.getTubelineHead());
            context.setTubelineHead(afterDumpTube);
         }

         if (!context.setTubelineHead(decorator.decorateClient(tubeCreator.createTube(context), context))) {
            if (afterDumpTube != null) {
               context.setTubelineHead(oldTubelineHead);
            }
         } else {
            String loggedTubeName = context.getTubelineHead().getClass().getName();
            if (afterDumpTube != null) {
               afterDumpTube.setLoggedTubeName(loggedTubeName);
            }

            if (msgDumpInfo.dumpBefore) {
               LoggingDumpTube beforeDumpTube = new LoggingDumpTube(msgDumpInfo.logLevel, LoggingDumpTube.Position.Before, context.getTubelineHead());
               beforeDumpTube.setLoggedTubeName(loggedTubeName);
               context.setTubelineHead(beforeDumpTube);
            }
         }

         if (first) {
            context.setTubelineHead(decorator.decorateClientTail(context.getTubelineHead(), context));
            first = false;
         }
      }

      return decorator.decorateClientHead(context.getTubelineHead(), context);
   }

   @NotNull
   public Tube createServer(@NotNull ServerTubeAssemblerContext jaxwsContext) {
      if (LOGGER.isLoggable(Level.FINER)) {
         LOGGER.finer("Assembling endpoint tubeline for WS endpoint: " + jaxwsContext.getEndpoint().getServiceName() + "::" + jaxwsContext.getEndpoint().getPortName());
      }

      DefaultServerTubelineAssemblyContext context = this.createServerContext(jaxwsContext);
      Collection<TubeCreator> tubeCreators = this.tubelineAssemblyController.getTubeCreators(context);
      Iterator var4 = tubeCreators.iterator();

      while(var4.hasNext()) {
         TubeCreator tubeCreator = (TubeCreator)var4.next();
         tubeCreator.updateContext(context);
      }

      TubelineAssemblyDecorator decorator = TubelineAssemblyDecorator.composite(ServiceFinder.find(TubelineAssemblyDecorator.class, (Component)context.getEndpoint().getContainer()));
      boolean first = true;
      Iterator var6 = tubeCreators.iterator();

      while(var6.hasNext()) {
         TubeCreator tubeCreator = (TubeCreator)var6.next();
         MetroTubelineAssembler.MessageDumpingInfo msgDumpInfo = this.setupMessageDumping(tubeCreator.getMessageDumpPropertyBase(), MetroTubelineAssembler.Side.Endpoint);
         Tube oldTubelineHead = context.getTubelineHead();
         LoggingDumpTube afterDumpTube = null;
         if (msgDumpInfo.dumpAfter) {
            afterDumpTube = new LoggingDumpTube(msgDumpInfo.logLevel, LoggingDumpTube.Position.After, context.getTubelineHead());
            context.setTubelineHead(afterDumpTube);
         }

         if (!context.setTubelineHead(decorator.decorateServer(tubeCreator.createTube(context), context))) {
            if (afterDumpTube != null) {
               context.setTubelineHead(oldTubelineHead);
            }
         } else {
            String loggedTubeName = context.getTubelineHead().getClass().getName();
            if (afterDumpTube != null) {
               afterDumpTube.setLoggedTubeName(loggedTubeName);
            }

            if (msgDumpInfo.dumpBefore) {
               LoggingDumpTube beforeDumpTube = new LoggingDumpTube(msgDumpInfo.logLevel, LoggingDumpTube.Position.Before, context.getTubelineHead());
               beforeDumpTube.setLoggedTubeName(loggedTubeName);
               context.setTubelineHead(beforeDumpTube);
            }
         }

         if (first) {
            context.setTubelineHead(decorator.decorateServerTail(context.getTubelineHead(), context));
            first = false;
         }
      }

      return decorator.decorateServerHead(context.getTubelineHead(), context);
   }

   private MetroTubelineAssembler.MessageDumpingInfo setupMessageDumping(String msgDumpSystemPropertyBase, MetroTubelineAssembler.Side side) {
      boolean dumpBefore = false;
      boolean dumpAfter = false;
      Level logLevel = Level.INFO;
      Boolean value = this.getBooleanValue("com.sun.metro.soap.dump");
      if (value != null) {
         dumpBefore = value;
         dumpAfter = value;
      }

      value = this.getBooleanValue("com.sun.metro.soap.dump.before");
      dumpBefore = value != null ? value : dumpBefore;
      value = this.getBooleanValue("com.sun.metro.soap.dump.after");
      dumpAfter = value != null ? value : dumpAfter;
      Level levelValue = this.getLevelValue("com.sun.metro.soap.dump.level");
      if (levelValue != null) {
         logLevel = levelValue;
      }

      value = this.getBooleanValue("com.sun.metro.soap.dump." + side.toString());
      if (value != null) {
         dumpBefore = value;
         dumpAfter = value;
      }

      value = this.getBooleanValue("com.sun.metro.soap.dump." + side.toString() + ".before");
      dumpBefore = value != null ? value : dumpBefore;
      value = this.getBooleanValue("com.sun.metro.soap.dump." + side.toString() + ".after");
      dumpAfter = value != null ? value : dumpAfter;
      levelValue = this.getLevelValue("com.sun.metro.soap.dump." + side.toString() + ".level");
      if (levelValue != null) {
         logLevel = levelValue;
      }

      value = this.getBooleanValue(msgDumpSystemPropertyBase);
      if (value != null) {
         dumpBefore = value;
         dumpAfter = value;
      }

      value = this.getBooleanValue(msgDumpSystemPropertyBase + ".before");
      dumpBefore = value != null ? value : dumpBefore;
      value = this.getBooleanValue(msgDumpSystemPropertyBase + ".after");
      dumpAfter = value != null ? value : dumpAfter;
      levelValue = this.getLevelValue(msgDumpSystemPropertyBase + ".level");
      if (levelValue != null) {
         logLevel = levelValue;
      }

      msgDumpSystemPropertyBase = msgDumpSystemPropertyBase + "." + side.toString();
      value = this.getBooleanValue(msgDumpSystemPropertyBase);
      if (value != null) {
         dumpBefore = value;
         dumpAfter = value;
      }

      value = this.getBooleanValue(msgDumpSystemPropertyBase + ".before");
      dumpBefore = value != null ? value : dumpBefore;
      value = this.getBooleanValue(msgDumpSystemPropertyBase + ".after");
      dumpAfter = value != null ? value : dumpAfter;
      levelValue = this.getLevelValue(msgDumpSystemPropertyBase + ".level");
      if (levelValue != null) {
         logLevel = levelValue;
      }

      return new MetroTubelineAssembler.MessageDumpingInfo(dumpBefore, dumpAfter, logLevel);
   }

   private Boolean getBooleanValue(String propertyName) {
      Boolean retVal = null;
      String stringValue = System.getProperty(propertyName);
      if (stringValue != null) {
         retVal = Boolean.valueOf(stringValue);
         LOGGER.fine(TubelineassemblyMessages.MASM_0018_MSG_LOGGING_SYSTEM_PROPERTY_SET_TO_VALUE(propertyName, retVal));
      }

      return retVal;
   }

   private Level getLevelValue(String propertyName) {
      Level retVal = null;
      String stringValue = System.getProperty(propertyName);
      if (stringValue != null) {
         LOGGER.fine(TubelineassemblyMessages.MASM_0018_MSG_LOGGING_SYSTEM_PROPERTY_SET_TO_VALUE(propertyName, stringValue));

         try {
            retVal = Level.parse(stringValue);
         } catch (IllegalArgumentException var5) {
            LOGGER.warning(TubelineassemblyMessages.MASM_0019_MSG_LOGGING_SYSTEM_PROPERTY_ILLEGAL_VALUE(propertyName, stringValue), (Throwable)var5);
         }
      }

      return retVal;
   }

   protected DefaultServerTubelineAssemblyContext createServerContext(ServerTubeAssemblerContext jaxwsContext) {
      return new DefaultServerTubelineAssemblyContext(jaxwsContext);
   }

   protected DefaultClientTubelineAssemblyContext createClientContext(ClientTubeAssemblerContext jaxwsContext) {
      return new DefaultClientTubelineAssemblyContext(jaxwsContext);
   }

   private static class MessageDumpingInfo {
      final boolean dumpBefore;
      final boolean dumpAfter;
      final Level logLevel;

      MessageDumpingInfo(boolean dumpBefore, boolean dumpAfter, Level logLevel) {
         this.dumpBefore = dumpBefore;
         this.dumpAfter = dumpAfter;
         this.logLevel = logLevel;
      }
   }

   private static enum Side {
      Client("client"),
      Endpoint("endpoint");

      private final String name;

      private Side(String name) {
         this.name = name;
      }

      public String toString() {
         return this.name;
      }
   }
}
