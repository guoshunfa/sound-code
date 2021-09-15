package org.w3c.dom.events;

public class EventException extends RuntimeException {
   public short code;
   public static final short UNSPECIFIED_EVENT_TYPE_ERR = 0;

   public EventException(short code, String message) {
      super(message);
      this.code = code;
   }
}
