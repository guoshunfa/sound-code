package com.sun.xml.internal.ws.dump;

import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.commons.xmlutil.Converter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

final class MessageDumpingTube extends AbstractFilterTubeImpl {
   static final String DEFAULT_MSGDUMP_LOGGING_ROOT = "com.sun.xml.internal.ws.messagedump";
   private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
   private final MessageDumper messageDumper;
   private final int tubeId;
   private final MessageDumpingFeature messageDumpingFeature;

   MessageDumpingTube(Tube next, MessageDumpingFeature feature) {
      super(next);
      this.messageDumpingFeature = feature;
      this.tubeId = ID_GENERATOR.incrementAndGet();
      this.messageDumper = new MessageDumper("MesageDumpingTube", Logger.getLogger(feature.getMessageLoggingRoot()), feature.getMessageLoggingLevel());
   }

   MessageDumpingTube(MessageDumpingTube that, TubeCloner cloner) {
      super(that, cloner);
      this.messageDumpingFeature = that.messageDumpingFeature;
      this.tubeId = ID_GENERATOR.incrementAndGet();
      this.messageDumper = that.messageDumper;
   }

   public MessageDumpingTube copy(TubeCloner cloner) {
      return new MessageDumpingTube(this, cloner);
   }

   public NextAction processRequest(Packet request) {
      this.dump(MessageDumper.MessageType.Request, Converter.toString(request), Fiber.current().owner.id);
      return super.processRequest(request);
   }

   public NextAction processResponse(Packet response) {
      this.dump(MessageDumper.MessageType.Response, Converter.toString(response), Fiber.current().owner.id);
      return super.processResponse(response);
   }

   public NextAction processException(Throwable t) {
      this.dump(MessageDumper.MessageType.Exception, Converter.toString(t), Fiber.current().owner.id);
      return super.processException(t);
   }

   protected final void dump(MessageDumper.MessageType messageType, String message, String engineId) {
      String logMessage;
      if (this.messageDumpingFeature.getMessageLoggingStatus()) {
         this.messageDumper.setLoggingLevel(this.messageDumpingFeature.getMessageLoggingLevel());
         logMessage = this.messageDumper.dump(messageType, MessageDumper.ProcessingState.Received, message, this.tubeId, engineId);
      } else {
         logMessage = this.messageDumper.createLogMessage(messageType, MessageDumper.ProcessingState.Received, this.tubeId, engineId, message);
      }

      this.messageDumpingFeature.offerMessage(logMessage);
   }
}
