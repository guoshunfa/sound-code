package com.sun.xml.internal.ws.dump;

import java.util.logging.Level;
import java.util.logging.Logger;

final class MessageDumper {
   private final String tubeName;
   private final Logger logger;
   private Level loggingLevel;

   public MessageDumper(String tubeName, Logger logger, Level loggingLevel) {
      this.tubeName = tubeName;
      this.logger = logger;
      this.loggingLevel = loggingLevel;
   }

   final boolean isLoggable() {
      return this.logger.isLoggable(this.loggingLevel);
   }

   final void setLoggingLevel(Level level) {
      this.loggingLevel = level;
   }

   final String createLogMessage(MessageDumper.MessageType messageType, MessageDumper.ProcessingState processingState, int tubeId, String engineId, String message) {
      return String.format("%s %s in Tube [ %s ] Instance [ %d ] Engine [ %s ] Thread [ %s ]:%n%s", messageType, processingState, this.tubeName, tubeId, engineId, Thread.currentThread().getName(), message);
   }

   final String dump(MessageDumper.MessageType messageType, MessageDumper.ProcessingState processingState, String message, int tubeId, String engineId) {
      String logMessage = this.createLogMessage(messageType, processingState, tubeId, engineId, message);
      this.logger.log(this.loggingLevel, logMessage);
      return logMessage;
   }

   static enum ProcessingState {
      Received("received"),
      Processed("processed");

      private final String name;

      private ProcessingState(String name) {
         this.name = name;
      }

      public String toString() {
         return this.name;
      }
   }

   static enum MessageType {
      Request("Request message"),
      Response("Response message"),
      Exception("Response exception");

      private final String name;

      private MessageType(String name) {
         this.name = name;
      }

      public String toString() {
         return this.name;
      }
   }
}
