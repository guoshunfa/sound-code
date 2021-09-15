package com.sun.xml.internal.org.jvnet.mimepull;

import java.nio.ByteBuffer;

abstract class MIMEEvent {
   static final MIMEEvent.StartMessage START_MESSAGE = new MIMEEvent.StartMessage();
   static final MIMEEvent.StartPart START_PART = new MIMEEvent.StartPart();
   static final MIMEEvent.EndPart END_PART = new MIMEEvent.EndPart();
   static final MIMEEvent.EndMessage END_MESSAGE = new MIMEEvent.EndMessage();

   abstract MIMEEvent.EVENT_TYPE getEventType();

   static final class EndMessage extends MIMEEvent {
      MIMEEvent.EVENT_TYPE getEventType() {
         return MIMEEvent.EVENT_TYPE.END_MESSAGE;
      }
   }

   static final class Content extends MIMEEvent {
      private final ByteBuffer buf;

      Content(ByteBuffer buf) {
         this.buf = buf;
      }

      MIMEEvent.EVENT_TYPE getEventType() {
         return MIMEEvent.EVENT_TYPE.CONTENT;
      }

      ByteBuffer getData() {
         return this.buf;
      }
   }

   static final class Headers extends MIMEEvent {
      InternetHeaders ih;

      Headers(InternetHeaders ih) {
         this.ih = ih;
      }

      MIMEEvent.EVENT_TYPE getEventType() {
         return MIMEEvent.EVENT_TYPE.HEADERS;
      }

      InternetHeaders getHeaders() {
         return this.ih;
      }
   }

   static final class EndPart extends MIMEEvent {
      MIMEEvent.EVENT_TYPE getEventType() {
         return MIMEEvent.EVENT_TYPE.END_PART;
      }
   }

   static final class StartPart extends MIMEEvent {
      MIMEEvent.EVENT_TYPE getEventType() {
         return MIMEEvent.EVENT_TYPE.START_PART;
      }
   }

   static final class StartMessage extends MIMEEvent {
      MIMEEvent.EVENT_TYPE getEventType() {
         return MIMEEvent.EVENT_TYPE.START_MESSAGE;
      }
   }

   static enum EVENT_TYPE {
      START_MESSAGE,
      START_PART,
      HEADERS,
      CONTENT,
      END_PART,
      END_MESSAGE;
   }
}
